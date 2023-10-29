package com.bangkit.storyappdicoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.storyappdicoding.data.local.SettingPreferences
import com.bangkit.storyappdicoding.data.local.UserPreference
import com.bangkit.storyappdicoding.data.local.model.UserModel
import com.bangkit.storyappdicoding.data.local.room.StoryDatabase
import com.bangkit.storyappdicoding.data.remote.response.ListStoryItem
import com.bangkit.storyappdicoding.data.remote.response.LoginResponse
import com.bangkit.storyappdicoding.data.remote.response.StandardResponse
import com.bangkit.storyappdicoding.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class AppRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val settingDatastore: SettingPreferences,
    private val userDatastore: UserPreference,
    private val user: UserModel? = null
) {

    // Auth
    suspend fun signup(
        name: String,
        email: String,
        password: String
    ): LiveData<ApiStatus<StandardResponse>> = liveData {
        emit(ApiStatus.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(ApiStatus.Success(response))
        } catch (e: HttpException) {
            val errorResponse = e.response()
            if (errorResponse != null) {
                val errorBody = errorResponse.errorBody()
                if (errorBody != null) {
                    val errorMessage = try {
                        val errorJson = JSONObject(errorBody.string())
                        errorJson.optString("message")
                    } catch (jsonException: JSONException) {
                        "An error occurred"
                    }
                    emit(ApiStatus.Error(e.message.toString(), errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(ApiStatus.Error(e.message.toString()))
        }
    }

    suspend fun login(email: String, password: String): LiveData<ApiStatus<LoginResponse>> =
        liveData {
            emit(ApiStatus.Loading)
            try {
                val response = apiService.login(email, password)
                emit(ApiStatus.Success(response))
            } catch (e: HttpException) {
                emit(ApiStatus.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(ApiStatus.Error(e.message.toString()))
            }
        }

    suspend fun saveSession(user: UserModel) {
        return userDatastore.saveSession(user)
    }

    fun getSession(): UserModel? {
        return user
    }

    suspend fun logout() {
        userDatastore.logout()
    }

    // Story
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                // StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun uploadStoryWithLocation(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        latitude: Float,
        longitude: Float
    ) =
        liveData {
            emit(ApiStatus.Loading)
            try {
                val response = apiService.uploadStoryWithLocation(
                    multipartBody,
                    requestBody,
                    latitude,
                    longitude
                )
                emit(ApiStatus.Success(response))
            } catch (e: Exception) {
                emit(ApiStatus.Error(e.message.toString()))
            }
        }

    suspend fun uploadStory(multipartBody: MultipartBody.Part, requestBody: RequestBody) =
        liveData {
            emit(ApiStatus.Loading)
            try {
                val response = apiService.uploadStory(multipartBody, requestBody)
                emit(ApiStatus.Success(response))
            } catch (e: Exception) {
                emit(ApiStatus.Error(e.message.toString()))
            }
        }

    fun getStoryLocation() = liveData {
        emit(ApiStatus.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            emit(ApiStatus.Success(response))
        } catch (e: Exception) {
            emit(ApiStatus.Error(e.message.toString()))
        }
    }

    // Setting Preferences
    fun getThemeSetting(): LiveData<Int> {
        return settingDatastore.getThemeSetting().asLiveData()
    }

    suspend fun saveThemeSetting(isDarkModeActive: Int) {
        settingDatastore.saveThemeSetting(isDarkModeActive)
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            settingDatastore: SettingPreferences,
            userDatastore: UserPreference,
            user: UserModel?
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(
                    storyDatabase,
                    apiService,
                    settingDatastore,
                    userDatastore,
                    user
                )
            }.also { instance = it }

        fun refreshInstance() {
            instance = null
        }
    }
}