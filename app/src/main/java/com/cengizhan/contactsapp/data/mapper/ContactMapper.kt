package com.cengizhan.contactsapp.data.mapper

import com.cengizhan.contactsapp.data.remote.CreateUserRequestDto
import com.cengizhan.contactsapp.data.remote.UpdateUserRequestDto
import com.cengizhan.contactsapp.data.remote.UserResponseDto
import com.cengizhan.contactsapp.domain.model.Contact

// --- DTO -> Domain ---

fun UserResponseDto.toContact(): Contact =
    Contact(
        id = id,
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        profileImageUrl = profileImageUrl,
        createdAt = createdAt
    )

fun List<UserResponseDto>?.toContacts(): List<Contact> =
    this?.map { it.toContact() } ?: emptyList()

// --- Domain -> DTO (request) ---

fun Contact.toCreateUserRequestDto(): CreateUserRequestDto =
    CreateUserRequestDto(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl
    )

fun Contact.toUpdateUserRequestDto(): UpdateUserRequestDto =
    UpdateUserRequestDto(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl
    )
