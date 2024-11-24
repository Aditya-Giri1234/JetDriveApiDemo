package com.example.jetdrivedemoapi.common.utils.helper

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jetdrivedemoapi.domain.models.drive.FileDetails
import com.example.jetdrivedemoapi.ui.theme.ArchivePink
import com.example.jetdrivedemoapi.ui.theme.AudioIndigo
import com.example.jetdrivedemoapi.ui.theme.DocumentPurple
import com.example.jetdrivedemoapi.ui.theme.FolderGreen
import com.example.jetdrivedemoapi.ui.theme.ImageRed
import com.example.jetdrivedemoapi.ui.theme.PdfOrange
import com.example.jetdrivedemoapi.ui.theme.PresentationAmber
import com.example.jetdrivedemoapi.ui.theme.SpreadsheetGreen
import com.example.jetdrivedemoapi.ui.theme.UnknownGrey
import com.example.jetdrivedemoapi.ui.theme.VideoBlue
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration

object Helper {
    private var toast : Toast?=null

    fun customToast(context : Context, message : String, duration : Int = Toast.LENGTH_LONG){
        toast?.cancel()
        toast = Toast.makeText(context , message , duration)
        toast?.show()
    }

    fun getFileIcon(fileName: String?, mimeType: String?): ImageVector {
        val extension = fileName?.substringAfterLast('.', "")?.lowercase()

        return when (extension) {
            null, "" -> {
                if (mimeType == "application/vnd.google-apps.folder") Icons.Default.Folder
                else Icons.Default.InsertDriveFile
            }
            // Image files
            "png", "jpg", "jpeg", "gif", "bmp" -> Icons.Default.Image

            // Video files
            "mp4", "mkv", "avi", "mov", "webm" -> Icons.Default.Movie

            // PDF files
            "pdf" -> Icons.Default.PictureAsPdf

            // Document files
            "doc", "docx" -> Icons.Default.Description

            // Spreadsheet files
            "xls", "xlsx" -> Icons.Default.GridOn

            // Presentation files
            "ppt", "pptx" -> Icons.Default.Slideshow

            // Audio files
            "mp3", "wav", "ogg" -> Icons.Default.MusicNote

            // Archive files
            "zip", "rar", "7z" -> Icons.Default.Archive

            // Default fallback for unknown types
            else -> Icons.AutoMirrored.Filled.InsertDriveFile
        }
    }

    fun getFileTint(fileName: String?, mimeType: String?): Color {
        val extension = fileName?.substringAfterLast('.', "")?.lowercase()

        return when (extension) {
            null, "" -> {
                if (mimeType == "application/vnd.google-apps.folder") FolderGreen
                else UnknownGrey
            }
            "png", "jpg", "jpeg", "gif", "bmp" -> ImageRed
            "mp4", "mkv", "avi", "mov", "webm" -> VideoBlue
            "pdf" -> PdfOrange
            "doc", "docx" -> DocumentPurple
            "xls", "xlsx" -> SpreadsheetGreen
            "ppt", "pptx" -> PresentationAmber
            "mp3", "wav", "ogg" -> AudioIndigo
            "zip", "rar", "7z" -> ArchivePink
            else -> UnknownGrey
        }
    }

    fun Context.getFileDetailsFromUri(uri: Uri): FileDetails {
        var fileName: String =""
        var fileSize: Long? = null


        // Retrieve file name and size
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) ?: ""
                fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            }
        }

        // Retrieve MIME type
        val mimeType = contentResolver.getType(uri)

        return FileDetails(
            name = fileName,
            mimeType = mimeType,
            size = fileSize ,
            uri = uri
        )
    }

    fun Context.copyUriToTempFile(uri: Uri, fileName: String): File {
        val tempFile = File(cacheDir, fileName)
        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return tempFile
    }

}