package com.example.jetdrivedemoapi.ui.components.common

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun ShowConfirmationDialog(
    homeViewModel: HomeViewModel,
    context: Context,
    account: GoogleSignInAccount,
    fileId: String,
    openDialog: MutableState<Boolean>
) {


    // Confirmation Dialog
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Confirm Action") },
        text = { Text("Are you sure recover this file or folder ?") },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                    homeViewModel.recoverFromTrash(account, context, fileId)
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("No")
            }
        }
    )
}