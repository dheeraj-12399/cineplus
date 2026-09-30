package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CineDao {

    // --- Movies ---
    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY rating DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFeatured = 1 AND isPublished = 1 LIMIT 1")
    fun getFeaturedMovie(): Flow<Movie?>

    @Query("SELECT * FROM movies WHERE isTrending = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getTrendingMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isPopular = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getPopularMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isRecentlyAdded = 1 AND isPublished = 1 ORDER BY createdAt DESC")
    fun getRecentlyAddedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE genres LIKE '%' || :genre || '%' AND isPublished = 1")
    fun getMoviesByGenre(genre: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE language = :language AND isPublished = 1 ORDER BY rating DESC")
    fun getMoviesByLanguage(language: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE (language = 'Telugu' OR isTeluguCinema = 1) AND isPublished = 1 ORDER BY releaseYear DESC, rating DESC")
    fun getTeluguMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE (language = 'Telugu' OR isTeluguCinema = 1) AND teluguCategory = :category AND isPublished = 1 ORDER BY rating DESC")
    fun getTeluguMoviesByCategory(category: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isOttContent = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getOttMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isOttContent = 1 AND ottCategory = :category AND isPublished = 1 ORDER BY rating DESC")
    fun getOttMoviesByCategory(category: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isOttContent = 1 AND ottPlatform = :platform AND isPublished = 1 ORDER BY rating DESC")
    fun getOttMoviesByPlatform(platform: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: String): Flow<Movie?>

    @Query("""
        SELECT * FROM movies 
        WHERE isPublished = 1 
          AND (title LIKE '%' || :query || '%' 
               OR `cast` LIKE '%' || :query || '%' 
               OR director LIKE '%' || :query || '%' 
               OR genres LIKE '%' || :query || '%')
        ORDER BY rating DESC
    """)
    fun searchMovies(query: String): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovie(id: String)

    // --- TV Shows ---
    @Query("SELECT * FROM tv_shows WHERE isPublished = 1 ORDER BY rating DESC")
    fun getAllTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isTrending = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getTrendingTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isPopular = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getPopularTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isRecentlyAdded = 1 AND isPublished = 1 ORDER BY createdAt DESC")
    fun getRecentlyAddedTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE genres LIKE '%' || :genre || '%' AND isPublished = 1")
    fun getTvShowsByGenre(genre: String): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE language = :language AND isPublished = 1 ORDER BY rating DESC")
    fun getTvShowsByLanguage(language: String): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE (language = 'Telugu' OR isTeluguSeries = 1) AND isPublished = 1 ORDER BY rating DESC")
    fun getTeluguTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isOttContent = 1 AND isPublished = 1 ORDER BY rating DESC")
    fun getOttTvShows(): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isOttContent = 1 AND ottCategory = :category AND isPublished = 1 ORDER BY rating DESC")
    fun getOttTvShowsByCategory(category: String): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE isOttContent = 1 AND ottPlatform = :platform AND isPublished = 1 ORDER BY rating DESC")
    fun getOttTvShowsByPlatform(platform: String): Flow<List<TvShow>>

    @Query("SELECT * FROM tv_shows WHERE id = :id")
    fun getTvShowById(id: String): Flow<TvShow?>

    @Query("""
        SELECT * FROM tv_shows 
        WHERE isPublished = 1 
          AND (title LIKE '%' || :query || '%' 
               OR `cast` LIKE '%' || :query || '%' 
               OR creator LIKE '%' || :query || '%' 
               OR genres LIKE '%' || :query || '%')
        ORDER BY rating DESC
    """)
    fun searchTvShows(query: String): Flow<List<TvShow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShows(shows: List<TvShow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(show: TvShow)

    @Update
    suspend fun updateTvShow(show: TvShow)

    @Query("DELETE FROM tv_shows WHERE id = :id")
    suspend fun deleteTvShow(id: String)

    // --- Seasons & Episodes ---
    @Query("SELECT * FROM tv_seasons WHERE showId = :showId ORDER BY seasonNumber ASC")
    fun getSeasonsForShow(showId: String): Flow<List<TvSeason>>

    @Query("SELECT * FROM episodes WHERE seasonId = :seasonId ORDER BY episodeNumber ASC")
    fun getEpisodesForSeason(seasonId: String): Flow<List<Episode>>

    @Query("SELECT * FROM episodes WHERE showId = :showId ORDER BY seasonNumber ASC, episodeNumber ASC")
    fun getEpisodesForShow(showId: String): Flow<List<Episode>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodeById(id: String): Flow<Episode?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeasons(seasons: List<TvSeason>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: TvSeason)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<Episode>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode)

    @Query("DELETE FROM episodes WHERE id = :id")
    suspend fun deleteEpisode(id: String)

    // --- Watch History & Continue Watching ---
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<WatchHistory>>

    @Query("SELECT * FROM watch_history WHERE progressPercent BETWEEN 1 AND 95 ORDER BY lastWatchedTimestamp DESC")
    fun getContinueWatching(): Flow<List<WatchHistory>>

    @Query("SELECT * FROM watch_history WHERE id = :id")
    fun getWatchHistoryItem(id: String): Flow<WatchHistory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchHistory(item: WatchHistory)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteWatchHistoryItem(id: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    // --- My List ---
    @Query("SELECT * FROM my_list ORDER BY addedTimestamp DESC")
    fun getMyList(): Flow<List<MyListItem>>

    @Query("SELECT * FROM my_list WHERE mediaType = 'MOVIE' ORDER BY addedTimestamp DESC")
    fun getMyListMovies(): Flow<List<MyListItem>>

    @Query("SELECT * FROM my_list WHERE mediaType = 'TV_SHOW' ORDER BY addedTimestamp DESC")
    fun getMyListShows(): Flow<List<MyListItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM my_list WHERE contentId = :contentId)")
    fun isInMyList(contentId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyListItem(item: MyListItem)

    @Query("DELETE FROM my_list WHERE contentId = :contentId")
    suspend fun deleteMyListItem(contentId: String)

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile)

    // --- Authorized Playback Sources (Admin Rights Mapping) ---
    @Query("SELECT * FROM authorized_playback_sources WHERE tmdbId = :tmdbId")
    fun getAuthorizedPlaybackSource(tmdbId: Long): Flow<AuthorizedPlaybackSource?>

    @Query("SELECT * FROM authorized_playback_sources WHERE tmdbId = :tmdbId")
    suspend fun getAuthorizedPlaybackSourceSync(tmdbId: Long): AuthorizedPlaybackSource?

    @Query("SELECT * FROM authorized_playback_sources ORDER BY updatedAt DESC")
    fun getAllAuthorizedPlaybackSources(): Flow<List<AuthorizedPlaybackSource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAuthorizedPlaybackSource(source: AuthorizedPlaybackSource)

    @Query("DELETE FROM authorized_playback_sources WHERE tmdbId = :tmdbId")
    suspend fun deleteAuthorizedPlaybackSource(tmdbId: Long)

    // --- Cached Media Metadata ---
    @Query("SELECT * FROM cached_media WHERE tmdbId = :tmdbId")
    fun getCachedMedia(tmdbId: Long): Flow<CachedMediaEntity?>

    @Query("SELECT * FROM cached_media WHERE tmdbId = :tmdbId")
    suspend fun getCachedMediaSync(tmdbId: Long): CachedMediaEntity?

    @Query("SELECT * FROM cached_media WHERE mediaType = :mediaType ORDER BY rating DESC")
    fun getCachedMediaByType(mediaType: String): Flow<List<CachedMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedMedia(item: CachedMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedMediaList(items: List<CachedMediaEntity>)

    @Query("DELETE FROM cached_media WHERE lastUpdated < :threshold")
    suspend fun pruneOldCache(threshold: Long)
}
