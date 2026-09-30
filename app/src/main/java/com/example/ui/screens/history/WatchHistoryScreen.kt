package com.example.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.WatchHistory
import com.example.ui.components.CineEmptyState
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchHistoryScreen(
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    onPlayMovie: (String) -> Unit,
    onPlayEpisode: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val history by viewModel.watchHistory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watch History", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllHistory() }) {
                            Text("Clear All", color = CineRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CineBackground)
            )
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (history.isEmpty()) {
            CineEmptyState(
                icon = Icons.Filled.History,
                title = "No Watch History",
                message = "Movies and TV episodes you stream will appear here so you can rewatch or pick up where you left off.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("watch_history_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    WatchHistoryRow(
                        item = item,
                        onClick = {
                            if (item.mediaType == "TV_SHOW" && item.episodeId != null) {
                                onPlayEpisode(item.contentId, item.episodeId)
                            } else {
                                onPlayMovie(item.contentId)
                            }
                        },
                        onDelete = { viewModel.removeHistoryItem(item.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun WatchHistoryRow(
    item: WatchHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(55.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                AsyncImage(
                    model = if (item.backdropUrl.isNotBlank()) item.backdropUrl else item.posterUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Progress indicator at bottom
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomCenter),
                    color = CineRed,
                    trackColor = Color(0x66000000)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.subtitle} • ${item.progressPercent}% watched",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove", tint = CineTextSecondary)
            }
        }
    }
}
