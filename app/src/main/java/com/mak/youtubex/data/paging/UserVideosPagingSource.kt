package com.mak.youtubex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mak.youtubex.data.remote.api.VideoApi
import com.mak.youtubex.data.remote.dto.video.UserVideoDto
import com.mak.youtubex.data.repository.VideoRepositoryImpl.Companion.NETWORK_PAGE_SIZE
import com.mak.youtubex.domain.model.UserVideoRequest
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class UserVideosPagingSource @Inject constructor(
    private val videoApi: VideoApi,
    private val userVideoRequest: UserVideoRequest
) : PagingSource<Int, UserVideoDto>() {
    companion object {
        private const val USER_VIDEO_STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserVideoDto> {
        val page = params.key ?: USER_VIDEO_STARTING_PAGE_INDEX //nextPageNumber
        return try {
            val response = videoApi.getAllVideosOfAUser(
                page = page,
                limit = NETWORK_PAGE_SIZE,
                query = userVideoRequest.query,
                sortBy = userVideoRequest.sortBy,
                sortType = userVideoRequest.sortType,
                userId = userVideoRequest.userId
            )

            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            /*val body = response.body() ?: return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )*/

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

    /***
     * When a refresh happens, Paging needs a starting page key so that:
     * The user stays close to the same scroll position
     * Data doesn’t jump back to the first page
     * ex- after rotation. */
    override fun getRefreshKey(state: PagingState<Int, UserVideoDto>): Int? {
        // current page ki position find kar rahe hain.
        // either by prevKey + 1 = current page
        // or by nextKey - 1 = current page
        // simple
        // If there’s no anchor position, return null
        // null → Paging restarts from the initial page
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}