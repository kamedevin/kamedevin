package com.kamedevin.budget.backup.googledrive

import android.accounts.Account
import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.kamedevin.budget.backup.api.BackupManager
import com.kamedevin.budget.backup.api.BackupMetadata
import com.kamedevin.budget.backup.api.SignInResultCoordinator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

private const val DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata"
private const val MAX_BACKUPS_TO_KEEP = 5
private val JSON_MEDIA_TYPE = "application/json".toMediaType()

/**
 * Uses the classic GoogleSignInClient + GoogleAuthUtil pattern (rather than the newer Credential
 * Manager + Authorization API) to get a Drive-scoped access token — it's been the standard,
 * heavily-documented way to do this for years, and only needs one Android-type OAuth client
 * registered in Cloud Console (no separate web client ID).
 *
 * [DriveApi] returns raw OkHttp bodies rather than typed objects — JSON (de)serialization happens
 * here directly via kotlinx.serialization, rather than through a Retrofit converter factory.
 */
class GoogleDriveBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signInResultCoordinator: SignInResultCoordinator,
    private val driveApi: DriveApi,
    private val backupPreferences: BackupPreferences,
    private val snapshotRepository: BackupSnapshotRepository,
    private val json: Json,
) : BackupManager {

    private val signInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_APPDATA_SCOPE))
            .build()
    }

    private fun currentAccount(): GoogleSignInAccount? {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return null
        return if (GoogleSignIn.hasPermissions(account, Scope(DRIVE_APPDATA_SCOPE))) account else null
    }

    override suspend fun isSignedIn(): Boolean = currentAccount() != null

    override suspend fun signIn(activity: Activity): Result<Unit> = runCatching {
        val client = GoogleSignIn.getClient(activity, signInOptions)
        val result: ActivityResult = signInResultCoordinator.launchAndAwaitResult(client.signInIntent)
        check(result.resultCode == Activity.RESULT_OK) { "Sign-in was cancelled" }

        val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            ?: error("Google didn't return a signed-in account")
        check(GoogleSignIn.hasPermissions(account, Scope(DRIVE_APPDATA_SCOPE))) {
            "Google Drive access wasn't granted"
        }
    }

    override suspend fun signOut() {
        // Plain signOut() only clears the local cache — it does NOT revoke the previously granted
        // OAuth scopes, so a later sign-in silently reuses the old (pre-email-scope) grant without
        // re-prompting. revokeAccess() actually revokes it server-side, forcing a fresh consent
        // screen next time. Also properly awaited, unlike a fire-and-forget Task call.
        withContext(Dispatchers.IO) {
            GoogleSignIn.getClient(context, signInOptions).revokeAccess().await()
        }
    }

    private suspend fun bearerToken(): String {
        val account = currentAccount() ?: error("Not signed in")
        // GoogleSignInAccount.account is deprecated and unreliably returns null even when signed
        // in successfully. Building the Account from the email (populated via requestEmail()) is
        // the reliable path — Google accounts are always registered under the "com.google" type.
        val email = account.email ?: error("Signed-in account has no email")
        val androidAccount = Account(email, "com.google")
        val token = withContext(Dispatchers.IO) {
            GoogleAuthUtil.getToken(context, androidAccount, "oauth2:$DRIVE_APPDATA_SCOPE")
        }
        return "Bearer $token"
    }

    private suspend fun ResponseBody.readText(): String = withContext(Dispatchers.IO) { string() }

    private suspend fun fetchFileList(token: String): List<DriveFile> {
        val text = driveApi.listFiles(authorization = token).readText()
        return json.decodeFromString(DriveFileListResponse.serializer(), text).files
    }

    override suspend fun backupNow(): Result<BackupMetadata> = runCatching {
        val token = bearerToken()
        val snapshot = snapshotRepository.buildSnapshot()
        val fileName = "kame-budget-backup-${System.currentTimeMillis()}.json"

        val metadataJson = json.encodeToString(
            DriveFileMetadataRequest.serializer(),
            DriveFileMetadataRequest(name = fileName, parents = listOf("appDataFolder")),
        )
        val createdText = driveApi.createFile(
            authorization = token,
            metadata = metadataJson.toRequestBody(JSON_MEDIA_TYPE),
        ).readText()
        val created = json.decodeFromString(DriveFile.serializer(), createdText)

        val payload = json.encodeToString(BackupSnapshot.serializer(), snapshot)
        val requestBody = payload.toRequestBody(JSON_MEDIA_TYPE)
        driveApi.uploadFileContent(authorization = token, fileId = created.id, content = requestBody)

        pruneOldBackups(token)

        val now = Instant.now()
        backupPreferences.setLastBackupTime(now)

        BackupMetadata(
            fileId = created.id,
            createdAt = now,
            sizeBytes = payload.toByteArray().size.toLong(),
            appVersion = BackupSnapshot.SCHEMA_VERSION,
        )
    }

    override suspend fun restoreLatest(): Result<Unit> = runCatching {
        val token = bearerToken()
        val latest = fetchFileList(token).firstOrNull() ?: error("No backups found in Google Drive")

        val text = driveApi.downloadFileContent(authorization = token, fileId = latest.id).readText()
        val snapshot = json.decodeFromString(BackupSnapshot.serializer(), text)

        snapshotRepository.applySnapshot(snapshot)
    }

    override suspend fun listBackups(): Result<List<BackupMetadata>> = runCatching {
        val token = bearerToken()
        fetchFileList(token).map { file ->
            BackupMetadata(
                fileId = file.id,
                createdAt = file.createdTime?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: Instant.EPOCH,
                sizeBytes = file.size?.toLongOrNull() ?: 0L,
                appVersion = BackupSnapshot.SCHEMA_VERSION,
            )
        }
    }

    override fun observeLastBackupTime(): Flow<Instant?> = backupPreferences.observeLastBackupTime()

    private suspend fun pruneOldBackups(token: String) {
        val files = fetchFileList(token)
        if (files.size <= MAX_BACKUPS_TO_KEEP) return
        files.drop(MAX_BACKUPS_TO_KEEP).forEach { file ->
            runCatching { driveApi.deleteFile(authorization = token, fileId = file.id) }
        }
    }
}
