package com.melody

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.melody.core.theme.MelodyTheme
import com.melody.core.ui.components.MiniPlayer
import com.melody.feature.settings.SettingsViewModel
import com.melody.navigation.MelodyNavGraph
import com.melody.navigation.Screen
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var melodyPlayer: MelodyPlayer

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions granted, scanner can query local files
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()

        setContent {
            // Collect the user-selected custom app color from settings
            val settingsViewModel: SettingsViewModel = viewModel()
            val appColorLong by settingsViewModel.appColor.collectAsState()
            val customColor = appColorLong?.let { Color(it) }

            MelodyTheme(customPrimaryColor = customColor) {
                val navController = rememberNavController()
                val playerState by melodyPlayer.playerState.collectAsState()
                var currentTab by remember { mutableStateOf<Screen>(Screen.Library) }

                // Calculate seek percentage for the miniplayer
                val progress = if (playerState.duration > 0) {
                    playerState.playbackPosition.toFloat() / playerState.duration.toFloat()
                } else 0f

                Scaffold(
                    bottomBar = {
                        Column {
                            // Persistent MiniPlayer directly above the navigation bar
                            if (playerState.currentSong != null) {
                                MiniPlayer(
                                    song = playerState.currentSong,
                                    isPlaying = playerState.isPlaying,
                                    progress = progress,
                                    onPlayPauseClick = {
                                        if (playerState.isPlaying) melodyPlayer.pause() else melodyPlayer.play()
                                    },
                                    onNextClick = { melodyPlayer.skipToNext() },
                                    onClick = {
                                        navController.navigate(Screen.Player.route)
                                    }
                                )
                            }
                            NavigationBar {
                                val items = listOf(
                                    Triple(Screen.Library, "Library", Icons.Default.LibraryMusic),
                                    Triple(Screen.Playlists, "Playlists", Icons.Default.PlaylistPlay),
                                    Triple(Screen.Search, "Search", Icons.Default.Search),
                                    Triple(Screen.Settings, "Settings", Icons.Default.Settings)
                                )
                                items.forEach { (screen, label, icon) ->
                                    NavigationBarItem(
                                        selected = currentTab == screen,
                                        onClick = {
                                            currentTab = screen
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(imageVector = icon, contentDescription = label) },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MelodyNavGraph(
                            navController = navController,
                            melodyPlayer = melodyPlayer
                        )
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            requestPermissionsLauncher.launch(missing.toTypedArray())
        }
    }
}
