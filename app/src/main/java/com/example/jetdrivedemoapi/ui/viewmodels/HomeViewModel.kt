package com.example.jetdrivedemoapi.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetdrivedemoapi.common.models.ListenerEmissionType
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.utils.helper.Constants
import com.example.jetdrivedemoapi.common.utils.manager.SoftwareManager
import com.example.jetdrivedemoapi.domain.models.drive.DriveItem
import com.example.jetdrivedemoapi.domain.repo.HomeRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepository,
    private val app: Application
) : AndroidViewModel(app) {

    private val _signInDriveStatus = MutableSharedFlow<TaskResponse<GoogleSignInAccount?>>(
        1, 64, BufferOverflow.DROP_OLDEST
    )

    val signInDrive get() = _signInDriveStatus.asSharedFlow()

    private val _myGoogleDriveFiles = MutableSharedFlow<TaskResponse<List<DriveItem>>>(
        1, 64, BufferOverflow.DROP_OLDEST
    )

    val myGoogleDriveFiles get() = _myGoogleDriveFiles.asSharedFlow()

    private val _folderCreate = MutableSharedFlow<TaskResponse<String>>(
        0, 64, BufferOverflow.DROP_OLDEST
    )

    val folderCreate get() = _folderCreate.asSharedFlow()


    fun singInGoogleDrive(result: ActivityResult) = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())
        if (SoftwareManager.isNetworkAvailable(app)) {
            val response = repo.signInGoogleDrive(result).first()
            if (response.isSuccess) {
                _signInDriveStatus.tryEmit(TaskResponse.Success(response.data))
            } else {
                _signInDriveStatus.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        } else {
            _signInDriveStatus.tryEmit(TaskResponse.Error("Internet Not Available !"))
        }
    }

    fun singOutGoogleDrive(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())
        if (SoftwareManager.isNetworkAvailable(app)) {
            val response = repo.signOutGoogleDrive(context).first()
            if (response.isSuccess) {
                _myGoogleDriveFiles.tryEmit(TaskResponse.Initial())
                _signInDriveStatus.tryEmit(TaskResponse.Success(response.data))
            } else {
                _signInDriveStatus.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        } else {
            _signInDriveStatus.tryEmit(TaskResponse.Error("Internet Not Available !"))
        }
    }

    fun returnSingInClient(context: Context) = repo.returnSingInClient(context)

    fun checkLoginStatus(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        _signInDriveStatus.tryEmit(TaskResponse.Loading())

        val account = repo.checkLoginStatus(context)

        _signInDriveStatus.tryEmit(TaskResponse.Success(account))
    }

    fun getDriveFilesAndFolders(account: GoogleSignInAccount, context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            _myGoogleDriveFiles.tryEmit(TaskResponse.Loading())
            if (SoftwareManager.isNetworkAvailable(app)) {
                repo.getDriveFilesAndFolders(account, context).onEach {
                    handleDriveEvent(it)
                }.launchIn(this)
            } else {
                _myGoogleDriveFiles.tryEmit(TaskResponse.Error("Internet Not Available !"))
            }
        }


    private fun handleDriveEvent(response: ListenerEmissionType<DriveItem, DriveItem>) {
        val myDriveFile =
            myGoogleDriveFiles.replayCache[0].data?.toMutableList() ?: mutableListOf<DriveItem>()

        when (response.emitChangeType) {
            Constants.ListenerEmitType.Added -> {
                if (response.isFirstTimeEmission) {
                    myDriveFile.clear()
                }

                if (response.isEmissionForList) {
                    response.responseList?.let { myDriveFile.addAll(it) }
                } else {
                    response.singleResponse?.let { myDriveFile.add(it) }
                }
                myDriveFile.sortByDescending { it.createdTime }
            }

            Constants.ListenerEmitType.Removed -> {
                // Don't do anything
            }

            Constants.ListenerEmitType.Modify -> {
                // don't do anything
            }
        }
        _myGoogleDriveFiles.tryEmit(TaskResponse.Success(myDriveFile))
    }

    fun createFolderInRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        folderName: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _folderCreate.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.createFolderInRootFolder(account, context, folderName).first()
            if(response.isSuccess){
                handleFolderCreationInRoot(response.data)
            }else{
                _folderCreate.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _folderCreate.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }

    private fun handleFolderCreationInRoot(response: DriveItem?) {
        val files = myGoogleDriveFiles.replayCache[0].data?.toMutableList() ?: mutableListOf()

        if(response ==null){
            _folderCreate.tryEmit(TaskResponse.Error("Something went wrong !"))
        }else{
            _folderCreate.tryEmit(TaskResponse.Success(""))
            files.add(response)
            files.sortByDescending { it.createdTime }
            _myGoogleDriveFiles.tryEmit(TaskResponse.Success(files))
        }
    }
}