package com.bangkit.storyappdicoding.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Nonnull

data class StoryResponse(

    @Json(name = "listStory")
    val listStory: List<ListStoryItem>,

    @Json(name = "error")
    val error: Boolean? = null,

    @Json(name = "message")
    val message: String? = null
)

@Entity(tableName = "story")
data class ListStoryItem(

    @PrimaryKey
    @Nonnull
    @Json(name = "id")
    val id: String,

    @Json(name = "photoUrl")
    val photoUrl: String? = null,

    @Json(name = "createdAt")
    val createdAt: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "description")
    val description: String? = null,

    @Json(name = "lon")
    val lon: Double? = null,

    @Json(name = "lat")
    val lat: Double? = null

) : java.io.Serializable
