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
import com.example.data.model.Episode
import com.example.data.model.TvShow
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun TvShowDetailsScreen(
    showId: String,
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    onPlayEpisode: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTvShows by viewModel.allTvShows.collectAsStateWithLifecycle()
    val myList by viewModel.myList.collectAsStateWithLifecycle()

    var liveShow by remember { mutableStateOf<TvShow?>(null) }
    var selectedSeasonNum by remember { mutableIntStateOf(1) }
    var liveEpisodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    var isLoadingEpisodes by remember { mutableStateOf(false) }

    // Fetch live TMDB TV Details
    LaunchedEffect(showId) {
        val idLong = showId.toLongOrNull()
        if (idLong != null) {
            viewModel.getTvDetails(idLong).onSuccess {
                liveShow = it
            }
        }
    }

    // Fetch live TMDB Season Episodes
    LaunchedEffect(showId, selectedSeasonNum) {
        val idLong = showId.toLongOrNull()
        if (idLong != null) {
            isLoadingEpisodes = true
            viewModel.getTvSeasonEpisodes(idLong, selectedSeasonNum).onSuccess {
                liveEpisodes = it
            }
            isLoadingEpisodes = false
        }
    }

    val show = liveShow ?: allTvShows.find { it.id == showId }
    val isInList = remember(myList, showId) { myList.any { it.contentId == showId } }

    if (show == null) {
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
                .testTag("show_details_scroll_column")
        ) {
            // Backdrop Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = show.backdropUrl.ifBlank { show.posterUrl },
                        contentDescription = show.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

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

                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .align(Alignment.TopStart)
                            .testTag("show_details_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    if (show.trailerUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = {
                                val firstEp = liveEpisodes.firstOrNull()?.id ?: "ep_1"
                                onPlayEpisode(show.id, firstEp)
                            }) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }

            // Info Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = show.posterUrl,
                        contentDescription = show.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(105.dp)
                            .height(158.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(show.title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)

                        if (show.originalTitle.isNotBlank() && show.originalTitle != show.title) {
                            Text(show.originalTitle, style = MaterialTheme.typography.bodySmall, color = CineCyan)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("${show.releaseYear}", style = MaterialTheme.typography.bodySmall, color = CineTextSecondary)
                            Text("•", color = CineTextTertiary)
                            Text("${show.totalSeasons} Season${if (show.totalSeasons > 1) "s" else ""}", style = MaterialTheme.typography.bodySmall, color = CineTextSecondary)
                            Text("•", color = CineTextTertiary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${show.rating}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                if (show.voteCount > 0) {
                                    Text(" (${show.voteCount})", style = MaterialTheme.typography.labelSmall, color = CineTextTertiary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Genres: ${show.genres}", style = MaterialTheme.typography.labelSmall, color = CineCyan)
                        Text("Language: ${show.language}", style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
                        if (show.creator.isNotBlank()) {
                            Text("Creator: ${show.creator}", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }

            // Legal streaming note
            item {
                Spacer(modifier = Modifier.height(14.dp))
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
                            Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (show.isStreamingAuthorized) "AUTHORIZED STREAM" else "Available information only — streaming unavailable in this app.",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (show.isStreamingAuthorized) Color(0xFF69F0AE) else Color(0xFFFFB74D)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (show.isStreamingAuthorized) "Stream authorized in app via ${show.ottPlatform}" else "Real TMDB series metadata and episode guides are provided. Commercial streaming is available on legal watch providers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CineTextSecondary
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (show.isStreamingAuthorized) {
                        Button(
                            onClick = {
                                val firstEp = liveEpisodes.firstOrNull()?.id ?: "ep_1"
                                onPlayEpisode(show.id, firstEp)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CineRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PLAY S1:E1", fontWeight = FontWeight.Bold)
                        }
                    } else if (show.trailerUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                onPlayEpisode(show.id, "trailer")
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trailer Preview")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.toggleMyListShow(show) },
                        modifier = Modifier
                            .weight(0.8f)
                            .height(46.dp),
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

            // Where to Watch (India)
            if (show.watchProvidersFlatrate.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = CineCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Tv, contentDescription = null, tint = CineCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Where to Watch (India)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Streaming Network: ${show.watchProvidersFlatrate}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF69F0AE))
                        }
                    }
                }
            }

            // Synopsis
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Synopsis", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(show.overview, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp), color = CineTextSecondary)
                }
            }

            // Seasons Selector Chips
            if (show.totalSeasons > 1) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Seasons", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((1..show.totalSeasons).toList()) { sNum ->
                            FilterChip(
                                selected = selectedSeasonNum == sNum,
                                onClick = { selectedSeasonNum = sNum },
                                label = { Text("Season $sNum") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CineRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Real Episodes List
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Season $selectedSeasonNum Episodes (${liveEpisodes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoadingEpisodes) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CineRed)
                    }
                }
            } else {
                items(liveEpisodes, key = { it.id }) { ep ->
                    EpisodeRowItem(
                        episode = ep,
                        onPlay = {
                            if (ep.availability == "AVAILABLE") {
                                onPlayEpisode(show.id, ep.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EpisodeRowItem(
    episode: Episode,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onPlay() },
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
                    .width(110.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                AsyncImage(
                    model = episode.thumbnailUrl,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (episode.availability == "AVAILABLE") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.title}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${episode.durationMinutes}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = CineTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
