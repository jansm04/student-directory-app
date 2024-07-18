package com.js.sd.https

import android.text.TextUtils
import com.js.sd.exceptions.InvalidCredentialsException
import com.js.sd.exceptions.InvalidUrlException
import com.js.sd.properties.Properties
import okhttp3.Credentials
import okhttp3.OkHttpClient

object ApiClient {

    private val httpClient = OkHttpClient.Builder()
    private lateinit var apiService: ApiService

    @Throws(InvalidCredentialsException::class, InvalidUrlException::class)
    fun createService(username: String, password: String): ApiService {
        if (TextUtils.isEmpty(username)) {
            throw InvalidCredentialsException("Username must not be empty")
        }
        if (TextUtils.isEmpty(password)) {
            throw InvalidCredentialsException("Password must not be empty")
        }
        val authToken = Credentials.basic(username, password)
        return createService(authToken)
    }

    @Throws(InvalidUrlException::class)
    fun createService(authToken: String): ApiService {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = AuthInterceptor(authToken)
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
                val retrofitClient = RetrofitClient.getClient(Properties.BASE_URL, httpClient.build())
                if (retrofitClient != null) {
                    apiService = retrofitClient.create(ApiService::class.java)
                }
            }
        }
        return apiService
    }
}