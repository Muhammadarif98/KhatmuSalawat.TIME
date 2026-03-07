package com.example.khatmusalawattime.presentation.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khatmusalawattime.data.backup.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    private val _backupMessage = MutableStateFlow<String?>(null)
    val backupMessage = _backupMessage.asStateFlow()

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            backupManager.exportBackup(uri)
                .onSuccess { _backupMessage.value = "Данные успешно сохранены" }
                .onFailure { _backupMessage.value = "Ошибка: ${it.message}" }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            backupManager.importBackup(uri)
                .onSuccess { _backupMessage.value = "Данные успешно восстановлены" }
                .onFailure { _backupMessage.value = "Ошибка: ${it.message}" }
        }
    }

    fun clearMessage() {
        _backupMessage.value = null
    }
}
