package com.example.jetdrivedemoapi.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.jetdrivedemoapi.common.utils.extension.AddVerticalSpace
import com.example.jetdrivedemoapi.common.utils.extension.safeClick
import com.example.jetdrivedemoapi.common.utils.helper.Helper
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.example.jetdrivedemoapi.ui.components.common.MyTopBar
import com.example.jetdrivedemoapi.ui.components.common.ShowConfirmationDialog
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.navigation.AppNavigationScreens
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.example.jetdrivedemoapi.ui.widgets.home.FileOperation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun TrashFileScreen(
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val fileOperation =
        homeViewModel.fileOperation.collectAsStateWithLifecycle(TaskResponse.Initial())
    val signInStatus = homeViewModel.signInDrive.collectAsStateWithLifecycle(TaskResponse.Initial())
    val account = remember(signInStatus.value) {
        mutableStateOf(signInStatus.value.data)
    }
    val myDriveTrashFiles =
        homeViewModel.myGoogleDriveTrashFiles.collectAsStateWithLifecycle(TaskResponse.Initial())

    LaunchedEffect(Unit) {
        homeViewModel.checkLoginStatus(context)
    }

    LaunchedEffect(signInStatus.value) {
        if (account.value != null) {
            homeViewModel.getDriveTrashFiles(account.value!!, context)
        }
    }

    Scaffold(
        topBar = {
            MyTopBar(
                title = "Trash File",
                screenName = AppNavigationScreens.TrashFileScreen,
                navController = navController,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBarSetUp(navController, signInStatus, homeViewModel)
                MainContent(navController, homeViewModel, account, myDriveTrashFiles)
            }

            FileOperation(fileOperation)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.MainContent(
    navController: NavController,
    homeViewModel: HomeViewModel,
    account: State<GoogleSignInAccount?>,
    myDriveFiles: State<TaskResponse<List<DriveItem>>>
) {
    val context = LocalContext.current
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
                    if (openDialog.value) {
                        ShowConfirmationDialog(
                            homeViewModel,
                            context,
                            account.value!!,
                            fileId.value,
                            openDialog
                        )
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
                        items(response.data, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .safeClick {
                                        fileId.value = item.id
                                        openDialog.value = true
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconWithoutDesc(
                                    Helper.getFileIcon(item.name, item.mimeType),
                                    tint = Helper.getFileTint(item.name, item.mimeType)
                                        .copy(alpha = .7f),
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.Black)
                    AddVerticalSpace(10)
                    Text(
                        "Files And Folder Loading ...",
                        style = MaterialTheme.typography.bodyLarge.copy(
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