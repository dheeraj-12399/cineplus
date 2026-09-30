package com.example.data.remote.tmdb

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    // --- Movie Discovery ---
    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String = "week",
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_original_language") language: String? = null,
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("primary_release_year") year: Int? = null,
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    // --- TV Discovery ---
    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTv(
        @Path("time_window") timeWindow: String = "week",
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    @GET("tv/popular")
    suspend fun getPopularTv(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    @GET("tv/airing_today")
    suspend fun getAiringTodayTv(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTv(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("with_original_language") language: String? = null,
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("first_air_date_year") year: Int? = null,
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>

    // --- Details ---
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Long,
        @Query("append_to_response") appendToResponse: String = "credits,videos,watch/providers"
    ): TmdbMovieDetails

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Long,
        @Query("append_to_response") appendToResponse: String = "credits,videos,watch/providers"
    ): TmdbTvDetails

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvSeasonDetails(
        @Path("tv_id") tvId: Long,
        @Path("season_number") seasonNumber: Int
    ): TmdbSeasonDetails

    // --- Search ---
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPageResponse<TmdbMultiSearchItem>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbMovie>

    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TmdbPageResponse<TmdbTvShow>
}
