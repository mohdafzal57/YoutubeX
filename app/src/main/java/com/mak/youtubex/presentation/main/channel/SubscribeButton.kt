package com.mak.youtubex.presentation.main.channel

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun YouTubeSubscribeButton(
    isSubscribed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }

    // Button scale animation
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "scale"
    )

    // Bell shake animation
    val bellRotation by animateFloatAsState(
        targetValue = if (isAnimating && isSubscribed) 15f else 0f,
        animationSpec = keyframes {
            durationMillis = 300
            0f at 0
            15f at 75
            -15f at 150
            15f at 225
            0f at 300
        },
        label = "bell_shake"
    )

    Button(
        onClick = {
            scope.launch {
                isAnimating = true
                onClick()
                delay(200)
                isAnimating = false
            }
        },
        modifier = modifier
            .height(36.dp)
            .scale(scale),
        // YouTube uses high-contrast Inverse colors for Subscribe
        // and SurfaceVariant/Tonal colors for Subscribed
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSubscribed) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface // Adaptive Black/White
            },
            contentColor = if (isSubscribed) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.surface // Adaptive White/Black
            }
        ),
        shape = CircleShape, // YouTube uses fully rounded corners
        contentPadding = PaddingValues(horizontal = 12.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        AnimatedContent(
            targetState = isSubscribed,
            label = "content_fade",
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            }
        ) { targetSubscribed ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (targetSubscribed) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { rotationZ = bellRotation }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Subscribed",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "Subscribe",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
        }
    }
}