package com.mak.youtubex.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mak.youtubex.data.local.PostEntity
import com.mak.youtubex.data.local.RemoteKeys
import com.mak.youtubex.data.local.YTDatabase
import com.mak.youtubex.data.remote.api.PostApi
import com.mak.youtubex.data.remote.mapper.toEntity
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val db: YTDatabase,
    private val postApi: PostApi
) : RemoteMediator<Int, PostEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val NETWORK_PAGE_SIZE = 10
        //private const val CACHE_TIMEOUT = 1 * 60 * 60 * 1000L // 1 hour
    }

    private val postDao = db.postDao()
    private val remoteKeysDao = db.remoteKeysDao()

    // ---------------- INIT ----------------

    /*override suspend fun initialize(): InitializeAction {
        val lastUpdated = postDao.getLastUpdatedTime() ?: 0L
        val isCacheValid = System.currentTimeMillis() - lastUpdated < CACHE_TIMEOUT

        return if (isCacheValid) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }*/

    override suspend fun initialize(): InitializeAction {
        // Most modern apps always launch a refresh to ensure data is up-to-date.
        // If there is no network, the load() function will catch the IOException
        // and the app will simply continue showing the cached Room data.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    // ---------------- LOAD ----------------

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        return try {

            val page = when (loadType) {

                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // 1. Get the last item loaded to find its remote key
                    val remoteKeys = getRemoteKeyForLastItem(state)

                    // 2. If nextKey is null, we've reached the end of the server data
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                    nextKey // This is the page we will fetch
                }
            }

            val response = postApi.getFeed(
                page = page,
                limit = NETWORK_PAGE_SIZE
            )

            if (!response.isSuccessful) {
                return MediatorResult.Error(HttpException(response))
            }

            val body = response.body()
            val docs = body?.data?.docs.orEmpty()
            val totalPages = body?.data?.totalPages ?: page

            val endOfPaginationReached = docs.isEmpty() || page >= totalPages

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    postDao.clearAll()
                }

                val currentTime = System.currentTimeMillis()

                val entities = docs.map {
                    it.toEntity(currentTime).copy(lastUpdated = currentTime)
                }

                val keys = docs.map {
                    RemoteKeys(
                        postId = it.id,
                        prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }

                remoteKeysDao.insertAll(keys)
                postDao.insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    // ---------------- HELPERS ----------------

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, PostEntity>
    ): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { post -> remoteKeysDao.remoteKeysPostId(post.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, PostEntity>
    ): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { post -> remoteKeysDao.remoteKeysPostId(post.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PostEntity>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeysDao.remoteKeysPostId(id)
            }
        }
    }
}