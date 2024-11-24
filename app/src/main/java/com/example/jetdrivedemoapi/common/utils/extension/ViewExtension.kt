package com.example.jetdrivedemoapi.common.utils.extension

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AddHorizontalSpace(space : Int) = Spacer(Modifier.padding(horizontal = space.dp))

@Composable
fun AddVerticalSpace(space : Int) = Spacer(Modifier.padding(vertical = space.dp))