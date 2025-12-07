package com.cengizhan.contactsapp.data.remote

data class UserResponseSuccessDto(
    val success: Boolean,
    val messages: List<String>?,
    val data: UserResponseDto?,       // Swagger'daki "UserResponse"
    val status: Int
)
