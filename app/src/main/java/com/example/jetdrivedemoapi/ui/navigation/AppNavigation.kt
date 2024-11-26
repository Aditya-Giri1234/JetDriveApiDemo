package com.example.jetdrivedemoapi.ui.navigation

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.jetdrivedemoapi.common.custom_class.getCustomNavTypeMap
import com.example.jetdrivedemoapi.domain.models.navigation.MyGoogleSignInAccount
import com.example.jetdrivedemoapi.ui.screens.HomeScreen
import com.example.jetdrivedemoapi.ui.screens.TrashFileScreen
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun  AppNavigation(){
    val navController  = rememberNavController()



    NavHost(navController = navController , startDestination = AppNavigationScreens.HomeScreen){
        composable<AppNavigationScreens.HomeScreen> {
            HomeScreen(navController , hiltViewModel())
        }
        composable<AppNavigationScreens.TrashFileScreen>(
//            typeMap = mapOf(getCustomNavTypeMap<MyGoogleSignInAccount>()),
        ) {
//                val account = it.toRoute<AppNavigationScreens.TrashFileScreen>().data.account
            TrashFileScreen(navController , hiltViewModel())
        }
    }
}