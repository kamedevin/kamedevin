package com.kamedevin.budget.backup.googledrive

import kotlinx.serialization.Serializable

@Serializable
data class DriveFileMetadataRequest(
    val name: String,
    val parents: List<String>,
)

@Serializable
data class DriveFile(
    val id: String,
    val name: String? = null,
    val createdTime: String? = null,
    val size: String? = null,
)

@Serializable
data class DriveFileListResponse(
    val files: List<DriveFile> = emptyList(),
)
