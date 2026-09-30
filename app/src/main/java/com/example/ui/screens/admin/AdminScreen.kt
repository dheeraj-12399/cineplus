package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Episode
import com.example.data.model.Movie
import com.example.data.model.TvSeason
import com.example.data.model.TvShow
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val allTvShows by viewModel.allTvShows.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Movies, 1: TV Shows

    var showAddMovieDialog by remember { mutableStateOf(false) }
    var showAddShowDialog by remember { mutableStateOf(false) }
    var showAddEpisodeDialog by remember { mutableStateOf<String?>(null) } // showId
    var editingMovieRights by remember { mutableStateOf<Movie?>(null) }

    if (showAddMovieDialog) {
        AddMovieDialog(
            onDismiss = { showAddMovieDialog = false },
            onSave = { movie ->
                viewModel.addMovie(movie)
                showAddMovieDialog = false
            }
        )
    }

    if (editingMovieRights != null) {
        EditMovieRightsDialog(
            movie = editingMovieRights!!,
            onDismiss = { editingMovieRights = null },
            onSave = { updatedMovie ->
                viewModel.updateMovie(updatedMovie)
                editingMovieRights = null
            }
        )
    }

    if (showAddShowDialog) {
        AddTvShowDialog(
            onDismiss = { showAddShowDialog = false },
            onSave = { show ->
                viewModel.addTvShow(show)
                showAddShowDialog = false
            }
        )
    }

    if (showAddEpisodeDialog != null) {
        AddEpisodeDialog(
            showId = showAddEpisodeDialog!!,
            onDismiss = { showAddEpisodeDialog = null },
            onSave = { ep ->
                viewModel.addEpisode(ep)
                showAddEpisodeDialog = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Content Management (CMS)", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Manage Telugu catalog & OTT streaming rights", fontSize = 11.sp, color = CineCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (selectedTab == 0) showAddMovieDialog = true else showAddShowDialog = true
                        },
                        modifier = Modifier.testTag("admin_add_content_button")
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Add Content", tint = CineRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CineBackground)
            )
        },
        containerColor = CineBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CineSurface,
                contentColor = Color.White,
                indicator = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Movies (${allMovies.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) CineRed else CineTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "TV Shows & Web Series (${allTvShows.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) CineRed else CineTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedTab == 0) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Surface(
                            color = CineSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Security, contentDescription = null, tint = CineCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Legal OTT Rights & Streaming Source Control", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Text("Tap the edit icon on any title (e.g. Salaar) to attach authorized streaming URLs or toggle in-app playback status.", fontSize = 11.sp, color = CineTextSecondary)
                                }
                            }
                        }
                    }

                    items(allMovies, key = { it.id }) { movie ->
                        AdminMovieItem(
                            movie = movie,
                            onEditRights = { editingMovieRights = movie },
                            onTogglePublish = {
                                viewModel.updateMovie(movie.copy(isPublished = !movie.isPublished))
                            },
                            onDelete = { viewModel.deleteMovie(movie.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allTvShows, key = { it.id }) { show ->
                        AdminShowItem(
                            show = show,
                            onAddEpisode = { showAddEpisodeDialog = show.id },
                            onTogglePublish = {
                                viewModel.updateTvShow(show.copy(isPublished = !show.isPublished))
                            },
                            onDelete = { viewModel.deleteTvShow(show.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun AdminMovieItem(
    movie: Movie,
    onEditRights: () -> Unit,
    onTogglePublish: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(movie.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        if (movie.language.equals("Telugu", ignoreCase = true)) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = CineRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("TELUGU", color = CineRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Text(
                        text = "${movie.releaseYear} • ${movie.language} • ${movie.genres}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CineTextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Streaming Rights Status Tag
                        Surface(
                            color = if (movie.isStreamingAuthorized) Color(0x3300C853) else Color(0x33FF9800),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (movie.isStreamingAuthorized) "✓ STREAM AUTHORIZED" else "TRAILER ONLY (NO RIGHTS)",
                                color = if (movie.isStreamingAuthorized) Color(0xFF69F0AE) else Color(0xFFFFB74D),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = CineSurfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Source: ${movie.ottPlatform}",
                                color = CineCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditRights) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Rights & Source", tint = CineCyan)
                    }

                    IconButton(onClick = onTogglePublish) {
                        Icon(
                            imageVector = if (movie.isPublished) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Publish",
                            tint = if (movie.isPublished) CineGreen else CineTextSecondary
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CineRed)
                    }
                }
            }
        }
    }
}

@Composable
fun EditMovieRightsDialog(
    movie: Movie,
    onDismiss: () -> Unit,
    onSave: (Movie) -> Unit
) {
    var availability by remember { mutableStateOf(movie.availability) }
    var playbackType by remember { mutableStateOf(movie.playbackType.ifBlank { "progressive" }) }
    var playbackUrl by remember { mutableStateOf(movie.playbackUrl.ifBlank { movie.videoUrl }) }
    var trailerUrl by remember { mutableStateOf(movie.trailerUrl) }
    var audioLanguages by remember { mutableStateOf(movie.audioLanguages) }
    var subtitleLanguages by remember { mutableStateOf(movie.subtitleLanguages) }
    var availableQualities by remember { mutableStateOf(movie.availableQualities) }
    var ottPlatform by remember { mutableStateOf(movie.ottPlatform) }
    var rightsNote by remember { mutableStateOf(movie.rightsNote) }
    var teluguCategory by remember { mutableStateOf(movie.teluguCategory) }

    val availabilityOptions = listOf("AVAILABLE", "UNAVAILABLE", "COMING_SOON")
    val playbackTypeOptions = listOf("progressive", "hls", "dash")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Movie & Playback Source", fontWeight = FontWeight.Bold, color = Color.White)
                Text(movie.title, fontSize = 12.sp, color = CineCyan)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                item {
                    // Availability Status
                    Text(
                        "Availability Status",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availabilityOptions.forEach { status ->
                            val isSelected = availability == status
                            Surface(
                                color = if (isSelected) {
                                    when (status) {
                                        "AVAILABLE" -> CineGreen
                                        "UNAVAILABLE" -> Color(0xFFFF9800)
                                        else -> CineCyan
                                    }
                                } else CineSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { availability = status }
                            ) {
                                Text(
                                    text = when (status) {
                                        "AVAILABLE" -> "Available"
                                        "UNAVAILABLE" -> "Unavailable"
                                        else -> "Coming Soon"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Playback Type
                    Text(
                        "Playback Type (Stream Protocol)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        playbackTypeOptions.forEach { type ->
                            FilterChip(
                                selected = playbackType.equals(type, ignoreCase = true),
                                onClick = { playbackType = type },
                                label = { Text(type.uppercase(), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CineRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Playback URL / Media Reference
                    OutlinedTextField(
                        value = playbackUrl,
                        onValueChange = { playbackUrl = it },
                        label = { Text("Authorized Playback URL (.mp4, .m3u8, .mpd)") },
                        placeholder = { Text("https://example.com/stream.m3u8") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Trailer URL
                    OutlinedTextField(
                        value = trailerUrl,
                        onValueChange = { trailerUrl = it },
                        label = { Text("Official Trailer URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio Tracks
                    OutlinedTextField(
                        value = audioLanguages,
                        onValueChange = { audioLanguages = it },
                        label = { Text("Audio Tracks (comma-separated)") },
                        placeholder = { Text("Telugu (Original), Hindi, Tamil, English") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle Tracks
                    OutlinedTextField(
                        value = subtitleLanguages,
                        onValueChange = { subtitleLanguages = it },
                        label = { Text("Subtitle Tracks (comma-separated)") },
                        placeholder = { Text("English, Telugu, Hindi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Available Qualities
                    OutlinedTextField(
                        value = availableQualities,
                        onValueChange = { availableQualities = it },
                        label = { Text("Available Qualities") },
                        placeholder = { Text("Auto, 1080p HD, 720p, 480p") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // OTT Platform / Rights Holder
                    OutlinedTextField(
                        value = ottPlatform,
                        onValueChange = { ottPlatform = it },
                        label = { Text("OTT Platform / Rights Holder") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rights Note
                    OutlinedTextField(
                        value = rightsNote,
                        onValueChange = { rightsNote = it },
                        label = { Text("Rights / Legal Note") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Telugu Category
                    OutlinedTextField(
                        value = teluguCategory,
                        onValueChange = { teluguCategory = it },
                        label = { Text("Telugu Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isAuth = availability == "AVAILABLE"
                    val validPlaybackUrl = if (isAuth && playbackUrl.isBlank()) {
                        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    } else playbackUrl

                    val updated = movie.copy(
                        availability = availability,
                        isStreamingAuthorized = isAuth,
                        playbackType = playbackType,
                        playbackUrl = validPlaybackUrl,
                        videoUrl = validPlaybackUrl,
                        trailerUrl = trailerUrl,
                        audioLanguages = audioLanguages,
                        subtitleLanguages = subtitleLanguages,
                        availableQualities = availableQualities,
                        streamingStatus = when (availability) {
                            "AVAILABLE" -> "AUTHORIZED_IN_APP"
                            "COMING_SOON" -> "COMING_SOON_OTT"
                            else -> "THEATRICAL_CATALOG_ONLY"
                        },
                        ottPlatform = ottPlatform,
                        rightsNote = rightsNote,
                        teluguCategory = teluguCategory
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CineCyan)
            ) {
                Text("Save Source & Publish", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CineTextSecondary) }
        },
        containerColor = CineCard
    )
}

@Composable
fun AdminShowItem(
    show: TvShow,
    onAddEpisode: () -> Unit,
    onTogglePublish: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, CineCardBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CineCard)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(show.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(
                    text = "${show.releaseYear} • ${show.language} • ${show.totalSeasons} Seasons • ${show.ottPlatform}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTextSecondary
                )
                Text(
                    text = "Creator: ${show.creator} • Rating: ${show.rating}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineCyan
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAddEpisode) {
                    Icon(Icons.Filled.VideoCall, contentDescription = "Add Episode", tint = CineCyan)
                }

                IconButton(onClick = onTogglePublish) {
                    Icon(
                        imageVector = if (show.isPublished) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Publish",
                        tint = if (show.isPublished) CineGreen else CineTextSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CineRed)
                }
            }
        }
    }
}

@Composable
fun AddMovieDialog(
    onDismiss: () -> Unit,
    onSave: (Movie) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var overview by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf("Action, Drama") }
    var language by remember { mutableStateOf("Telugu") }
    var year by remember { mutableStateOf("2024") }
    var duration by remember { mutableStateOf("145") }
    var rating by remember { mutableStateOf("8.8") }
    var director by remember { mutableStateOf("SS Rajamouli") }
    var cast by remember { mutableStateOf("Prabhas, Jr NTR, Anushka Shetty") }
    var ottPlatform by remember { mutableStateOf("Aha Video") }
    var teluguCategory by remember { mutableStateOf("New Telugu Releases") }
    var availability by remember { mutableStateOf("AVAILABLE") }
    var playbackType by remember { mutableStateOf("progressive") }
    var playbackUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var trailerUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") }
    var audioLanguages by remember { mutableStateOf("Telugu (Original), Hindi, Tamil, English") }
    var subtitleLanguages by remember { mutableStateOf("English, Telugu, Hindi") }
    var availableQualities by remember { mutableStateOf("Auto, 1080p HD, 720p, 480p") }

    val availabilityOptions = listOf("AVAILABLE", "UNAVAILABLE", "COMING_SOON")
    val playbackTypeOptions = listOf("progressive", "hls", "dash")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Movie to Catalog", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp)) {
                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (e.g. Salaar)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = overview, onValueChange = { overview = it }, label = { Text("Overview / Synopsis") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = language, onValueChange = { language = it }, label = { Text("Language") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Year") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = genres, onValueChange = { genres = it }, label = { Text("Genres") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = cast, onValueChange = { cast = it }, label = { Text("Cast") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = director, onValueChange = { director = it }, label = { Text("Director") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = ottPlatform, onValueChange = { ottPlatform = it }, label = { Text("OTT Platform (e.g. Aha, Netflix)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = teluguCategory, onValueChange = { teluguCategory = it }, label = { Text("Telugu Category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    // Availability Status
                    Text("Availability Status", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        availabilityOptions.forEach { status ->
                            val isSelected = availability == status
                            Surface(
                                color = if (isSelected) {
                                    when (status) {
                                        "AVAILABLE" -> CineGreen
                                        "UNAVAILABLE" -> Color(0xFFFF9800)
                                        else -> CineCyan
                                    }
                                } else CineSurfaceVariant,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f).clickable { availability = status }
                            ) {
                                Text(
                                    text = when (status) {
                                        "AVAILABLE" -> "Available"
                                        "UNAVAILABLE" -> "Unavailable"
                                        else -> "Coming Soon"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Playback Type
                    Text("Playback Type", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        playbackTypeOptions.forEach { type ->
                            FilterChip(
                                selected = playbackType == type,
                                onClick = { playbackType = type },
                                label = { Text(type.uppercase(), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CineRed, selectedLabelColor = Color.White)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (availability == "AVAILABLE") {
                        OutlinedTextField(
                            value = playbackUrl,
                            onValueChange = { playbackUrl = it },
                            label = { Text("Authorized Playback URL (.mp4, .m3u8, .mpd)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    OutlinedTextField(
                        value = trailerUrl,
                        onValueChange = { trailerUrl = it },
                        label = { Text("Official Trailer URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = audioLanguages,
                        onValueChange = { audioLanguages = it },
                        label = { Text("Audio Tracks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = subtitleLanguages,
                        onValueChange = { subtitleLanguages = it },
                        label = { Text("Subtitle Tracks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = availableQualities,
                        onValueChange = { availableQualities = it },
                        label = { Text("Available Qualities") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val isTelugu = language.equals("Telugu", ignoreCase = true)
                        val isAuth = availability == "AVAILABLE"
                        val finalPlaybackUrl = if (isAuth && playbackUrl.isBlank()) {
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        } else if (isAuth) playbackUrl else ""

                        val m = Movie(
                            id = "mov_${UUID.randomUUID().toString().take(8)}",
                            title = title,
                            overview = overview.ifBlank { "A cinematic marvel." },
                            backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1280&auto=format&fit=crop&q=80",
                            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
                            trailerUrl = trailerUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4" },
                            videoUrl = finalPlaybackUrl,
                            playbackUrl = finalPlaybackUrl,
                            playbackType = playbackType,
                            availability = availability,
                            availableQualities = availableQualities,
                            releaseDate = "$year-01-01",
                            releaseYear = year.toIntOrNull() ?: 2024,
                            durationMinutes = duration.toIntOrNull() ?: 140,
                            rating = rating.toFloatOrNull() ?: 8.5f,
                            genres = genres,
                            language = language,
                            director = director,
                            cast = cast,
                            audioLanguages = audioLanguages,
                            subtitleLanguages = subtitleLanguages,
                            ottPlatform = ottPlatform,
                            isStreamingAuthorized = isAuth,
                            streamingStatus = when (availability) {
                                "AVAILABLE" -> "AUTHORIZED_IN_APP"
                                "COMING_SOON" -> "COMING_SOON_OTT"
                                else -> "THEATRICAL_CATALOG_ONLY"
                            },
                            rightsNote = if (isAuth) "Authorized in-app stream." else "Theatrical catalog entry. Trailer only.",
                            isTeluguCinema = isTelugu,
                            teluguCategory = teluguCategory,
                            isOttContent = true,
                            ottCategory = if (isTelugu) "Telugu OTT" else "Popular OTT Movies",
                            isRecentlyAdded = true
                        )
                        onSave(m)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CineRed)
            ) {
                Text("Publish to Catalog")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CineTextSecondary) }
        },
        containerColor = CineCard
    )
}

@Composable
fun AddTvShowDialog(
    onDismiss: () -> Unit,
    onSave: (TvShow) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var overview by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf("Drama, Thriller") }
    var language by remember { mutableStateOf("Telugu") }
    var seasons by remember { mutableStateOf("1") }
    var ottPlatform by remember { mutableStateOf("Aha Video") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Series to Catalog", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Series Title (e.g. Dhootha)") }, singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = overview, onValueChange = { overview = it }, label = { Text("Series Overview") })
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = seasons, onValueChange = { seasons = it }, label = { Text("Seasons") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = language, onValueChange = { language = it }, label = { Text("Language") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = ottPlatform, onValueChange = { ottPlatform = it }, label = { Text("OTT Platform (e.g. Aha Video, Prime)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val isTelugu = language.equals("Telugu", ignoreCase = true)
                        val s = TvShow(
                            id = "show_${UUID.randomUUID().toString().take(8)}",
                            title = title,
                            overview = overview.ifBlank { "Compelling episodic storytelling." },
                            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1280&auto=format&fit=crop&q=80",
                            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
                            releaseYear = 2024,
                            totalSeasons = seasons.toIntOrNull() ?: 1,
                            rating = 8.5f,
                            genres = genres,
                            language = language,
                            creator = "Creator",
                            cast = "Lead Actor, Ensemble",
                            ottPlatform = ottPlatform,
                            isTeluguSeries = isTelugu,
                            isOttContent = true,
                            ottCategory = if (isTelugu) "Telugu OTT" else "Popular OTT Series",
                            isRecentlyAdded = true
                        )
                        onSave(s)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CineRed)
            ) {
                Text("Publish Show")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CineTextSecondary) }
        },
        containerColor = CineCard
    )
}

@Composable
fun AddEpisodeDialog(
    showId: String,
    onDismiss: () -> Unit,
    onSave: (Episode) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var overview by remember { mutableStateOf("") }
    var seasonNum by remember { mutableStateOf("1") }
    var episodeNum by remember { mutableStateOf("1") }
    var duration by remember { mutableStateOf("45") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Episode", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Episode Title") }, singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = overview, onValueChange = { overview = it }, label = { Text("Episode Synopsis") })
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = seasonNum, onValueChange = { seasonNum = it }, label = { Text("Season #") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = episodeNum, onValueChange = { episodeNum = it }, label = { Text("Episode #") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val sNum = seasonNum.toIntOrNull() ?: 1
                        val ep = Episode(
                            id = "ep_${UUID.randomUUID().toString().take(8)}",
                            showId = showId,
                            seasonId = "sea_${showId}_$sNum",
                            seasonNumber = sNum,
                            episodeNumber = episodeNum.toIntOrNull() ?: 1,
                            title = title,
                            overview = overview.ifBlank { "An exciting new episode." },
                            durationMinutes = duration.toIntOrNull() ?: 45,
                            thumbnailUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop&q=80",
                            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
                        )
                        onSave(ep)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CineRed)
            ) {
                Text("Add Episode")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CineTextSecondary) }
        },
        containerColor = CineCard
    )
}
