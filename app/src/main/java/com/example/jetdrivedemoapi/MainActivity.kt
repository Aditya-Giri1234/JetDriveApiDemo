package com.example.jetdrivedemoapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.jetdrivedemoapi.ui.navigation.AppNavigation
import com.example.jetdrivedemoapi.ui.theme.JetDriveDemoApiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetDriveApiDemo()
        }
    }
}

@Composable
private fun JetDriveApiDemo() {
    JetDriveDemoApiTheme {
        AppNavigation()
    }
}

