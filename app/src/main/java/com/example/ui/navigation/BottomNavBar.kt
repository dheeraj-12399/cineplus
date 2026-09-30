package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CineRed
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineTextSecondary

data class CineBottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun CineBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        CineBottomNavItem(
            route = Screen.Home.route,
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        CineBottomNavItem(
            route = Screen.Telugu.route,
            title = "Telugu",
            selectedIcon = Icons.Filled.MovieFilter,
            unselectedIcon = Icons.Outlined.MovieFilter,
            testTag = "nav_telugu"
        ),
        CineBottomNavItem(
            route = Screen.Ott.route,
            title = "OTT",
            selectedIcon = Icons.Filled.LiveTv,
            unselectedIcon = Icons.Outlined.LiveTv,
            testTag = "nav_ott"
        ),
        CineBottomNavItem(
            route = Screen.Movies.route,
            title = "Movies",
            selectedIcon = Icons.Filled.Movie,
            unselectedIcon = Icons.Outlined.Movie,
            testTag = "nav_movies"
        ),
        CineBottomNavItem(
            route = Screen.MyList.route,
            title = "My List",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            testTag = "nav_my_list"
        )
    )

    NavigationBar(
        containerColor = CineSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("cine_bottom_nav_bar")
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = CineTextSecondary,
                    unselectedTextColor = CineTextSecondary,
                    indicatorColor = CineRed
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
