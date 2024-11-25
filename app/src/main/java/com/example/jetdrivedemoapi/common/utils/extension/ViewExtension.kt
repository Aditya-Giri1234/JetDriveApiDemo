package com.example.jetdrivedemoapi.common.utils.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.jetdrivedemoapi.common.utils.helper.Constants


@Composable
fun AddHorizontalSpace(space : Int) = Spacer(Modifier.padding(horizontal = space.dp))

@Composable
fun AddVerticalSpace(space : Int) = Spacer(Modifier.padding(vertical = space.dp))

//Safe Click
var lastClickTimeInMillis = 0L

fun Modifier.safeClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role
    ) {
        val currentTimeInMillis = System.currentTimeMillis()
        if (currentTimeInMillis - lastClickTimeInMillis > Constants.SAFE_CLICK_DELAY) {
            lastClickTimeInMillis = currentTimeInMillis
            onClick()
        }
    }
}
