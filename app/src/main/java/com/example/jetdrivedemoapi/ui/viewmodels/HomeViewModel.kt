package com.example.jetdrivedemoapi.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
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

    private val _fileOrFolderCreate = MutableSharedFlow<TaskResponse<String>>(
        0, 64, BufferOverflow.DROP_OLDEST
    )

    val fileOrFolderCreate get() = _fileOrFolderCreate.asSharedFlow()

    private val _fileOperation = MutableSharedFlow<TaskResponse<String>>(
        0, 64, BufferOverflow.DROP_OLDEST
    )

    val fileOperation get() = _fileOperation.asSharedFlow()

    private val _myGoogleDriveTrashFiles = MutableSharedFlow<TaskResponse<List<DriveItem>>>(
        1, 64, BufferOverflow.DROP_OLDEST
    )

    val myGoogleDriveTrashFiles get() = _myGoogleDriveTrashFiles.asSharedFlow()



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

    fun getDriveFilesAndFolders(account: GoogleSignInAccount, context: Context, folderId : String ?=null) =
        viewModelScope.launch(Dispatchers.IO) {
            _myGoogleDriveFiles.tryEmit(TaskResponse.Loading())
            if (SoftwareManager.isNetworkAvailable(app)) {
                repo.getDriveFilesAndFolders(account, context , folderId).onEach {
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
        folderName: String,
        parentFolderId : String?=null
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fileOrFolderCreate.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.createFolderInRootFolder(account, context, folderName , parentFolderId).first()
            if(response.isSuccess){
                handleFolderCreationInRoot(response.data , "Folder Create Successfully !")
            }else{
                _fileOrFolderCreate.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _fileOrFolderCreate.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }

    fun uploadFileToRootFolder(
        account: GoogleSignInAccount,
        context: Context,
        fileUri: Uri,
        parentFolderId : String?=null
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fileOrFolderCreate.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.uploadFileToRootFolder(account, context, fileUri , parentFolderId).first()
            if(response.isSuccess){
                handleFolderCreationInRoot(response.data , "File Upload Successfully !")
            }else{
                _fileOrFolderCreate.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _fileOrFolderCreate.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }


    private fun handleFolderCreationInRoot(response: DriveItem? , message : String) {
        val files = myGoogleDriveFiles.replayCache[0].data?.toMutableList() ?: mutableListOf()

        if(response ==null){
            _fileOrFolderCreate.tryEmit(TaskResponse.Error("Something went wrong !"))
        }else{
            _fileOrFolderCreate.tryEmit(TaskResponse.Success(message))
            files.add(response)
            files.sortByDescending { it.createdTime }
            _myGoogleDriveFiles.tryEmit(TaskResponse.Success(files))
        }
    }

     fun moveToTrash(
        account: GoogleSignInAccount,
        context: Context,
        fileId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fileOperation.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.moveToTrash(account, context, fileId).first()
            if(response.isSuccess){
                handleMoveToTrashOperation(fileId)
            }else{
                _fileOperation.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _fileOperation.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }

    private fun handleMoveToTrashOperation(fileId: String) {
        val myDriveFiles = myGoogleDriveFiles.replayCache[0].data?.toMutableList() ?: mutableListOf()

        myDriveFiles.removeIf { it.id==fileId }

        _myGoogleDriveFiles.tryEmit(TaskResponse.Success(myDriveFiles))
        _fileOperation.tryEmit(TaskResponse.Success("Move To Trash Successfully !"))
    }

     fun recoverFromTrash(
        account: GoogleSignInAccount,
        context: Context,
        fileId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fileOperation.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.recoverFromTrash(account, context, fileId).first()
            if(response.isSuccess){
                handleRecoverTrashOperation(fileId)
            }else{
                _fileOperation.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _fileOperation.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }

    private fun handleRecoverTrashOperation(fileId: String) {
        val myDriveTrashFiles = myGoogleDriveTrashFiles.replayCache[0].data?.toMutableList() ?: mutableListOf()

        myDriveTrashFiles.removeIf { it.id==fileId }

        _myGoogleDriveTrashFiles.tryEmit(TaskResponse.Success(myDriveTrashFiles))
        _fileOperation.tryEmit(TaskResponse.Success("Recover From Trash Successfully !"))
    }

     fun deleteFileOrFolder(
        account: GoogleSignInAccount,
        context: Context,
        fileId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fileOperation.tryEmit(TaskResponse.Loading())

        if(SoftwareManager.isNetworkAvailable(app)){
            val response = repo.deleteFileOrFolder(account, context, fileId).first()
            if(response.isSuccess){
                handleDeleteFolderOrFileOperation(fileId)
            }else{
                _fileOperation.tryEmit(TaskResponse.Error(response.errorMessage.toString()))
            }
        }else{
            _fileOperation.tryEmit(TaskResponse.Error("No Internet Available !"))
        }
    }

    private fun handleDeleteFolderOrFileOperation(fileId: String) {
        val myDriveFiles = myGoogleDriveFiles.replayCache[0].data?.toMutableList() ?: mutableListOf()

        myDriveFiles.removeIf { it.id==fileId }

        _myGoogleDriveFiles.tryEmit(TaskResponse.Success(myDriveFiles))
        _fileOperation.tryEmit(TaskResponse.Success("Deleted Successfully !"))
    }

    fun getDriveTrashFiles(account: GoogleSignInAccount, context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            _myGoogleDriveTrashFiles.tryEmit(TaskResponse.Loading())
            if (SoftwareManager.isNetworkAvailable(app)) {
                repo.getAllFilesFromTrash(account, context).onEach {
                    handleDriveTrashFileEvent(it)
                }.launchIn(this)
            } else {
                _myGoogleDriveTrashFiles.tryEmit(TaskResponse.Error("Internet Not Available !"))
            }
        }


    private fun handleDriveTrashFileEvent(response: ListenerEmissionType<DriveItem, DriveItem>) {
        val myDriveTrashFile =
            myGoogleDriveTrashFiles.replayCache[0].data?.toMutableList() ?: mutableListOf<DriveItem>()

        when (response.emitChangeType) {
            Constants.ListenerEmitType.Added -> {
                if (response.isFirstTimeEmission) {
                    myDriveTrashFile.clear()
                }

                if (response.isEmissionForList) {
                    response.responseList?.let { myDriveTrashFile.addAll(it) }
                } else {
                    response.singleResponse?.let { myDriveTrashFile.add(it) }
                }
                myDriveTrashFile.sortByDescending { it.createdTime }
            }

            Constants.ListenerEmitType.Removed -> {
                // Don't do anything
            }

            Constants.ListenerEmitType.Modify -> {
                // don't do anything
            }
        }
        _myGoogleDriveTrashFiles.tryEmit(TaskResponse.Success(myDriveTrashFile))
    }

}