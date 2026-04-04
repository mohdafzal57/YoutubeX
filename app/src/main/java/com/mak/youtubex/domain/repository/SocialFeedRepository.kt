package com.mak.youtubex.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import com.mak.youtubex.domain.model.Comment
import com.mak.youtubex.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface SocialRepository {

    fun getSocialFeed(): Flow<PagingData<Post>>
    fun getUserPosts(username: String): Flow<PagingData<Post>>

    suspend fun getPostById(postId: String): Result<Post, NetworkError>

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