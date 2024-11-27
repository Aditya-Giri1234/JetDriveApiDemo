package com.example.jetdrivedemoapi.ui.navigation

import com.example.jetdrivedemoapi.domain.models.navigation.MyGoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.util.Data
import kotlinx.serialization.Serializable

sealed interface AppNavigationScreens{
    @Serializable
    data object HomeScreen : AppNavigationScreens

    @Serializable
    data object TrashFileScreen : AppNavigationScreens
}