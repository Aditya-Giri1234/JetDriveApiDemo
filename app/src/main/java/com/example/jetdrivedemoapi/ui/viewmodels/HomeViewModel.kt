package com.example.jetdrivedemoapi.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.models.UpdateResponse
import com.example.jetdrivedemoapi.common.utils.manager.SoftwareManager
import com.example.jetdrivedemoapi.data.google_drive.GoogleDriveManager
import com.example.jetdrivedemoapi.domain.repo.HomeRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo : HomeRepository , private val app: Application) : AndroidViewModel(app) {

    private val _signInDriveStatus = MutableSharedFlow<TaskResponse<GoogleSignInAccount?>>(
        1 , 64, BufferOverflow.DROP_OLDEST
    )

    val signInDrive get() = _signInDriveStatus.asSharedFlow()


    fun singInGoogleDrive(result: ActivityResult)  = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())
        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.signInGoogleDrive(result).first()
            if(response.isSuccess){
                _signInDriveStatus.tryEmit(TaskResponse.Success(response.data))
            }else{
                _signInDriveStatus.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _signInDriveStatus.tryEmit(TaskResponse.Error("Internet Not Available !"))
        }
    }

    fun singOutGoogleDrive(context : Context)  = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())
        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.signOutGoogleDrive(context).first()
            if(response.isSuccess){
                _signInDriveStatus.tryEmit(TaskResponse.Success(response.data))
            }else{
                _signInDriveStatus.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _signInDriveStatus.tryEmit(TaskResponse.Error("Internet Not Available !"))
        }
    }

    fun returnSingInClient(context : Context) = repo.returnSingInClient(context)

    fun checkLoginStatus(context : Context) = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())

        val account = repo.checkLoginStatus(context)

        _signInDriveStatus.tryEmit(TaskResponse.Success(account))
    }

}