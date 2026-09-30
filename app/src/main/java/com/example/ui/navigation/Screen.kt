package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Telugu : Screen("telugu")
    data object Ott : Screen("ott")
    data object Movies : Screen("movies")
    data object TvShows : Screen("tv_shows")
    data object MyList : Screen("my_list")
    data object Profile : Screen("profile")
    data object Search : Screen("search")
    data object WatchHistory : Screen("watch_history")
    data object Admin : Screen("admin")

    data object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: String) = "movie_details/$movieId"
    }

    data object TvShowDetails : Screen("show_details/{showId}") {
        fun createRoute(showId: String) = "show_details/$showId"
    }

    data object VideoPlayer : Screen("video_player/{mediaType}/{contentId}?episodeId={episodeId}") {
        fun createRoute(mediaType: String, contentId: String, episodeId: String? = null): String {
            return if (episodeId != null) {
                "video_player/$mediaType/$contentId?episodeId=$episodeId"
            } else {
                "video_player/$mediaType/$contentId?episodeId="
            }
        }
    }
}
