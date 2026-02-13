package com.mak.notex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mak.notex.data.remote.api.VideoApi
import com.mak.notex.data.remote.dto.video.VideoFeedDto
import okio.IOException
import retrofit2.HttpException

class VideoFeedPagingSource(
    private val query: String?,
    private val api: VideoApi
) : PagingSource<Int, VideoFeedDto>() {
    override fun getRefreshKey(state: PagingState<Int, VideoFeedDto>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoFeedDto> {
        return try {
            val page = params.key ?: 1
            val response = api.getVideoFeed(
                page = page,
                limit = 5, // params.loadSize
                query = query
            )

            if (!response.isSuccessful && response.body() == null) {
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
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (body.data.totalPages <= page) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}