package com.mak.youtubex.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(

    @PrimaryKey
    val id: String,

    val content: String,

    val images: List<String>,

    val visibility: String,

    val deletedAt: String?,

    val likesCount: Int,

    val commentsCount: Int,

    val createdAt: String,

    val updatedAt: String,

    val authorId: String,

    val username: String,

    val fullName: String,

    val avatar: String,

    val isLiked: Boolean,

    // ---------- NEW (for cache control) ----------
    val lastUpdated: Long
)