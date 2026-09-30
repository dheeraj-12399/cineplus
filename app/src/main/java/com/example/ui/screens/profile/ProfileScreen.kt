package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel

@Composable
fun ProfileScreen(
    viewModel: CineViewModel,
    onNavigateToMyList: () -> Unit,
    onNavigateToWatchHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val myList by viewModel.myList.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out of CinePulse?", color = Color.White) },
            text = { Text("You will need to sign in again to sync your watchlist and playback history.", color = CineTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineRed)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineCard
        )
    }

    Scaffold(
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("profile_scroll_column"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Profile Avatar & Name
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(CineRed, CineCyan)))
                            .border(2.dp, CineRedLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.avatarEmoji ?: "🎬",
                            fontSize = 40.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile?.name ?: "Alex Vance",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Premium VIP Subscriber",
                        style = MaterialTheme.typography.labelSmall,
                        color = CineGold
                    )
                }
            }

            // Quick Stats Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CineCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${myList.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("In Watchlist", style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${continueWatching.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = CineCyan)
                            Text("Resume Play", style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${watchHistory.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = CineRed)
                            Text("History", style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
                        }
                    }
                }
            }

            // Navigation Sections
            item {
                Text(
                    text = "Library & Content",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CineTextSecondary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CineCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        ProfileRow(
                            icon = Icons.Filled.Bookmark,
                            title = "My List",
                            subtitle = "${myList.size} Saved titles",
                            onClick = onNavigateToMyList
                        )
                        HorizontalDivider(color = CineCardBorder)
                        ProfileRow(
                            icon = Icons.Filled.History,
                            title = "Watch History",
                            subtitle = "${watchHistory.size} Recently streamed movies and episodes",
                            onClick = onNavigateToWatchHistory
                        )
                        HorizontalDivider(color = CineCardBorder)
                        ProfileRow(
                            icon = Icons.Filled.PlayCircle,
                            title = "Continue Watching",
                            subtitle = "${continueWatching.size} Titles in progress",
                            onClick = onNavigateToWatchHistory
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Platform Settings & Administration",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CineTextSecondary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CineCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        ProfileRow(
                            icon = Icons.Filled.AdminPanelSettings,
                            title = "Admin Content Manager",
                            subtitle = "Publish, edit, delete movies, series, seasons, and episodes",
                            onClick = onNavigateToAdmin
                        )
                        HorizontalDivider(color = CineCardBorder)
                        ProfileRow(
                            icon = Icons.Filled.Hd,
                            title = "Streaming Quality",
                            subtitle = profile?.streamingQuality ?: "1080p (Full HD)",
                            onClick = {}
                        )
                        HorizontalDivider(color = CineCardBorder)
                        ProfileRow(
                            icon = Icons.Filled.Language,
                            title = "Preferred Audio Language",
                            subtitle = profile?.preferredLanguage ?: "English",
                            onClick = {}
                        )
                    }
                }
            }

            // Logout Button
            item {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_logout_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CineRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CineRed.copy(alpha = 0.5f), CineRed))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, tint = CineRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CineRed, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = CineTextTertiary)
    }
}
