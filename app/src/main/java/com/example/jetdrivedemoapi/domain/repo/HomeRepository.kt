package com.example.jetdrivedemoapi.domain.repo

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.example.jetdrivedemoapi.data.google_drive.GoogleDriveManager
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class HomeRepository @Inject constructor() {

    suspend fun signInGoogleDrive(result: ActivityResult) = GoogleDriveManager.signInGoogleDrive(result)
    suspend fun signOutGoogleDrive(context : Context) = GoogleDriveManager.signOutGoogleDrive(context)
    fun returnSingInClient(context : Context) = GoogleDriveManager.returnSingInClient(context)

    fun checkLoginStatus(context : Context) : GoogleSignInAccount? = GoogleDriveManager.checkLoginStatus(context)

    suspend fun getDriveFilesAndFolders(account: GoogleSignInAccount, context: Context, folderId : String ?=null) = GoogleDriveManager.getDriveFilesAndFolders(account,context , folderId)

    suspend fun createFolderInRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        folderName: String,
        parentFolderId : String?=null
    ) = GoogleDriveManager.createFolderInRootFolder(account, context, folderName , parentFolderId)

    suspend fun uploadFileToRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        fileUri: Uri,
        parentFolderId : String?=null
    ) = GoogleDriveManager.uploadFileToRootFolder(account, context, fileUri,parentFolderId)
}