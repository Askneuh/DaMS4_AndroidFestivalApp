package com.example.festivalapp

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.festivalapp.data.APIService
import com.example.festivalapp.data.AppContainer
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.data.FestivalDatabase
import com.example.festivalapp.data.RetrofitInstance
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.session.DataStoreCookieJar
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.data.user.room.DefaultUserRepository
import com.example.festivalapp.data.user.room.UserRepository
import com.example.festivalapp.data.session.sessionDataStore
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
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
        val httpClientBuilder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        val certificate = CertificateFactory.getInstance("X.509")
            .generateCertificate(resources.openRawResource(R.raw.localhost))

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("server", certificate)
        }

        val trustManagerFactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }
        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManager), null)
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }  // cert est pour "localhost", serveur est une IP
            .cookieJar(cookieJar)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

}