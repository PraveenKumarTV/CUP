package com.example.cup2.model

data class SavedResponse(
    val id: Int,
    val question: String,
    val answer: String,
    val aiResponse: String,
    val timestamp: String
)
