package com.cengizhan.contactsapp.data.remote

data class CreateUserRequestDto(
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val profileImageUrl: String?
)
