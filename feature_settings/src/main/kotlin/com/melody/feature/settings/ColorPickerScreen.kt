package com.melody.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// 24 beautiful preset colors (Material You palette + premium tones)
private val presetColors = listOf(
    Color(0xFF6650A4),  // Material Purple
    Color(0xFF9C27B0),  // Deep Purple
    Color(0xFF3F51B5),  // Indigo
    Color(0xFF2196F3),  // Blue
    Color(0xFF03A9F4),  // Light Blue
    Color(0xFF00BCD4),  // Cyan
    Color(0xFF009688),  // Teal
    Color(0xFF4CAF50),  // Green
    Color(0xFF8BC34A),  // Light Green
    Color(0xFFCDDC39),  // Lime
    Color(0xFFFFC107),  // Amber
    Color(0xFFFF9800),  // Orange
    Color(0xFFFF5722),  // Deep Orange
    Color(0xFFF44336),  // Red
    Color(0xFFE91E63),  // Pink
    Color(0xFF607D8B),  // Blue Grey
    Color(0xFF1B5E20),  // Deep Forest Green
    Color(0xFF1A237E),  // Deep Indigo
    Color(0xFF880E4F),  // Dark Pink
    Color(0xFF4A148C),  // Dark Purple
    Color(0xFF006064),  // Dark Cyan
    Color(0xFF33691E),  // Dark Olive
    Color(0xFFBF360C),  // Dark Deep Orange
    Color(0xFF37474F),  // Dark Blue Grey
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedColor by viewModel.appColor.collectAsState()
    val currentSelected = selectedColor?.let { Color(it) }
    val defaultPrimary = MaterialTheme.colorScheme.primary
    var customRed by remember {
        mutableFloatStateOf(currentSelected?.red ?: defaultPrimary.red)
    }
    var customGreen by remember {
        mutableFloatStateOf(currentSelected?.green ?: defaultPrimary.green)
    }
    var customBlue by remember {
        mutableFloatStateOf(currentSelected?.blue ?: defaultPrimary.blue)
    }

    val customColor = Color(customRed, customGreen, customBlue)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Color") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Preview swatch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Selected Color",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(currentSelected ?: MaterialTheme.colorScheme.primary)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Preset Colors",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Preset color grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(presetColors) { color ->
                    val isSelected = currentSelected == color
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                else Modifier
                            )
                            .clickable { viewModel.setAppColor(color.toArgb().toLong()) }
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            // Custom color slider section
            Text(
                text = "Custom Color",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Preview of custom color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(customColor)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Red slider
            Text("Red", style = MaterialTheme.typography.labelMedium, color = Color(0xFFF44336))
            Slider(
                value = customRed,
                onValueChange = { customRed = it },
                colors = SliderDefaults.colors(thumbColor = Color(0xFFF44336), activeTrackColor = Color(0xFFF44336)),
                modifier = Modifier.fillMaxWidth()
            )

            // Green slider
            Text("Green", style = MaterialTheme.typography.labelMedium, color = Color(0xFF4CAF50))
            Slider(
                value = customGreen,
                onValueChange = { customGreen = it },
                colors = SliderDefaults.colors(thumbColor = Color(0xFF4CAF50), activeTrackColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            )

            // Blue slider
            Text("Blue", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2196F3))
            Slider(
                value = customBlue,
                onValueChange = { customBlue = it },
                colors = SliderDefaults.colors(thumbColor = Color(0xFF2196F3), activeTrackColor = Color(0xFF2196F3)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Apply custom color button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(customColor)
                    .clickable { viewModel.setAppColor(customColor.toArgb().toLong()) }
            ) {
                Text(
                    text = "Apply This Color",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    }
}
