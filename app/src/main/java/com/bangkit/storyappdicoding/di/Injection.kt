package com.bangkit.storyappdicoding.di

import android.content.Context
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.local.SettingPreferences
import com.bangkit.storyappdicoding.data.local.UserPreference
import com.bangkit.storyappdicoding.data.local.room.StoryDatabase
import com.bangkit.storyappdicoding.data.local.settingDataStore
import com.bangkit.storyappdicoding.data.local.userDataStore
import com.bangkit.storyappdicoding.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): AppRepository {
        val datastore = SettingPreferences.getInstance(context.settingDataStore)
        val userDatastore = UserPreference.getInstance(context.userDataStore)
        val user = runBlocking { userDatastore.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getInstance(context)
        return AppRepository.getInstance(storyDatabase,apiService, datastore, userDatastore, user)
    }

    fun refreshRepository() {
        AppRepository.refreshInstance()
    }
}