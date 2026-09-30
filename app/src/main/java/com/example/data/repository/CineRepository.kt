package com.example.data.repository

import com.example.data.local.CineDao
import com.example.data.model.*
import com.example.data.remote.tmdb.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CineRepository(
    private val dao: CineDao,
    private val api: TmdbApiService = TmdbClient.apiService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    init {
        scope.launch {
            initDefaults()
        }
    }

    private suspend fun initDefaults() {
        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.upsertUserProfile(UserProfile())
        }

        // Initialize default authorized test source if none exist (e.g. for legally authorized playback testing)
        val sources = dao.getAllAuthorizedPlaybackSources().firstOrNull() ?: emptyList()
        if (sources.isEmpty()) {
            // Seed a legally authorized test title (e.g. Tears of Steel / Big Buck Bunny open source test stream)
            dao.upsertAuthorizedPlaybackSource(
                AuthorizedPlaybackSource(
                    tmdbId = 1376856L, // Real Telugu title "The Paradise"
                    mediaType = "movie",
                    title = "The Paradise",
                    availability = "AVAILABLE",
                    playbackType = "progressive",
                    playbackUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    audioLanguages = "Telugu (Original), Hindi, Tamil, English",
                    subtitleLanguages = "English, Telugu",
                    availableQualities = "Auto, 1080p HD, 720p, 480p",
                    ottPlatform = "CinePulse License",
                    rightsNote = "Legally licensed playback stream for CinePulse streaming verification."
                )
            )

            // Seed Salaar (770906) as UNAVAILABLE (catalog only, no in-app streaming rights without admin unlock)
            dao.upsertAuthorizedPlaybackSource(
                AuthorizedPlaybackSource(
                    tmdbId = 770906L,
                    mediaType = "movie",
                    title = "Salaar: Part 1 - Ceasefire",
                    availability = "UNAVAILABLE",
                    playbackType = "progressive",
                    playbackUrl = "",
                    trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    audioLanguages = "Telugu (Original), Hindi, Tamil, Kannada, Malayalam",
                    subtitleLanguages = "English, Telugu, Hindi",
                    availableQualities = "Auto, 1080p HD, 720p, 480p",
                    ottPlatform = "Netflix / Theatrical",
                    rightsNote = "Theatrical release catalog entry. Official trailer available. Full movie streaming rights held by Netflix & theatrical distributors."
                )
            )
        }
    }

    // --- Dynamic TMDB Movie Discovery ---

    suspend fun getTrendingMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getTrendingMovies("week", page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getPopularMovies(page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getNowPlayingMovies(page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getUpcomingMovies(page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getTopRatedMovies(page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun discoverMoviesByLanguage(
        languageCode: String,
        page: Int = 1,
        sortBy: String = "popularity.desc"
    ): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.discoverMovies(
                language = languageCode,
                sortBy = sortBy,
                page = page
            )
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun discoverMoviesGeneral(
        languageCode: String? = null,
        genreId: Int? = null,
        sortBy: String = "popularity.desc",
        page: Int = 1
    ): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.discoverMovies(
                language = languageCode,
                genres = genreId?.toString(),
                sortBy = sortBy,
                page = page
            )
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun discoverTvGeneral(
        languageCode: String? = null,
        genreId: Int? = null,
        sortBy: String = "popularity.desc",
        page: Int = 1
    ): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.discoverTv(
                language = languageCode,
                genres = genreId?.toString(),
                sortBy = sortBy,
                page = page
            )
            mapAndCacheTv(response.results)
        }
    }

    suspend fun discoverTeluguMovies(
        category: String,
        page: Int = 1
    ): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val (genreId, sortBy, year) = when (category) {
                "New Telugu Releases" -> Triple(null, "primary_release_date.desc", 2024)
                "Popular Telugu Movies" -> Triple(null, "popularity.desc", null)
                "Telugu Blockbusters" -> Triple(28, "vote_count.desc", null)
                "Telugu Action" -> Triple(28, "popularity.desc", null)
                "Telugu Romance" -> Triple(10749, "popularity.desc", null)
                "Telugu Comedy" -> Triple(35, "popularity.desc", null)
                "Telugu Thriller" -> Triple(53, "popularity.desc", null)
                "Telugu Horror" -> Triple(27, "popularity.desc", null)
                "Telugu Crime" -> Triple(80, "popularity.desc", null)
                "Telugu Family Movies" -> Triple(10751, "popularity.desc", null)
                "Telugu Classics" -> Triple(null, "vote_average.desc", 2015)
                "Telugu OTT Movies" -> Triple(null, "popularity.desc", null)
                else -> Triple(null, "popularity.desc", null)
            }

            val response = api.discoverMovies(
                language = "te",
                genres = genreId?.toString(),
                sortBy = sortBy,
                year = year,
                page = page
            )
            mapAndCacheMovies(response.results)
        }
    }

    // --- Dynamic TMDB TV Discovery ---

    suspend fun getTrendingTv(page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getTrendingTv("week", page)
            mapAndCacheTv(response.results)
        }
    }

    suspend fun getPopularTv(page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getPopularTv(page)
            mapAndCacheTv(response.results)
        }
    }

    suspend fun getAiringTodayTv(page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getAiringTodayTv(page)
            mapAndCacheTv(response.results)
        }
    }

    suspend fun getOnTheAirTv(page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getOnTheAirTv(page)
            mapAndCacheTv(response.results)
        }
    }

    suspend fun getTopRatedTv(page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getTopRatedTv(page)
            mapAndCacheTv(response.results)
        }
    }

    suspend fun discoverTvByLanguage(
        languageCode: String,
        page: Int = 1
    ): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.discoverTv(
                language = languageCode,
                sortBy = "popularity.desc",
                page = page
            )
            mapAndCacheTv(response.results)
        }
    }

    // --- Details ---

    suspend fun getMovieDetails(movieId: Long): Result<Movie> = withContext(Dispatchers.IO) {
        runCatching {
            val playbackSource = dao.getAuthorizedPlaybackSourceSync(movieId)
            val details = api.getMovieDetails(movieId)
            val movie = details.toMovie(playbackSource)

            // Cache in Room
            dao.upsertCachedMedia(
                CachedMediaEntity(
                    tmdbId = movieId,
                    mediaType = "movie",
                    title = movie.title,
                    originalTitle = movie.originalTitle,
                    overview = movie.overview,
                    posterPath = details.posterPath,
                    backdropPath = details.backdropPath,
                    releaseDate = movie.releaseDate,
                    releaseYear = movie.releaseYear,
                    language = details.originalLanguage ?: "en",
                    genres = movie.genres,
                    runtime = movie.durationMinutes,
                    rating = movie.rating,
                    voteCount = movie.voteCount,
                    cast = movie.cast,
                    crew = movie.director,
                    trailerYoutubeKey = movie.youtubeTrailerKey,
                    watchProvidersFlatrate = movie.watchProvidersFlatrate,
                    watchProvidersRent = movie.watchProvidersRent,
                    watchProvidersBuy = movie.watchProvidersBuy
                )
            )
            dao.insertMovie(movie)
            movie
        }
    }

    suspend fun getTvDetails(tvId: Long): Result<TvShow> = withContext(Dispatchers.IO) {
        runCatching {
            val playbackSource = dao.getAuthorizedPlaybackSourceSync(tvId)
            val details = api.getTvDetails(tvId)
            val show = details.toTvShow(playbackSource)

            // Cache in Room
            dao.upsertCachedMedia(
                CachedMediaEntity(
                    tmdbId = tvId,
                    mediaType = "tv",
                    title = show.title,
                    originalTitle = show.originalTitle,
                    overview = show.overview,
                    posterPath = details.posterPath,
                    backdropPath = details.backdropPath,
                    releaseDate = show.releaseDate,
                    releaseYear = show.releaseYear,
                    language = details.originalLanguage ?: "en",
                    genres = show.genres,
                    runtime = details.episodeRunTime?.firstOrNull() ?: 45,
                    rating = show.rating,
                    voteCount = show.voteCount,
                    cast = show.cast,
                    crew = show.creator,
                    trailerYoutubeKey = show.youtubeTrailerKey,
                    watchProvidersFlatrate = show.watchProvidersFlatrate,
                    watchProvidersRent = show.watchProvidersRent,
                    watchProvidersBuy = show.watchProvidersBuy
                )
            )
            dao.insertTvShow(show)
            show
        }
    }

    suspend fun getTvSeasonEpisodes(tvId: Long, seasonNumber: Int): Result<List<Episode>> = withContext(Dispatchers.IO) {
        runCatching {
            val playbackSource = dao.getAuthorizedPlaybackSourceSync(tvId)
            val details = api.getTvSeasonDetails(tvId, seasonNumber)
            val isAuth = playbackSource != null && playbackSource.availability == "AVAILABLE" && playbackSource.playbackUrl.isNotBlank()
            val avail = playbackSource?.availability ?: "UNAVAILABLE"
            val pUrl = if (isAuth) playbackSource!!.playbackUrl else ""

            val eps = details.episodes?.map { ep ->
                Episode(
                    id = "ep_${tvId}_${seasonNumber}_${ep.episodeNumber}",
                    showId = tvId.toString(),
                    seasonId = "sea_${tvId}_$seasonNumber",
                    seasonNumber = seasonNumber,
                    episodeNumber = ep.episodeNumber,
                    title = ep.name.ifBlank { "Episode ${ep.episodeNumber}" },
                    overview = ep.overview?.ifBlank { "Episode ${ep.episodeNumber} of Season $seasonNumber" } ?: "",
                    durationMinutes = ep.runtime ?: 45,
                    thumbnailUrl = TmdbClient.backdropUrl(ep.stillPath, "w500"),
                    videoUrl = pUrl,
                    playbackUrl = pUrl,
                    playbackType = playbackSource?.playbackType ?: "progressive",
                    availability = avail
                )
            } ?: emptyList()

            dao.insertEpisodes(eps)
            eps
        }
    }

    // --- Search ---

    suspend fun searchMulti(query: String, page: Int = 1): Result<List<TmdbMultiSearchItem>> = withContext(Dispatchers.IO) {
        runCatching {
            api.searchMulti(query, page).results
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.searchMovies(query, page)
            mapAndCacheMovies(response.results)
        }
    }

    suspend fun searchTv(query: String, page: Int = 1): Result<List<TvShow>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.searchTv(query, page)
            mapAndCacheTv(response.results)
        }
    }

    // --- Caching & Mapping Helpers ---

    private suspend fun mapAndCacheMovies(dtos: List<TmdbMovie>): List<Movie> {
        return dtos.map { dto ->
            val source = dao.getAuthorizedPlaybackSourceSync(dto.id)
            val movie = dto.toMovie(source)
            dao.insertMovie(movie)
            movie
        }
    }

    private suspend fun mapAndCacheTv(dtos: List<TmdbTvShow>): List<TvShow> {
        return dtos.map { dto ->
            val source = dao.getAuthorizedPlaybackSourceSync(dto.id)
            val show = dto.toTvShow(source)
            dao.insertTvShow(show)
            show
        }
    }

    // --- Authorized Playback Sources (Admin CMS) ---

    fun getAuthorizedPlaybackSource(tmdbId: Long): Flow<AuthorizedPlaybackSource?> =
        dao.getAuthorizedPlaybackSource(tmdbId)

    suspend fun getAuthorizedPlaybackSourceSync(tmdbId: Long): AuthorizedPlaybackSource? =
        dao.getAuthorizedPlaybackSourceSync(tmdbId)

    val allAuthorizedPlaybackSources: Flow<List<AuthorizedPlaybackSource>> =
        dao.getAllAuthorizedPlaybackSources()

    suspend fun saveAuthorizedPlaybackSource(source: AuthorizedPlaybackSource) = withContext(Dispatchers.IO) {
        dao.upsertAuthorizedPlaybackSource(source)
        // Refresh local movie record if present
        val m = dao.getMovieById(source.tmdbId.toString()).firstOrNull()
        if (m != null) {
            val isAuth = source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
            dao.updateMovie(
                m.copy(
                    availability = source.availability,
                    isStreamingAuthorized = isAuth,
                    playbackType = source.playbackType,
                    playbackUrl = if (isAuth) source.playbackUrl else "",
                    videoUrl = if (isAuth) source.playbackUrl else "",
                    trailerUrl = if (source.trailerUrl.isNotBlank()) source.trailerUrl else m.trailerUrl,
                    audioLanguages = source.audioLanguages,
                    subtitleLanguages = source.subtitleLanguages,
                    availableQualities = source.availableQualities,
                    ottPlatform = source.ottPlatform,
                    streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
                    rightsNote = if (isAuth) source.rightsNote else "Available information only — streaming unavailable in this app."
                )
            )
        }
        val s = dao.getTvShowById(source.tmdbId.toString()).firstOrNull()
        if (s != null) {
            val isAuth = source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
            dao.updateTvShow(
                s.copy(
                    availability = source.availability,
                    isStreamingAuthorized = isAuth,
                    trailerUrl = if (source.trailerUrl.isNotBlank()) source.trailerUrl else s.trailerUrl,
                    ottPlatform = source.ottPlatform,
                    streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
                    rightsNote = if (isAuth) source.rightsNote else "Available information only — streaming unavailable in this app."
                )
            )
        }
    }

    suspend fun deleteAuthorizedPlaybackSource(tmdbId: Long) = withContext(Dispatchers.IO) {
        dao.deleteAuthorizedPlaybackSource(tmdbId)
        val m = dao.getMovieById(tmdbId.toString()).firstOrNull()
        if (m != null) {
            dao.updateMovie(
                m.copy(
                    availability = "UNAVAILABLE",
                    isStreamingAuthorized = false,
                    playbackUrl = "",
                    videoUrl = "",
                    rightsNote = "Available information only — streaming unavailable in this app."
                )
            )
        }
    }

    // --- Offline Cache Access ---
    fun getMovieById(id: String): Flow<Movie?> = dao.getMovieById(id)
    fun getTvShowById(id: String): Flow<TvShow?> = dao.getTvShowById(id)
    fun getEpisodeById(id: String): Flow<Episode?> = dao.getEpisodeById(id)

    suspend fun insertMovie(movie: Movie) = withContext(Dispatchers.IO) { dao.insertMovie(movie) }
    suspend fun updateMovie(movie: Movie) = withContext(Dispatchers.IO) { dao.updateMovie(movie) }
    suspend fun deleteMovie(id: String) = withContext(Dispatchers.IO) { dao.deleteMovie(id) }

    suspend fun insertTvShow(show: TvShow) = withContext(Dispatchers.IO) { dao.insertTvShow(show) }
    suspend fun updateTvShow(show: TvShow) = withContext(Dispatchers.IO) { dao.updateTvShow(show) }
    suspend fun deleteTvShow(id: String) = withContext(Dispatchers.IO) { dao.deleteTvShow(id) }

    suspend fun insertEpisode(episode: Episode) = withContext(Dispatchers.IO) { dao.insertEpisodes(listOf(episode)) }

    // --- Watch History & Continue Watching ---
    val continueWatching: Flow<List<WatchHistory>> = dao.getContinueWatching()
    val watchHistory: Flow<List<WatchHistory>> = dao.getWatchHistory()
    fun getWatchHistoryItem(id: String): Flow<WatchHistory?> = dao.getWatchHistoryItem(id)

    suspend fun updatePlaybackProgress(
        mediaType: String,
        contentId: String,
        episodeId: String? = null,
        title: String,
        subtitle: String,
        posterUrl: String,
        backdropUrl: String,
        positionSeconds: Long,
        durationSeconds: Long
    ) = withContext(Dispatchers.IO) {
        val pct = if (durationSeconds > 0) ((positionSeconds * 100) / durationSeconds).toInt() else 0
        val historyId = if (episodeId != null) "wh_${contentId}_$episodeId" else "wh_$contentId"

        dao.upsertWatchHistory(
            WatchHistory(
                id = historyId,
                mediaType = mediaType,
                contentId = contentId,
                episodeId = episodeId,
                title = title,
                subtitle = subtitle,
                posterUrl = posterUrl,
                backdropUrl = backdropUrl,
                positionSeconds = positionSeconds,
                durationSeconds = durationSeconds,
                progressPercent = pct.coerceIn(0, 100),
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeWatchHistory(id: String) = withContext(Dispatchers.IO) { dao.deleteWatchHistoryItem(id) }
    suspend fun clearWatchHistory() = withContext(Dispatchers.IO) { dao.clearWatchHistory() }

    // --- My List ---
    val myList: Flow<List<MyListItem>> = dao.getMyList()
    val myListMovies: Flow<List<MyListItem>> = dao.getMyListMovies()
    val myListShows: Flow<List<MyListItem>> = dao.getMyListShows()
    fun isInMyList(contentId: String): Flow<Boolean> = dao.isInMyList(contentId)

    suspend fun toggleMyList(
        contentId: String,
        mediaType: String,
        title: String,
        posterUrl: String,
        backdropUrl: String,
        genres: String,
        releaseYear: Int,
        rating: Float
    ) = withContext(Dispatchers.IO) {
        val exists = dao.isInMyList(contentId).firstOrNull() ?: false
        if (exists) {
            dao.deleteMyListItem(contentId)
        } else {
            dao.insertMyListItem(
                MyListItem(
                    contentId = contentId,
                    mediaType = mediaType,
                    title = title,
                    posterUrl = posterUrl,
                    backdropUrl = backdropUrl,
                    genres = genres,
                    releaseYear = releaseYear,
                    rating = rating
                )
            )
        }
    }

    // --- User Profile ---
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) { dao.upsertUserProfile(profile) }
}
