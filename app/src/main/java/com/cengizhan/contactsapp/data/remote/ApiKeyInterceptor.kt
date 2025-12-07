package com.cengizhan.contactsapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader(ApiConstants.API_KEY_HEADER, apiKey)   // <-- Doğru header adı buradan gelir
            .build()

        return chain.proceed(newRequest)
    }
}
