package com.kamedevin.budget.backup.googledrive

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Talks to the Drive REST API v3 directly via Retrofit/OkHttp rather than pulling in the full
 * google-api-services-drive client library, keeping this module lean. All calls target the
 * app-private `appDataFolder` space, which is hidden from the user's visible Drive.
 */
interface DriveApi {

    @POST("drive/v3/files")
    suspend fun createFile(
        @Header("Authorization") authorization: String,
        @Body metadata: DriveFileMetadataRequest,
    ): DriveFile

    @PATCH("upload/drive/v3/files/{fileId}")
    suspend fun uploadFileContent(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String,
        @Query("uploadType") uploadType: String = "media",
        @Body content: RequestBody,
    ): DriveFile

    @GET("drive/v3/files")
    suspend fun listFiles(
        @Header("Authorization") authorization: String,
        @Query("spaces") spaces: String = "appDataFolder",
        @Query("fields") fields: String = "files(id,name,createdTime,size)",
        @Query("orderBy") orderBy: String = "createdTime desc",
    ): DriveFileListResponse

    @GET("drive/v3/files/{fileId}")
    @Streaming
    suspend fun downloadFileContent(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String,
        @Query("alt") alt: String = "media",
    ): ResponseBody

    @DELETE("drive/v3/files/{fileId}")
    suspend fun deleteFile(
        @Header("Authorization") authorization: String,
        @Path("fileId") fileId: String,
    )
}
