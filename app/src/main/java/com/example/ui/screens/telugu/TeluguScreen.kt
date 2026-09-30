package com.example.ui.screens.telugu

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
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.CineEmptyState
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun TeluguScreen(
    viewModel: CineViewModel,
    onNavigateToMovie: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teluguTrending by viewModel.teluguTrending.collectAsStateWithLifecycle()
    val teluguCategoryMovies by viewModel.teluguCategoryMovies.collectAsStateWithLifecycle()

    val categories = listOf(
        "All Telugu",
        "New Telugu Releases",
        "Popular Telugu Movies",
        "Telugu Blockbusters",
        "Telugu Action",
        "Telugu Romance",
        "Telugu Comedy",
        "Telugu Thriller",
        "Telugu Horror",
        "Telugu Crime",
        "Telugu Family Movies",
        "Telugu Classics",
        "Telugu OTT Movies"
    )

    var selectedCategory by remember { mutableStateOf("All Telugu") }
    var filterOnlyStreamable by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCategory) {
        viewModel.loadTeluguCategory(selectedCategory, resetPage = true)
    }

    val displayList = remember(teluguCategoryMovies, teluguTrending, selectedCategory, filterOnlyStreamable) {
        val base = if (teluguCategoryMovies.isNotEmpty()) teluguCategoryMovies else teluguTrending
        if (filterOnlyStreamable) base.filter { it.isStreamingAuthorized } else base
    }

    val heroTelugu = remember(teluguTrending) {
        teluguTrending.find { it.title.contains("Salaar", true) } ?: teluguTrending.firstOrNull()
    }

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
                                text = "తెలుగు సినిమా",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp
                                ),
                                color = CineGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = CineRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "TMDB LIVE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Real Telugu Theatrical Releases & Licensed OTT",
                            style = MaterialTheme.typography.labelSmall,
                            color = CineTextSecondary
                        )
                    }

                    // Streamable Only Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (filterOnlyStreamable) CineRed.copy(alpha = 0.2f) else CineSurface)
                            .border(1.dp, if (filterOnlyStreamable) CineRed else CineCardBorder, RoundedCornerShape(20.dp))
                            .clickable { filterOnlyStreamable = !filterOnlyStreamable }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (filterOnlyStreamable) Icons.Filled.PlayCircle else Icons.Filled.FilterList,
                            contentDescription = null,
                            tint = if (filterOnlyStreamable) CineRed else CineTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (filterOnlyStreamable) "Streamable" else "All Titles",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (filterOnlyStreamable) CineRed else CineTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Telugu Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineRed,
                                selectedLabelColor = Color.White,
                                containerColor = CineSurface,
                                labelColor = CineTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) CineRed else CineCardBorder
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
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
                .testTag("telugu_screen_scroll_column")
        ) {
            // Featured Spotlight Hero
            heroTelugu?.let { hero ->
                item {
                    TeluguSpotlightHero(
                        movie = hero,
                        onClick = { onNavigateToMovie(hero.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCategory (${displayList.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            if (displayList.isEmpty()) {
                item {
                    CineEmptyState(
                        icon = Icons.Filled.MovieFilter,
                        title = "No Movies Found",
                        subtitle = "Try selecting another Telugu category or turn off 'Streamable' filter.",
                        actionLabel = "Show All Telugu",
                        onAction = {
                            selectedCategory = "All Telugu"
                            filterOnlyStreamable = false
                        },
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            } else {
                val chunked = displayList.chunked(3)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (movie in rowItems) {
                            Box(modifier = Modifier.weight(1f)) {
                                TeluguPosterCard(
                                    movie = movie,
                                    onClick = { onNavigateToMovie(movie.id) }
                                )
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Load More Button for Pagination
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { viewModel.loadNextTeluguPage() },
                            colors = ButtonDefaults.buttonColors(containerColor = CineSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("telugu_load_more_button")
                        ) {
                            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = CineCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load More Real Telugu Movies", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeluguSpotlightHero(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.backdropUrl.ifBlank { movie.posterUrl },
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xCC0B0F19),
                                CineCard
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CineRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "TELUGU SPOTLIGHT",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0x99000000),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (movie.isStreamingAuthorized) "✓ STREAMING" else "THEATRICAL CATALOG",
                            color = if (movie.isStreamingAuthorized) Color(0xFF69F0AE) else Color(0xFFFFB74D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )

                Text(
                    text = "${movie.releaseYear} • ${movie.genres} • ⭐ ${movie.rating}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextSecondary
                )
            }
        }
    }
}

@Composable
fun TeluguPosterCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("telugu_poster_${movie.id}"),
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

                Surface(
                    color = if (movie.isStreamingAuthorized) Color(0xCC00C853) else Color(0xCCFF9800),
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (movie.isStreamingAuthorized) "▶ STREAM" else "CATALOG",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                if (movie.rating > 0f) {
                    Surface(
                        color = Color(0xDD000000),
                        shape = RoundedCornerShape(topStart = 6.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(9.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${movie.rating}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                Text(
                    text = "${movie.releaseYear} • ${movie.genres.split(",").firstOrNull() ?: "Cinema"}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CineTextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
