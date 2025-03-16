package com.example.khatmusalawattime.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.R

/**
 * Экран-заглушка со всеми иконками приложения.
 * Используйте этот экран для просмотра доступных иконок.
 * Чтобы использовать иконку в своем коде, используйте:
 * painterResource(id = R.drawable.имя_иконки)
 */
@Composable
fun IconsStubScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EFD9))
            .padding(16.dp)
    ) {
        Text(
            text = "Доступные иконки",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Карандаш/Заметки
        IconRow(
            name = "Иконка карандаша (ic_old_edit.xml)",
            resId = R.drawable.ic_old_edit
        )
        
        IconRow(
            name = "Иконка карандаша 2 (ic_notes.xml)",
            resId = R.drawable.ic_notes
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Дом
        IconRow(
            name = "Иконка дома уютная (ic_home_cozy.xml)",
            resId = R.drawable.ic_home_cozy
        )
        
        IconRow(
            name = "Иконка дома стандартная (ic_home.xml)",
            resId = R.drawable.ic_home
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Колокольчик и настройки
        IconRow(
            name = "Иконка колокольчика (ic_bell.xml)",
            resId = R.drawable.ic_bell
        )
        
        IconRow(
            name = "Иконка настроек (ic_settings.xml)",
            resId = R.drawable.ic_settings
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Таймер иконки
        IconRow(
            name = "Иконка воспроизведения (ic_play.xml)",
            resId = R.drawable.ic_play
        )
        
        IconRow(
            name = "Иконка паузы (ic_pause.xml)",
            resId = R.drawable.ic_pause
        )
        
        IconRow(
            name = "Иконка сброса (ic_reset.xml)",
            resId = R.drawable.ic_reset
        )
        
        IconRow(
            name = "Иконка назад (ic_back.xml)",
            resId = R.drawable.ic_back
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Лампа
        IconRow(
            name = "Иконка лампы (ic_lamp.xml)",
            resId = R.drawable.ic_lamp
        )
    }
}

@Composable
fun IconRow(name: String, resId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = name,
            tint = Color(0xFF8B7E66),
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = name,
            fontSize = 16.sp,
            color = Color(0xFF8B7E66)
        )
    }
} 