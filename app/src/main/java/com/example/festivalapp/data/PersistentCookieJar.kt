package com.example.festivalapp.data

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs = context.getSharedPreferences("okhttp_cookies", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = prefs.edit()
        cookies.forEach { cookie ->
            // Sérialisation manuelle des champs importants
            val value = "${cookie.name}|${cookie.domain}|${cookie.path}|" +
                    "${cookie.value}|${cookie.expiresAt}|${cookie.secure}|${cookie.httpOnly}"
            editor.putString(cookie.name, value)
        }
        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return prefs.all.mapNotNull { (_, raw) ->
            val parts = (raw as? String)?.split("|") ?: return@mapNotNull null
            if (parts.size != 7) return@mapNotNull null
            try {
                Cookie.Builder()
                    .name(parts[0])
                    .domain(parts[1])
                    .path(parts[2])
                    .value(parts[3])
                    .expiresAt(parts[4].toLong())
                    .apply { if (parts[5] == "true") secure() }
                    .apply { if (parts[6] == "true") httpOnly() }
                    .build()
            } catch (e: Exception) { null }
        }
    }

    fun clear() = prefs.edit().clear().apply() // Pour le logout
}
