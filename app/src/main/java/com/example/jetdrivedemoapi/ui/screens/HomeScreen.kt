package com.example.jetdrivedemoapi.ui.screens

import android.app.Activity.RESULT_OK
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.utils.extension.AddVerticalSpace
import com.example.jetdrivedemoapi.common.utils.extension.safeClick
import com.example.jetdrivedemoapi.common.utils.helper.Constants
import com.example.jetdrivedemoapi.common.utils.helper.Helper
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.example.jetdrivedemoapi.ui.components.common.MyTopBar
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.navigation.AppNavigationScreens
import com.example.jetdrivedemoapi.ui.theme.FolderGreen
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.example.jetdrivedemoapi.ui.widgets.home.FileActionDialog
import com.example.jetdrivedemoapi.ui.widgets.home.FileOperation
import com.example.jetdrivedemoapi.ui.widgets.home.FolderOrFileCreateDialog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {

    val context = LocalContext.current
    val singInResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                homeViewModel.singInGoogleDrive(result)
            } else {
                Helper.customToast(context, "Some Error Occurred ")
            }
        }

    val isFolderCreationOn = remember {
        mutableStateOf(false)
    }
    val signInStatus = homeViewModel.signInDrive.collectAsStateWithLifecycle(TaskResponse.Initial())
    val fileOperation = homeViewModel.fileOperation.collectAsStateWithLifecycle(TaskResponse.Initial())
    val myDriveFiles =
        homeViewModel.myGoogleDriveFiles.collectAsStateWithLifecycle(TaskResponse.Initial())
    val account = remember(signInStatus.value) {
        mutableStateOf(signInStatus.value.data)
    }
    val rootFolder = remember {
        DriveItem(id = Constants.ROOT, name = "MyDrive" , mimeType = Constants.GOOGLE_FOLDER)
    }
    val parentFolderId = remember{
        mutableStateOf<DriveItem>(rootFolder)
    }


    val folderPathArray = remember {
        mutableStateOf(arrayOf<DriveItem>(rootFolder))
    }


    LaunchedEffect(Unit) {
        homeViewModel.checkLoginStatus(context)
    }

    LaunchedEffect(signInStatus.value , parentFolderId.value) {
        if (account.value != null) {
            homeViewModel.getDriveFilesAndFolders(account.value!!, context , parentFolderId.value.id)
        }
    }

    BackHandler {
        when{
            folderPathArray.value.size > 1 ->{
                folderPathArray.value = folderPathArray.value.copyOfRange(0,folderPathArray.value.size-1)
                parentFolderId.value = folderPathArray.value[folderPathArray.value.size - 1]
            }
            else->{
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            MyTopBar(
                screenName = AppNavigationScreens.HomeScreen,
                signInStatus = signInStatus.value.data,
                navController = navController,
                onSync = {
                    homeViewModel.getDriveFilesAndFolders(account.value!!,context , parentFolderId.value.id)
                }
            ) {
                if (signInStatus.value.data == null) {
                    singInResult.launch(homeViewModel.returnSingInClient(context).signInIntent)
                } else {
                    homeViewModel.singOutGoogleDrive(context)
                }
            }
        } ,
        floatingActionButton = {
            if(account.value!=null){
                FloatingActionButton(onClick = {
                    isFolderCreationOn.value = true
                } , shape = CircleShape , containerColor = FolderGreen) {
                    IconWithoutDesc(Icons.Filled.Add , tint = Color.White)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp) , contentAlignment = Alignment.Center){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FolderView(folderPathArray)
                    TopAppBarSetUp(navController, signInStatus, homeViewModel)
                    MainContent(navController, homeViewModel, myDriveFiles , account){item ->
//                    homeViewModel.getDriveFilesAndFolders(account.value!!,context,item.id)
                        folderPathArray.value+=item
                        parentFolderId.value=item
                    }
                    if(isFolderCreationOn.value){
                        FolderOrFileCreateDialog(account = account.value!!,homeViewModel , parentFolderId, onDismiss = {
                            isFolderCreationOn.value = false
                        } )
                    }

                }
                FileOperation(fileOperation)
            }

        }

    }
}



@Composable
fun ColumnScope.FolderView(folderPathArray: State<Array<DriveItem>>) {
    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).background(Color.Red)) {
        Column {
            Text(Helper.getFolderPathFromArray(folderPathArray.value) , overflow = TextOverflow.Ellipsis , maxLines = 1 , style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black.copy(
                    alpha = .8f,
                ) ,
                fontWeight = FontWeight.SemiBold
            ))
            AddVerticalSpace(5)
            HorizontalDivider(thickness = 1.dp , color = Color.Black.copy(alpha = .1f))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.MainContent(
    navController: NavController,
    homeViewModel: HomeViewModel,
    myDriveFiles: State<TaskResponse<List<DriveItem>>>,
    account: State<GoogleSignInAccount?>,
    onFolderClick: (item: DriveItem) -> Unit
) {
    val context  = LocalContext.current
    // The dialog state
    val openDialog = remember { mutableStateOf(false) }
    val fileId = remember {
        mutableStateOf("")
    }
    when (val response = myDriveFiles.value) {
        is TaskResponse.Initial -> {
            Text(
                "Data is not available !", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        is TaskResponse.Success -> {
            if (response.data.isEmpty()) {
                Text(
                    "No Files or Folders found.", style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            } else {
                Box {
                    if(openDialog.value){
                        FileActionDialog(homeViewModel,account
                            .value!!,fileId.value , openDialog)
                    }
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        state = rememberLazyGridState(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        reverseLayout = false,
                        userScrollEnabled = true,
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    ) {
                        items(response.data , key = {it.id}) { item ->
                            Column(
                                modifier = Modifier.fillMaxSize().combinedClickable(
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    } ,
                                    indication = null,
                                    onLongClick = {
                                       openDialog.value = true
                                        fileId.value = item.id
                                    } ,
                                    onClick = {
                                        safeClick {
                                            if(item.isFolder){
                                                onFolderClick(item)
                                            }else{
                                                Helper.openDriveFile(account = account.value!! , context , item.id)
                                            }
                                        }
                                    },
                                ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconWithoutDesc(
                                    Helper.getFileIcon(item.name,item.mimeType),
                                    tint = Helper.getFileTint(item.name,item.mimeType).copy(alpha = .7f),
                                    Modifier.size(80.dp)
                                )
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
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
                }
            }
        }

        is TaskResponse.Loading -> {
            Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                Column(verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){
                    CircularProgressIndicator(color = Color.Black)
                    AddVerticalSpace(10)
                    Text(
                        "Files And Folder Loading ...", style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        is TaskResponse.Error -> {
            Text(
                response.message, style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun ColumnScope.TopAppBarSetUp(
    navController: NavController,
    signInStatus: State<TaskResponse<GoogleSignInAccount?>>,
    homeViewModel: HomeViewModel
) {

    when (val result = signInStatus.value) {
        is TaskResponse.Initial -> {
            InitialView()
        }

        is TaskResponse.Success -> {
           // Nothing
        }

        is TaskResponse.Loading -> {
            LoadingView()
        }

        is TaskResponse.Error -> {
            ErrorView(result.message)
        }
    }

}

@Composable
fun ColumnScope.InitialView() {
    Text(
        "User Validation Ongoing , please wait ...",
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    )
}




@Composable
fun ColumnScope.LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.Black)
    }
}

@Composable
fun ColumnScope.ErrorView(message: String) {
    Text(
        message, style = MaterialTheme.typography.displayLarge.copy(
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    )
}
