package com.cengizhan.contactsapp.data.remote

data class UserListResponseSuccessDto(
    val success: Boolean,
    val messages: List<String>?,
    val data: UserListResponseDto?,
    val status: Int
)
