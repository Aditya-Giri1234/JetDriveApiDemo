package com.example.jetdrivedemoapi.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.sharp.Login
import androidx.compose.material.icons.automirrored.sharp.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Login
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.jetdrivedemoapi.common.custom_class.recomposeHighlighter
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.navigation.AppNavigationScreens
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title : String = "JetDriveApiDemo",
    screenName : AppNavigationScreens,
    signInStatus : GoogleSignInAccount? ,
    navController: NavController,
    onActionButtonClick : () -> Unit
) {

    TopAppBar(
        title = {
            Text(title , style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ))
        },
        navigationIcon = {
            NavigationIcon(screenName,title, navController)
        } ,
        actions = {
            ActionIcon(screenName,signInStatus,navController,onActionButtonClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray
        )
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationIcon(screenName: AppNavigationScreens , title: String , navController: NavController ){
    when(screenName){
        AppNavigationScreens.HomeScreen -> {

        }
        else ->{
            IconWithoutDesc(Icons.AutoMirrored.Filled.ArrowBack , tint = Color.Black , modifier = Modifier.clickable {
                navController.navigateUp()
            })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.ActionIcon(screenName: AppNavigationScreens ,signInStatus : GoogleSignInAccount?, navController: NavController  , onActionButtonClick: () -> Unit){
    val isSignIn = remember(signInStatus) {
        signInStatus!=null
    }
    when(screenName){
        AppNavigationScreens.HomeScreen -> {
            if(isSignIn){
                IconWithoutDesc(Icons.AutoMirrored.Sharp.Logout, tint = Color.Black , modifier = Modifier.clickable {
                    onActionButtonClick()
                }.recomposeHighlighter())
            }else{
                IconWithoutDesc(Icons.AutoMirrored.Filled.Login , tint = Color.Black , modifier = Modifier.clickable {
                    onActionButtonClick()
                }.recomposeHighlighter())
            }

        }
        else ->{
        }
    }

}

