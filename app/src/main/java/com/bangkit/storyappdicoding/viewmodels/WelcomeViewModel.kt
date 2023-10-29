package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.local.model.UserModel

class WelcomeViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _sessionLiveData = MutableLiveData<UserModel>()
    val sessionLiveData: LiveData<UserModel> = _sessionLiveData

    init {
        getSession()
    }

    fun getSession() {
        _sessionLiveData.value = appRepository.getSession()
    }
}