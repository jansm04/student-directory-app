package com.js.sd.https

import com.js.sd.exceptions.InvalidUrlException
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    @Throws(InvalidUrlException::class)
    fun getClient(baseUrl: String, client: OkHttpClient): Retrofit? {
        if (retrofit == null) {
            try {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            } catch (e: RuntimeException) {
                throw InvalidUrlException("Invalid URL provided to Retrofit Builder.")
            }
        }
        return retrofit
    }
}