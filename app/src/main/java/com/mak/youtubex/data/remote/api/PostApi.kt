package com.mak.youtubex.data.remote.api

import com.mak.youtubex.data.remote.dto.ApiResponse
import com.mak.youtubex.data.remote.dto.comment.CommentDto
import com.mak.youtubex.data.remote.dto.PaginatedResponseDto
import com.mak.youtubex.data.remote.dto.comment.CommentRequest
import com.mak.youtubex.data.remote.dto.like.LikeDto
import com.mak.youtubex.data.remote.dto.post.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostApi {

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Part("content") content: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<ApiResponse<Unit>>

    @GET("posts/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<PaginatedResponseDto<PostDto>>>

    @GET("posts/user/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<PaginatedResponseDto<PostDto>>>

    @POST("posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): Response<ApiResponse<Unit>>

    @DELETE("posts/{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): Response<ApiResponse<Unit>>

    @GET("posts/{postId}/likes")
    suspend fun getPostLikes(
        @Path("postId") postId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<PaginatedResponseDto<LikeDto>>>

    @POST("posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: String,
        @Body request: CommentRequest
    ): Response<ApiResponse<Unit>>

    @GET("posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<PaginatedResponseDto<CommentDto>>>

    @DELETE("posts/{postId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String
    ): Response<ApiResponse<Unit>>

    @Multipart
    @PATCH("posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: String,
        @Part("content") content: RequestBody?,
        @Part("visibility") visibility: RequestBody?,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<ApiResponse<PostDto>>

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String
    ): Response<ApiResponse<Unit>>
}