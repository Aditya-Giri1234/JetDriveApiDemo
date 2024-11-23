package com.example.jetdrivedemoapi.ui.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.utils.helper.Helper
import com.example.jetdrivedemoapi.ui.viewmodels.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun HomeScreen(navController: NavController = rememberNavController() , homeViewModel: HomeViewModel =  hiltViewModel<HomeViewModel>()){

    val context = LocalContext.current
    val singInResult  = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK && result.data != null){
            homeViewModel.singInGoogleDrive(result)
        }else{
            Helper.customToast(context , "Some Error Occurred ")
        }
    }

    val signInStatus = homeViewModel.signInDrive.collectAsStateWithLifecycle(TaskResponse.Initial())


    LaunchedEffect(Unit) {
        homeViewModel.checkLoginStatus(context)
    }

    Scaffold { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column (modifier = Modifier.fillMaxSize().padding(20.dp) , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){
                MainContent(navController, signInStatus , homeViewModel , singInResult)
            }
        }
    }
}

@Composable
fun ColumnScope.MainContent(navController: NavController , signInStatus : State<TaskResponse<GoogleSignInAccount?>>  , homeViewModel: HomeViewModel , signInResult : ManagedActivityResultLauncher<Intent , ActivityResult>){

    when(val result = signInStatus.value){
        is TaskResponse.Initial -> {
            InitialView(result.data , homeViewModel , signInResult)
        }
        is TaskResponse.Success -> {
            MainView(result.data ,homeViewModel , signInResult)
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
fun ColumnScope.InitialView(data: GoogleSignInAccount?, homeViewModel: HomeViewModel, signInResult: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    Text("User Validation Ongoing , please wait ..." , style = MaterialTheme.typography.displayLarge.copy(
        color = Color.Black ,
        fontWeight = FontWeight.Bold
    ))
}


@Composable
fun ColumnScope.MainView(
    data: GoogleSignInAccount?,
    homeViewModel: HomeViewModel,
    signInResult: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val context = LocalContext.current
    val (message , isSingIn) = if(data ==null){
        "Sign In" to false
    }else{
        "Sign Out" to true
    }
    TextButton(onClick = {
        if(isSingIn){
            homeViewModel.singOutGoogleDrive(context)
        }else{
            signInResult.launch(homeViewModel.returnSingInClient(context).signInIntent)
        }
    }) {
        Text(message , style = MaterialTheme.typography.displayLarge.copy(
            color = Color.Black ,
            fontWeight = FontWeight.Bold
        ))
    }
}

@Composable
fun ColumnScope.LoadingView() {
    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
        CircularProgressIndicator(color = Color.Black)
    }
}

@Composable
fun ColumnScope.ErrorView(message: String) {
        Text(message , style = MaterialTheme.typography.displayLarge.copy(
            color = Color.Black ,
            fontWeight = FontWeight.Bold
        ))
}
