package com.example.fortex

data class Comment(
    val postId: String = "",
    val userId: String = "",
    val name: String = "",
    val commentText: String = "",
    val id: String
) {
    constructor() : this("", "", "", "", "")
}

