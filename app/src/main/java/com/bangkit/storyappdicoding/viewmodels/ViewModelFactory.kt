package com.bangkit.storyappdicoding.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.bangkit.storyappdicoding.data.AppRepository
import com.bangkit.storyappdicoding.di.Injection

class ViewModelFactory private constructor(
    private val appRepository: AppRepository
) : NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            return DetailStoryViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(appRepository) as T
        }

        if (modelClass.isAssignableFrom(StoryLocationViewModel::class.java)) {
            return StoryLocationViewModel(appRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }

        fun refreshInstance() {
            instance = null
            Injection.refreshRepository()
        }
    }
}