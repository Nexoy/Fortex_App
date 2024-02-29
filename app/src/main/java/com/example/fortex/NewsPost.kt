package com.example.fortex

data class NewsPost(
    val postId: String,
    val gameName: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    var isLiked: Boolean,
    val userId: String
)

