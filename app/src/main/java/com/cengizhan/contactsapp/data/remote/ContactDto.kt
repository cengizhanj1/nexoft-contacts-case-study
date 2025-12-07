package com.cengizhan.contactsapp.data.remote

data class ContactDto(
    val id: Int,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val isFromDevice: Boolean?
)
