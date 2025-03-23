package com.example.khatmusalawattime.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun component()
{
    Row {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_popup_reminder),
            contentDescription = "Уведомления",
            tint = Color(0xFF8B7E66),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun prev(){
    component()
}