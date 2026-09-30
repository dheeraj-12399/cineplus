package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CineDatabase
import com.example.data.model.*
import com.example.data.remote.tmdb.TmdbMultiSearchItem
import com.example.data.repository.CineRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CineViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CineDatabase.getInstance(application)
    val repository = CineRepository(db.dao())

    // --- Loading & Error States ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _networkError = MutableStateFlow<String?>(null)
    val networkError: StateFlow<String?> = _networkError.asStateFlow()

    // --- Featured Movie for Hero Banner ---
    private val _featuredMovie = MutableStateFlow<Movie?>(null)
    val featuredMovie: StateFlow<Movie?> = _featuredMovie.asStateFlow()

    // --- Dynamic TMDB Movie Discovery Lists ---
    private val _trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val trendingMovies: StateFlow<List<Movie>> = _trendingMovies.asStateFlow()

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies.asStateFlow()

    private val _nowPlayingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val nowPlayingMovies: StateFlow<List<Movie>> = _nowPlayingMovies.asStateFlow()

    private val _upcomingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val upcomingMovies: StateFlow<List<Movie>> = _upcomingMovies.asStateFlow()

    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies.asStateFlow()

    // --- Telugu Cinema Streams ---
    private val _teluguTrending = MutableStateFlow<List<Movie>>(emptyList())
    val teluguTrending: StateFlow<List<Movie>> = _teluguTrending.asStateFlow()

    private val _newTeluguMovies = MutableStateFlow<List<Movie>>(emptyList())
    val newTeluguMovies: StateFlow<List<Movie>> = _newTeluguMovies.asStateFlow()

    private val _popularTeluguMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularTeluguMovies: StateFlow<List<Movie>> = _popularTeluguMovies.asStateFlow()

    private val _teluguWebSeries = MutableStateFlow<List<TvShow>>(emptyList())
    val teluguWebSeries: StateFlow<List<TvShow>> = _teluguWebSeries.asStateFlow()

    // Dynamic Category in Telugu Screen
    private val _teluguCategoryMovies = MutableStateFlow<List<Movie>>(emptyList())
    val teluguCategoryMovies: StateFlow<List<Movie>> = _teluguCategoryMovies.asStateFlow()

    // --- Regional Cinema Rows ---
    private val _hindiMovies = MutableStateFlow<List<Movie>>(emptyList())
    val hindiMovies: StateFlow<List<Movie>> = _hindiMovies.asStateFlow()

    private val _tamilMovies = MutableStateFlow<List<Movie>>(emptyList())
    val tamilMovies: StateFlow<List<Movie>> = _tamilMovies.asStateFlow()

    private val _malayalamMovies = MutableStateFlow<List<Movie>>(emptyList())
    val malayalamMovies: StateFlow<List<Movie>> = _malayalamMovies.asStateFlow()

    private val _kannadaMovies = MutableStateFlow<List<Movie>>(emptyList())
    val kannadaMovies: StateFlow<List<Movie>> = _kannadaMovies.asStateFlow()

    private val _englishMovies = MutableStateFlow<List<Movie>>(emptyList())
    val englishMovies: StateFlow<List<Movie>> = _englishMovies.asStateFlow()

    private val _bengaliMovies = MutableStateFlow<List<Movie>>(emptyList())
    val bengaliMovies: StateFlow<List<Movie>> = _bengaliMovies.asStateFlow()

    private val _marathiMovies = MutableStateFlow<List<Movie>>(emptyList())
    val marathiMovies: StateFlow<List<Movie>> = _marathiMovies.asStateFlow()

    private val _punjabiMovies = MutableStateFlow<List<Movie>>(emptyList())
    val punjabiMovies: StateFlow<List<Movie>> = _punjabiMovies.asStateFlow()

    // --- Scalable Paged Catalog Lists ---
    private val _pagedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val pagedMovies: StateFlow<List<Movie>> = _pagedMovies.asStateFlow()

    private val _pagedTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val pagedTvShows: StateFlow<List<TvShow>> = _pagedTvShows.asStateFlow()

    // --- TV Shows Discovery Lists ---
    private val _trendingTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val trendingTvShows: StateFlow<List<TvShow>> = _trendingTvShows.asStateFlow()

    private val _popularTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val popularTvShows: StateFlow<List<TvShow>> = _popularTvShows.asStateFlow()

    private val _airingTodayTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val airingTodayTvShows: StateFlow<List<TvShow>> = _airingTodayTvShows.asStateFlow()

    private val _onTheAirTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val onTheAirTvShows: StateFlow<List<TvShow>> = _onTheAirTvShows.asStateFlow()

    private val _topRatedTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    val topRatedTvShows: StateFlow<List<TvShow>> = _topRatedTvShows.asStateFlow()

    // --- Aggregated Lists for Compatibility ---
    val allMovies: StateFlow<List<Movie>> = combine(
        listOf(
            _trendingMovies,
            _popularMovies,
            _teluguTrending,
            _teluguCategoryMovies,
            _hindiMovies,
            _tamilMovies,
            _malayalamMovies,
            _kannadaMovies,
            _englishMovies,
            _bengaliMovies,
            _marathiMovies,
            _punjabiMovies,
            _pagedMovies
        )
    ) { arrays ->
        arrays.flatMap { it }.distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTvShows: StateFlow<List<TvShow>> = combine(
        listOf(
            _trendingTvShows,
            _popularTvShows,
            _teluguWebSeries,
            _airingTodayTvShows,
            _onTheAirTvShows,
            _topRatedTvShows,
            _pagedTvShows
        )
    ) { arrays ->
        arrays.flatMap { it }.distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teluguMovies: StateFlow<List<Movie>> = combine(
        _teluguTrending,
        _newTeluguMovies,
        _popularTeluguMovies,
        _teluguCategoryMovies
    ) { t1, t2, t3, t4 ->
        (t1 + t2 + t3 + t4).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ottMovies: StateFlow<List<Movie>> = combine(
        _popularMovies,
        _trendingMovies,
        _teluguTrending
    ) { m1, m2, m3 ->
        (m1 + m2 + m3).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ottTvShows: StateFlow<List<TvShow>> = combine(
        _popularTvShows,
        _trendingTvShows,
        _teluguWebSeries
    ) { t1, t2, t3 ->
        (t1 + t2 + t3).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyAddedMovies: StateFlow<List<Movie>> = _nowPlayingMovies
    val popularTeluguTvShows: StateFlow<List<TvShow>> = _teluguWebSeries

    // --- Continue Watching & Watch History ---
    val continueWatching: StateFlow<List<WatchHistory>> = repository.continueWatching
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory: StateFlow<List<WatchHistory>> = repository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- My List ---
    val myList: StateFlow<List<MyListItem>> = repository.myList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myListMovies: StateFlow<List<MyListItem>> = repository.myListMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myListShows: StateFlow<List<MyListItem>> = repository.myListShows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Admin CMS Authorized Playback Sources ---
    val allAuthorizedPlaybackSources: StateFlow<List<AuthorizedPlaybackSource>> =
        repository.allAuthorizedPlaybackSources
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilterType = MutableStateFlow("ALL") // "ALL", "MOVIES", "TV_SHOWS", "PERSON"
    val searchFilterType: StateFlow<String> = _searchFilterType.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("All")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    private val _selectedLanguageFilter = MutableStateFlow("All")
    val selectedLanguageFilter: StateFlow<String> = _selectedLanguageFilter.asStateFlow()

    private val _multiSearchResults = MutableStateFlow<List<TmdbMultiSearchItem>>(emptyList())
    val multiSearchResults: StateFlow<List<TmdbMultiSearchItem>> = _multiSearchResults.asStateFlow()

    private val _movieSearchResults = MutableStateFlow<List<Movie>>(emptyList())
    val movieSearchResults: StateFlow<List<Movie>> = _movieSearchResults.asStateFlow()

    private val _tvSearchResults = MutableStateFlow<List<TvShow>>(emptyList())
    val tvSearchResults: StateFlow<List<TvShow>> = _tvSearchResults.asStateFlow()

    private var searchJob: Job? = null

    // Pagination trackers
    private var teluguCategoryPage = 1
    private var currentTeluguCategory = "All Telugu"

    init {
        loadInitialCatalog()
    }

    fun loadInitialCatalog() {
        viewModelScope.launch {
            _isLoading.value = true
            _networkError.value = null

            // 1. Trending & Popular Movies
            repository.getTrendingMovies().onSuccess { list ->
                _trendingMovies.value = list
                if (_featuredMovie.value == null && list.isNotEmpty()) {
                    _featuredMovie.value = list.first()
                }
            }

            repository.getPopularMovies().onSuccess { list ->
                _popularMovies.value = list
            }

            repository.getNowPlayingMovies().onSuccess { list ->
                _nowPlayingMovies.value = list
            }

            repository.getUpcomingMovies().onSuccess { list ->
                _upcomingMovies.value = list
            }

            repository.getTopRatedMovies().onSuccess { list ->
                _topRatedMovies.value = list
            }

            // 2. Telugu Cinema discovery
            repository.discoverTeluguMovies("Popular Telugu Movies").onSuccess { list ->
                _teluguTrending.value = list
                // If featured is null or we want a standout Telugu hero, prioritize prominent title
                val salaar = list.find { it.title.contains("Salaar", true) }
                if (salaar != null) {
                    _featuredMovie.value = salaar
                }
            }

            repository.discoverTeluguMovies("New Telugu Releases").onSuccess { list ->
                _newTeluguMovies.value = list
            }

            repository.discoverTeluguMovies("Telugu Blockbusters").onSuccess { list ->
                _popularTeluguMovies.value = list
            }

            repository.discoverTvByLanguage("te").onSuccess { list ->
                _teluguWebSeries.value = list
            }

            // 3. Regional Languages
            repository.discoverMoviesByLanguage("hi").onSuccess { list -> _hindiMovies.value = list }
            repository.discoverMoviesByLanguage("ta").onSuccess { list -> _tamilMovies.value = list }
            repository.discoverMoviesByLanguage("ml").onSuccess { list -> _malayalamMovies.value = list }
            repository.discoverMoviesByLanguage("kn").onSuccess { list -> _kannadaMovies.value = list }
            repository.discoverMoviesByLanguage("en").onSuccess { list -> _englishMovies.value = list }
            repository.discoverMoviesByLanguage("bn").onSuccess { list -> _bengaliMovies.value = list }
            repository.discoverMoviesByLanguage("mr").onSuccess { list -> _marathiMovies.value = list }
            repository.discoverMoviesByLanguage("pa").onSuccess { list -> _punjabiMovies.value = list }

            // 4. TV Discovery
            repository.getTrendingTv().onSuccess { list -> _trendingTvShows.value = list }
            repository.getPopularTv().onSuccess { list -> _popularTvShows.value = list }
            repository.getAiringTodayTv().onSuccess { list -> _airingTodayTvShows.value = list }
            repository.getOnTheAirTv().onSuccess { list -> _onTheAirTvShows.value = list }
            repository.getTopRatedTv().onSuccess { list -> _topRatedTvShows.value = list }

            // Default Telugu Category & Default Paged Catalog
            loadTeluguCategory("All Telugu", resetPage = true)
            loadPagedMovies(reset = true)
            loadPagedTvShows(reset = true)

            _isLoading.value = false
        }
    }

    // --- Telugu Category Discovery with Pagination ---
    fun loadTeluguCategory(category: String, resetPage: Boolean = false) {
        if (resetPage) {
            teluguCategoryPage = 1
            currentTeluguCategory = category
            _teluguCategoryMovies.value = emptyList()
        }

        viewModelScope.launch {
            repository.discoverTeluguMovies(category, teluguCategoryPage).onSuccess { list ->
                if (resetPage) {
                    _teluguCategoryMovies.value = list
                } else {
                    _teluguCategoryMovies.value = (_teluguCategoryMovies.value + list).distinctBy { it.id }
                }
            }
        }
    }

    fun loadNextTeluguPage() {
        teluguCategoryPage++
        loadTeluguCategory(currentTeluguCategory, resetPage = false)
    }

    // --- Scalable Multi-Page Movies & TV Shows Discovery ---
    private var moviesBrowsePage = 1
    private var currentMovieLang = "All"
    private var currentMovieGenre = "All"

    fun loadPagedMovies(language: String = "All", genre: String = "All", reset: Boolean = false) {
        if (reset) {
            moviesBrowsePage = 1
            currentMovieLang = language
            currentMovieGenre = genre
            _pagedMovies.value = emptyList()
        }
        val langCode = com.example.data.remote.tmdb.TmdbLanguageHelper.getLanguageCode(language)
        val genreId = com.example.data.remote.tmdb.TmdbLanguageHelper.getGenreId(genre)
        viewModelScope.launch {
            repository.discoverMoviesGeneral(
                languageCode = langCode,
                genreId = genreId,
                page = moviesBrowsePage
            ).onSuccess { list ->
                if (reset) {
                    _pagedMovies.value = list
                } else {
                    _pagedMovies.value = (_pagedMovies.value + list).distinctBy { it.id }
                }
            }
        }
    }

    fun loadNextMoviesPage() {
        moviesBrowsePage++
        loadPagedMovies(currentMovieLang, currentMovieGenre, reset = false)
    }

    private var tvBrowsePage = 1
    private var currentTvLang = "All"
    private var currentTvGenre = "All"

    fun loadPagedTvShows(language: String = "All", genre: String = "All", reset: Boolean = false) {
        if (reset) {
            tvBrowsePage = 1
            currentTvLang = language
            currentTvGenre = genre
            _pagedTvShows.value = emptyList()
        }
        val langCode = com.example.data.remote.tmdb.TmdbLanguageHelper.getLanguageCode(language)
        val genreId = com.example.data.remote.tmdb.TmdbLanguageHelper.getGenreId(genre)
        viewModelScope.launch {
            repository.discoverTvGeneral(
                languageCode = langCode,
                genreId = genreId,
                page = tvBrowsePage
            ).onSuccess { list ->
                if (reset) {
                    _pagedTvShows.value = list
                } else {
                    _pagedTvShows.value = (_pagedTvShows.value + list).distinctBy { it.id }
                }
            }
        }
    }

    fun loadNextTvShowsPage() {
        tvBrowsePage++
        loadPagedTvShows(currentTvLang, currentTvGenre, reset = false)
    }

    // --- Real TMDB Search ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _multiSearchResults.value = emptyList()
            _movieSearchResults.value = emptyList()
            _tvSearchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _isLoading.value = true

            // Multi Search
            repository.searchMulti(query).onSuccess { list ->
                _multiSearchResults.value = list
            }

            // Movie Search
            repository.searchMovies(query).onSuccess { list ->
                _movieSearchResults.value = list
            }

            // TV Search
            repository.searchTv(query).onSuccess { list ->
                _tvSearchResults.value = list
            }

            _isLoading.value = false
        }
    }

    fun setSearchFilterType(type: String) {
        _searchFilterType.value = type
    }

    fun setSelectedGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
    }

    fun setSelectedLanguageFilter(language: String) {
        _selectedLanguageFilter.value = language
    }

    fun clearSearchFilters() {
        _searchQuery.value = ""
        _selectedGenreFilter.value = "All"
        _selectedLanguageFilter.value = "All"
        _searchFilterType.value = "ALL"
        _multiSearchResults.value = emptyList()
        _movieSearchResults.value = emptyList()
        _tvSearchResults.value = emptyList()
    }

    // --- Details Fetching ---
    suspend fun getMovieDetails(movieId: Long): Result<Movie> {
        return repository.getMovieDetails(movieId)
    }

    suspend fun getTvDetails(tvId: Long): Result<TvShow> {
        return repository.getTvDetails(tvId)
    }

    suspend fun getTvSeasonEpisodes(tvId: Long, seasonNumber: Int): Result<List<Episode>> {
        return repository.getTvSeasonEpisodes(tvId, seasonNumber)
    }

    // --- Admin CMS: Associate Authorized Source with Real TMDB ID ---
    fun saveAuthorizedPlaybackSource(source: AuthorizedPlaybackSource) {
        viewModelScope.launch {
            repository.saveAuthorizedPlaybackSource(source)
            // Refresh feeds to update availability
            loadInitialCatalog()
        }
    }

    fun deleteAuthorizedPlaybackSource(tmdbId: Long) {
        viewModelScope.launch {
            repository.deleteAuthorizedPlaybackSource(tmdbId)
            loadInitialCatalog()
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insertMovie(movie)
            val source = AuthorizedPlaybackSource(
                tmdbId = movie.id.toLongOrNull() ?: System.currentTimeMillis(),
                mediaType = "movie",
                title = movie.title,
                availability = movie.availability,
                playbackType = movie.playbackType,
                playbackUrl = movie.playbackUrl,
                trailerUrl = movie.trailerUrl,
                audioLanguages = movie.audioLanguages,
                subtitleLanguages = movie.subtitleLanguages,
                availableQualities = movie.availableQualities,
                ottPlatform = movie.ottPlatform,
                rightsNote = movie.rightsNote
            )
            repository.saveAuthorizedPlaybackSource(source)
            loadInitialCatalog()
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie)
            val source = AuthorizedPlaybackSource(
                tmdbId = movie.id.toLongOrNull() ?: 0L,
                mediaType = "movie",
                title = movie.title,
                availability = movie.availability,
                playbackType = movie.playbackType,
                playbackUrl = movie.playbackUrl,
                trailerUrl = movie.trailerUrl,
                audioLanguages = movie.audioLanguages,
                subtitleLanguages = movie.subtitleLanguages,
                availableQualities = movie.availableQualities,
                ottPlatform = movie.ottPlatform,
                rightsNote = movie.rightsNote
            )
            repository.saveAuthorizedPlaybackSource(source)
            loadInitialCatalog()
        }
    }

    fun deleteMovie(movieId: String) {
        viewModelScope.launch {
            repository.deleteMovie(movieId)
            val idLong = movieId.toLongOrNull()
            if (idLong != null) {
                repository.deleteAuthorizedPlaybackSource(idLong)
            }
            loadInitialCatalog()
        }
    }

    fun addTvShow(show: TvShow) {
        viewModelScope.launch {
            repository.insertTvShow(show)
            loadInitialCatalog()
        }
    }

    fun updateTvShow(show: TvShow) {
        viewModelScope.launch {
            repository.updateTvShow(show)
            val source = AuthorizedPlaybackSource(
                tmdbId = show.id.toLongOrNull() ?: 0L,
                mediaType = "tv",
                title = show.title,
                availability = show.availability,
                playbackType = "progressive",
                playbackUrl = "",
                trailerUrl = show.trailerUrl,
                audioLanguages = show.language,
                subtitleLanguages = "English",
                availableQualities = "1080p HD, 720p",
                ottPlatform = show.ottPlatform,
                rightsNote = show.rightsNote
            )
            repository.saveAuthorizedPlaybackSource(source)
            loadInitialCatalog()
        }
    }

    fun deleteTvShow(showId: String) {
        viewModelScope.launch {
            repository.deleteTvShow(showId)
            val idLong = showId.toLongOrNull()
            if (idLong != null) {
                repository.deleteAuthorizedPlaybackSource(idLong)
            }
            loadInitialCatalog()
        }
    }

    fun addEpisode(episode: Episode) {
        viewModelScope.launch {
            repository.insertEpisode(episode)
        }
    }

    // --- Playback Tracking ---
    fun updateProgress(
        mediaType: String,
        contentId: String,
        episodeId: String? = null,
        title: String,
        subtitle: String,
        posterUrl: String,
        backdropUrl: String,
        positionSeconds: Long,
        durationSeconds: Long
    ) {
        viewModelScope.launch {
            repository.updatePlaybackProgress(
                mediaType = mediaType,
                contentId = contentId,
                episodeId = episodeId,
                title = title,
                subtitle = subtitle,
                posterUrl = posterUrl,
                backdropUrl = backdropUrl,
                positionSeconds = positionSeconds,
                durationSeconds = durationSeconds
            )
        }
    }

    fun removeFromWatchHistory(historyId: String) {
        viewModelScope.launch {
            repository.removeWatchHistory(historyId)
        }
    }

    fun removeHistoryItem(id: String) = removeFromWatchHistory(id)

    fun clearAllWatchHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
        }
    }

    fun clearAllHistory() = clearAllWatchHistory()

    // --- My List ---
    fun toggleMyListMovie(movie: Movie) {
        viewModelScope.launch {
            repository.toggleMyList(
                contentId = movie.id,
                mediaType = "MOVIE",
                title = movie.title,
                posterUrl = movie.posterUrl,
                backdropUrl = movie.backdropUrl,
                genres = movie.genres,
                releaseYear = movie.releaseYear,
                rating = movie.rating
            )
        }
    }

    fun toggleMyListShow(show: TvShow) {
        viewModelScope.launch {
            repository.toggleMyList(
                contentId = show.id,
                mediaType = "TV_SHOW",
                title = show.title,
                posterUrl = show.posterUrl,
                backdropUrl = show.backdropUrl,
                genres = show.genres,
                releaseYear = show.releaseYear,
                rating = show.rating
            )
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }
}
