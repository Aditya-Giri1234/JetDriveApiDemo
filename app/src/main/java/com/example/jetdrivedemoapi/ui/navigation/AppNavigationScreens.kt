package com.example.jetdrivedemoapi.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppNavigationScreens{
    @Serializable
    data object HomeScreen : AppNavigationScreens
}