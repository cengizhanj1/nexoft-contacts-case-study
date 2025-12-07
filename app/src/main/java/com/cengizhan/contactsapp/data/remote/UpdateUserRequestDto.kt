package com.cengizhan.contactsapp.data.remote

data class UpdateUserRequestDto(
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val profileImageUrl: String?
)
