package com.mak.youtubex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginatedResponseDto<T>(
    @SerializedName("docs")
    val docs: List<T> = emptyList(),

    @SerializedName("totalDocs")
    val totalDocs: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("hasNextPage")
    val hasNextPage: Boolean,

    @SerializedName("hasPrevPage")
    val hasPrevPage: Boolean,

    @SerializedName("nextPage")
    val nextPage: Int?,

    @SerializedName("prevPage")
    val prevPage: Int?
)
