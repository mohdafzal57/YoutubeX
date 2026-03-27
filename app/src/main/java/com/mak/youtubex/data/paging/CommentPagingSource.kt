package com.mak.youtubex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mak.youtubex.data.remote.api.PostApi
import com.mak.youtubex.data.remote.dto.comment.CommentDto
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class CommentPagingSource @Inject constructor(
    private val postApi: PostApi,
    private val postId: String
) : PagingSource<Int, CommentDto>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val NETWORK_PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentDto> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = postApi.getComments(
                postId = postId,
                page = page,
                limit = NETWORK_PAGE_SIZE
            )

            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val body = response.body()

            if (body == null || body.data == null) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            LoadResult.Page(
                data = body.data.docs,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (body.data.totalPages <= page) null else page + 1
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommentDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}