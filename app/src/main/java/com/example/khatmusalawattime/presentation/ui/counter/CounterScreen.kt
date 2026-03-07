package com.example.khatmusalawattime.presentation.ui.counter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.R

@Composable
fun CounterScreen(
    viewModel: CounterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val textColor = Color(0xFFFFFFFF) // Цвет текста
    val buttonBgColor = Color(0xFFEBE3C9) // Цвет кнопок
    val count by viewModel.count.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.counterback),
                contentScale = ContentScale.Crop
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(371.dp))

            Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 100.sp,
                    fontFamily = FontFamily(Font(R.font.russo_one_regular)),
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )

            ClickableImageButton(
                onClick = { viewModel.increment() },
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = -10.dp,)
                ,
                imageNormalRes = R.drawable.clickbtn,      // картинка для обычного состояния
                imagePressedRes = R.drawable.clickpressedbtn, // картинка для нажатого состояния
                contentDescription = "Увеличить счетчик"
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatefulImageButton(
                    onClick = onNavigateBack,
                    imageNormalRes = R.drawable.countbackbtn,
                    imagePressedRes = R.drawable.countbackpressedbtn,
                    contentDescription = "Назад"
                )

                StatefulImageButton(
                    onClick = { viewModel.decrement() },
                    imageNormalRes = R.drawable.countresetbtn,
                    imagePressedRes = R.drawable.countresetpressedbtn,
                    contentDescription = "Уменьшить"
                )

                StatefulImageButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.offset(x = 5.dp),
                    imageNormalRes = R.drawable.countzerobtn,
                    imagePressedRes = R.drawable.countzeropressedbtn,
                    contentDescription = "Сбросить"
                )
            }
        }
    }
}

@Composable
fun StatefulImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageNormalRes: Int,
    imagePressedRes: Int,
    contentDescription: String?
) {
    var pressed by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = if (pressed) imagePressedRes else imageNormalRes),
        contentDescription = contentDescription,
        modifier = modifier
            .size(90.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
    )
}
@Composable
fun ClickableImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageNormalRes: Int,
    imagePressedRes: Int,
    contentDescription: String?,
) {
    var pressed by remember { mutableStateOf(false) }

    androidx.compose.foundation.Image(
        painter = painterResource(id = if (pressed) imagePressedRes else imageNormalRes),
        contentDescription = contentDescription,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
    )
}


@Composable
fun NavigationButton(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit,
    bgColor: Color,
    iconTint: Color,
    withBorder: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(
                width = if (withBorder) 2.dp else 0.dp,
                color = iconTint.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (icon) {
            is androidx.compose.ui.graphics.vector.ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            is Int -> {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            is String -> {
                Text(
                    text = icon,
                    color = iconTint,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
