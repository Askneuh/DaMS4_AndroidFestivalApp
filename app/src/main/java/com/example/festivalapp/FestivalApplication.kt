package com.example.festivalapp

import android.app.Application
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.data.AppContainer
import com.example.festivalapp.data.RetrofitInstance
import com.example.festivalapp.data.session.DataStoreCookieJar
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class FestivalApplication : Application() {
    lateinit var container: AppContainer
    lateinit var cookieJar: DataStoreCookieJar

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        cookieJar = DataStoreCookieJar(container.sessionRepository)
        
        val okHttpClient = generateSecureOkHttpClient()
        RetrofitInstance.okHttpClient = okHttpClient
    }

    private fun generateSecureOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .cookieJar(cookieJar)
            .build()
    }
}