package com.mak.youtubex.data.local

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface PostDao {

    // ---------- PAGING ----------

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    // ---------- INSERT ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    // ---------- CLEAR ----------

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    // ---------- CACHE CONTROL ----------

    @Query("SELECT lastUpdated FROM posts ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdatedTime(): Long?

    @Query("UPDATE posts SET lastUpdated = :timestamp")
    suspend fun updateLastUpdatedTime(timestamp: Long)

    // ---------- LIKE SYSTEM (ATOMIC) ----------

    @Query("""
        UPDATE posts 
        SET 
            isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END,
            likesCount = CASE 
                WHEN isLiked = 1 THEN MAX(likesCount - 1, 0)
                ELSE likesCount + 1 
            END
        WHERE id = :postId
    """)
    suspend fun toggleLike(postId: String)

    // ---------- SINGLE FETCH ----------

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): PostEntity?
}