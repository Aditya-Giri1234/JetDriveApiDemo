package com.example.jetdrivedemoapi.ui.screens

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.jetdrivedemoapi.common.utils.helper.Helper
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.example.jetdrivedemoapi.ui.components.common.MyTopBar
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.navigation.AppNavigationScreens
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.example.jetdrivedemoapi.ui.widgets.home.FolderOrFileCreateDialog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlin.math.sign

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
    val myDriveFiles =
        homeViewModel.myGoogleDriveFiles.collectAsStateWithLifecycle(TaskResponse.Initial())
    val account = remember(signInStatus.value) {
        mutableStateOf(signInStatus.value.data)
    }


    LaunchedEffect(Unit) {
        homeViewModel.checkLoginStatus(context)
    }

    LaunchedEffect(signInStatus.value) {
        if (account.value != null) {
            homeViewModel.getDriveFilesAndFolders(account.value!!, context)
        }
    }

    Scaffold(
        topBar = {
            MyTopBar(
                screenName = AppNavigationScreens.HomeScreen,
                signInStatus = signInStatus.value.data,
                navController = navController,
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
                } , shape = CircleShape , containerColor = Color.Magenta) {
                    IconWithoutDesc(Icons.Filled.Add , tint = Color.White)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBarSetUp(navController, signInStatus, homeViewModel)
                MainContent(navController, homeViewModel, myDriveFiles)
                if(isFolderCreationOn.value){
                    FolderOrFileCreateDialog(account = account.value!!,homeViewModel, onDismiss = {
                        isFolderCreationOn.value = false
                    } )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.MainContent(
    navController: NavController,
    homeViewModel: HomeViewModel,
    myDriveFiles: State<TaskResponse<List<DriveItem>>>
) {
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
                    items(response.data) { item ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
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

        is TaskResponse.Loading -> {
            CircularProgressIndicator(color = Color.Black)
            Text(
                "Files And Folder Loading ...", style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
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
fun ColumnScope.TopAppBarSetUp(
    navController: NavController,
    signInStatus: State<TaskResponse<GoogleSignInAccount?>>,
    homeViewModel: HomeViewModel
) {

    when (val result = signInStatus.value) {
        is TaskResponse.Initial -> {
            InitialView()
        }

        is TaskResponse.Success -> {
            MainView(result.data, homeViewModel)
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
        style = MaterialTheme.typography.displayLarge.copy(
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    )
}


@Composable
fun ColumnScope.MainView(
    data: GoogleSignInAccount?,
    homeViewModel: HomeViewModel
) {

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
