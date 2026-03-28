package com.mak.youtubex.presentation.main.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YTPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .align(Alignment.TopCenter),
                containerColor = MaterialTheme.colorScheme.surface,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()

    // Pulse animation while refreshing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "rotationAngle"
    )

    // Hue shift for chromatic ring
    val hueShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "hueShift"
    )

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val surface = MaterialTheme.colorScheme.surface

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Chromatic glow ring (only while refreshing)
                if (isRefreshing) {
                    Canvas(
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer { rotationZ = rotationAngle }
                    ) {
                        val strokeWidth = 4.dp.toPx()
                        val radius = size.minDimension / 2f - strokeWidth
                        val sweepAngle = 270f

                        // Rainbow sweep arc
                        val colors = List(6) { i ->
                            Color.hsv((hueShift + i * 60f) % 360f, 0.9f, 1f)
                        }
                        val brush = Brush.sweepGradient(colors)
                        drawArc(
                            brush = brush,
                            startAngle = 0f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(strokeWidth, strokeWidth),
                            size = Size(radius * 2, radius * 2)
                        )
                    }
                }

                // Blob container — squircle-ish with animated scale
                Box(
                    modifier = Modifier
                        .size(if (isRefreshing) 52.dp else 48.dp)
                        .graphicsLayer {
                            scaleX = if (isRefreshing) pulseScale * 0.95f else 1f
                            scaleY = if (isRefreshing) pulseScale else 1f
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    tertiary.copy(alpha = 0.85f),
                                    primary
                                )
                            ),
                            shape = GenericShape { size, _ ->
                                // Squircle path (superellipse feel)
                                val r = size.minDimension / 2f
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                moveTo(cx, cy - r)
                                cubicTo(cx + r * 0.85f, cy - r, cx + r, cy - r * 0.85f, cx + r, cy)
                                cubicTo(cx + r, cy + r * 0.85f, cx + r * 0.85f, cy + r, cx, cy + r)
                                cubicTo(cx - r * 0.85f, cy + r, cx - r, cy + r * 0.85f, cx - r, cy)
                                cubicTo(cx - r, cy - r * 0.85f, cx - r * 0.85f, cy - r, cx, cy - r)
                                close()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRefreshing) {
                        // Bouncy icon while loading
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Refreshing",
                            tint = surface,
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer { rotationZ = rotationAngle * 0.5f }
                        )
                    } else {
                        // Pull progress indicator
                        val pullFraction = state.distanceFraction.coerceIn(0f, 1f)
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Pull to refresh",
                            tint = surface,
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer {
                                    rotationZ = pullFraction * 180f
                                    scaleX = 0.6f + pullFraction * 0.4f
                                    scaleY = 0.6f + pullFraction * 0.4f
                                }
                        )
                    }
                }
            }
        }
    ) {
        content()
    }
}
