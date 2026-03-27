package com.mak.youtubex.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import com.mak.youtubex.core.data.util.map
import com.mak.youtubex.core.data.util.safeCall
import com.mak.youtubex.data.local.YTDatabase
import com.mak.youtubex.data.paging.CommentPagingSource
import com.mak.youtubex.data.paging.PostFeedPagingSource
import com.mak.youtubex.data.paging.PostRemoteMediator
import com.mak.youtubex.data.remote.api.PostApi
import com.mak.youtubex.data.remote.dto.comment.CommentRequest
import com.mak.youtubex.data.remote.mapper.toCompressedMultipart
import com.mak.youtubex.data.remote.mapper.toDomain
import com.mak.youtubex.data.remote.mapper.toTextRequestBody
import com.mak.youtubex.domain.model.Like
import com.mak.youtubex.domain.repository.SocialRepository
import com.mak.youtubex.presentation.main.social_feed.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import com.mak.youtubex.domain.model.Comment
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val db: YTDatabase,
    private val contentResolver: ContentResolver
) : SocialRepository {

    override suspend fun createPost(
        content: String,
        visibility: String,
        images: List<Uri>?
    ): Result<Unit, NetworkError> {
        return safeCall {
            postApi.createPost(
                content = content.toTextRequestBody(),
                visibility = visibility.toTextRequestBody(),
                images = images?.map { uri ->
                    uri.toCompressedMultipart(
                        resolver = contentResolver,
                        partName = "images"
                    )
                }
            )
        }
    }
    @OptIn(ExperimentalPagingApi::class)
    override fun getSocialFeed(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            remoteMediator = PostRemoteMediator(db, postApi),
            pagingSourceFactory = {
                db.postDao().pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getUserPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PostFeedPagingSource(postApi)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun likePost(
        postId: String
    ): Result<Unit, NetworkError> {
        val result = safeCall {
            postApi.likePost(postId)
        }
        println("LIKE_POST: $result")
        return result
    }

    override suspend fun unLikePost(
        postId: String
    ): Result<Unit, NetworkError> {
        return safeCall {
            postApi.unlikePost(postId)
        }
    }

    override suspend fun addComment(
        postId: String,
        content: String
    ): Result<Unit, NetworkError> {
        return safeCall {
            postApi.addComment(
                postId,
                CommentRequest(content)
            )
        }
    }

    override fun getComments(
        postId: String
    ): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentPagingSource(postApi, postId)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun deleteComment(
        postId: String,
        commentId: String
    ): Result<Unit, NetworkError> {
        return safeCall {
            postApi.deleteComment(postId, commentId)
        }
    }

    override suspend fun deletePost(
        postId: String
    ): Result<Unit, NetworkError> {
        return safeCall {
            postApi.deletePost(postId)
        }
    }
}