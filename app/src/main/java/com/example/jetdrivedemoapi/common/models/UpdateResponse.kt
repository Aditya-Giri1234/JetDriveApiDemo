package com.example.jetdrivedemoapi.common.models

data class UpdateResponse<T>(
    val isSuccess:Boolean=false,
    val data : T ?=null,
    val errorMessage:String?=null
)
