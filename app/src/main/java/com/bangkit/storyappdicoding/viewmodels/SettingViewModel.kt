package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.AppRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val appRepository: AppRepository) : ViewModel() {

    fun getThemeSettings(): LiveData<Int> {
        return appRepository.getThemeSetting()
    }

    fun saveThemeSetting(isDarkModeActive: Int) {
        viewModelScope.launch {
            appRepository.saveThemeSetting(isDarkModeActive)
        }
    }
}