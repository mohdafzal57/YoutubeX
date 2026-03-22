package com.mak.youtubex.presentation.upload_video

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mak.youtubex.presentation.upload.ContentCreationMode
import java.util.Locale.getDefault
import kotlin.math.roundToInt


private const val ITEM_WIDTH_DP = 90f

@Composable
fun ModeSelector(
    selectedMode: ContentCreationMode,
    onModeSelected: (ContentCreationMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = ContentCreationMode.allModes
    val selectedIndex = modes.indexOf(selectedMode).coerceAtLeast(0)
    val density = LocalDensity.current
    val itemWidthPx = with(density) { ITEM_WIDTH_DP.dp.toPx() }

    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val settledTranslation by animateFloatAsState(
        targetValue = -(selectedIndex * itemWidthPx),
        animationSpec = tween(
            durationMillis = 180,
            easing = FastOutSlowInEasing
        ),
        label = "settled"
    )

    // Always compute from live values — works correctly during and after drag
    val rowTranslationPx = if (isDragging) {
        settledTranslation + dragOffsetPx
    } else {
        settledTranslation
    }

    val liveIndex: Int = run {
        val raw = (-rowTranslationPx / itemWidthPx).roundToInt()
        raw.coerceIn(0, modes.lastIndex)
    }

    Box(
        modifier = modifier
            .width((ITEM_WIDTH_DP * modes.size).dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {

        // ── Sliding labels ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = rowTranslationPx +
                            (modes.size * itemWidthPx / 2f) -
                            (itemWidthPx / 2f)
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragOffsetPx = 0f
                        },
                        onHorizontalDrag = { _, delta ->
                            dragOffsetPx += delta
                        },
                        onDragEnd = {
                            // Compute final translation using the last drag offset
                            val finalTranslation = settledTranslation + dragOffsetPx

                            val snappedIndex = (-finalTranslation / itemWidthPx)
                                .roundToInt()
                                .coerceIn(0, modes.lastIndex)

                            dragOffsetPx = 0f
                            isDragging = false

                            onModeSelected(modes[snappedIndex])
                        },
                        onDragCancel = {
                            dragOffsetPx = 0f
                            isDragging = false
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            modes.forEachIndexed { index, mode ->
                val isLive = index == liveIndex

                val alpha by animateFloatAsState(
                    targetValue = when {
                        isLive -> 1f
                        kotlin.math.abs(index - liveIndex) == 1 -> 0.5f
                        else -> 0.3f
                    },
                    animationSpec = tween(150),
                    label = "alpha_$index"
                )
                val scale by animateFloatAsState(
                    targetValue = if (isLive) 1f else 0.88f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "scale_$index"
                )

                Box(
                    modifier = Modifier
                        .width(ITEM_WIDTH_DP.dp)
                        .height(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onModeSelected(mode) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                getDefault()
                            ) else it.toString()
                        },
                        color = Color.White.copy(alpha = alpha),
                        fontSize = 13.sp,
                        fontWeight = if (isLive) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )
                }
            }
        }

        // ── Fixed frosted-glass pill ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(ITEM_WIDTH_DP.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.07f)
                        )
                    )
                )
                .border(
                    width = 0.6.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        )
    }
}



