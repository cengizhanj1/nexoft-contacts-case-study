package com.cengizhan.contactsapp.data.remote

data class UploadImageResponseSuccessDto(
    val success: Boolean,
    val messages: List<String>?,
    val data: UploadImageResponseDto?,
    val status: Int
)
