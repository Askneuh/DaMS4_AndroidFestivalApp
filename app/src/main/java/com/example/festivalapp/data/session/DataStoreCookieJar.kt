package com.example.festivalapp.data.session

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class DataStoreCookieJar(private val sessionRepository: SessionRepository) : CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val accessValue = runBlocking {
            sessionRepository.accessCookieFlow.first()
        }
        val refreshValue = runBlocking {
            sessionRepository.refreshCookieFlow.first()
        }

        val cookies = mutableListOf<Cookie>()

        if (accessValue != null) {
            cookies.add(
                Cookie.Builder()
                    .name("access_token")
                    .value(accessValue)
                    .domain("162.38.111.35")
                    .path("/")
                    .secure()
                    .httpOnly()
                    .build()
            )
        }

        if (refreshValue != null) {
            cookies.add(
                Cookie.Builder()
                    .name("refresh_token")
                    .value(refreshValue)
                    .domain("162.38.111.35")
                    .path("/")
                    .secure()
                    .httpOnly()
                    .build()
            )
        }
        return cookies

    }

    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>
    ) {
        for (cookie in cookies) {
            if (cookie.name == "access_token") {
                runBlocking { sessionRepository.saveAccessCookie(cookie.value) }
            }
            else if (cookie.name == "refresh_token") {
                runBlocking { sessionRepository.saveRefreshCookie(cookie.value) }
            }
        }
    }

}