package com.example.ui.screens.details

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
import com.example.ui.components.CineSectionHeader
import com.example.ui.components.MoviePosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun MovieDetailsScreen(
    movieId: String,
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    onPlayMovie: (String) -> Unit,
    onNavigateToMovie: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val myList by viewModel.myList.collectAsStateWithLifecycle()

    var liveMovie by remember { mutableStateOf<Movie?>(null) }
    var isLoadingDetails by remember { mutableStateOf(true) }

    // Fetch full live metadata from TMDB API
    LaunchedEffect(movieId) {
        isLoadingDetails = true
        val idLong = movieId.toLongOrNull()
        if (idLong != null) {
            viewModel.getMovieDetails(idLong).onSuccess {
                liveMovie = it
            }
        }
        isLoadingDetails = false
    }

    val movie = liveMovie ?: allMovies.find { it.id == movieId }
    val isInList = remember(myList, movieId) { myList.any { it.contentId == movieId } }

    val similarMovies = remember(allMovies, movie) {
        if (movie == null) emptyList()
        else {
            val mainGenre = movie.genres.split(",").firstOrNull()?.trim() ?: "Action"
            allMovies.filter { it.id != movie.id && it.genres.contains(mainGenre, ignoreCase = true) }
        }
    }

    if (movie == null) {
        Box(modifier = Modifier.fillMaxSize().background(CineBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CineRed)
        }
        return
    }

    Scaffold(
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("movie_details_scroll_column")
        ) {
            // Large Cinematic Backdrop Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = movie.backdropUrl.ifBlank { movie.posterUrl },
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x88000000),
                                        Color(0xCC08080C),
                                        CineBackground
                                    )
                                )
                            )
                    )

                    // Top Back Button
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .align(Alignment.TopStart)
                            .testTag("movie_details_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    // Play / Trailer Backdrop Button in Center
                    if (movie.availability == "AVAILABLE") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { onPlayMovie(movie.id) },
                                modifier = Modifier.testTag("details_backdrop_play_button")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                    } else if (movie.trailerUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .border(2.dp, Color(0xFFFFB74D), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { onPlayMovie(movie.id) },
                                modifier = Modifier.testTag("details_backdrop_trailer_button")
                            ) {
                                Icon(Icons.Filled.Videocam, contentDescription = "Watch Trailer", tint = Color(0xFFFFB74D), modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }

            // Poster + Title + Meta Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poster thumbnail
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(105.dp)
                            .height(158.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )

                        if (movie.originalTitle.isNotBlank() && movie.originalTitle != movie.title) {
                            Text(
                                text = movie.originalTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = CineCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${movie.releaseYear}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CineTextSecondary
                            )
                            Text("•", color = CineTextTertiary)
                            Text(
                                text = "${movie.durationMinutes / 60}h ${movie.durationMinutes % 60}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = CineTextSecondary
                            )
                            Text("•", color = CineTextTertiary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${movie.rating}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                if (movie.voteCount > 0) {
                                    Text(
                                        text = " (${movie.voteCount})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CineTextTertiary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Genres: ${movie.genres}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CineCyan
                        )

                        Text(
                            text = "Original Language: ${movie.language}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CineTextSecondary
                        )

                        if (movie.director.isNotBlank()) {
                            Text(
                                text = "Director: ${movie.director}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Legal Watch Availability Status & Notice
            item {
                Spacer(modifier = Modifier.height(14.dp))
                when (movie.availability) {
                    "AVAILABLE" -> {
                        Surface(
                            color = Color(0x3300C853),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x8800C853)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AUTHORIZED IN-APP STREAMING",
                                        color = Color(0xFF69F0AE),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Licensed for CinePulse playback via ${movie.ottPlatform}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    else -> { // UNAVAILABLE or COMING_SOON
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x18FF9800)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FF9800)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Available information only — streaming unavailable in this app.",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFFFB74D)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "In accordance with legal distribution rights, commercial movie streaming is only provided when authorized playback arrangements are licensed. You can watch the official trailer or see legal watch providers below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CineTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons: Play, Trailer, Add to My List
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (movie.availability) {
                        "AVAILABLE" -> {
                            Button(
                                onClick = { onPlayMovie(movie.id) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(46.dp)
                                    .testTag("details_play_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CineRed),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PLAY", fontWeight = FontWeight.Bold)
                            }

                            if (movie.trailerUrl.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { onPlayMovie(movie.id) },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(46.dp)
                                        .testTag("details_trailer_button"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Filled.Videocam, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Trailer")
                                }
                            }
                        }

                        "UNAVAILABLE" -> {
                            // Do NOT show fake Play button
                            Surface(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp),
                                color = Color(0x22FF9800),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x88FF9800))
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                    Text(
                                        text = "Currently unavailable for streaming.",
                                        color = Color(0xFFFFB74D),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            if (movie.trailerUrl.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { onPlayMovie(movie.id) },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(46.dp)
                                        .testTag("details_play_trailer_button"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Trailer")
                                }
                            }
                        }

                        else -> { // "COMING_SOON"
                            Surface(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(46.dp),
                                color = CineSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CineCyan)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                    Text(
                                        text = "Coming soon.",
                                        color = CineCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (movie.trailerUrl.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { onPlayMovie(movie.id) },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Trailer")
                                }
                            }
                        }
                    }

                    // My List Button
                    OutlinedButton(
                        onClick = { viewModel.toggleMyListMovie(movie) },
                        modifier = Modifier
                            .weight(0.8f)
                            .height(46.dp)
                            .testTag("details_my_list_button"),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = if (isInList) Icons.Filled.Check else Icons.Filled.Add,
                            contentDescription = null,
                            tint = if (isInList) CineCyan else Color.White
                        )
                    }
                }
            }

            // Real Watch Providers Information (India - Primary Region)
            if (movie.watchProvidersFlatrate.isNotBlank() || movie.watchProvidersRent.isNotBlank() || movie.watchProvidersBuy.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = CineCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Tv, contentDescription = null, tint = CineCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Where to Watch (India)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            if (movie.watchProvidersFlatrate.isNotBlank()) {
                                Text(
                                    text = "📺 Streaming / Subscription: ${movie.watchProvidersFlatrate}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF69F0AE)
                                )
                            }
                            if (movie.watchProvidersRent.isNotBlank()) {
                                Text(
                                    text = "🎟️ Rent: ${movie.watchProvidersRent}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CineTextSecondary
                                )
                            }
                            if (movie.watchProvidersBuy.isNotBlank()) {
                                Text(
                                    text = "💳 Buy: ${movie.watchProvidersBuy}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CineTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Overview Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = CineTextSecondary
                    )
                }
            }

            // Cast & Crew Section
            if (movie.cast.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Starring",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = movie.cast,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CineCyan
                        )
                    }
                }
            }

            // Similar Movies Row
            if (similarMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CineSectionHeader(title = "More Like This")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        items(similarMovies.take(10), key = { it.id }) { item ->
                            MoviePosterCard(movie = item, onClick = { onNavigateToMovie(item.id) })
                        }
                    }
                }
            }
        }
    }
}
