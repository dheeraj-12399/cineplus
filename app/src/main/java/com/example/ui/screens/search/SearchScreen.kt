package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.data.remote.tmdb.TmdbClient
import com.example.data.remote.tmdb.TmdbMultiSearchItem
import com.example.ui.components.CineEmptyState
import com.example.ui.components.MoviePosterCard
import com.example.ui.components.TvShowPosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun SearchScreen(
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMovie: (String) -> Unit,
    onNavigateToShow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchFilterType by viewModel.searchFilterType.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val multiResults by viewModel.multiSearchResults.collectAsStateWithLifecycle()
    val movieResults by viewModel.movieSearchResults.collectAsStateWithLifecycle()
    val tvResults by viewModel.tvSearchResults.collectAsStateWithLifecycle()

    val quickSearches = listOf("Salaar", "Kalki 2898 AD", "Devara", "RRR", "Pushpa", "Prabhas", "Dhootha")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CineBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search movies, TV shows, actors, directors...", color = CineTextTertiary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = CineRed) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = CineTextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CineRed,
                            unfocusedBorderColor = CineCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_query_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Media Type Filter Tabs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "All", "MOVIES" to "Movies", "TV_SHOWS" to "TV Shows", "PERSON" to "People").forEach { (type, label) ->
                        val isSelected = searchFilterType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSearchFilterType(type) },
                            label = { Text(label, fontSize = 12.sp) },
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
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Quick Popular Search Tags
                if (searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Trending Searches:", style = MaterialTheme.typography.labelSmall, color = CineTextTertiary)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickSearches) { tag ->
                            Surface(
                                color = CineSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                                modifier = Modifier.clickable { viewModel.setSearchQuery(tag) }
                            ) {
                                Text(
                                    text = tag,
                                    color = CineCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
                .testTag("search_results_column")
        ) {
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CineRed)
                    }
                }
            } else if (searchQuery.isBlank()) {
                item {
                    CineEmptyState(
                        icon = Icons.Filled.Search,
                        title = "Real TMDB Search",
                        subtitle = "Search thousands of real movies, TV shows, and cast members with live TMDB data.",
                        modifier = Modifier.padding(top = 60.dp)
                    )
                }
            } else {
                when (searchFilterType) {
                    "MOVIES" -> {
                        if (movieResults.isEmpty()) {
                            item { CineEmptyState(title = "No Movies Found", subtitle = "No movies matching '$searchQuery'", modifier = Modifier.padding(top = 40.dp)) }
                        } else {
                            val chunked = movieResults.chunked(3)
                            items(chunked) { row ->
                                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    row.forEach { m ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            MoviePosterCard(movie = m, onClick = { onNavigateToMovie(m.id) })
                                        }
                                    }
                                    repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }

                    "TV_SHOWS" -> {
                        if (tvResults.isEmpty()) {
                            item { CineEmptyState(title = "No Shows Found", subtitle = "No TV shows matching '$searchQuery'", modifier = Modifier.padding(top = 40.dp)) }
                        } else {
                            val chunked = tvResults.chunked(3)
                            items(chunked) { row ->
                                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    row.forEach { s ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            TvShowPosterCard(show = s, onClick = { onNavigateToShow(s.id) })
                                        }
                                    }
                                    repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }

                    "PERSON" -> {
                        val people = multiResults.filter { it.mediaType == "person" }
                        if (people.isEmpty()) {
                            item { CineEmptyState(title = "No People Found", subtitle = "No actors or directors matching '$searchQuery'", modifier = Modifier.padding(top = 40.dp)) }
                        } else {
                            items(people, key = { "person_${it.id}" }) { person ->
                                PersonSearchResultRow(person = person, onNavigateToMovie = onNavigateToMovie)
                            }
                        }
                    }

                    else -> { // "ALL"
                        if (multiResults.isEmpty()) {
                            item { CineEmptyState(title = "No Results Found", subtitle = "No real titles matching '$searchQuery'", modifier = Modifier.padding(top = 40.dp)) }
                        } else {
                            items(multiResults, key = { "${it.mediaType}_${it.id}" }) { item ->
                                MultiSearchRowItem(
                                    item = item,
                                    onMovieClick = { onNavigateToMovie(item.id.toString()) },
                                    onTvClick = { onNavigateToShow(item.id.toString()) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MultiSearchRowItem(
    item: TmdbMultiSearchItem,
    onMovieClick: () -> Unit,
    onTvClick: () -> Unit
) {
    val isMovie = item.mediaType == "movie"
    val isTv = item.mediaType == "tv"
    val isPerson = item.mediaType == "person"

    val title = item.title ?: item.name ?: "Unknown"
    val subtitle = when {
        isMovie -> "Movie • ${item.releaseDate?.take(4) ?: "Cinema"}"
        isTv -> "TV Series • ${item.firstAirDate?.take(4) ?: "TV"}"
        isPerson -> "Person • ${item.knownForDepartment ?: "Acting"}"
        else -> "Title"
    }

    val imgUrl = when {
        isPerson -> TmdbClient.profileUrl(item.profilePath)
        else -> TmdbClient.thumbnailUrl(item.posterPath ?: item.backdropPath)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable {
                if (isMovie) onMovieClick()
                else if (isTv) onTvClick()
            },
        colors = CardDefaults.cardColors(containerColor = CineCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(84.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CineSurface)
            ) {
                if (imgUrl.isNotBlank()) {
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = CineTextSecondary)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when {
                            isMovie -> CineRed
                            isTv -> CineCyan
                            else -> CineGold
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = (item.mediaType ?: "TITLE").uppercase(),
                            color = if (isTv || isPerson) Color.Black else Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    if (item.voteAverage != null && item.voteAverage > 0.0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.Search, contentDescription = null, tint = CineGold, modifier = Modifier.size(10.dp))
                        Text(
                            text = " ⭐ ${((item.voteAverage * 10).toInt() / 10f)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextSecondary
                )

                if (!item.overview.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = CineTextTertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PersonSearchResultRow(
    person: TmdbMultiSearchItem,
    onNavigateToMovie: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CineCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CineSurface)
                ) {
                    if (!person.profilePath.isNullOrBlank()) {
                        AsyncImage(
                            model = TmdbClient.profileUrl(person.profilePath),
                            contentDescription = person.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(person.name ?: "Artist", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Department: ${person.knownForDepartment ?: "Acting"}", style = MaterialTheme.typography.labelSmall, color = CineCyan)
                }
            }

            if (!person.knownFor.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Known For:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CineTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(person.knownFor) { k ->
                        Surface(
                            color = CineSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                            modifier = Modifier.clickable { onNavigateToMovie(k.id.toString()) }
                        ) {
                            Text(
                                text = k.title ?: k.name ?: "Title",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
