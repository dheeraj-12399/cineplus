package com.example.ui.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CineEmptyState
import com.example.ui.components.MoviePosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun MoviesScreen(
    viewModel: CineViewModel,
    onNavigateToMovie: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val pagedMovies by viewModel.pagedMovies.collectAsStateWithLifecycle()

    val categories = listOf(
        "All", "Trending", "Popular", "Recently Added", "Action", "Adventure",
        "Comedy", "Drama", "Romance", "Thriller", "Horror", "Science Fiction",
        "Fantasy", "Animation", "Crime", "Mystery", "Family"
    )

    val languages = listOf(
        "All", "Telugu", "Hindi", "Tamil", "Malayalam", "Kannada",
        "English", "Bengali", "Marathi", "Punjabi"
    )

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLanguage by remember { mutableStateOf("All") }

    LaunchedEffect(selectedCategory, selectedLanguage) {
        viewModel.loadPagedMovies(selectedLanguage, selectedCategory, reset = true)
    }

    val filteredMovies = remember(allMovies, pagedMovies, selectedCategory, selectedLanguage) {
        val merged = (pagedMovies + allMovies).distinctBy { it.id }
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
                    text = "Movies",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = "Curated cinema from Hollywood, Tollywood, Bollywood, and world cinema",
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
                .testTag("movies_screen_column")
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
                            label = { Text(if (lang == "All") "All Languages" else "$lang Cinema") },
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
                    text = "${filteredMovies.size} Movies Available",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CineTextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (filteredMovies.isEmpty()) {
                item {
                    CineEmptyState(
                        icon = Icons.Filled.Movie,
                        title = "No Movies Found",
                        message = "No titles match the selected genre and language filter. Try resetting your filter.",
                        actionText = "Reset Filters",
                        onActionClick = {
                            selectedCategory = "All"
                            selectedLanguage = "All"
                        }
                    )
                }
            } else {
                // 2-column or 3-column poster grid
                val chunked = filteredMovies.chunked(3)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { movie ->
                            MoviePosterCard(
                                movie = movie,
                                onClick = { onNavigateToMovie(movie.id) },
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

            if (filteredMovies.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Button(
                            onClick = { viewModel.loadNextMoviesPage() },
                            colors = ButtonDefaults.buttonColors(containerColor = CineSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("movies_load_more_button")
                        ) {
                            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = CineCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load More Movies from TMDB", color = Color.White, fontWeight = FontWeight.Bold)
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
