package com.mak.notex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mak.notex.data.remote.api.UserApi
import com.mak.notex.data.remote.dto.user.WatchHistoryDto
import com.mak.notex.data.repository.VideoRepositoryImpl.Companion.NETWORK_PAGE_SIZE
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class WatchHistoryPagingSource @Inject constructor(
    private val userApi: UserApi,
) : PagingSource<Int, WatchHistoryDto>() {
    companion object {
        private const val USER_VIDEO_STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WatchHistoryDto> {
        val page = params.key ?: USER_VIDEO_STARTING_PAGE_INDEX
        return try {
            val response = userApi.getWatchHistory(
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
                prevKey = if (page == USER_VIDEO_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (body.data.totalPages <= page) null else page + 1,
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WatchHistoryDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}