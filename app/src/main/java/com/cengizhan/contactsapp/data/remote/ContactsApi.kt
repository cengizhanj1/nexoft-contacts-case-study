package com.cengizhan.contactsapp.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// Swagger: http://146.59.52.68:11235/swagger

interface ContactsApi {

    @GET("api/User/GetAll")
    suspend fun getAllUsers(): UserListResponseSuccessDto


    @GET("api/User/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): UserResponseSuccessDto


    @POST("api/User")
    suspend fun createUser(
        @Body request: CreateUserRequestDto
    ): UserResponseSuccessDto


    @PUT("api/User/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequestDto
    ): UserResponseSuccessDto


    @DELETE("api/User/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): EmptyResponseSuccessDto


    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): UploadImageResponseSuccessDto
}
