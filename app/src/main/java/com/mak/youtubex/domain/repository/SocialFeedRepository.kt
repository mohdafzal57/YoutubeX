package com.mak.youtubex.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.domain.model.Like
import com.mak.youtubex.presentation.main.social_feed.Post
import okhttp3.MultipartBody
import com.mak.youtubex.domain.model.Comment
import kotlinx.coroutines.flow.Flow
import com.mak.youtubex.core.data.util.Result

interface SocialRepository {

    fun getSocialFeed(): Flow<PagingData<Post>>
    fun getUserPosts(): Flow<PagingData<Post>>

    suspend fun likePost(
        postId: String
    ): Result<Unit, NetworkError>

    suspend fun unLikePost(
        postId: String
    ): Result<Unit, NetworkError>

    suspend fun addComment(
        postId: String,
        content: String
    ): Result<Unit, NetworkError>

    fun getComments(
        postId: String
    ): Flow<PagingData<Comment>>

    suspend fun deleteComment(
        postId: String,
        commentId: String
    ): Result<Unit, NetworkError>

    suspend fun createPost(
        content: String,
        visibility: String,
        images: List<Uri>? = null
    ): Result<Unit, NetworkError>

    suspend fun deletePost(
        postId: String
    ): Result<Unit, NetworkError>
}