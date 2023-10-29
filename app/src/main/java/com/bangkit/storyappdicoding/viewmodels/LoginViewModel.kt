package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.local.model.UserModel
import com.bangkit.storyappdicoding.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _sessionLiveData = MutableLiveData<UserModel>()
    val sessionLiveData: LiveData<UserModel> = _sessionLiveData

    private val _loginResultLiveData = MutableLiveData<ApiStatus<LoginResponse>>()
    val loginResultLiveData: LiveData<ApiStatus<LoginResponse>> = _loginResultLiveData

    fun getSession() {
        _sessionLiveData.value = appRepository.getSession()
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch { appRepository.saveSession(user) }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResultLiveData.value = ApiStatus.Loading
            appRepository.login(email, password).observeForever { result ->
                _loginResultLiveData.value = result
            }
        }
    }
}