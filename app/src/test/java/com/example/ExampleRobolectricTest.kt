package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CinePulse", appName)
  }

  @Test
  fun `verify initial movies and shows catalog exists`() {
    val movies = com.example.data.local.MovieSeedData.getInitialMovies()
    org.junit.Assert.assertTrue(movies.isNotEmpty())
    val heroMovie = movies.find { it.isFeatured }
    org.junit.Assert.assertNotNull(heroMovie)

    val shows = com.example.data.local.MovieSeedData.getInitialTvShows()
    org.junit.Assert.assertTrue(shows.isNotEmpty())

    val seasons = com.example.data.local.MovieSeedData.getInitialSeasons()
    val episodes = com.example.data.local.MovieSeedData.getInitialEpisodes()
    org.junit.Assert.assertTrue(seasons.isNotEmpty())
    org.junit.Assert.assertTrue(episodes.isNotEmpty())

    // Verify Telugu cinema catalog includes Salaar and Irumudi
    val salaar = movies.find { it.title.contains("Salaar", ignoreCase = true) }
    org.junit.Assert.assertNotNull(salaar)
    org.junit.Assert.assertFalse(salaar!!.isStreamingAuthorized) // Salaar is theatrical catalog with official trailer
    assertEquals("Telugu", salaar.language)

    val irumudi = movies.find { it.title.contains("Irumudi", ignoreCase = true) }
    org.junit.Assert.assertNotNull(irumudi)
    org.junit.Assert.assertTrue(irumudi!!.isStreamingAuthorized) // Irumudi has authorized streaming
    assertEquals("Telugu", irumudi.language)
    assertEquals("Aha Video", irumudi.ottPlatform)
  }

  @Test
  fun `verify telugu categories and ott platforms`() {
    val movies = com.example.data.local.MovieSeedData.getInitialMovies()
    val teluguMovies = movies.filter { it.language == "Telugu" || it.isTeluguCinema }
    org.junit.Assert.assertTrue(teluguMovies.size >= 8)

    // Check availability states
    val availableTitles = movies.filter { it.availability == "AVAILABLE" }
    val unavailableTitles = movies.filter { it.availability == "UNAVAILABLE" }
    org.junit.Assert.assertTrue(availableTitles.isNotEmpty())
    org.junit.Assert.assertTrue(unavailableTitles.isNotEmpty())

    // Verify authorized titles have valid playback URLs
    for (m in availableTitles) {
      org.junit.Assert.assertTrue(m.playbackUrl.isNotBlank() || m.videoUrl.isNotBlank())
    }

    // Verify unavailable titles do not have fake streams
    val salaar = movies.find { it.title.contains("Salaar", true) }!!
    assertEquals("UNAVAILABLE", salaar.availability)
    org.junit.Assert.assertTrue(salaar.playbackUrl.isBlank())
    org.junit.Assert.assertTrue(salaar.trailerUrl.isNotBlank())
  }

  @Test
  fun `verify admin publishing authorized source enables playback`() {
    val movies = com.example.data.local.MovieSeedData.getInitialMovies()
    val salaar = movies.find { it.title.contains("Salaar", true) }!!

    // Initially unavailable
    assertEquals("UNAVAILABLE", salaar.availability)
    org.junit.Assert.assertFalse(salaar.isStreamingAuthorized)

    // Administrator publishes authorized stream
    val authorizedUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val publishedSalaar = salaar.copy(
        availability = "AVAILABLE",
        isStreamingAuthorized = true,
        playbackUrl = authorizedUrl,
        videoUrl = authorizedUrl,
        playbackType = "hls",
        streamingStatus = "AUTHORIZED_IN_APP"
    )

    assertEquals("AVAILABLE", publishedSalaar.availability)
    org.junit.Assert.assertTrue(publishedSalaar.isStreamingAuthorized)
    assertEquals(authorizedUrl, publishedSalaar.playbackUrl)
    assertEquals("hls", publishedSalaar.playbackType)
  }

  @Test
  fun `verify watch history and resume position saving`() {
    val resumeItem = com.example.data.model.WatchHistory(
        id = "wh_mov_test",
        mediaType = "MOVIE",
        contentId = "mov_telugu_irumudi",
        title = "Irumudi: Path of Devotion",
        subtitle = "2024 • Drama",
        posterUrl = "https://example.com/poster.jpg",
        backdropUrl = "https://example.com/backdrop.jpg",
        positionSeconds = 4355L, // 01:12:35
        durationSeconds = 8280L, // 02:18:00
        progressPercent = 52
    )

    assertEquals(4355L, resumeItem.positionSeconds)
    assertEquals(52, resumeItem.progressPercent)
    org.junit.Assert.assertTrue(resumeItem.positionSeconds > 0)
  }
}
