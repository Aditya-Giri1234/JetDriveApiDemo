package com.example.jetdrivedemoapi.ui.widgets.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.jetdrivedemoapi.common.models.TaskResponse
import com.example.jetdrivedemoapi.common.utils.extension.AddVerticalSpace
import com.example.jetdrivedemoapi.common.utils.helper.Helper

@Composable
fun BoxScope.FileOperation(fileOperation: State<TaskResponse<String>>) {
    val context = LocalContext.current
    when(val response = fileOperation.value){
        is TaskResponse.Initial -> {}
        is TaskResponse.Success -> {
            Helper.customToast(context, response.data)
        }
        is TaskResponse.Loading -> {
            Column(verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){
                CircularProgressIndicator(color = Color.Black)
                AddVerticalSpace(10)
                Text(
                    "Please Wait ...", style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        is TaskResponse.Error -> {
            Helper.customToast(context, response.message)
        }
    }
}