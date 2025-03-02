package com.example.khatmusalawattime.presentation.ui.alarm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AlarmScreen() {
    Text(
        text = "Экран будильника",
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen()
}