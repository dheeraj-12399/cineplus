package com.example.ui.screens.shows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CineEmptyState
import com.example.ui.components.TvShowPosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun TvShowsScreen(
    viewModel: CineViewModel,
    onNavigateToShow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTvShows by viewModel.allTvShows.collectAsStateWithLifecycle()
    val pagedTvShows by viewModel.pagedTvShows.collectAsStateWithLifecycle()

    val categories = listOf(
        "All", "Trending", "Popular", "Recently Added", "Action", "Comedy",
        "Drama", "Thriller", "Crime", "Science Fiction", "Fantasy", "Animation"
    )

    val languages = listOf(
        "All", "Telugu", "Hindi", "Tamil", "Malayalam", "Kannada",
        "English", "Bengali", "Marathi", "Punjabi"
    )

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLanguage by remember { mutableStateOf("All") }

    LaunchedEffect(selectedCategory, selectedLanguage) {
        viewModel.loadPagedTvShows(selectedLanguage, selectedCategory, reset = true)
    }

    val filteredShows = remember(allTvShows, pagedTvShows, selectedCategory, selectedLanguage) {
        val merged = (pagedTvShows + allTvShows).distinctBy { it.id }
        var list = merged

        if (selectedCategory != "All") {
            list = when (selectedCategory) {
                "Trending" -> list.filter { it.isTrending }
                "Popular" -> list.filter { it.isPopular }
                "Recently Added" -> list.filter { it.isRecentlyAdded }
                else -> list.filter { it.genres.contains(selectedCategory, ignoreCase = true) }
            }
        }

        if (selectedLanguage != "All") {
            list = list.filter { it.language.equals(selectedLanguage, ignoreCase = true) }
        }

        list
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CineBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "TV Shows & Series",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = "Binge-worthy web series, drama sagas, and serialized episodic adventures",
                    style = MaterialTheme.typography.bodySmall,
                    color = CineTextSecondary
                )
            }
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("shows_screen_column")
        ) {
            // Genre Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineRed,
                                selectedLabelColor = Color.White,
                                containerColor = CineSurface,
                                labelColor = CineTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == cat,
                                borderColor = CineCardBorder
                            )
                        )
                    }
                }
            }

            // Language Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(languages) { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { selectedLanguage = lang },
                            label = { Text(if (lang == "All") "All Languages" else "$lang Shows") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineCyan.copy(alpha = 0.3f),
                                selectedLabelColor = Color.White,
                                containerColor = CineSurface,
                                labelColor = CineTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedLanguage == lang,
                                borderColor = if (selectedLanguage == lang) CineCyan else CineCardBorder
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${filteredShows.size} Series Available",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CineTextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (filteredShows.isEmpty()) {
                item {
                    CineEmptyState(
                        icon = Icons.Filled.Tv,
                        title = "No TV Shows Found",
                        message = "No serialized series match the selected filter. Try selecting 'All' or a different category.",
                        actionText = "Reset Filters",
                        onActionClick = {
                            selectedCategory = "All"
                            selectedLanguage = "All"
                        }
                    )
                }
            } else {
                val chunked = filteredShows.chunked(3)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { show ->
                            TvShowPosterCard(
                                show = show,
                                onClick = { onNavigateToShow(show.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size < 3) {
                            for (i in 0 until (3 - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            if (filteredShows.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Button(
                            onClick = { viewModel.loadNextTvShowsPage() },
                            colors = ButtonDefaults.buttonColors(containerColor = CineSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("tv_shows_load_more_button")
                        ) {
                            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = CineCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load More TV Shows from TMDB", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
