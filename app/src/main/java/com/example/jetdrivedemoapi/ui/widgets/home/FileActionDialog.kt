package com.example.jetdrivedemoapi.ui.widgets.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun FileActionDialog(
    homeViewModel: HomeViewModel,
    account: GoogleSignInAccount,
    fileId: String ,
    openDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val options = listOf("Move File to Trash", "Delete File")
    var selectedOption by remember { mutableStateOf(0) } // Default: Move File to Trash


    val choiceMode = remember {
        mutableStateOf(true)
    }

    when {
        choiceMode.value -> {
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text(text = "Choose an action") },
                    text = {
                        Column {
                            options.forEachIndexed { index, option ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedOption == index,
                                        onClick = { selectedOption = index }
                                    )
                                    Text(text = option)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Proceed to confirmation dialog
                                choiceMode.value = false

                            }
                        ) {
                            Text("Next")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        else -> {
            if (openDialog.value) {
                ShowConfirmationDialog(
                    selectedOption,
                    homeViewModel,
                    context,
                    account,
                    fileId,
                    openDialog
                )
            }
        }
    }


}

@Composable
private fun ShowConfirmationDialog(
    selectedOption: Int,
    homeViewModel: HomeViewModel,
    context: Context,
    account: GoogleSignInAccount,
    fileId: String,
    openDialog: MutableState<Boolean>
) {
    val actionMessage = when (selectedOption) {
        0 -> "You want to move this file to trash?"
        1 -> "You want to delete this file?"
        else -> "Are you sure?"
    }

    // Confirmation Dialog
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Confirm Action") },
        text = { Text(actionMessage) },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                    if (selectedOption == 0) {
                        homeViewModel.moveToTrash(account, context, fileId)
                    } else {
                        homeViewModel.deleteFileOrFolder(account, context, fileId)
                    }
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


