package com.bangkit.storyappdicoding.data.remote.response

import com.squareup.moshi.Json

data class StandardResponse(

    @Json(name = "error")
    val error: Boolean,

    @Json(name = "message")
    val message: String
)
