package com.example.jetdrivedemoapi.common.models

import com.example.jetdrivedemoapi.common.utils.helper.Constants

data class ListenerEmissionType<T,R>(
    val emitChangeType: Constants.ListenerEmitType,
    val isEmissionForList : Boolean = false,
    val isFirstTimeEmission : Boolean = true,
    val responseList: List<T>? = null,
    val singleResponse: R? = null,
)