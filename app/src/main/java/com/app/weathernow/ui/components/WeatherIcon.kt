package com.app.weathernow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherIcon(
    iconCode: String?,
    modifier: Modifier = Modifier,
    size: Int = 80
) {
    val emoji = when {
        iconCode == null -> "🌤️"
        iconCode.startsWith("01") -> "☀️" // clear sky
        iconCode.startsWith("02") -> "⛅" // few clouds
        iconCode.startsWith("03") -> "☁️" // scattered clouds
        iconCode.startsWith("04") -> "☁️" // broken clouds
        iconCode.startsWith("09") -> "🌧️" // shower rain
        iconCode.startsWith("10") -> "🌦️" // rain
        iconCode.startsWith("11") -> "⛈️" // thunderstorm
        iconCode.startsWith("13") -> "❄️" // snow
        iconCode.startsWith("50") -> "🌫️" // mist
        else -> "🌤️"
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size * 0.5).sp
        )
    }
}
