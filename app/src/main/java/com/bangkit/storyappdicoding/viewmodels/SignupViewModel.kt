package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.remote.response.StandardResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _signupResultLiveData = MutableLiveData<ApiStatus<StandardResponse>>()
    val signupResultLiveData: LiveData<ApiStatus<StandardResponse>> = _signupResultLiveData

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signupResultLiveData.value = ApiStatus.Loading
            appRepository.signup(name, email, password).observeForever { result ->
                _signupResultLiveData.value = result
            }
        }

    }
}