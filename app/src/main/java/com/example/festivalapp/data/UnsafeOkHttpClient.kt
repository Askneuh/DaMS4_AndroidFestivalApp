package com.example.festivalapp.data

import com.example.festivalapp.data.RetrofitInstance.cookieJar
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.cert.X509Certificate
import javax.net.ssl.*

object UnsafeOkHttpClient {

    // CookieManager partagé — stocke les cookies entre les requêtes
    val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    fun getClient(cookieJar: CookieJar): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .cookieJar(cookieJar)
            .build()
    }
}
