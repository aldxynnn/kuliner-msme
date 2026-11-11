package com.kuliner.msme.admin.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = tokenManager.getToken()
        if (!token.isNull meEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun String?.isNull meEmpty(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
