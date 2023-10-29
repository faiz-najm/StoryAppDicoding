package com.bangkit.storyappdicoding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.data.local.model.UserModel
import com.bangkit.storyappdicoding.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val appRepository: AppRepository) : ViewModel() {

    private var _sessionLiveData = MutableLiveData<UserModel>()
    val sessionLiveData: LiveData<UserModel> = _sessionLiveData

    private var _storyPagingData = appRepository.getStory().cachedIn(viewModelScope)
    val storyPagingData: LiveData<PagingData<ListStoryItem>> = _storyPagingData

    fun logout() {
        viewModelScope.launch {
            appRepository.logout()
            _sessionLiveData.value = appRepository.getSession()
        }.onJoin
    }

    fun getSession() {
        _sessionLiveData.value = appRepository.getSession()
    }
}