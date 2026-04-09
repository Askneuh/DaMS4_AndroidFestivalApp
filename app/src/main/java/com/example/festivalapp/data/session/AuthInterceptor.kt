package com.example.festivalapp.data.session

import com.example.festivalapp.data.auth.AuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionRepository: SessionRepository,
    private val authApiProvider: () -> AuthApiService
) : Interceptor {

    private val authApi: AuthApiService by lazy { authApiProvider() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            if (originalRequest.url.encodedPath.contains("/auth/refresh")) {
                runBlocking { sessionRepository.clearSession() }
                return response
            }

            val refreshed = runBlocking { tryRefresh() }

            if (refreshed) {
                response.close()
                return chain.proceed(originalRequest)
            } else {
                runBlocking { sessionRepository.clearSession() }
            }
        }

        if (response.code == 403 && !originalRequest.url.encodedPath.contains("/auth/")) {
            val refreshed = runBlocking { tryRefresh() }
            if (refreshed) {
                response.close()
                return chain.proceed(originalRequest)
            }
        }

        return response
    }

    private suspend fun tryRefresh(): Boolean {
        return try {
            val refreshResponse = authApi.refresh()
            refreshResponse.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
