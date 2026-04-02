package com.example.khatmusalawattime.presentation.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.ReminderData
import com.example.khatmusalawattime.presentation.notification.cancelScheduledNotification
import com.example.khatmusalawattime.presentation.notification.scheduleDailyNotification
import com.example.khatmusalawattime.presentation.ui.components.CustomSwitch
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsGroup
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsItem
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsSection
import com.example.khatmusalawattime.presentation.ui.components.settings.TimePickerDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val PREFS_NAME = "SettingsPrefs"
private const val REMINDER_TIME_KEY = "reminder_time"
private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
private const val DAILY_REMINDER_ENABLED_KEY = "daily_reminder_enabled"
private const val TIMER_NOTIFICATION_ENABLED_KEY = "timer_notification_enabled"
private const val DARK_MODE_ENABLED_KEY = "dark_mode_enabled"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    // Бэкап
    val backupMessage by viewModel.backupMessage.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importBackup(it) }
    }

    LaunchedEffect(backupMessage) {
        backupMessage?.let {
            snackState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    
    // Состояние для TimePicker
    var showCustomTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(16) }
    var selectedMinute by remember { mutableStateOf(0) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    // Загружаем сохраненные настройки
    var reminderTime by remember { mutableStateOf(SettingsPreferences.loadReminderTime(context)) }
    var notificationsEnabled by remember { mutableStateOf(SettingsPreferences.loadNotificationsEnabled(context)) }
    var dailyReminderEnabled by remember { mutableStateOf(SettingsPreferences.loadDailyReminderEnabled(context)) }
    var timerNotificationEnabled by remember { mutableStateOf(SettingsPreferences.loadTimerNotificationEnabled(context)) }
    var isDarkMode by remember { mutableStateOf(SettingsPreferences.loadDarkModeEnabled(context)) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Разбираем сохраненное время
    LaunchedEffect(Unit) {
        val timeParts = reminderTime.split(":")
        if (timeParts.size == 2) {
            selectedHour = timeParts[0].toIntOrNull() ?: 16
            selectedMinute = timeParts[1].toIntOrNull() ?: 0
        }
    }
    
    // Кэшируем цвета для UI
    val backgroundColor = remember { Color(0xFFFAF7F2) }
    val textColor = remember { Color(0xFF7C5F23) }
    val accentColor = remember { Color(0xFF827868) }

    // Кэшируем градиент для обводки
    val borderGradient = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFF4DBAD),
                Color(0xFFFDFBCC),
                Color(0xFFF9EBBD),
                Color(0xFFF4DBAD)
            )
        )
    }

    // Кэшируем градиент для фона карточек
    val cardGradient = remember {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFF1E4D1),
                Color(0xFFFFFFFC),
                Color(0xFFF1E4D1)
            )
        )
    }

    // Используем кастомный диалог выбора времени
    TimePickerDialog(
        showDialog = showCustomTimePicker,
        onDismiss = { showCustomTimePicker = false },
        selectedHour = selectedHour,
        selectedMinute = selectedMinute,
        onTimeSelected = { hour, minute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.isLenient = false
            
            // Сохраняем выбранное время
            val newTime = formatter.format(cal.time)
            SettingsPreferences.saveReminderTime(context, newTime)
            reminderTime = newTime
            
            // Перепланируем уведомление с новым временем
            if (notificationsEnabled && dailyReminderEnabled) {
                try {
                    // Загружаем данные из JSON файла
                    val jsonString = context.resources.openRawResource(R.raw.reminder_data)
                        .bufferedReader()
                        .use { it.readText() }
                    
                    // Парсим JSON в объект ReminderData
                    val reminderData = Gson().fromJson(jsonString, ReminderData::class.java)
                    
                    val currentDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date())
                    
                    // Планируем регулярное уведомление
                    scheduleDailyNotification(context, reminderData, currentDate)
                    
                    snackScope.launch {
                        snackState.showSnackbar("Время уведомлений установлено на $reminderTime")
                    }
                } catch (e: Exception) {
                    snackScope.launch {
                        snackState.showSnackbar("Ошибка: ${e.message}")
                    }
                }
            } else {
                snackScope.launch {
                    snackState.showSnackbar("Время установлено на $newTime, но уведомления отключены")
                }
            }
        },
        textColor = textColor,
        borderGradient = borderGradient,
        cardGradient = cardGradient
    )

    // Диалог "Про приложение"
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            textColor = textColor,
            borderGradient = borderGradient
        )
    }

    // Обработчик сохранения настроек уведомлений
    val saveNotificationSettings = { 
        SettingsPreferences.saveNotificationSettings(
            context, 
            notificationsEnabled, 
            dailyReminderEnabled, 
            timerNotificationEnabled
        )
    }

    // Обработчик сохранения настроек темы
    val saveThemeSettings = {
        SettingsPreferences.saveDarkModeEnabled(context, isDarkMode)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Добавляем фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.backsetting),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Добавляем затемнение поверх изображения
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f))
        )
        
        // Заголовок экрана
        Text(
            text = "Настройки",
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF716550),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
                .background(Color.White.copy(alpha = 0f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Секция уведомлений
            item {
                SettingsSection(title = "Уведомления") {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        // Ежедневное напоминание (активно только если включены уведомления)
                        SettingsItem(
                            title = "Ежедневное напоминание",
                            icon = Icons.Default.Notifications,
                            textColor = textColor,
                            subtitle = "Установить время напоминания",
                            modifier = Modifier.alpha(if (notificationsEnabled) 1f else 0.5f),
                            trailingContent = {
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    CustomSwitch(
                                        checked = dailyReminderEnabled && notificationsEnabled,
                                        onCheckedChange = { 
                                            if (notificationsEnabled) {
                                                dailyReminderEnabled = it
                                                saveNotificationSettings()
                                                
                                                // Если ежедневные напоминания отключены, отменяем запланированные уведомления
                                                if (!it) {
                                                    cancelScheduledNotification(context)
                                                    snackScope.launch {
                                                        snackState.showSnackbar("Ежедневные напоминания отключены")
                                                    }
                                                } else {
                                                    // Если напоминания включены, планируем уведомление
                                                    try {
                                                        // Загружаем данные из JSON файла
                                                        val jsonString = context.resources.openRawResource(R.raw.reminder_data)
                                                            .bufferedReader()
                                                            .use { it.readText() }
                                                        
                                                        // Парсим JSON в объект ReminderData
                                                        val reminderData = Gson().fromJson(jsonString, ReminderData::class.java)
                                                        
                                                        val currentDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date())
                                                        
                                                        // Планируем регулярное уведомление
                                                        scheduleDailyNotification(context, reminderData, currentDate)
                                                        
                                                        snackScope.launch {
                                                            snackState.showSnackbar("Уведомления включены, ежедневное напоминание в $reminderTime")
                                                        }
                                                    } catch (e: Exception) {
                                                        snackScope.launch {
                                                            snackState.showSnackbar("Ошибка: ${e.message}")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    
                                    Text(
                                        text = reminderTime,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = accentColor,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            },
                            onClick = { 
                                if (notificationsEnabled && dailyReminderEnabled) {
                                    showCustomTimePicker = true
                                }
                            }
                        )
                    }
                }
            }
            
            // Секция Разработка
            item {
                SettingsSection(title = "Разработка",
                    ) {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        // Исходный код
                        SettingsItem(
                            title = "Исходный код",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_settings),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Muhammadarif98/KhatmuSalawat.TIME"))
                                context.startActivity(intent)
                            },
                            trailingContent = {}
                        )
                        
                        // Отслеживание проблем
                        SettingsItem(
                            title = "Отслеживание проблем",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_notes),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:mrwildoswildos@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Сообщение о проблеме - KhatmuSalawat.TIME")
                                }
                                context.startActivity(intent)
                            },
                            trailingContent = {}
                        )
                    }
                }
            }
            
            // Секция Другое
            item {
                SettingsSection(title = "Другое") {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        // Про приложение
                        SettingsItem(
                            title = "Про приложение",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_settings),
                            onClick = { showAboutDialog = true },
                            trailingContent = {}
                        )
                        
                        // Вступление
                        SettingsItem(
                            title = "Вступление",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_notes),
                            onClick = {
                                SettingsPreferences.resetOnboarding(context)
                                onNavigateToOnboarding()
                            },
                            trailingContent = {}
                        )
                    }
                }
            }
            
            // Секция Поддержать
            item {
                SettingsSection(title = "Поддержать") {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        // Оценить
                        SettingsItem(
                            title = "Оценить приложение",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_settings),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rustore.ru/catalog/app/com.example.khatmusalawattime"))
                                context.startActivity(intent)
                            },
                            trailingContent = {}
                        )
                        
                        // Поддержка разработки
                        SettingsItem(
                            title = "Поддержка разработки",
                            textColor = textColor,
                            leadingIcon = painterResource(id = R.drawable.ic_mosque),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:mrwildoswildos@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Поддержка разработки KhatmuSalawat.TIME")
                                }
                                context.startActivity(intent)
                            },
                            trailingContent = {}
                        )
                    }
                }
            }
            
            // Секция Данные (бэкап/восстановление)
            item {
                SettingsSection(title = "Данные") {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        SettingsItem(
                            title = "Сохранить данные",
                            subtitle = "Экспорт в файл",
                            textColor = textColor,
                            leadingIcon = R.drawable.ic_upload,
                            trailingContent = {},
                            onClick = {
                                exportLauncher.launch("khatmu_backup.json")
                            }
                        )

                        SettingsItem(
                            title = "Восстановить данные",
                            subtitle = "Импорт из файла",
                            textColor = textColor,
                            leadingIcon = R.drawable.ic_download,
                            trailingContent = {},
                            onClick = {
                                importLauncher.launch(arrayOf("application/json"))
                            }
                        )
                    }
                }
            }

            // Информация
            item {
                SettingsSection(title = "Информация") {
                    SettingsGroup(
                        backgroundGradient = cardGradient,
                        borderGradient = borderGradient
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 30.dp, end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "Ваши данные хранятся исключительно на вашем устройстве. Удаление данных вашего приложения может привести к безвозвратной потере данных. Чтобы предотвратить это, не забудьте сохранить файл данных перед выполнением сброса.",
                                color = textColor.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(hostState = snackState)
    }
}

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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
fun SettingsItemLocal(
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
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF7C5F23),
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        )
        
        content()
    }
}

private fun saveReminderTime(context: Context, time: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putString(REMINDER_TIME_KEY, time)
        }
}

private fun loadReminderTime(context: Context): String {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(REMINDER_TIME_KEY, "16:00") ?: "16:00"
}

private fun saveNotificationsEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putBoolean(NOTIFICATIONS_ENABLED_KEY, enabled)
        }
}

private fun loadNotificationsEnabled(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(NOTIFICATIONS_ENABLED_KEY, true)
}

private fun saveDailyReminderEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putBoolean(DAILY_REMINDER_ENABLED_KEY, enabled)
        }
}

private fun loadDailyReminderEnabled(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(DAILY_REMINDER_ENABLED_KEY, true)
}

private fun saveTimerNotificationEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putBoolean(TIMER_NOTIFICATION_ENABLED_KEY, enabled)
        }
}

private fun loadTimerNotificationEnabled(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(TIMER_NOTIFICATION_ENABLED_KEY, true)
}

private fun saveDarkModeEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putBoolean(DARK_MODE_ENABLED_KEY, enabled)
        }
}

private fun loadDarkModeEnabled(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(DARK_MODE_ENABLED_KEY, false)
}

/**
 * Диалог "Про приложение" с описанием функционала
 */
@Composable
private fun AboutDialog(
    onDismiss: () -> Unit,
    textColor: Color,
    borderGradient: Brush
) {
    val bgColor = Color(0xFFF1E4D1)
    val accentColor = Color(0xFFB8956E)
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 4.dp,
                    brush = borderGradient,
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(bgColor)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок
                Text(
                    text = "KhatmuSalawat.TIME",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = textColor
                )

                Text(
                    text = "Версия 1.0",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    color = textColor.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Описание
                AboutSectionTitle(text = "О приложении", textColor = textColor)
                AboutText(
                    text = "Приложение для отслеживания времени коллективного чтения Хатму и Салавата. Помогает мусульманам не пропустить важные духовные практики.",
                    textColor = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Локации
                AboutSectionTitle(text = "Локации", textColor = textColor)
                AboutFeatureItem(
                    icon = "📍",
                    title = "Хунзах",
                    description = "Расписание для жителей Хунзаха",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "📍",
                    title = "Чиркей",
                    description = "Расписание для жителей Чиркея",
                    textColor = textColor,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Расписание
                AboutSectionTitle(text = "Расписание", textColor = textColor)
                AboutFeatureItem(
                    icon = "📖",
                    title = "Четверг — Салават",
                    description = "Время коллективного чтения Салавата",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "🕌",
                    title = "Пятница — Шазалийский Хатму",
                    description = "Особое чтение по пятницам",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "📿",
                    title = "Остальные дни — Хатму",
                    description = "Ежедневное время чтения Хатму",
                    textColor = textColor,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Функции
                AboutSectionTitle(text = "Функции", textColor = textColor)
                AboutFeatureItem(
                    icon = "⏰",
                    title = "Таймер",
                    description = "Таймер для отслеживания времени чтения",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "🔢",
                    title = "Счётчик",
                    description = "Тасбих-счётчик с разными режимами",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "📝",
                    title = "Заметки",
                    description = "Личные заметки и записи",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "🔔",
                    title = "Уведомления",
                    description = "Напоминания о времени Хатму/Салавата",
                    textColor = textColor,
                    accentColor = accentColor
                )
                AboutFeatureItem(
                    icon = "💾",
                    title = "Бэкап данных",
                    description = "Сохранение и восстановление данных",
                    textColor = textColor,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка закрытия
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x33604D2E))
                        .clickable { onDismiss() }
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Закрыть",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSectionTitle(text: String, textColor: Color) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun AboutText(text: String, textColor: Color) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontFamily = FontFamily.Serif,
        color = textColor,
        lineHeight = 20.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AboutFeatureItem(
    icon: String,
    title: String,
    description: String,
    textColor: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            modifier = Modifier.padding(end = 12.dp, top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif,
                color = textColor
            )
            Text(
                text = description,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                color = textColor.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
    }
} 