package com.example.khatmusalawattime.presentation.ui.counter

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.CounterMode
import com.example.khatmusalawattime.domain.model.ZikrItem
import java.util.UUID

data class EditableZikrItem(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var targetCount: String = "33",
    var color: Long = availableColors.random()
) {
    companion object {
        val availableColors = listOf(
            0xFFE57373L, // красный
            0xFFFF8A65L, // оранжевый
            0xFFFFB74DL, // жёлтый
            0xFF81C784L, // зелёный
            0xFF64B5F6L, // голубой
            0xFF9575CDL, // фиолетовый
            0xFFBA68C8L, // розовый
            0xFF4DB6ACL  // бирюзовый
        )
    }

    fun toZikrItem(): ZikrItem? {
        val count = targetCount.toIntOrNull() ?: return null
        if (name.isBlank() || count <= 0) return null
        return ZikrItem(
            id = id,
            name = name.trim(),
            targetCount = count,
            color = color
        )
    }
}

@Composable
fun CustomCounterSetupScreen(
    onNavigateBack: () -> Unit,
    onStartCounter: (CounterMode.Custom) -> Unit,
    initialItems: List<ZikrItem> = emptyList()
) {
    val russoOneFamily = FontFamily(Font(R.font.russo_one_regular))
    val cardBgColor = Color(0xFFFAF7F2)
    val textColor = Color(0xFF4A3D2A)
    val accentColor = Color(0xFF7C5F23)

    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF4DBAD),
            Color(0xFFFDFBCC),
            Color(0xFFF4DBAD)
        )
    )

    val items = remember {
        mutableStateListOf<EditableZikrItem>().apply {
            if (initialItems.isNotEmpty()) {
                addAll(initialItems.map { item ->
                    EditableZikrItem(
                        id = item.id,
                        name = item.name,
                        targetCount = item.targetCount.toString(),
                        color = item.color
                    )
                })
            } else {
                add(EditableZikrItem(name = "", targetCount = "33"))
            }
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.newback),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(cardBgColor.copy(alpha = 0.9f))
                        .border(2.dp, borderGradient, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Свой набор",
                    fontFamily = russoOneFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                // Start button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF81C784))
                        .clickable {
                            val zikrItems = items.mapNotNull { it.toZikrItem() }
                            if (zikrItems.isEmpty()) {
                                errorMessage = "Добавьте хотя бы один зикр"
                            } else {
                                onStartCounter(CounterMode.Custom(zikrItems))
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Начать",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFE57373),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Total count info
            val totalCount = items.sumOf { it.targetCount.toIntOrNull() ?: 0 }
            Text(
                text = "Всего: $totalCount",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Items list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.id }
                ) { index, item ->
                    ZikrItemCard(
                        item = item,
                        index = index + 1,
                        borderGradient = borderGradient,
                        cardBgColor = cardBgColor,
                        textColor = textColor,
                        onNameChange = { newName ->
                            items[index] = items[index].copy(name = newName)
                            errorMessage = null
                        },
                        onCountChange = { newCount ->
                            items[index] = items[index].copy(targetCount = newCount)
                            errorMessage = null
                        },
                        onColorChange = { newColor ->
                            items[index] = items[index].copy(color = newColor)
                        },
                        onDelete = {
                            if (items.size > 1) {
                                items.removeAt(index)
                            }
                        },
                        canDelete = items.size > 1
                    )
                }

                // Add button
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, borderGradient, RoundedCornerShape(16.dp))
                            .background(cardBgColor.copy(alpha = 0.7f))
                            .clickable {
                                items.add(EditableZikrItem())
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Добавить зикр",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = accentColor
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun ZikrItemCard(
    item: EditableZikrItem,
    index: Int,
    borderGradient: Brush,
    cardBgColor: Color,
    textColor: Color,
    onNameChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onColorChange: (Long) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderGradient, RoundedCornerShape(16.dp))
            .background(cardBgColor.copy(alpha = 0.95f))
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(item.color))
                        .clickable { showColorPicker = !showColorPicker },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Название",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                    BasicTextField(
                        value = item.name,
                        onValueChange = onNameChange,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        ),
                        cursorBrush = SolidColor(textColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        decorationBox = { innerTextField ->
                            Box {
                                if (item.name.isEmpty()) {
                                    Text(
                                        text = "Введите название...",
                                        fontSize = 16.sp,
                                        color = textColor.copy(alpha = 0.4f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Count input
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Кол-во",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                    BasicTextField(
                        value = item.targetCount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                onCountChange(newValue)
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(textColor),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(60.dp)
                            .background(
                                textColor.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete button
                if (canDelete) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE57373).copy(alpha = 0.15f))
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Color picker
            if (showColorPicker) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EditableZikrItem.availableColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .then(
                                    if (color == item.color) {
                                        Modifier.border(2.dp, textColor, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    onColorChange(color)
                                    showColorPicker = false
                                }
                        )
                    }
                }
            }
        }
    }
}
