package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class StoryLocationViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _storyResultLiveData = MutableLiveData<ApiStatus<StoryResponse>>()
    val storyResultLiveData: MutableLiveData<ApiStatus<StoryResponse>> = _storyResultLiveData

    init {
        getStoryLocation()
    }

    fun getStoryLocation() {
        viewModelScope.launch {
            _storyResultLiveData.value = ApiStatus.Loading
            appRepository.getStoryLocation().observeForever { result ->
                _storyResultLiveData.value = result
            }
        }
    }
}