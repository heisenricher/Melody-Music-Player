package com.melody.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class MelodyWidget : GlanceAppWidget() {

    override suspend fun provideContent(context: Context, id: GlanceId) {
        provideContent {
            // Widget UI using Glance Compose components
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "Melody Player",
                    style = TextStyle(
                        color = ColorProvider(android.R.color.white)
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Button(
                        text = "Prev",
                        onClick = {}
                    )
                    Spacer(modifier = GlanceModifier.padding(horizontal = 8.dp))
                    Button(
                        text = "Play",
                        onClick = {}
                    )
                    Spacer(modifier = GlanceModifier.padding(horizontal = 8.dp))
                    Button(
                        text = "Next",
                        onClick = {}
                    )
                }
            }
        }
    }
}
