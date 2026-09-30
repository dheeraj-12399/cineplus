package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.data.model.Movie
import com.example.data.model.TvShow
import com.example.data.model.WatchHistory
import com.example.ui.theme.*

@Composable
fun CineHeroBanner(
    title: String,
    overview: String,
    backdropUrl: String,
    metadataLine: String, // e.g. "2024 • Sci-Fi, Drama • 2h 38m"
    rating: Float,
    isInList: Boolean,
    isAvailable: Boolean = true,
    hasTrailer: Boolean = true,
    onPlayClick: () -> Unit,
    onMyListClick: () -> Unit,
    onMoreInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(440.dp)
            .testTag("cine_hero_banner")
    ) {
        // Backdrop Image
        AsyncImage(
            model = backdropUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Multi-directional cinematic vignette gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color(0x6608080C),
                            Color(0xCC08080C),
                            CineBackground
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CineRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FEATURED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = CineGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Text(
                    text = metadataLine,
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.White,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = overview,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                color = CineTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons: Play / Trailer, My List, More Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAvailable) {
                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("hero_play_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else if (hasTrailer) {
                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("hero_trailer_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Filled.Videocam, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trailer", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp),
                        color = Color(0x33FF9800),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Unavailable", color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedButton(
                    onClick = onMyListClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("hero_my_list_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF555566), Color(0xFF777788)))
                    )
                ) {
                    Icon(
                        imageVector = if (isInList) Icons.Filled.Check else Icons.Filled.Add,
                        contentDescription = null,
                        tint = if (isInList) CineCyan else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isInList) "Added" else "My List", color = if (isInList) CineCyan else Color.White)
                }

                IconButton(
                    onClick = onMoreInfoClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .testTag("hero_more_info_button")
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "More Info", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun MoviePosterCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(135.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("movie_card_${movie.id}"),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating badge top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC08080C))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${movie.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${movie.releaseYear} • ${movie.language}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CineTextSecondary
                )
            }
        }
    }
}

@Composable
fun TvShowPosterCard(
    show: TvShow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(135.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("show_card_${show.id}"),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                AsyncImage(
                    model = show.posterUrl,
                    contentDescription = show.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC08080C))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = CineGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${show.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = show.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${show.totalSeasons} Season${if (show.totalSeasons > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CineTextSecondary
                )
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    item: WatchHistory,
    onPlayClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onPlayClick)
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp))
            .testTag("continue_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = if (item.backdropUrl.isNotBlank()) item.backdropUrl else item.posterUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark vignette & play button overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x44000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Resume", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }

                // Progress Bar at bottom of thumbnail
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter),
                    color = CineRed,
                    trackColor = Color(0x66000000)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CineTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "Info", tint = CineTextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CineSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            ),
            color = Color.White
        )

        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CineCyan
                )
            }
        }
    }
}

@Composable
fun CineTopBar(
    onSearchClick: () -> Unit,
    onAdminClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CineBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "CINE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = CineRed
            )
            Text(
                text = "PULSE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search_button")
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
            }
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("top_bar_admin_button")
            ) {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin CMS", tint = CineCyan)
            }
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.testTag("top_bar_profile_button")
            ) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = Color.White)
            }
        }
    }
}

@Composable
fun CineEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Info,
    message: String = "",
    subtitle: String = message,
    actionText: String? = null,
    actionLabel: String? = actionText,
    onActionClick: (() -> Unit)? = null,
    onAction: (() -> Unit)? = onActionClick
) {
    val finalMessage = if (subtitle.isNotBlank()) subtitle else message
    val finalAction = actionLabel ?: actionText
    val finalOnClick = onAction ?: onActionClick

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CineSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CineTextSecondary, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        if (finalMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = finalMessage,
                style = MaterialTheme.typography.bodySmall,
                color = CineTextSecondary,
                lineHeight = 18.sp
            )
        }

        if (finalAction != null && finalOnClick != null) {
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = finalOnClick,
                colors = ButtonDefaults.buttonColors(containerColor = CineRed),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(finalAction, fontWeight = FontWeight.Bold)
            }
        }
    }
}
