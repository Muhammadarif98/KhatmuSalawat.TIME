package com.example.khatmusalawattime.data.backup

import android.content.Context
import android.net.Uri
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.dao.CounterGoalDao
import com.example.khatmusalawattime.data.local.dao.CounterHistoryDao
import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.data.local.entity.CounterGoalEntity
import com.example.khatmusalawattime.data.local.entity.CounterHistoryEntity
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class BackupData(
    @SerializedName("version") val version: Int = 3,
    @SerializedName("counter") val counter: Int = 0,
    @SerializedName("noteLists") val noteLists: List<BackupNoteList> = emptyList(),
    @SerializedName("settings") val settings: BackupSettings = BackupSettings(),
    @SerializedName("counterHistory") val counterHistory: List<BackupCounterHistory> = emptyList(),
    @SerializedName("counterGoals") val counterGoals: List<BackupCounterGoal> = emptyList()
)

data class BackupNoteList(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("isCompleted") val isCompleted: Boolean,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long,
    @SerializedName("tasks") val tasks: List<BackupTask>
)

data class BackupTask(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("isCompleted") val isCompleted: Boolean,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long
)

data class BackupSettings(
    @SerializedName("reminderTime") val reminderTime: String = "16:00",
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean = true,
    @SerializedName("dailyReminderEnabled") val dailyReminderEnabled: Boolean = true,
    @SerializedName("timerNotificationEnabled") val timerNotificationEnabled: Boolean = true,
    @SerializedName("darkModeEnabled") val darkModeEnabled: Boolean = false,
    @SerializedName("customCounterItems") val customCounterItems: String? = null,
    @SerializedName("freeCount") val freeCount: Int = 0,
    // Прогресс режимов счётчика
    @SerializedName("azkarIndex") val azkarIndex: Int = 0,
    @SerializedName("azkarCount") val azkarCount: Int = 0,
    @SerializedName("wirdCountPerZikr") val wirdCountPerZikr: Int = 100,
    @SerializedName("wirdIndex") val wirdIndex: Int = 0,
    @SerializedName("wirdCount") val wirdCount: Int = 0,
    @SerializedName("customIndex") val customIndex: Int = 0,
    @SerializedName("customCount") val customCount: Int = 0
)

data class BackupCounterHistory(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: Long,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("sessionsCount") val sessionsCount: Int,
    @SerializedName("modes") val modes: String,
    @SerializedName("createdAt") val createdAt: Long
)

data class BackupCounterGoal(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("goalType") val goalType: String,
    @SerializedName("targetCount") val targetCount: Int,
    @SerializedName("currentCount") val currentCount: Int,
    @SerializedName("isCompleted") val isCompleted: Boolean,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("createdAt") val createdAt: Long
)

class BackupManager(
    private val context: Context,
    private val noteDao: NoteDao,
    private val counterDao: CounterDao,
    private val counterHistoryDao: CounterHistoryDao,
    private val counterGoalDao: CounterGoalDao,
    private val gson: Gson
) {
    private val counterPrefs by lazy {
        context.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)
    }

    suspend fun exportBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val noteLists = noteDao.getAllNoteLists().first()
            val allNotes = noteDao.getAllNotes().first()
            val counter = counterDao.getCounter()

            // Загружаем данные кастомного счётчика
            val customItems = counterPrefs.getString("custom_items", null)
            val freeCount = counterPrefs.getInt("free_count", 0)

            // Загружаем прогресс режимов счётчика
            val azkarIndex = counterPrefs.getInt("azkar_current_index", 0)
            val azkarCount = counterPrefs.getInt("azkar_current_count", 0)
            val wirdCountPerZikr = counterPrefs.getInt("wird_count_per_zikr", 100)
            val wirdIndex = counterPrefs.getInt("wird_current_index", 0)
            val wirdCount = counterPrefs.getInt("wird_current_count", 0)
            val customIndex = counterPrefs.getInt("custom_current_index", 0)
            val customCount = counterPrefs.getInt("custom_current_count", 0)

            val backupNoteLists = noteLists.map { list ->
                val tasks = allNotes.filter { it.noteListId == list.id }
                BackupNoteList(
                    id = list.id,
                    title = list.title,
                    isCompleted = list.isCompleted,
                    createdAt = list.createdAt,
                    updatedAt = list.updatedAt,
                    tasks = tasks.map { task ->
                        BackupTask(
                            id = task.id,
                            title = task.title,
                            isCompleted = task.isCompleted,
                            createdAt = task.createdAt,
                            updatedAt = task.updatedAt
                        )
                    }
                )
            }

            // Экспортируем историю счётчика
            val historyList = counterHistoryDao.getAllHistory().first()
            val backupHistory = historyList.map { h ->
                BackupCounterHistory(
                    id = h.id,
                    date = h.date,
                    totalCount = h.totalCount,
                    sessionsCount = h.sessionsCount,
                    modes = h.modes,
                    createdAt = h.createdAt
                )
            }

            // Экспортируем цели счётчика
            val goalsList = counterGoalDao.getAllGoals().first()
            val backupGoals = goalsList.map { g ->
                BackupCounterGoal(
                    id = g.id,
                    title = g.title,
                    goalType = g.goalType,
                    targetCount = g.targetCount,
                    currentCount = g.currentCount,
                    isCompleted = g.isCompleted,
                    deadline = g.deadline,
                    createdAt = g.createdAt
                )
            }

            val backupData = BackupData(
                counter = counter?.count ?: 0,
                noteLists = backupNoteLists,
                settings = BackupSettings(
                    reminderTime = SettingsPreferences.loadReminderTime(context),
                    notificationsEnabled = SettingsPreferences.loadNotificationsEnabled(context),
                    dailyReminderEnabled = SettingsPreferences.loadDailyReminderEnabled(context),
                    timerNotificationEnabled = SettingsPreferences.loadTimerNotificationEnabled(context),
                    darkModeEnabled = SettingsPreferences.loadDarkModeEnabled(context),
                    customCounterItems = customItems,
                    freeCount = freeCount,
                    azkarIndex = azkarIndex,
                    azkarCount = azkarCount,
                    wirdCountPerZikr = wirdCountPerZikr,
                    wirdIndex = wirdIndex,
                    wirdCount = wirdCount,
                    customIndex = customIndex,
                    customCount = customCount
                ),
                counterHistory = backupHistory,
                counterGoals = backupGoals
            )

            val json = gson.toJson(backupData)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            } ?: return@withContext Result.failure(Exception("Не удалось открыть файл для записи"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext Result.failure(Exception("Не удалось открыть файл для чтения"))

            val backupData = gson.fromJson(json, BackupData::class.java)
                ?: return@withContext Result.failure(Exception("Неверный формат файла"))

            // Восстанавливаем счётчик
            counterDao.saveCounter(CounterEntity(id = 0, count = backupData.counter))

            // Очищаем текущие заметки и восстанавливаем из бэкапа
            noteDao.deleteAllNotesByQuery()
            noteDao.deleteAllNoteListsByQuery()

            for (list in backupData.noteLists) {
                val noteEntities = list.tasks.map { task ->
                    NoteEntity(
                        id = task.id,
                        noteListId = list.id,
                        title = task.title,
                        content = "",
                        isCompleted = task.isCompleted,
                        createdAt = task.createdAt,
                        updatedAt = task.updatedAt
                    )
                }

                noteDao.insertNoteList(
                    NoteListEntity(
                        id = list.id,
                        title = list.title,
                        isCompleted = list.isCompleted,
                        tasks = noteEntities,
                        createdAt = list.createdAt,
                        updatedAt = list.updatedAt
                    )
                )

                for (note in noteEntities) {
                    noteDao.insertNote(note)
                }
            }

            // Восстанавливаем настройки
            val settings = backupData.settings
            SettingsPreferences.saveReminderTime(context, settings.reminderTime)
            SettingsPreferences.saveNotificationsEnabled(context, settings.notificationsEnabled)
            SettingsPreferences.saveDailyReminderEnabled(context, settings.dailyReminderEnabled)
            SettingsPreferences.saveTimerNotificationEnabled(context, settings.timerNotificationEnabled)
            SettingsPreferences.saveDarkModeEnabled(context, settings.darkModeEnabled)

            // Восстанавливаем данные кастомного счётчика и прогресс режимов
            counterPrefs.edit().apply {
                settings.customCounterItems?.let { putString("custom_items", it) }
                putInt("free_count", settings.freeCount)
                // Прогресс режимов
                putInt("azkar_current_index", settings.azkarIndex)
                putInt("azkar_current_count", settings.azkarCount)
                putInt("wird_count_per_zikr", settings.wirdCountPerZikr)
                putInt("wird_current_index", settings.wirdIndex)
                putInt("wird_current_count", settings.wirdCount)
                putInt("custom_current_index", settings.customIndex)
                putInt("custom_current_count", settings.customCount)
                apply()
            }

            // Восстанавливаем историю счётчика
            counterHistoryDao.deleteAll()
            for (h in backupData.counterHistory) {
                counterHistoryDao.insert(
                    CounterHistoryEntity(
                        id = h.id,
                        date = h.date,
                        totalCount = h.totalCount,
                        sessionsCount = h.sessionsCount,
                        modes = h.modes,
                        createdAt = h.createdAt
                    )
                )
            }

            // Восстанавливаем цели счётчика
            counterGoalDao.deleteAll()
            for (g in backupData.counterGoals) {
                counterGoalDao.insert(
                    CounterGoalEntity(
                        id = g.id,
                        title = g.title,
                        goalType = g.goalType,
                        targetCount = g.targetCount,
                        currentCount = g.currentCount,
                        isCompleted = g.isCompleted,
                        deadline = g.deadline,
                        createdAt = g.createdAt
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
