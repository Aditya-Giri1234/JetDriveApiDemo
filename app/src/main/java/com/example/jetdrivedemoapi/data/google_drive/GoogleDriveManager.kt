package com.example.jetdrivedemoapi.data.google_drive

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.example.jetdrivedemoapi.R
import com.example.jetdrivedemoapi.common.models.ListenerEmissionType
import com.example.jetdrivedemoapi.common.models.UpdateResponse
import com.example.jetdrivedemoapi.common.utils.helper.Constants
import com.example.jetdrivedemoapi.common.utils.helper.Helper.copyUriToTempFile
import com.example.jetdrivedemoapi.common.utils.helper.Helper.getFileDetailsFromUri
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.io.IOException

object GoogleDriveManager {

    private fun getGoogleSignOption(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopesFromSet(getDriveScopes())
            .requestEmail()
            .build()
    }

    private fun GoogleSignInOptions.Builder.requestScopesFromSet(scopes: Set<Scope>): GoogleSignInOptions.Builder {
        if (scopes.isNotEmpty()) {
            val scopeList = scopes.toList()
            this.requestScopes(scopeList.first(), *scopeList.drop(1).toTypedArray())
        }
        return this
    }


    private fun getDriveScopes(): Set<Scope> {
        return hashSetOf(
            Scope(DriveScopes.DRIVE),                    // Full access to the user's Google Drive
            Scope(DriveScopes.DRIVE_APPDATA),            // Access to app-specific data
            Scope(DriveScopes.DRIVE_FILE)                // Full access to files
        )
    }


    fun returnSingInClient(context: Context): GoogleSignInClient {
        return GoogleSignIn.getClient(context, getGoogleSignOption())
    }

    suspend fun signInGoogleDrive(result: ActivityResult) =
        callbackFlow<UpdateResponse<GoogleSignInAccount>> {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).addOnSuccessListener {
                trySend(UpdateResponse(true, it))
            }.addOnFailureListener {
                trySend(UpdateResponse(false, errorMessage = it.message))
            }

            awaitClose {
                close()
            }
        }

    suspend fun signOutGoogleDrive(context: Context) =
        callbackFlow<UpdateResponse<GoogleSignInAccount?>> {
            returnSingInClient(context).signOut().addOnSuccessListener {
                trySend(UpdateResponse(true, data = null))
            }.addOnFailureListener {
                trySend(UpdateResponse(false, errorMessage = it.message))
            }
            awaitClose {
                close()
            }
        }

    fun checkLoginStatus(context: Context): GoogleSignInAccount? {
        val signInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val containScope = signInAccount?.grantedScopes?.containsAll(getDriveScopes())

        return if (signInAccount != null && containScope == true) {
            signInAccount
        } else {
            null
        }
    }

    //region:: Create Folders And Files

    suspend fun createFolderInRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        folderName: String
    ) = callbackFlow<UpdateResponse<DriveItem?>> {
        val driveService = createDriveService(account, context)
        try {
            // Define a folder
            val gFolder = com.google.api.services.drive.model.File().apply {
                name = folderName // Folder name
                mimeType = Constants.GOOGLE_FOLDER // MIME type for folders
                parents = listOf("root") // Set parent as root to create in My Drive
            }

            // Create the folder in Drive
            val createdFolder = driveService.files()
                .create(gFolder)
                .setFields("id, name, mimeType, createdTime, modifiedTime")
                .execute()

            // Return DriveItem representing the created folder
            val item = DriveItem(
                id = createdFolder.id,
                name = createdFolder.name,
                mimeType = createdFolder.mimeType,
                size = 0, // Folders have no size
                createdTime = createdFolder.createdTime.toString(),
                modifiedTime = createdFolder.modifiedTime.toString()
            )

            trySend(UpdateResponse(true, item))
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(UpdateResponse(false, null, e.message.toString()))
        }


        awaitClose {
            close()
        }
    }

    suspend fun uploadFileToRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        fileUri: Uri
    ) = callbackFlow<UpdateResponse<DriveItem?>> {
        val driveService = createDriveService(account, context)

        try {
            // Retrieve file details from Uri
            val fileDetails = context.getFileDetailsFromUri(fileUri)

            // Copy the file to a temporary location
            val tempFile = context.copyUriToTempFile(fileUri, fileDetails.name)

            // Create metadata for the file
            val gFile = File().apply {
                name = fileDetails.name
                mimeType = fileDetails.mimeType
                parents = listOf("root") // Upload to My Drive (root folder)
            }

            // Prepare file content
            val fileContent = FileContent(fileDetails.mimeType, tempFile)

            // Upload file to Google Drive
            val uploadedFile = driveService.files()
                .create(gFile, fileContent)
                .setFields("id, name, mimeType, size, createdTime, modifiedTime")
                .execute()

            // Map the uploaded file details to DriveItem
            val item = DriveItem(
                id = uploadedFile.id,
                name = uploadedFile.name,
                mimeType = uploadedFile.mimeType,
                size = uploadedFile.size.toLong(),
                createdTime = uploadedFile.createdTime.toString(),
                modifiedTime = uploadedFile.modifiedTime.toString()
            )

            trySend(UpdateResponse(true, item))
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(UpdateResponse(false, null, e.message.toString()))
        }

        awaitClose {
            close()
        }
    }

    //endregion

//region :: Get List of File and folder from drive api

    // Function to create Drive service using the signed-in GoogleSignInAccount
    private fun createDriveService(account: GoogleSignInAccount, context: Context): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, ArrayList(getDriveScopes().map { it.scopeUri })
        ).apply {
            selectedAccount = account.account // Set the Google account
        }

        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(context.getString(R.string.app_name))
            .build()
    }

    // Function to get all files and folders from Google Drive using suspend and callbackFlow
    suspend fun getDriveFilesAndFolders(account: GoogleSignInAccount, context: Context) =
        callbackFlow<ListenerEmissionType<DriveItem, DriveItem>> {
            val driveService = createDriveService(account, context)

            // Make request to list files and folders
            val request = driveService.files().list()
//                .setQ("trashed = false")  // It will give whole files and folder in drive which one share or open or etc.
                .setQ("'root' in parents and trashed = false")  // It will give files from My Drive Folder
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, mimeType, size, createdTime, modifiedTime)")
                .setPageSize(100)

            try {
                val fileList: FileList = request.execute()
                var isFirstTime = true
                do {
                    val driveItems = fileList.files.map { file ->
                        DriveItem(
                            id = file.id,
                            name = file.name,
                            mimeType = file.mimeType,
                            size = file.size.toLong(),
                            createdTime = file.createdTime.toString(),
                            modifiedTime = file.modifiedTime.toString()
                        )
                    }
                    trySend(
                        ListenerEmissionType(
                            Constants.ListenerEmitType.Added,
                            isEmissionForList = true,
                            isFirstTime,
                            responseList = driveItems
                        )
                    )

                    isFirstTime = false
                    request.pageToken = fileList.nextPageToken
                } while (fileList.nextPageToken != null)
            } catch (e: IOException) {
                close(e) // Close the flow in case of error
            }

            awaitClose {
                close()
            }
        }

    //endregion

}