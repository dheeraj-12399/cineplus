package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun HomeScreen(
    viewModel: CineViewModel,
    onNavigateToMovie: (String) -> Unit,
    onNavigateToShow: (String) -> Unit,
    onPlayMovie: (String) -> Unit,
    onPlayEpisode: (String, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredMovie by viewModel.featuredMovie.collectAsStateWithLifecycle()
    val trendingMovies by viewModel.trendingMovies.collectAsStateWithLifecycle()
    val popularMovies by viewModel.popularMovies.collectAsStateWithLifecycle()
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsStateWithLifecycle()
    val upcomingMovies by viewModel.upcomingMovies.collectAsStateWithLifecycle()
    val topRatedMovies by viewModel.topRatedMovies.collectAsStateWithLifecycle()

    val teluguTrending by viewModel.teluguTrending.collectAsStateWithLifecycle()
    val newTeluguMovies by viewModel.newTeluguMovies.collectAsStateWithLifecycle()
    val popularTeluguMovies by viewModel.popularTeluguMovies.collectAsStateWithLifecycle()
    val teluguWebSeries by viewModel.teluguWebSeries.collectAsStateWithLifecycle()

    val hindiMovies by viewModel.hindiMovies.collectAsStateWithLifecycle()
    val tamilMovies by viewModel.tamilMovies.collectAsStateWithLifecycle()
    val malayalamMovies by viewModel.malayalamMovies.collectAsStateWithLifecycle()
    val kannadaMovies by viewModel.kannadaMovies.collectAsStateWithLifecycle()
    val englishMovies by viewModel.englishMovies.collectAsStateWithLifecycle()
    val bengaliMovies by viewModel.bengaliMovies.collectAsStateWithLifecycle()
    val marathiMovies by viewModel.marathiMovies.collectAsStateWithLifecycle()
    val punjabiMovies by viewModel.punjabiMovies.collectAsStateWithLifecycle()

    val popularTvShows by viewModel.popularTvShows.collectAsStateWithLifecycle()
    val topRatedTvShows by viewModel.topRatedTvShows.collectAsStateWithLifecycle()

    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()
    val myList by viewModel.myList.collectAsStateWithLifecycle()

    val featuredInList = remember(myList, featuredMovie) {
        featuredMovie?.let { f -> myList.any { it.contentId == f.id } } ?: false
    }

    Scaffold(
        topBar = {
            CineTopBar(
                onSearchClick = onNavigateToSearch,
                onAdminClick = onNavigateToAdmin,
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("cine_home_scroll_column")
        ) {
            // 1. Featured Cinematic Hero Banner (Real TMDB Data)
            featuredMovie?.let { hero ->
                item {
                    CineHeroBanner(
                        title = hero.title,
                        overview = hero.overview,
                        backdropUrl = hero.backdropUrl.ifBlank { hero.posterUrl },
                        metadataLine = "${hero.releaseYear} • ${hero.genres} • ${hero.language}",
                        rating = hero.rating,
                        isInList = featuredInList,
                        isAvailable = hero.availability == "AVAILABLE",
                        hasTrailer = hero.trailerUrl.isNotBlank(),
                        onPlayClick = {
                            if (hero.availability == "AVAILABLE" || hero.trailerUrl.isNotBlank()) {
                                onPlayMovie(hero.id)
                            } else {
                                onNavigateToMovie(hero.id)
                            }
                        },
                        onMyListClick = { viewModel.toggleMyListMovie(hero) },
                        onMoreInfoClick = { onNavigateToMovie(hero.id) }
                    )
                }
            }

            // 2. Continue Watching (Actual playback history)
            if (continueWatching.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Continue Watching")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(continueWatching, key = { it.id }) { item ->
                            ContinueWatchingCard(
                                item = item,
                                onPlayClick = {
                                    if (item.mediaType == "TV_SHOW" && item.episodeId != null) {
                                        onPlayEpisode(item.contentId, item.episodeId)
                                    } else {
                                        onPlayMovie(item.contentId)
                                    }
                                },
                                onInfoClick = {
                                    if (item.mediaType == "TV_SHOW") {
                                        onNavigateToShow(item.contentId)
                                    } else {
                                        onNavigateToMovie(item.contentId)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 3. 🔥 Telugu Trending
            if (teluguTrending.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "🔥 Telugu Trending")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(teluguTrending, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 4. 🎬 New Telugu Movies
            if (newTeluguMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "🎬 New Telugu Movies")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(newTeluguMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 5. ⭐ Popular Telugu Movies
            if (popularTeluguMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "⭐ Popular Telugu Movies")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(popularTeluguMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 6. 📺 Telugu OTT
            if (teluguTrending.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "📺 Telugu OTT")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(teluguTrending.reversed(), key = { "ott_${it.id}" }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 7. 🔥 New OTT Releases
            if (popularMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "🔥 New OTT Releases")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(popularMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 8. 🎭 Telugu Web Series
            if (teluguWebSeries.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "🎭 Telugu Web Series")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(teluguWebSeries, key = { it.id }) { show ->
                            TvShowPosterCard(show = show, onClick = { onNavigateToShow(show.id) })
                        }
                    }
                }
            }

            // 9. 🎞️ Recently Added (Now Playing in Theatres)
            if (nowPlayingMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "🎞️ Recently Added")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(nowPlayingMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 10. Trending Movies Worldwide
            if (trendingMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Trending Movies Worldwide 🔥")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(trendingMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 11. Top Rated Movies
            if (topRatedMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Top Rated Movies 🌟")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(topRatedMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 12. Upcoming Movies
            if (upcomingMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Upcoming Movies 📅")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(upcomingMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 13. Hindi Movies
            if (hindiMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Hindi Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(hindiMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 14. Tamil Movies
            if (tamilMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Tamil Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(tamilMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 15. Malayalam Movies
            if (malayalamMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Malayalam Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(malayalamMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 16. Kannada Movies
            if (kannadaMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Kannada Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(kannadaMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 17. English Movies
            if (englishMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "English Movies 🌐")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(englishMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 18. Bengali Movies
            if (bengaliMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Bengali Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(bengaliMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 19. Marathi Movies
            if (marathiMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Marathi Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(marathiMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 20. Punjabi Movies
            if (punjabiMovies.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Punjabi Cinema 🇮🇳")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(punjabiMovies, key = { it.id }) { movie ->
                            MoviePosterCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                        }
                    }
                }
            }

            // 21. Popular TV Shows
            if (popularTvShows.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Popular TV Shows 📺")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(popularTvShows, key = { it.id }) { show ->
                            TvShowPosterCard(show = show, onClick = { onNavigateToShow(show.id) })
                        }
                    }
                }
            }

            // 20. Top Rated TV Shows
            if (topRatedTvShows.isNotEmpty()) {
                item {
                    CineSectionHeader(title = "Top Rated TV Series 🏆")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        items(topRatedTvShows, key = { it.id }) { show ->
                            TvShowPosterCard(show = show, onClick = { onNavigateToShow(show.id) })
                        }
                    }
                }
            }
        }
    }
}
