package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.remote.response.StandardResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val appRepository: AppRepository) : ViewModel() {

    private var _uploadResultLiveData = MutableLiveData<ApiStatus<StandardResponse>>()
    val uploadResultLiveData: LiveData<ApiStatus<StandardResponse>> = _uploadResultLiveData

    fun uploadStory(multipartBody: MultipartBody.Part, requestBody: RequestBody) {
        viewModelScope.launch {
            _uploadResultLiveData.value = ApiStatus.Loading
            appRepository.uploadStory(multipartBody, requestBody).observeForever { result ->
                _uploadResultLiveData.value = result
            }
        }
    }

    fun uploadStoryWithLocation(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        latitude: Float,
        longitude: Float
    ) {
        viewModelScope.launch {
            _uploadResultLiveData.value = ApiStatus.Loading
            appRepository.uploadStoryWithLocation(
                multipartBody,
                requestBody,
                latitude,
                longitude
            ).observeForever { result ->
                _uploadResultLiveData.value = result
            }
        }
    }

}