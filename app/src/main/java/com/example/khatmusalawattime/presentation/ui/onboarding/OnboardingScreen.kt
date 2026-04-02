package com.example.khatmusalawattime.presentation.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Данные для страницы онбординга
 */
data class OnboardingPage(
    val icon: String,
    val title: String,
    val subtitle: String,
    val features: List<OnboardingFeature>
)

data class OnboardingFeature(
    val icon: String,
    val text: String
)

/**
 * Страницы онбординга
 */
private val onboardingPages = listOf(
    OnboardingPage(
        icon = "🕌",
        title = "Ассаламу алейкум!",
        subtitle = "Добро пожаловать в KhatmuSalawat.TIME",
        features = listOf(
            OnboardingFeature("📅", "Актуальное расписание Хатму и Салавата"),
            OnboardingFeature("📍", "Поддержка локаций: Хунзах и Чиркей"),
            OnboardingFeature("🔔", "Ежедневные напоминания о времени чтения")
        )
    ),
    OnboardingPage(
        icon = "⏰",
        title = "Полезные инструменты",
        subtitle = "Всё необходимое для духовной практики",
        features = listOf(
            OnboardingFeature("⏱️", "Таймер для отслеживания времени чтения"),
            OnboardingFeature("📿", "Тасбих-счётчик с разными режимами"),
            OnboardingFeature("🔢", "Азкар, вирд и произвольный счёт")
        )
    ),
    OnboardingPage(
        icon = "✨",
        title = "Дополнительные возможности",
        subtitle = "Персонализируйте своё использование",
        features = listOf(
            OnboardingFeature("📝", "Личные заметки и списки целей"),
            OnboardingFeature("💾", "Резервное копирование данных"),
            OnboardingFeature("🎨", "Приятный и удобный интерфейс")
        )
    )
)

/**
 * Экран онбординга с тремя страницами
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    // Цвета
    val backgroundColor = Color(0xFFFAF3E6)
    val primaryColor = Color(0xFF604D2E)
    val accentColor = Color(0xFF8B7355)
    val cardColor = Color(0xFFF1E4D1)

    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF4DBAD),
            Color(0xFFFDFBCC),
            Color(0xFFF9EBBD),
            Color(0xFFF4DBAD)
        )
    )

    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF8B7355),
            Color(0xFF604D2E),
            Color(0xFF8B7355)
        )
    )

    // Завершение онбординга
    val finishOnboarding = {
        SettingsPreferences.saveOnboardingShown(context, true)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Декоративные элементы на фоне
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFAF3E6),
                            Color(0xFFF5EBD7),
                            Color(0xFFFAF3E6)
                        )
                    )
                )
        )

        // Кнопка "Пропустить" сверху справа
        AnimatedVisibility(
            visible = pagerState.currentPage < onboardingPages.size - 1,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
        ) {
            Text(
                text = "Пропустить",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif,
                color = accentColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { finishOnboarding() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Основной контент
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Pager с контентом
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPageContent(
                    page = onboardingPages[pageIndex],
                    pageIndex = pageIndex,
                    isCurrentPage = pagerState.currentPage == pageIndex,
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    cardColor = cardColor,
                    borderGradient = borderGradient
                )
            }

            // Индикаторы страниц
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "indicator_width"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.4f,
                        animationSpec = tween(300),
                        label = "indicator_alpha"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(width)
                            .height(8.dp)
                            .alpha(alpha)
                            .clip(RoundedCornerShape(4.dp))
                            .background(primaryColor)
                    )
                }
            }

            // Кнопка "Далее" / "Начать"
            val isLastPage = pagerState.currentPage == onboardingPages.size - 1

            Box(
                modifier = Modifier
                    .padding(horizontal = 48.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 3.dp,
                        brush = borderGradient,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(buttonGradient)
                    .clickable {
                        if (isLastPage) {
                            finishOnboarding()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLastPage) "Начать" else "Далее",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    isCurrentPage: Boolean,
    primaryColor: Color,
    accentColor: Color,
    cardColor: Color,
    borderGradient: Brush
) {
    var animationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            animationTriggered = false
            delay(100)
            animationTriggered = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Большая иконка
        AnimatedVisibility(
            visible = animationTriggered || !isCurrentPage,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 }
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 4.dp,
                        brush = borderGradient,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.icon,
                    fontSize = 56.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Заголовок
        AnimatedVisibility(
            visible = animationTriggered || !isCurrentPage,
            enter = fadeIn(tween(500, delayMillis = 150)) + slideInVertically(tween(500, delayMillis = 150)) { 30 }
        ) {
            Text(
                text = page.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = primaryColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Подзаголовок
        AnimatedVisibility(
            visible = animationTriggered || !isCurrentPage,
            enter = fadeIn(tween(500, delayMillis = 250)) + slideInVertically(tween(500, delayMillis = 250)) { 30 }
        ) {
            Text(
                text = page.subtitle,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                color = accentColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Карточка с фичами
        AnimatedVisibility(
            visible = animationTriggered || !isCurrentPage,
            enter = fadeIn(tween(500, delayMillis = 350)) + slideInVertically(tween(500, delayMillis = 350)) { 50 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        brush = borderGradient,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardColor)
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    page.features.forEachIndexed { index, feature ->
                        FeatureItem(
                            feature = feature,
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            delayMillis = index * 100
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    feature: OnboardingFeature,
    primaryColor: Color,
    accentColor: Color,
    delayMillis: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка в круге
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x20604D2E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = feature.icon,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Текст
        Text(
            text = feature.text,
            fontSize = 15.sp,
            fontFamily = FontFamily.Serif,
            color = primaryColor,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
