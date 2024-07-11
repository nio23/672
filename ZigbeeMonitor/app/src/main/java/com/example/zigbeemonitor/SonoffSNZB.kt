package com.example.zigbeemonitor

data class SonoffSNZB(
    val battery: Float = 0f,
    val humidity: Float = 0f,
    val linkquality: Int = 0,
    val temperature: Float = 0f,
    val voltage: Int = 0,
)
