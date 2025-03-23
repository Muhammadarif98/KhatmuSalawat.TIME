package com.example.khatmusalawattime.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Точные цвета из требования
    val trackEnabledColor = Color(0xFFBEA787)  // Коричневатый (когда включен)
    val trackDisabledColor = Color(0xFFFDF1E1) // Светло-бежевый (когда выключен) 
    val thumbColor = Color(0xFFFFFAE0)         // Светло-бежевый для индикатора
    
    // Стандартные размеры для переключателя
    val trackWidth = 52.dp    // Стандартная ширина трека
    val trackHeight = 32.dp   // Стандартная высота трека
    val thumbSize = 28.dp     // Стандартный размер индикатора
    
    // Анимация для перемещения индикатора
    val thumbPosition by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - 2.dp else 2.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (checked) trackEnabledColor else trackDisabledColor
            )
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = true)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCheckedChange(!checked)
            }
    ) {
        // Внутренняя тень для трека
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
        )
        
        // Индикатор (круг)
        Surface(
            modifier = Modifier
                .size(thumbSize)
                .align(Alignment.CenterStart)
                .offset(x = thumbPosition)
                .shadow(
                    elevation = 2.dp, 
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                ),
            shape = CircleShape,
            color = thumbColor
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSwitchPreview() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Выключенное состояние
        CustomSwitch(
            checked = false,
            onCheckedChange = {}
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Включенное состояние
        CustomSwitch(
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InteractiveSwitchPreview() {
    var checked by remember { mutableStateOf(false) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        CustomSwitch(
            checked = checked,
            onCheckedChange = { checked = it }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(text = if (checked) "Включено" else "Выключено")
    }
} 