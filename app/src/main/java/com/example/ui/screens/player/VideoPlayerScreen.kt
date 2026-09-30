package com.example.ui.screens.player

import android.net.Uri
import kotlin.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.Episode
import com.example.data.model.Movie
import com.example.data.model.TvShow
import com.example.data.model.WatchHistory
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    mediaType: String, // "MOVIE" or "TV_SHOW"
    contentId: String,
    episodeId: String?,
    viewModel: CineViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val allTvShows by viewModel.allTvShows.collectAsStateWithLifecycle()

    val movie = remember(allMovies, contentId) { allMovies.find { it.id == contentId } }
    val show = remember(allTvShows, contentId) { allTvShows.find { it.id == contentId } }

    val episodeState = produceState<Episode?>(initialValue = null, episodeId) {
        if (episodeId != null && episodeId.isNotBlank()) {
            viewModel.repository.getEpisodeById(episodeId).collect { value = it }
        }
    }
    val episode = episodeState.value

    val historyItemState = produceState<WatchHistory?>(initialValue = null, contentId, episodeId) {
        val hId = if (episodeId != null && episodeId.isNotBlank()) "wh_${contentId}_$episodeId" else "wh_$contentId"
        viewModel.repository.getWatchHistoryItem(hId).collect { value = it }
    }
    val savedHistory = historyItemState.value

    // Determine actual playback URL and format
    val isMovie = mediaType == "MOVIE"
    val isAvailable = if (isMovie) (movie?.availability == "AVAILABLE" && movie.playbackUrl.isNotBlank()) else true
    val isTrailerMode = isMovie && !isAvailable && (movie?.trailerUrl?.isNotBlank() == true)

    val rawPlaybackUrl = if (isMovie) {
        if (isAvailable) movie!!.playbackUrl.ifBlank { movie.videoUrl }
        else movie?.trailerUrl ?: ""
    } else {
        episode?.playbackUrl?.ifBlank { episode.videoUrl } ?: show?.trailerUrl ?: ""
    }

    val playbackType = if (isMovie) (movie?.playbackType ?: "progressive") else (episode?.playbackType ?: "progressive")

    val title = if (!isMovie) {
        "${show?.title ?: "Show"}: ${episode?.title ?: "Episode"}"
    } else {
        if (isTrailerMode) "${movie?.title ?: "Movie"} (Official Trailer)" else (movie?.title ?: "Movie")
    }

    val subtitle = if (!isMovie) {
        "Season ${episode?.seasonNumber ?: 1} Episode ${episode?.episodeNumber ?: 1} • ${show?.ottPlatform ?: "OTT"}"
    } else {
        if (isTrailerMode) {
            "Trailer Preview • In-App Full Streaming Not Licensed • Rights: ${movie?.ottPlatform ?: "Theatrical"}"
        } else {
            "${movie?.releaseYear ?: 2024} • ${movie?.genres ?: "Cinema"} • Source: ${movie?.ottPlatform ?: "Authorized"}"
        }
    }

    val posterUrl = if (!isMovie) (episode?.thumbnailUrl ?: show?.posterUrl ?: "") else (movie?.posterUrl ?: "")
    val backdropUrl = if (!isMovie) (show?.backdropUrl ?: "") else (movie?.backdropUrl ?: "")

    // Real Media3 ExoPlayer Instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    var isPlaying by remember { mutableStateOf(true) }
    var playbackState by remember { mutableIntStateOf(Player.STATE_IDLE) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var controlsTimeout by remember { mutableIntStateOf(0) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedQuality by remember { mutableStateOf("1080p (Full HD)") }
    var selectedAudioTrack by remember { mutableStateOf(movie?.audioLanguages?.split(",")?.firstOrNull()?.trim() ?: "Original 5.1 (Dolby Atmos)") }
    var selectedSubtitle by remember { mutableStateOf(movie?.subtitleLanguages?.split(",")?.firstOrNull()?.trim() ?: "English [CC]") }
    var volume by remember { mutableFloatStateOf(1.0f) }
    var isFitMode by remember { mutableStateOf(true) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var hasResumedFromSaved by remember { mutableStateOf(false) }

    // Setup player listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                if (state == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    playerError = null

                    // Auto resume from saved position once ready
                    if (!hasResumedFromSaved && savedHistory != null && savedHistory.positionSeconds > 0) {
                        val seekTarget = (savedHistory.positionSeconds * 1000L).coerceAtMost(durationMs)
                        if (seekTarget > 0) {
                            exoPlayer.seekTo(seekTarget)
                        }
                        hasResumedFromSaved = true
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                playerError = error.localizedMessage ?: "Network or playback error occurred."
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Set media item to ExoPlayer
    LaunchedEffect(rawPlaybackUrl) {
        if (rawPlaybackUrl.isNotBlank()) {
            try {
                playerError = null
                val mime = when {
                    playbackType.equals("hls", ignoreCase = true) || rawPlaybackUrl.endsWith(".m3u8", ignoreCase = true) -> MimeTypes.APPLICATION_M3U8
                    playbackType.equals("dash", ignoreCase = true) || rawPlaybackUrl.endsWith(".mpd", ignoreCase = true) -> MimeTypes.APPLICATION_MPD
                    else -> MimeTypes.VIDEO_MP4
                }

                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(rawPlaybackUrl))
                    .setMimeType(mime)
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            } catch (e: Exception) {
                playerError = "Failed to load stream: ${e.message}"
            }
        }
    }

    // Progress polling loop & auto-save to database
    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                val curDuration = exoPlayer.duration.coerceAtLeast(0L)
                if (curDuration > 0) {
                    durationMs = curDuration
                }

                val posSec = currentPositionMs / 1000L
                val durSec = durationMs / 1000L

                // Auto save every 4 seconds
                if (durSec > 0 && posSec % 4 == 0L) {
                    viewModel.updateProgress(
                        mediaType = mediaType,
                        contentId = contentId,
                        episodeId = episodeId,
                        title = title,
                        subtitle = subtitle,
                        posterUrl = posterUrl,
                        backdropUrl = backdropUrl,
                        positionSeconds = posSec,
                        durationSeconds = durSec
                    )
                }
            }

            // Controls auto-hide timer
            if (showControls && isPlaying) {
                controlsTimeout++
                if (controlsTimeout > 5) {
                    showControls = false
                    controlsTimeout = 0
                }
            } else {
                controlsTimeout = 0
            }

            delay(1000)
        }
    }

    fun saveAndExit() {
        val posSec = currentPositionMs / 1000L
        val durSec = durationMs / 1000L
        if (durSec > 0) {
            viewModel.updateProgress(
                mediaType = mediaType,
                contentId = contentId,
                episodeId = episodeId,
                title = title,
                subtitle = subtitle,
                posterUrl = posterUrl,
                backdropUrl = backdropUrl,
                positionSeconds = posSec,
                durationSeconds = durSec
            )
        }
        exoPlayer.pause()
        onNavigateBack()
    }

    // Handle UNAVAILABLE title with no trailer
    if (!isAvailable && rawPlaybackUrl.isBlank()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CineBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CineCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, CineCardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Currently Unavailable for Streaming",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This title (${movie?.title ?: "Movie"}) does not currently have an authorized in-app streaming source configured.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CineTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = CineRed)
                    ) {
                        Text("Back to Details")
                    }
                }
            }
        }
        return
    }

    // Main Player Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
                controlsTimeout = 0
            }
            .testTag("cine_real_video_player")
    ) {
        // Real Media3 PlayerView inside AndroidView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Use custom M3 Jetpack Compose controls
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    keepScreenOn = true
                }
            },
            update = { view ->
                view.resizeMode = if (isFitMode) AspectRatioFrameLayout.RESIZE_MODE_FIT else AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Spinner
        if (playbackState == Player.STATE_BUFFERING) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CineRed, modifier = Modifier.size(54.dp), strokeWidth = 4.dp)
            }
        }

        // Error Banner
        if (playerError != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xDDF44336)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Playback Error", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(playerError ?: "", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            playerError = null
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Retry Playback", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Custom Cinematic Jetpack Compose Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x77000000))
            ) {
                // Top App Bar Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = { saveAndExit() },
                            modifier = Modifier.testTag("player_exit_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = CineTextSecondary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Stream format pill
                        Surface(
                            color = CineRed,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = playbackType.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { isFitMode = !isFitMode },
                            modifier = Modifier.testTag("player_aspect_ratio_button")
                        ) {
                            Icon(
                                imageVector = if (isFitMode) Icons.Filled.Fullscreen else Icons.Filled.FullscreenExit,
                                contentDescription = "Resize Aspect",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier.testTag("player_settings_button")
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                }

                // Notice Banner if Trailer Only
                if (isTrailerMode) {
                    Surface(
                        color = Color(0xEEFF9800),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 76.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "ℹ️ Official Trailer Preview • In-app full streaming not licensed • Rights: ${movie?.ottPlatform ?: "Theatrical"}",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Center Controls: Rewind 10s, Real Play/Pause, Forward 10s
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("player_rewind_10_button")
                    ) {
                        Icon(Icons.Filled.Replay10, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // Play / Pause Toggle
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CineRed)
                            .clickable {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            }
                            .testTag("player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10_000L).coerceAtMost(durationMs)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("player_forward_10_button")
                    ) {
                        Icon(Icons.Filled.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Bottom Timeline, Seek Bar, Volume & Speed
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Time text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatDuration(currentPositionMs / 1000L), style = MaterialTheme.typography.labelSmall, color = Color.White)
                        Text(formatDuration(durationMs / 1000L), style = MaterialTheme.typography.labelSmall, color = CineTextSecondary)
                    }

                    // Real ExoPlayer Seek Bar
                    val progressFloat = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
                    Slider(
                        value = progressFloat,
                        onValueChange = { frac ->
                            val targetMs = (frac * durationMs).toLong()
                            currentPositionMs = targetMs
                        },
                        onValueChangeFinished = {
                            exoPlayer.seekTo(currentPositionMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CineRed,
                            activeTrackColor = CineRed,
                            inactiveTrackColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_seek_slider")
                    )

                    // Quick bottom actions: Volume slider, Speed pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Volume toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (volume > 0.05f) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                contentDescription = "Volume",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        volume = if (volume > 0.05f) 0f else 1f
                                        exoPlayer.volume = volume
                                    }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Slider(
                                value = volume,
                                onValueChange = { v ->
                                    volume = v
                                    exoPlayer.volume = v
                                },
                                modifier = Modifier.width(90.dp),
                                colors = SliderDefaults.colors(thumbColor = CineCyan, activeTrackColor = CineCyan)
                            )
                        }

                        // Playback Speed Selector Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { spd ->
                                Surface(
                                    color = if (playbackSpeed == spd) CineRed else Color(0x66000000),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable {
                                        playbackSpeed = spd
                                        exoPlayer.setPlaybackSpeed(spd)
                                    }
                                ) {
                                    Text(
                                        text = "${spd}x",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Settings Modal Sheet (Audio Tracks, Subtitles, Quality)
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = CineCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Playback Preferences", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                // Audio Tracks
                Text("Audio Track", style = MaterialTheme.typography.labelMedium, color = CineTextSecondary)
                val audioTracks = (movie?.audioLanguages ?: "Telugu (Original), Hindi, Tamil, English").split(",").map { it.trim() }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    audioTracks.take(4).forEach { track ->
                        FilterChip(
                            selected = selectedAudioTrack.contains(track, ignoreCase = true),
                            onClick = { selectedAudioTrack = track },
                            label = { Text(track, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CineRed, selectedLabelColor = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitles
                Text("Subtitles", style = MaterialTheme.typography.labelMedium, color = CineTextSecondary)
                val subTracks = listOf("Off", "English [CC]", "Telugu", "Hindi")
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    subTracks.forEach { sub ->
                        FilterChip(
                            selected = selectedSubtitle == sub,
                            onClick = { selectedSubtitle = sub },
                            label = { Text(sub, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CineCyan, selectedLabelColor = Color.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video Quality
                Text("Video Quality (ExoPlayer Stream)", style = MaterialTheme.typography.labelMedium, color = CineTextSecondary)
                val qualities = listOf("Auto (Adaptive)", "1080p (Full HD)", "720p", "480p")
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    qualities.forEach { q ->
                        FilterChip(
                            selected = selectedQuality == q,
                            onClick = { selectedQuality = q },
                            label = { Text(q, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
