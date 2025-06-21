package com.example.khatmusalawattime.presentation.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsGroup(
    backgroundGradient: Brush,
    borderGradient: Brush,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 10.dp,
                brush = borderGradient,
                shape = RoundedCornerShape(35.dp)
            ),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    textColor: Color,
    icon: ImageVector? = null,
    leadingIcon: Any? = null,
    subtitle: String? = null,
    hasBottomContent: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .let { 
                if (onClick != null) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        icon != null -> {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        leadingIcon != null && leadingIcon is Int -> {
                            Icon(
                                painter = painterResource(id = leadingIcon),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 16.sp
                    )
                }
                
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                    )
                }
            }
            
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                trailingContent()
            }
        }
        
        if (!hasBottomContent) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 56.dp, end = 16.dp)
                    .align(Alignment.BottomCenter)
                    .background(textColor.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 25.sp,
            color = Color(0xFF716550),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        )
        
        content()
    }
} 