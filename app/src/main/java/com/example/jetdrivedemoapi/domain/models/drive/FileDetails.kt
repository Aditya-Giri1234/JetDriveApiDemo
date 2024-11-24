package com.example.jetdrivedemoapi.domain.models.drive

import android.net.Uri

data class FileDetails(
    val name: String,
    val mimeType: String?,
    val uri : Uri,
    val size: Long?
)