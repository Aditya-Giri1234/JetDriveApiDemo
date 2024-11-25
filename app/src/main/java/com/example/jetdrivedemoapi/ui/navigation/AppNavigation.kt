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
import com.example.jetdrivedemoapi.ui.screens.HomeScreen

@Composable
fun  AppNavigation(){
    val navController  = rememberNavController()



    NavHost(navController = navController , startDestination = AppNavigationScreens.HomeScreen){
        composable<AppNavigationScreens.HomeScreen> {
            HomeScreen(navController , hiltViewModel())
        }
    }
}