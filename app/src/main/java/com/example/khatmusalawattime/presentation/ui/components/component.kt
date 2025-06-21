package com.example.khatmusalawattime.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
@Composable
fun AnimateContent(shortText: String, longText: String) {
    //  val shortText = stringResource(id = R.string.short_text)
    //  val longText = stringResource(id = R.string.long_text)
    var short by remember { mutableStateOf(true) }
    val st1 = Color(0xFFF4DBAD)
    val st2 = Color(0xFFFDFBCC)
    val st3 = Color(0xFFF9EBBD)
    val st4 = Color(0xFFF4DBAD)

    Box(
        modifier = Modifier
            .background(Color(0xFFF1E4D1), RoundedCornerShape(50.dp))
            .border(
                7.dp, brush = Brush.verticalGradient(
                    colors = listOf(
                        st1,
                        st2,
                        st3,
                        st4,
                    )
                ), shape = RoundedCornerShape(50.dp)
            )
            .clickable { short = !short }
            .padding(start = 25.dp, top = 10.dp, bottom = 10.dp, end = 25.dp)
            .wrapContentSize()
            .animateContentSize(
                tween(1000)
            ),

        ) {
        Text(
            text = if (short) {
                shortText
            } else {
                longText
            },
            softWrap = true,
            style = if (short) {
                MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF604D2E),
                    fontSize = 30.sp
                )
            } else {
                MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF604D2E),
                    fontSize = 19.sp
                )
            },
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun prev(){
    component()
}