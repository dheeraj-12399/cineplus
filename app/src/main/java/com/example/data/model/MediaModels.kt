package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.tmdb.*

@Entity(tableName = "authorized_playback_sources")
data class AuthorizedPlaybackSource(
    @PrimaryKey val tmdbId: Long,
    val mediaType: String = "movie", // "movie" or "tv"
    val title: String,
    val availability: String = "AVAILABLE", // "AVAILABLE", "UNAVAILABLE", "COMING_SOON"
    val playbackType: String = "progressive", // "progressive", "hls", "dash"
    val playbackUrl: String = "",
    val trailerUrl: String = "",
    val audioLanguages: String = "Telugu (Original), Hindi, Tamil, English",
    val subtitleLanguages: String = "English, Telugu, Hindi",
    val availableQualities: String = "Auto, 1080p HD, 720p, 480p",
    val ottPlatform: String = "CinePulse License",
    val rightsNote: String = "Official streaming rights authorized for in-app playback.",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_media")
data class CachedMediaEntity(
    @PrimaryKey val tmdbId: Long,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val originalTitle: String? = null,
    val overview: String = "",
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String = "",
    val releaseYear: Int = 2024,
    val language: String = "en",
    val genres: String = "",
    val runtime: Int = 0,
    val rating: Float = 0f,
    val voteCount: Int = 0,
    val cast: String = "",
    val crew: String = "",
    val trailerYoutubeKey: String? = null,
    val watchProvidersFlatrate: String? = null,
    val watchProvidersRent: String? = null,
    val watchProvidersBuy: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: String, // tmdbId.toString()
    val title: String,
    val overview: String,
    val backdropUrl: String,
    val posterUrl: String,
    val trailerUrl: String = "",
    val videoUrl: String = "",
    val playbackUrl: String = "",
    val playbackType: String = "progressive",
    val availability: String = "UNAVAILABLE", // Real default: UNAVAILABLE until authorized
    val availableQualities: String = "Auto, 1080p HD, 720p, 480p",
    val releaseDate: String = "2024",
    val releaseYear: Int = 2024,
    val durationMinutes: Int = 135,
    val rating: Float = 7.5f,
    val voteCount: Int = 0,
    val originalTitle: String = "",
    val genres: String = "Cinema",
    val language: String = "Telugu",
    val director: String = "",
    val cast: String = "",
    val audioLanguages: String = "Telugu (Original), Hindi, Tamil, English",
    val subtitleLanguages: String = "English, Telugu",
    val ottPlatform: String = "Theatrical",
    val isStreamingAuthorized: Boolean = false,
    val streamingStatus: String = "THEATRICAL_CATALOG_ONLY",
    val rightsNote: String = "Available information only — streaming unavailable in this app.",
    val isTeluguCinema: Boolean = false,
    val teluguCategory: String = "Popular Telugu Movies",
    val isOttContent: Boolean = false,
    val ottCategory: String = "Telugu OTT",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isRecentlyAdded: Boolean = false,
    val isPublished: Boolean = true,
    val watchProvidersFlatrate: String = "",
    val watchProvidersRent: String = "",
    val watchProvidersBuy: String = "",
    val youtubeTrailerKey: String? = null,
    val tmdbId: Long = id.toLongOrNull() ?: 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tv_shows")
data class TvShow(
    @PrimaryKey val id: String,
    val title: String,
    val overview: String,
    val backdropUrl: String,
    val posterUrl: String,
    val trailerUrl: String = "",
    val availability: String = "UNAVAILABLE",
    val releaseDate: String = "2024",
    val releaseYear: Int = 2024,
    val totalSeasons: Int = 1,
    val rating: Float = 8.0f,
    val voteCount: Int = 0,
    val originalTitle: String = "",
    val genres: String = "Drama",
    val language: String = "Telugu",
    val creator: String = "",
    val cast: String = "",
    val audioLanguages: String = "Telugu (Original), Hindi, Tamil, English",
    val subtitleLanguages: String = "English, Telugu",
    val ottPlatform: String = "OTT",
    val isStreamingAuthorized: Boolean = false,
    val streamingStatus: String = "THEATRICAL_CATALOG_ONLY",
    val rightsNote: String = "Available information only — streaming unavailable in this app.",
    val isTeluguSeries: Boolean = false,
    val isOttContent: Boolean = true,
    val ottCategory: String = "Telugu OTT",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isRecentlyAdded: Boolean = false,
    val isPublished: Boolean = true,
    val watchProvidersFlatrate: String = "",
    val watchProvidersRent: String = "",
    val watchProvidersBuy: String = "",
    val youtubeTrailerKey: String? = null,
    val tmdbId: Long = id.toLongOrNull() ?: 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tv_seasons")
data class TvSeason(
    @PrimaryKey val id: String,
    val showId: String,
    val seasonNumber: Int,
    val title: String,
    val episodeCount: Int
)

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey val id: String,
    val showId: String,
    val seasonId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val title: String,
    val overview: String,
    val durationMinutes: Int,
    val thumbnailUrl: String,
    val videoUrl: String = "",
    val playbackUrl: String = "",
    val playbackType: String = "progressive",
    val availability: String = "UNAVAILABLE"
)

@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey val id: String,
    val mediaType: String,
    val contentId: String,
    val episodeId: String? = null,
    val title: String,
    val subtitle: String,
    val posterUrl: String,
    val backdropUrl: String,
    val positionSeconds: Long = 0,
    val durationSeconds: Long = 0,
    val progressPercent: Int = 0,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "my_list")
data class MyListItem(
    @PrimaryKey val contentId: String,
    val mediaType: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val genres: String,
    val releaseYear: Int,
    val rating: Float,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String = "default_user",
    val name: String = "Alex",
    val avatarEmoji: String = "🎬",
    val preferredLanguage: String = "Telugu",
    val streamingQuality: String = "1080p (Full HD)",
    val autoplayNext: Boolean = true
)

// --- TMDB Model Converters ---

fun TmdbMovie.toMovie(source: AuthorizedPlaybackSource? = null): Movie {
    val langName = TmdbLanguageHelper.getLanguageName(originalLanguage)
    val genreStr = TmdbLanguageHelper.mapGenreIdsToString(genreIds)
    val rYear = releaseDate?.take(4)?.toIntOrNull() ?: 2024
    val isTe = originalLanguage == "te"

    val isAuth = source != null && source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
    val avail = source?.availability ?: "UNAVAILABLE"
    val pUrl = if (isAuth) source!!.playbackUrl else ""

    return Movie(
        id = id.toString(),
        title = title,
        originalTitle = originalTitle ?: title,
        overview = overview?.ifBlank { "Official TMDB movie synopsis." } ?: "Official TMDB movie synopsis.",
        backdropUrl = TmdbClient.backdropUrl(backdropPath),
        posterUrl = TmdbClient.posterUrl(posterPath),
        trailerUrl = source?.trailerUrl ?: "",
        videoUrl = pUrl,
        playbackUrl = pUrl,
        playbackType = source?.playbackType ?: "progressive",
        availability = avail,
        availableQualities = source?.availableQualities ?: "Auto, 1080p HD, 720p, 480p",
        releaseDate = releaseDate ?: "$rYear",
        releaseYear = rYear,
        durationMinutes = 135,
        rating = ((voteAverage ?: 0.0) * 10).toInt() / 10f,
        voteCount = voteCount ?: 0,
        genres = genreStr,
        language = langName,
        director = "",
        cast = "",
        audioLanguages = source?.audioLanguages ?: "$langName (Original)",
        subtitleLanguages = source?.subtitleLanguages ?: "English, $langName",
        ottPlatform = source?.ottPlatform ?: "Theatrical / Digital",
        isStreamingAuthorized = isAuth,
        streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
        rightsNote = if (isAuth) (source?.rightsNote ?: "Authorized in-app streaming.") else "Available information only — streaming unavailable in this app.",
        isTeluguCinema = isTe,
        teluguCategory = when {
            rYear >= 2024 -> "New Telugu Releases"
            (voteAverage ?: 0.0) >= 8.5 -> "Telugu Blockbusters"
            else -> "Popular Telugu Movies"
        },
        isOttContent = true,
        ottCategory = if (isTe) "Telugu OTT" else "Popular OTT Movies",
        tmdbId = id
    )
}

fun TmdbMovieDetails.toMovie(source: AuthorizedPlaybackSource? = null): Movie {
    val langName = TmdbLanguageHelper.getLanguageName(originalLanguage)
    val genreStr = genres?.joinToString(", ") { it.name }?.ifBlank { "Cinema" } ?: "Cinema"
    val rYear = releaseDate?.take(4)?.toIntOrNull() ?: 2024
    val isTe = originalLanguage == "te"

    val dirName = credits?.crew?.firstOrNull { it.job.equals("Director", ignoreCase = true) }?.name ?: ""
    val castStr = credits?.cast?.take(6)?.joinToString(", ") { it.name } ?: ""

    val indiaProviders = watchProviders?.results?.get("IN")
    val flatrateNames = indiaProviders?.flatrate?.joinToString(", ") { it.providerName } ?: ""
    val rentNames = indiaProviders?.rent?.joinToString(", ") { it.providerName } ?: ""
    val buyNames = indiaProviders?.buy?.joinToString(", ") { it.providerName } ?: ""

    val trailerKey = videos?.results?.firstOrNull {
        it.site.equals("YouTube", ignoreCase = true) && (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true))
    }?.key

    val isAuth = source != null && source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
    val avail = source?.availability ?: "UNAVAILABLE"
    val pUrl = if (isAuth) source!!.playbackUrl else ""
    val tUrl = if (source?.trailerUrl?.isNotBlank() == true) source.trailerUrl else TmdbClient.youtubeTrailerUrl(trailerKey)

    return Movie(
        id = id.toString(),
        title = title,
        originalTitle = originalTitle ?: title,
        overview = overview?.ifBlank { tagline ?: "Official TMDB movie synopsis." } ?: (tagline ?: "Official TMDB movie synopsis."),
        backdropUrl = TmdbClient.backdropUrl(backdropPath),
        posterUrl = TmdbClient.posterUrl(posterPath),
        trailerUrl = tUrl,
        videoUrl = pUrl,
        playbackUrl = pUrl,
        playbackType = source?.playbackType ?: "progressive",
        availability = avail,
        availableQualities = source?.availableQualities ?: "Auto, 1080p HD, 720p, 480p",
        releaseDate = releaseDate ?: "$rYear",
        releaseYear = rYear,
        durationMinutes = runtime ?: 135,
        rating = ((voteAverage ?: 0.0) * 10).toInt() / 10f,
        voteCount = voteCount ?: 0,
        genres = genreStr,
        language = langName,
        director = dirName,
        cast = castStr,
        audioLanguages = source?.audioLanguages ?: "$langName (Original), Hindi, Tamil, English",
        subtitleLanguages = source?.subtitleLanguages ?: "English, $langName",
        ottPlatform = if (source != null && source.ottPlatform.isNotBlank()) source.ottPlatform else flatrateNames.ifBlank { "Theatrical" },
        isStreamingAuthorized = isAuth,
        streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
        rightsNote = if (isAuth) (source?.rightsNote ?: "Authorized in-app stream.") else "Available information only — streaming unavailable in this app.",
        isTeluguCinema = isTe,
        teluguCategory = when {
            rYear >= 2024 -> "New Telugu Releases"
            (voteAverage ?: 0.0) >= 8.5 -> "Telugu Blockbusters"
            else -> "Popular Telugu Movies"
        },
        watchProvidersFlatrate = flatrateNames,
        watchProvidersRent = rentNames,
        watchProvidersBuy = buyNames,
        youtubeTrailerKey = trailerKey,
        tmdbId = id
    )
}

fun TmdbTvShow.toTvShow(source: AuthorizedPlaybackSource? = null): TvShow {
    val langName = TmdbLanguageHelper.getLanguageName(originalLanguage)
    val genreStr = TmdbLanguageHelper.mapGenreIdsToString(genreIds)
    val rYear = firstAirDate?.take(4)?.toIntOrNull() ?: 2024
    val isTe = originalLanguage == "te"

    val isAuth = source != null && source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
    val avail = source?.availability ?: "UNAVAILABLE"

    return TvShow(
        id = id.toString(),
        title = name,
        originalTitle = originalName ?: name,
        overview = overview?.ifBlank { "Official TMDB television series." } ?: "Official TMDB television series.",
        backdropUrl = TmdbClient.backdropUrl(backdropPath),
        posterUrl = TmdbClient.posterUrl(posterPath),
        trailerUrl = source?.trailerUrl ?: "",
        availability = avail,
        releaseDate = firstAirDate ?: "$rYear",
        releaseYear = rYear,
        totalSeasons = 1,
        rating = ((voteAverage ?: 0.0) * 10).toInt() / 10f,
        voteCount = voteCount ?: 0,
        genres = genreStr,
        language = langName,
        creator = "",
        cast = "",
        ottPlatform = source?.ottPlatform ?: "OTT Network",
        isStreamingAuthorized = isAuth,
        streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
        rightsNote = if (isAuth) (source?.rightsNote ?: "Authorized in-app stream.") else "Available information only — streaming unavailable in this app.",
        isTeluguSeries = isTe,
        isOttContent = true,
        ottCategory = if (isTe) "Telugu OTT" else "Popular OTT Series",
        tmdbId = id
    )
}

fun TmdbTvDetails.toTvShow(source: AuthorizedPlaybackSource? = null): TvShow {
    val langName = TmdbLanguageHelper.getLanguageName(originalLanguage)
    val genreStr = genres?.joinToString(", ") { it.name }?.ifBlank { "Series" } ?: "Series"
    val rYear = firstAirDate?.take(4)?.toIntOrNull() ?: 2024
    val isTe = originalLanguage == "te"

    val creatorName = createdBy?.firstOrNull()?.name ?: ""
    val castStr = credits?.cast?.take(6)?.joinToString(", ") { it.name } ?: ""

    val indiaProviders = watchProviders?.results?.get("IN")
    val flatrateNames = indiaProviders?.flatrate?.joinToString(", ") { it.providerName } ?: ""
    val rentNames = indiaProviders?.rent?.joinToString(", ") { it.providerName } ?: ""
    val buyNames = indiaProviders?.buy?.joinToString(", ") { it.providerName } ?: ""

    val trailerKey = videos?.results?.firstOrNull {
        it.site.equals("YouTube", ignoreCase = true) && (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true))
    }?.key

    val isAuth = source != null && source.availability == "AVAILABLE" && source.playbackUrl.isNotBlank()
    val avail = source?.availability ?: "UNAVAILABLE"
    val tUrl = if (source?.trailerUrl?.isNotBlank() == true) source.trailerUrl else TmdbClient.youtubeTrailerUrl(trailerKey)

    return TvShow(
        id = id.toString(),
        title = name,
        originalTitle = originalName ?: name,
        overview = overview?.ifBlank { tagline ?: "Official TMDB television series." } ?: (tagline ?: "Official TMDB television series."),
        backdropUrl = TmdbClient.backdropUrl(backdropPath),
        posterUrl = TmdbClient.posterUrl(posterPath),
        trailerUrl = tUrl,
        availability = avail,
        releaseDate = firstAirDate ?: "$rYear",
        releaseYear = rYear,
        totalSeasons = numberOfSeasons ?: (seasons?.size ?: 1),
        rating = ((voteAverage ?: 0.0) * 10).toInt() / 10f,
        voteCount = voteCount ?: 0,
        genres = genreStr,
        language = langName,
        creator = creatorName,
        cast = castStr,
        ottPlatform = if (source != null && source.ottPlatform.isNotBlank()) source.ottPlatform else flatrateNames.ifBlank { "OTT Streaming" },
        isStreamingAuthorized = isAuth,
        streamingStatus = if (isAuth) "AUTHORIZED_IN_APP" else "THEATRICAL_CATALOG_ONLY",
        rightsNote = if (isAuth) (source?.rightsNote ?: "Authorized in-app stream.") else "Available information only — streaming unavailable in this app.",
        isTeluguSeries = isTe,
        isOttContent = true,
        ottCategory = if (isTe) "Telugu OTT" else "Popular OTT Series",
        watchProvidersFlatrate = flatrateNames,
        watchProvidersRent = rentNames,
        watchProvidersBuy = buyNames,
        youtubeTrailerKey = trailerKey,
        tmdbId = id
    )
}
