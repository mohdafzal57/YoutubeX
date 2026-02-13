package com.mak.notex.domain.model

data class Like(
    val id: String,
    val user: String,
    val targetId: String,
    val targetModel: String,
    val createdAt: String,
    val updatedAt: String
)