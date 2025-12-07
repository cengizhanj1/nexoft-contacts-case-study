package com.cengizhan.contactsapp.data.remote

data class EmptyResponseSuccessDto(
    val success: Boolean,
    val messages: List<String>?,
    val data: EmptyResponseDto?,
    val status: Int
)
