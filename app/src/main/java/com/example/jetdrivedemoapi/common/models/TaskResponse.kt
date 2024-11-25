package com.example.jetdrivedemoapi.common.models

sealed class TaskResponse<T>(open val data : T?=null , open val message :String?=null) {
    class Initial<T>() : TaskResponse<T>()
    class Loading<T>() : TaskResponse<T>()
    data class Success<T>(override val data: T) : TaskResponse<T>(data){
        var isMessageSeen : Boolean = false
    }
    data class Error<T>(override val message: String) : TaskResponse<T>(message = message){
        var isMessageSeen : Boolean = false
    }

}