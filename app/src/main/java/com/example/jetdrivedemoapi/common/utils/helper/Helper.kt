package com.example.jetdrivedemoapi.common.utils.helper

import android.content.Context
import android.widget.Toast
import kotlin.time.Duration

object Helper {
    private var toast : Toast?=null

    fun customToast(context : Context, message : String, duration : Int = Toast.LENGTH_LONG){
        toast?.cancel()
        toast = Toast.makeText(context , message , duration)
        toast?.show()
    }
}