package com.example.jetdrivedemoapi.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.sharp.Logout
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.jetdrivedemoapi.common.custom_class.recomposeHighlighter
import com.example.jetdrivedemoapi.common.utils.extension.AddHorizontalSpace
import com.example.jetdrivedemoapi.domain.models.navigation.MyGoogleSignInAccount
import com.example.jetdrivedemoapi.ui.components.common.wrapper.IconWithoutDesc
import com.example.jetdrivedemoapi.ui.navigation.AppNavigationScreens
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String = "JetDriveApiDemo",
    screenName: AppNavigationScreens,
    signInStatus: GoogleSignInAccount?=null,
    navController: NavController,
    onSync :()-> Unit = {},
    onActionButtonClick: () -> Unit = {}
) {

    TopAppBar(
        title = {
            Text(
                title, style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            )
        },
        navigationIcon = {
            NavigationIcon(screenName, title, navController)
        },
        actions = {
                ActionIcon(screenName, signInStatus, navController, onSync ,onActionButtonClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray
        )
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationIcon(
    screenName: AppNavigationScreens,
    title: String,
    navController: NavController
) {
    when (screenName) {
        AppNavigationScreens.HomeScreen -> {

        }

        else -> {
            IconWithoutDesc(
                Icons.AutoMirrored.Filled.ArrowBack,
                tint = Color.Black,
                modifier = Modifier.clickable {
                    navController.navigateUp()
                })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.ActionIcon(
    screenName: AppNavigationScreens,
    signInStatus: GoogleSignInAccount?,
    navController: NavController,
    onSync: () -> Unit,
    onActionButtonClick: () -> Unit
) {
    val isSignIn = remember(signInStatus) {
        signInStatus != null
    }
    when (screenName) {
        AppNavigationScreens.HomeScreen -> {
            if (isSignIn) {
                IconWithoutDesc(
                    Icons.Filled.Sync,
                    tint = Color.Black,
                    modifier = Modifier
                        .clickable {
                            onSync()
                        }
                        .recomposeHighlighter()
                )
                AddHorizontalSpace(10)
                IconWithoutDesc(
                    Icons.Filled.RestoreFromTrash,
                    tint = Color.Black,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(AppNavigationScreens.TrashFileScreen)
                        }
                        .recomposeHighlighter()
                )
                AddHorizontalSpace(10)
                IconWithoutDesc(
                    Icons.AutoMirrored.Sharp.Logout,
                    tint = Color.Black,
                    modifier = Modifier
                        .clickable {
                            onActionButtonClick()
                        }
                        .recomposeHighlighter()
                )
            } else {
                IconWithoutDesc(
                    Icons.AutoMirrored.Filled.Login,
                    tint = Color.Black,
                    modifier = Modifier
                        .clickable {
                            onActionButtonClick()
                        }
                        .recomposeHighlighter()
                )
            }

        }

        else -> {
        }
    }

}

