package com.cengizhan.contactsapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        // API key interceptor – ApiConstants içindeki key kullanılıyor
        .addInterceptor(ApiKeyInterceptor(ApiConstants.API_KEY))
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: ContactsApi by lazy {
        retrofit.create(ContactsApi::class.java)
    }
}
