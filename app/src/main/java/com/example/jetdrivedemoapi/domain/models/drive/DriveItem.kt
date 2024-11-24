package com.example.jetdrivedemoapi.domain.models.drive

data class DriveItem(
    val id: String,
    val name: String,
    val mimeType: String, // Helps distinguish between file and folder
    val size: Long? = null, // Optional, only used for files
    val createdTime: String? = null, // Optional, can be used for both files and folders
    val modifiedTime: String? = null // Optional, can be used for both files and folders
) {
    // This property allows checking if the item is a folder or a file
    val isFolder: Boolean
        get() = mimeType == "application/vnd.google-apps.folder"
    
    val isFile: Boolean
        get() = mimeType != "application/vnd.google-apps.folder"
}