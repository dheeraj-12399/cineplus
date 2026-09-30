package com.example.ui.screens.mylist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.MyListItem
import com.example.ui.components.CineEmptyState
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun MyListScreen(
    viewModel: CineViewModel,
    onNavigateToMovie: (String) -> Unit,
    onNavigateToShow: (String) -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myListMovies by viewModel.myListMovies.collectAsStateWithLifecycle()
    val myListShows by viewModel.myListShows.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Movies, 1: TV Shows

    val currentList = if (selectedTab == 0) myListMovies else myListShows

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CineBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "My Watchlist",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CineSurface,
                    contentColor = CineRed
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Movies (${myListMovies.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color.White else CineTextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "TV Shows (${myListShows.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color.White else CineTextSecondary
                            )
                        }
                    )
                }
            }
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (currentList.isEmpty()) {
            CineEmptyState(
                icon = Icons.Filled.BookmarkBorder,
                title = if (selectedTab == 0) "No Movies in My List" else "No TV Shows in My List",
                message = "Bookmark movies and TV series to save them to your personal watchlist for streaming later.",
                actionText = "Discover Titles",
                onActionClick = onExplore,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("my_list_scroll_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentList, key = { it.contentId }) { item ->
                    MyListItemRow(
                        item = item,
                        onClick = {
                            if (item.mediaType == "TV_SHOW") {
                                onNavigateToShow(item.contentId)
                            } else {
                                onNavigateToMovie(item.contentId)
                            }
                        },
                        onRemove = {
                            viewModel.toggleMyListMovie(
                                com.example.data.model.Movie(
                                    id = item.contentId,
                                    title = item.title,
                                    overview = "",
                                    backdropUrl = item.backdropUrl,
                                    posterUrl = item.posterUrl,
                                    releaseYear = item.releaseYear,
                                    durationMinutes = 120,
                                    genres = item.genres,
                                    language = "English",
                                    director = "",
                                    cast = ""
                                )
                            )
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun MyListItemRow(
    item: MyListItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
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
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.releaseYear} • ${item.genres}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CineTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = CineGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${item.rating}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove from List", tint = CineTextSecondary)
            }
        }
    }
}
