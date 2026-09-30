package com.example.ui.screens.ott

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Movie
import com.example.data.model.TvShow
import com.example.ui.components.CineEmptyState
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun OttScreen(
    viewModel: CineViewModel,
    onNavigateToMovie: (String) -> Unit,
    onNavigateToShow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val ottMovies by viewModel.ottMovies.collectAsStateWithLifecycle()
    val ottTvShows by viewModel.ottTvShows.collectAsStateWithLifecycle()

    val categories = listOf(
        "All OTT",
        "Telugu OTT",
        "Hindi OTT",
        "Tamil OTT",
        "Malayalam OTT",
        "Kannada OTT",
        "English OTT",
        "New OTT Releases",
        "Popular OTT Movies",
        "Popular OTT Series"
    )

    val platforms = listOf(
        "All Platforms",
        "Aha Video",
        "Netflix",
        "Prime Video",
        "Disney+ Hotstar",
        "ZEE5",
        "CinePulse Original"
    )

    var selectedCategory by remember { mutableStateOf("All OTT") }
    var selectedPlatform by remember { mutableStateOf("All Platforms") }
    var contentTypeFilter by remember { mutableStateOf("ALL") } // "ALL", "MOVIES", "SERIES"

    // Filter movies
    val filteredMovies = remember(ottMovies, selectedCategory, selectedPlatform, contentTypeFilter) {
        if (contentTypeFilter == "SERIES") return@remember emptyList<Movie>()

        var list = ottMovies

        if (selectedCategory != "All OTT") {
            list = when (selectedCategory) {
                "Telugu OTT" -> list.filter { it.language.equals("Telugu", ignoreCase = true) || it.isTeluguCinema }
                "Hindi OTT" -> list.filter { it.language.equals("Hindi", ignoreCase = true) }
                "Tamil OTT" -> list.filter { it.language.equals("Tamil", ignoreCase = true) }
                "Malayalam OTT" -> list.filter { it.language.equals("Malayalam", ignoreCase = true) }
                "Kannada OTT" -> list.filter { it.language.equals("Kannada", ignoreCase = true) }
                "English OTT" -> list.filter { it.language.equals("English", ignoreCase = true) }
                "New OTT Releases" -> list.filter { it.isRecentlyAdded || it.releaseYear >= 2024 }
                "Popular OTT Movies" -> list.filter { it.isPopular || it.rating >= 8.6f }
                "Popular OTT Series" -> emptyList() // Movies excluded for series category
                else -> list.filter { it.ottCategory.equals(selectedCategory, ignoreCase = true) }
            }
        }

        if (selectedPlatform != "All Platforms") {
            list = list.filter { it.ottPlatform.contains(selectedPlatform, ignoreCase = true) }
        }

        list
    }

    // Filter shows
    val filteredShows = remember(ottTvShows, selectedCategory, selectedPlatform, contentTypeFilter) {
        if (contentTypeFilter == "MOVIES") return@remember emptyList<TvShow>()

        var list = ottTvShows

        if (selectedCategory != "All OTT") {
            list = when (selectedCategory) {
                "Telugu OTT" -> list.filter { it.language.equals("Telugu", ignoreCase = true) || it.isTeluguSeries }
                "Hindi OTT" -> list.filter { it.language.equals("Hindi", ignoreCase = true) }
                "Tamil OTT" -> list.filter { it.language.equals("Tamil", ignoreCase = true) }
                "Malayalam OTT" -> list.filter { it.language.equals("Malayalam", ignoreCase = true) }
                "Kannada OTT" -> list.filter { it.language.equals("Kannada", ignoreCase = true) }
                "English OTT" -> list.filter { it.language.equals("English", ignoreCase = true) }
                "New OTT Releases" -> list.filter { it.isRecentlyAdded || it.releaseYear >= 2024 }
                "Popular OTT Movies" -> emptyList() // Shows excluded for movies category
                "Popular OTT Series" -> list.filter { it.isPopular || it.rating >= 8.8f }
                else -> list.filter { it.ottCategory.equals(selectedCategory, ignoreCase = true) }
            }
        }

        if (selectedPlatform != "All Platforms") {
            list = list.filter { it.ottPlatform.contains(selectedPlatform, ignoreCase = true) }
        }

        list
    }

    val totalCount = filteredMovies.size + filteredShows.size

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CineBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "OTT Hub",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = CineCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CineCyan)
                            ) {
                                Text(
                                    text = "AUTHORIZED",
                                    color = CineCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Legally licensed streaming movies and web series",
                            style = MaterialTheme.typography.bodySmall,
                            color = CineTextSecondary
                        )
                    }

                    // Content Type Segmented Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CineSurfaceVariant)
                            .padding(2.dp)
                    ) {
                        listOf("ALL", "MOVIES", "SERIES").forEach { type ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (contentTypeFilter == type) CineRed else Color.Transparent)
                                    .clickable { contentTypeFilter = type }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (type) {
                                        "ALL" -> "All"
                                        "MOVIES" -> "Movies"
                                        else -> "Series"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (contentTypeFilter == type) Color.White else CineTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("ott_screen_column")
        ) {
            // Category Chips Row
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineRed,
                                selectedLabelColor = Color.White,
                                containerColor = CineSurface,
                                labelColor = CineTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == cat,
                                borderColor = if (selectedCategory == cat) CineRed else CineCardBorder
                            )
                        )
                    }
                }
            }

            // Platform Filter Row
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(platforms) { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform,
                            onClick = { selectedPlatform = platform },
                            label = { Text(platform, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when {
                                        platform.contains("Aha") -> Icons.Filled.LiveTv
                                        platform.contains("Netflix") -> Icons.Filled.Movie
                                        platform.contains("Prime") -> Icons.Filled.ShoppingBag
                                        platform.contains("Hotstar") -> Icons.Filled.Star
                                        else -> Icons.Filled.Tv
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineCyan,
                                selectedLabelColor = Color.Black,
                                selectedLeadingIconColor = Color.Black,
                                containerColor = CineSurfaceVariant,
                                labelColor = CineTextSecondary,
                                iconColor = CineTextSecondary
                            )
                        )
                    }
                }
            }

            // Header summary
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCategory ($totalCount)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = if (selectedPlatform == "All Platforms") "All Sources" else selectedPlatform,
                        style = MaterialTheme.typography.labelSmall,
                        color = CineCyan
                    )
                }
            }

            // Empty state
            if (totalCount == 0) {
                item {
                    CineEmptyState(
                        icon = Icons.Filled.Tv,
                        title = "No OTT Content Found",
                        message = "No titles match the selected category '$selectedCategory' on '$selectedPlatform'. Try resetting filters."
                    )
                }
            }

            // OTT Web Series Section (if any)
            if (filteredShows.isNotEmpty()) {
                item {
                    Text(
                        text = "Web Series & Shows (${filteredShows.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                val showChunks = filteredShows.chunked(3)
                items(showChunks) { rowShows ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowShows.forEach { show ->
                            Box(modifier = Modifier.weight(1f)) {
                                OttShowCard(show = show, onClick = { onNavigateToShow(show.id) })
                            }
                        }
                        if (rowShows.size < 3) {
                            repeat(3 - rowShows.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // OTT Movies Section (if any)
            if (filteredMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "OTT Movies (${filteredMovies.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                val movieChunks = filteredMovies.chunked(3)
                items(movieChunks) { rowMovies ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowMovies.forEach { movie ->
                            Box(modifier = Modifier.weight(1f)) {
                                OttMovieCard(movie = movie, onClick = { onNavigateToMovie(movie.id) })
                            }
                        }
                        if (rowMovies.size < 3) {
                            repeat(3 - rowMovies.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun OttMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("ott_movie_${movie.id}"),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Platform tag at bottom
                Surface(
                    color = Color(0xEE0B0F19),
                    shape = RoundedCornerShape(topStart = 6.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = movie.ottPlatform.split("/").first().trim(),
                        color = CineCyan,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Streaming badge at top
                Surface(
                    color = if (movie.isStreamingAuthorized) Color(0xCC00C853) else Color(0xCCFF9800),
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (movie.isStreamingAuthorized) "STREAM" else "TRAILER",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(6.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${movie.language} • ${movie.releaseYear}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = CineTextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${movie.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OttShowCard(
    show: TvShow,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("ott_show_${show.id}"),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = show.posterUrl,
                    contentDescription = show.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color(0xEE0B0F19),
                    shape = RoundedCornerShape(topStart = 6.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = show.ottPlatform.split("/").first().trim(),
                        color = CineCyan,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = CineRed,
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "SERIES",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(6.dp)) {
                Text(
                    text = show.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${show.totalSeasons} S • ${show.language}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = CineTextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${show.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
