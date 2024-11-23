package com.example.jetdrivedemoapi.data.google_drive

import android.content.Context
import androidx.activity.result.ActivityResult
import com.example.jetdrivedemoapi.common.models.UpdateResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.SignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object GoogleDriveManager {

    private fun getGoogleSignOption(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA), Scope(DriveScopes.DRIVE_FILE))
            .requestEmail().build()
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

    suspend fun signOutGoogleDrive(context: Context) = callbackFlow<UpdateResponse<GoogleSignInAccount?>> {
        returnSingInClient(context).signOut().addOnSuccessListener {
            trySend(UpdateResponse(true , data = null))
        }.addOnFailureListener {
            trySend(UpdateResponse(false, errorMessage = it.message))
        }
        awaitClose {
            close()
        }
    }

     fun checkLoginStatus(context : Context) : GoogleSignInAccount? {
        val requireScopes = HashSet<Scope>(2)
        requireScopes.add(Scope(DriveScopes.DRIVE_METADATA))
        requireScopes.add(Scope(DriveScopes.DRIVE_FILE))
        val signInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val containScope = signInAccount?.grantedScopes?.containsAll(requireScopes)

        return if(signInAccount!=null && containScope == true){
            signInAccount
        }else{
            null
        }
    }


}