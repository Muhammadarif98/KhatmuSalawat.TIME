package com.example.khatmusalawattime.data.backup

import android.content.Context
import android.net.Uri
import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.dao.NoteDao
import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.data.local.entity.NoteEntity
import com.example.khatmusalawattime.data.local.entity.NoteListEntity
import com.example.khatmusalawattime.presentation.ui.components.settings.SettingsPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class BackupData(
    @SerializedName("version") val version: Int = 1,
    @SerializedName("counter") val counter: Int = 0,
    @SerializedName("noteLists") val noteLists: List<BackupNoteList> = emptyList(),
    @SerializedName("settings") val settings: BackupSettings = BackupSettings()
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
    @SerializedName("freeCount") val freeCount: Int = 0
)

class BackupManager(
    private val context: Context,
    private val noteDao: NoteDao,
    private val counterDao: CounterDao,
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
                    freeCount = freeCount
                )
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

            // Восстанавливаем данные кастомного счётчика
            counterPrefs.edit().apply {
                settings.customCounterItems?.let { putString("custom_items", it) }
                putInt("free_count", settings.freeCount)
                apply()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
