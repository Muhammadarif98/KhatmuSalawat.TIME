package com.example.khatmusalawattime.presentation.ui.counter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.R

/**
 * Плашка счётчика с цифрами в секциях
 * Каждая цифра в своей клетке, максимум 4 цифры (0-9999)
 * Цифры выравниваются справа: 3 цифры — в ячейках 2,3,4
 */
@Composable
fun CounterDisplayPlate(
    count: Int,
    modifier: Modifier = Modifier,
    plateHeight: Dp = 90.dp,
    textColor: Color = Color.White
) {
    // Выравниваем цифры справа: пробелы слева
    val digits = count.toString().takeLast(4).padStart(4, ' ')

    val plateColor = Color(0xFFE8D5B8)
    val borderColor = Color(0xFFF5EDE0)
    val innerBorderColor = Color(0xFFD4C4A8)
    val dividerColor = Color(0xFFD4C4A8)
    val shadowColor = Color(0xFF8B7355)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(plateHeight + 20.dp)
            .drawBehind {
                drawCounterPlate(
                    plateColor = plateColor,
                    borderColor = borderColor,
                    innerBorderColor = innerBorderColor,
                    dividerColor = dividerColor,
                    shadowColor = shadowColor
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            digits.forEach { digit ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (digit == ' ') "" else digit.toString(),
                        fontSize = 75.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.russo_one_regular)),
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Базовая плашка без контента (для кастомного использования)
 */
@Composable
fun CounterStandPlate(
    modifier: Modifier = Modifier,
    plateHeight: Dp = 80.dp,
    content: @Composable () -> Unit = {}
) {
    val plateColor = Color(0xFFE8D5B8)
    val borderColor = Color(0xFFF5EDE0)
    val innerBorderColor = Color(0xFFD4C4A8)
    val dividerColor = Color(0xFFD4C4A8)
    val shadowColor = Color(0xFF8B7355)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(plateHeight + 20.dp)
            .drawBehind {
                drawCounterPlate(
                    plateColor = plateColor,
                    borderColor = borderColor,
                    innerBorderColor = innerBorderColor,
                    dividerColor = dividerColor,
                    shadowColor = shadowColor
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private fun DrawScope.drawCounterPlate(
    plateColor: Color,
    borderColor: Color,
    innerBorderColor: Color,
    dividerColor: Color,
    shadowColor: Color
) {
    val cornerRadius = 24.dp.toPx()
    val shadowOffset = 12.dp.toPx()
    val borderWidth = 4.dp.toPx()

    val plateWidth = size.width - shadowOffset
    val plateHeight = size.height - shadowOffset

    // 1. Тень (несколько слоёв для мягкости)
    for (i in 3 downTo 0) {
        val alpha = 0.1f - (i * 0.02f)
        val offset = shadowOffset - (i * 3.dp.toPx())
        drawRoundRect(
            color = shadowColor.copy(alpha = alpha.coerceAtLeast(0.02f)),
            topLeft = Offset(offset, offset),
            size = Size(plateWidth, plateHeight),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }

    // 2. Внешняя светлая рамка
    drawRoundRect(
        color = borderColor,
        topLeft = Offset.Zero,
        size = Size(plateWidth, plateHeight),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // 3. Основная плашка
    val innerPadding = borderWidth
    drawRoundRect(
        color = plateColor,
        topLeft = Offset(innerPadding, innerPadding),
        size = Size(plateWidth - innerPadding * 2, plateHeight - innerPadding * 2),
        cornerRadius = CornerRadius(cornerRadius - innerPadding / 2)
    )

    // 4. Внутренняя тёмная рамка
    drawRoundRect(
        color = innerBorderColor,
        topLeft = Offset(innerPadding, innerPadding),
        size = Size(plateWidth - innerPadding * 2, plateHeight - innerPadding * 2),
        cornerRadius = CornerRadius(cornerRadius - innerPadding / 2),
        style = Stroke(width = 2.dp.toPx())
    )

    // 5. Вертикальные разделители (4 секции)
    val sectionWidth = (plateWidth - innerPadding * 2) / 4
    val lineStartY = innerPadding + 8.dp.toPx()
    val lineEndY = plateHeight - innerPadding - 8.dp.toPx()

    for (i in 1..3) {
        val x = innerPadding + sectionWidth * i
        drawLine(
            color = dividerColor,
            start = Offset(x, lineStartY),
            end = Offset(x, lineEndY),
            strokeWidth = 1.5.dp.toPx()
        )
    }

    // 6. Верхний блик
    val highlightBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.Transparent
        ),
        startY = innerPadding,
        endY = innerPadding + 20.dp.toPx()
    )
    drawRoundRect(
        brush = highlightBrush,
        topLeft = Offset(innerPadding + 4.dp.toPx(), innerPadding + 2.dp.toPx()),
        size = Size(plateWidth - innerPadding * 2 - 8.dp.toPx(), 16.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
}

@Preview(showBackground = true)
@Composable
private fun CounterDisplayPlate4DigitsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8956C))
            .padding(vertical = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CounterDisplayPlate(count = 1234)
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterDisplayPlate2DigitsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8956C))
            .padding(vertical = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // 2 цифры — в ячейках 3, 4 (справа)
        CounterDisplayPlate(count = 42)
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterDisplayPlate1DigitPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8956C))
            .padding(vertical = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1 цифра — в 4-й ячейке (справа)
        CounterDisplayPlate(count = 7)
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterDisplayPlate3DigitsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8956C))
            .padding(vertical = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // 3 цифры — в ячейках 2, 3, 4 (справа)
        CounterDisplayPlate(count = 123)
    }
}
