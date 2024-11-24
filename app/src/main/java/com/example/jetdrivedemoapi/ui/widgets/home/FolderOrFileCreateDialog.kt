package com.example.jetdrivedemoapi.ui.widgets.home

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.utils.extension.AddHorizontalSpace
import com.example.jetdrivedemoapi.common.utils.extension.AddVerticalSpace
import com.example.jetdrivedemoapi.common.utils.helper.Helper
import com.example.jetdrivedemoapi.common.utils.helper.Helper.getFileDetailsFromUri
import com.example.jetdrivedemoapi.domain.models.drive.FileDetails
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.theme.FolderGreen
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import androidx.activity.compose.rememberLauncherForActivityResult as rememberLauncherForActivityResult1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderOrFileCreateDialog(
    account: GoogleSignInAccount ,
    homeViewModel: HomeViewModel ,
    onDismiss: ()->Unit
){

    val context = LocalContext.current
    val createFolder =  homeViewModel.fileOrFolderCreate.collectAsStateWithLifecycle(TaskResponse.Initial())
    val isFileSelected = remember {
        mutableStateOf(true)
    }
    val folderName = remember {
        mutableStateOf("")
    }
    val isButtonEnabled = remember {
        mutableStateOf(true)
    }
    var selectedFile by remember { mutableStateOf<FileDetails?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult1(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if(uri!=null){
                selectedFile = context.getFileDetailsFromUri(uri)
            }else{

            }
        }
    )


    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(
        dismissOnClickOutside = true, dismissOnBackPress = true
    )) {
        Card(modifier = Modifier.fillMaxWidth().wrapContentHeight() , elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) , shape = RoundedCornerShape(10.dp) , colors = CardDefaults.cardColors(
            contentColor = Color.White
        )) {
            Box (modifier = Modifier.wrapContentSize() , contentAlignment = Alignment.Center){
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp , vertical = 10.dp) , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Start) {
                        RadioButton(selected = isFileSelected.value, onClick = {
                            isFileSelected.value = true
                        } ,
                            colors =RadioButtonDefaults.colors(
                                selectedColor = FolderGreen.copy(alpha = .8f) ,
                                unselectedColor = Color.Gray
                            ))
                        AddHorizontalSpace(10)
                        Text("Upload File " , style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black ,
                            fontWeight = FontWeight.Medium
                        ))

                    }
                    if (isFileSelected.value){
                        if(selectedFile==null){
                            Surface(modifier = Modifier.padding(horizontal = 20.dp , vertical = 10.dp).fillMaxWidth().background(Color.Gray.copy(alpha = .5f)).clickable {
                                filePickerLauncher.launch(arrayOf("*/*"))
                            } , border = BorderStroke(1.dp, brush = Brush.linearGradient(
                                colors = listOf(Color.Gray.copy(alpha = .5f), Color.Gray.copy(alpha = .5f))
                            ),)) {
                                IconWithoutDesc(Icons.Filled.Upload , tint = Color.Black , modifier = Modifier.padding(10.dp).wrapContentSize())
                            }
                        }
                        else{
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconWithoutDesc(
                                    Helper.getFileIcon(selectedFile!!.name,selectedFile!!.mimeType),
                                    tint = Helper.getFileTint(selectedFile!!.name,selectedFile!!.mimeType).copy(alpha = .7f),
                                    Modifier.size(80.dp)
                                )
                                Text(
                                    selectedFile!!.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold ,
                                        color = Color.Black
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    }


                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Start) {
                        RadioButton(selected = !isFileSelected.value, onClick = {
                            selectedFile = null
                            isFileSelected.value = false
                        } ,
                            colors =RadioButtonDefaults.colors(
                                selectedColor = FolderGreen.copy(alpha = .8f) ,
                                unselectedColor = Color.Gray
                            ))
                        AddHorizontalSpace(10)
                        Text("Create Folder " , style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black ,
                            fontWeight = FontWeight.Medium
                        ))

                    }
                    if (!isFileSelected.value){
                        OutlinedTextField(value = folderName.value , onValueChange = {
                            folderName.value = it
                        }, label = {
                            Text("Please Enter Folder Name ...")
                        } , keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done , keyboardType = KeyboardType.Text) , maxLines = 1)
                    }

                    AddVerticalSpace(20)
                    if(!isFileSelected.value || isFileSelected.value&&selectedFile!=null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(15.dp),
                                enabled = isButtonEnabled.value
                            ) {
                                Text(
                                    "Cancel", style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.Red.copy(
                                            alpha = .8f
                                        )
                                    )
                                )
                            }
                            AddHorizontalSpace(10)
                            OutlinedButton(onClick = {
                                if(isFileSelected.value){
                                    homeViewModel.uploadFileToRootFolder(account,context,selectedFile!!.uri)
                                }else{
                                    if(folderName.value.isNotBlank()){
                                        homeViewModel.createFolderInRootFolder(
                                            account,
                                            context,
                                            folderName.value
                                        )
                                        isButtonEnabled.value = false
                                    }else{
                                        Helper.customToast(context,"Pleaser Enter Folder Name !")
                                    }
                                }

                            }, shape = RoundedCornerShape(15.dp), enabled = isButtonEnabled.value) {
                                val message = if(isFileSelected.value){
                                    "Upload"
                                }else{
                                    "Create"
                                }
                                Text(
                                    message, style = MaterialTheme.typography.titleLarge.copy(
                                        color = FolderGreen.copy(
                                            alpha = .8f
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
                HandleFolderCreationPart(createFolder , isButtonEnabled , onDismiss)
            }
        }
    }

}

@Composable
private fun BoxScope.HandleFolderCreationPart(createFolder : State<TaskResponse<String>>  , isButtonEnable : MutableState<Boolean>, onDismiss: () -> Unit){
    val context = LocalContext.current
    when(val response = createFolder.value){
        is TaskResponse.Initial -> {}
        is TaskResponse.Success -> {
            Helper.customToast(context, response.data)
            onDismiss()
        }
        is TaskResponse.Loading -> {
            CircularProgressIndicator(color = Color.Black , modifier = Modifier.align(Alignment.Center))
        }
        is TaskResponse.Error -> {
            isButtonEnable.value = true
            Helper.customToast(context, response.message)
        }
    }
}