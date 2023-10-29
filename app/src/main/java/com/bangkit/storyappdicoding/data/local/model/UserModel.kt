package com.bangkit.storyappdicoding.data.local.model

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)