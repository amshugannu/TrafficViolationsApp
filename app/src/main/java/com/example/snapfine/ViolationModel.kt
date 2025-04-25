package com.example.snapfine

data class ViolationModel(
    val vehicleNumber: String = "",
    val reportedByUID: String = "",
    val reportedToUID: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val status: String = ""
)