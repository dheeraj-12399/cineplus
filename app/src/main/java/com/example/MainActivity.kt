package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.navigation.CineBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.details.MovieDetailsScreen
import com.example.ui.screens.details.TvShowDetailsScreen
import com.example.ui.screens.history.WatchHistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.movies.MoviesScreen
import com.example.ui.screens.mylist.MyListScreen
import com.example.ui.screens.ott.OttScreen
import com.example.ui.screens.player.VideoPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.shows.TvShowsScreen
import com.example.ui.screens.telugu.TeluguScreen
import com.example.ui.theme.CineBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CineViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CinePulseApp()
            }
        }
    }
}

@Composable
fun CinePulseApp(
    viewModel: CineViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topLevelRoutes = listOf(
        Screen.Home.route,
        Screen.Telugu.route,
        Screen.Ott.route,
        Screen.Movies.route,
        Screen.MyList.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        containerColor = CineBackground,
        bottomBar = {
            if (showBottomBar) {
                CineBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    },
                    onNavigateToShow = { showId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(showId))
                    },
                    onPlayMovie = { movieId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("MOVIE", movieId))
                    },
                    onPlayEpisode = { showId, episodeId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("TV_SHOW", showId, episodeId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToAdmin = {
                        navController.navigate(Screen.Admin.route)
                    }
                )
            }

            // 2. Telugu Cinema Screen
            composable(Screen.Telugu.route) {
                TeluguScreen(
                    viewModel = viewModel,
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    }
                )
            }

            // 3. OTT Hub Screen
            composable(Screen.Ott.route) {
                OttScreen(
                    viewModel = viewModel,
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    },
                    onNavigateToShow = { showId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(showId))
                    }
                )
            }

            // 4. Movies Screen
            composable(Screen.Movies.route) {
                MoviesScreen(
                    viewModel = viewModel,
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    }
                )
            }

            // 5. TV Shows Screen
            composable(Screen.TvShows.route) {
                TvShowsScreen(
                    viewModel = viewModel,
                    onNavigateToShow = { showId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(showId))
                    }
                )
            }

            // 4. My List Screen
            composable(Screen.MyList.route) {
                MyListScreen(
                    viewModel = viewModel,
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    },
                    onNavigateToShow = { showId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(showId))
                    },
                    onExplore = {
                        navController.navigate(Screen.Movies.route)
                    }
                )
            }

            // 5. Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToMyList = {
                        navController.navigate(Screen.MyList.route)
                    },
                    onNavigateToWatchHistory = {
                        navController.navigate(Screen.WatchHistory.route)
                    },
                    onNavigateToAdmin = {
                        navController.navigate(Screen.Admin.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            // 6. Movie Details Screen
            composable(
                route = Screen.MovieDetails.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                MovieDetailsScreen(
                    movieId = movieId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPlayMovie = { mId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("MOVIE", mId))
                    },
                    onNavigateToMovie = { mId ->
                        navController.navigate(Screen.MovieDetails.createRoute(mId))
                    }
                )
            }

            // 7. TV Show Details Screen
            composable(
                route = Screen.TvShowDetails.route,
                arguments = listOf(navArgument("showId") { type = NavType.StringType })
            ) { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId") ?: ""
                TvShowDetailsScreen(
                    showId = showId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPlayEpisode = { sId, epId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("TV_SHOW", sId, epId))
                    }
                )
            }

            // 8. Fullscreen Video Player Screen
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(
                    navArgument("mediaType") { type = NavType.StringType },
                    navArgument("contentId") { type = NavType.StringType },
                    navArgument("episodeId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "MOVIE"
                val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
                val episodeId = backStackEntry.arguments?.getString("episodeId")
                VideoPlayerScreen(
                    mediaType = mediaType,
                    contentId = contentId,
                    episodeId = episodeId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 9. Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovie = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    },
                    onNavigateToShow = { showId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(showId))
                    }
                )
            }

            // 10. Watch History Screen
            composable(Screen.WatchHistory.route) {
                WatchHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPlayMovie = { movieId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("MOVIE", movieId))
                    },
                    onPlayEpisode = { showId, episodeId ->
                        navController.navigate(Screen.VideoPlayer.createRoute("TV_SHOW", showId, episodeId))
                    }
                )
            }

            // 11. Admin CMS Screen
            composable(Screen.Admin.route) {
                AdminScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
