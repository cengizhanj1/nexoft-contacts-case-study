package com.cengizhan.contactsapp.data.repository

import com.cengizhan.contactsapp.data.mapper.toContact
import com.cengizhan.contactsapp.data.mapper.toContacts
import com.cengizhan.contactsapp.data.mapper.toCreateUserRequestDto
import com.cengizhan.contactsapp.data.mapper.toUpdateUserRequestDto
import com.cengizhan.contactsapp.data.remote.ContactsApi
import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.domain.repository.ContactsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ContactsRepositoryImpl(
    private val api: ContactsApi
) : ContactsRepository {

    override suspend fun getContacts(): List<Contact> {
        val response = api.getAllUsers()          // UserListResponseSuccessDto
        val usersDtoList = response.data?.users   // List<UserResponseDto>?
            ?: emptyList()

        return usersDtoList.toContacts()
    }

    override suspend fun createContact(contact: Contact): Contact {
        val request = contact.toCreateUserRequestDto()
        val response = api.createUser(request)    // UserResponseSuccessDto
        val userDto = response.data
            ?: throw IllegalStateException("Create user response body is null")

        return userDto.toContact()
    }

    override suspend fun updateContact(contact: Contact): Contact {
        // id nullable ise, burada zorunlu hale getiriyoruz
        val id = contact.id
            ?: throw IllegalArgumentException("Contact id is required for update")

        val request = contact.toUpdateUserRequestDto()
        val response = api.updateUser(id, request)   // UserResponseSuccessDto
        val userDto = response.data
            ?: throw IllegalStateException("Update user response body is null")

        return userDto.toContact()
    }

    override suspend fun deleteContact(id: String) {
        // EmptyResponseSuccessDto dönüyor, gövdesini kullanmıyoruz
        api.deleteUser(id)
    }

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String {
        // ByteArray -> RequestBody
        val requestBody = imageBytes.toRequestBody(
            contentType = "image/*".toMediaTypeOrNull()
        )

        // "image" ismi, server tarafındaki form field ismi olmalı
        val imagePart = MultipartBody.Part.createFormData(
            name = "image",
            filename = fileName,
            body = requestBody
        )

        val response = api.uploadImage(imagePart)   // UploadImageResponseSuccessDto

        val url = response.data?.imageUrl
            ?: throw IllegalStateException("Upload image response body is null")

        return url
    }
}
