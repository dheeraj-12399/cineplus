package com.example.data.remote.tmdb

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TmdbClient {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    // Working fallback key in case BuildConfig field is blank
    private const val FALLBACK_TMDB_API_KEY = "4e44d9029b1270a757cddc766a1bcb63"

    @Volatile
    private var customApiKey: String? = null

    fun setCustomApiKey(key: String?) {
        customApiKey = key?.trim()?.ifBlank { null }
    }

    fun getActiveApiKey(): String {
        return customApiKey
            ?: runCatching { BuildConfig.TMDB_API_KEY }.getOrNull()?.trim()?.ifBlank { null }
            ?: FALLBACK_TMDB_API_KEY
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val apiKey = getActiveApiKey()

        val urlWithParams = originalUrl.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(urlWithParams)
            .addHeader("Accept", "application/json")
            .build()

        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }

    // --- Official Image URL Helpers ---
    fun posterUrl(path: String?, size: String = "w500"): String {
        if (path.isNullOrBlank()) return ""
        val clean = path.trimStart('/')
        return "$IMAGE_BASE_URL$size/$clean"
    }

    fun backdropUrl(path: String?, size: String = "w1280"): String {
        if (path.isNullOrBlank()) return ""
        val clean = path.trimStart('/')
        return "$IMAGE_BASE_URL$size/$clean"
    }

    fun thumbnailUrl(path: String?, size: String = "w342"): String {
        if (path.isNullOrBlank()) return ""
        val clean = path.trimStart('/')
        return "$IMAGE_BASE_URL$size/$clean"
    }

    fun profileUrl(path: String?, size: String = "w185"): String {
        if (path.isNullOrBlank()) return ""
        val clean = path.trimStart('/')
        return "$IMAGE_BASE_URL$size/$clean"
    }

    fun logoUrl(path: String?, size: String = "w154"): String {
        if (path.isNullOrBlank()) return ""
        val clean = path.trimStart('/')
        return "$IMAGE_BASE_URL$size/$clean"
    }

    fun youtubeTrailerUrl(key: String?): String {
        if (key.isNullOrBlank()) return ""
        return "https://www.youtube.com/watch?v=$key"
    }

    fun youtubeThumbnailUrl(key: String?): String {
        if (key.isNullOrBlank()) return ""
        return "https://img.youtube.com/vi/$key/hqdefault.jpg"
    }
}
