package ru.ikarenkov.kombucha.learn_compose.impl

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun <T> CardsStack(
    items: List<T>,
    topItem: Int,
    onTopSwiped: () -> Unit,
    visibleItemsCount: Int = 4,
    itemContent: @Composable BoxScope.(pos: Int) -> Unit
) {
    val offset: Animatable<Offset, AnimationVector2D> = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        val maxWidthPx = LocalDensity.current.run { minWidth.toPx() }
        val relativeCompletion = (offset.value.x.absoluteValue / (maxWidthPx / 4)).coerceAtMost(1f)

        val actualCardsCount = min(visibleItemsCount, items.count() - topItem)
        val lastVisibleItem = topItem + actualCardsCount - 1
        val hiddenItem = lastVisibleItem + 1
        if (hiddenItem < items.size) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .scaleAndOffset(
                        hiddenItem - topItem,
                        relativeCompletion
                    )
                    .alpha(relativeCompletion),
            ) {
                itemContent(hiddenItem)
            }
        }
        for (visibleIndex in lastVisibleItem downTo topItem) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .scaleAndOffset(
                        visibleIndex - topItem,
                        if (visibleIndex != topItem) relativeCompletion else 0f
                    )
                    .then(
                        if (visibleIndex == topItem) {
                            Modifier.swipeToDismiss(
                                offset = offset,
                                onDismissed = {
                                    scope.launch { offset.snapTo(Offset.Zero) }
                                    onTopSwiped()
                                },
                                maxWidthPx = maxWidthPx
                            )
                        } else {
                            Modifier
                        }
                    ),
            ) {
                itemContent(visibleIndex)
            }
        }
    }
}

private fun Modifier.swipeToDismiss(
    offset: Animatable<Offset, AnimationVector2D>,
    onDismissed: () -> Unit,
    maxWidthPx: Float
): Modifier = composed {
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay = splineBasedDecay<Offset>(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (isActive) {
                Log.d("swipeToDismiss", "while true")
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                offset.stop()
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    Log.d("swipeToDismiss", "awaitPointerEventScope")
                    drag(pointerId) { change ->
                        val dragOffset = offset.value + change.positionChange()
                        launch {
                            offset.snapTo(dragOffset)
                        }
                        // Record the velocity of the drag.
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // Consume the gesture event, not passed to external
                        change.consumePositionChange()
                    }
                }
                // Dragging finished. Calculate the velocity of the fling.
                val velocity = velocityTracker.calculateVelocity()
                    .let { Offset(it.x, it.y) }
                val targetOffset = decay.calculateTargetValue(Offset.VectorConverter, offset.value, velocity)
                offset.updateBounds(
                    lowerBound = Offset(-maxWidthPx, -size.height.toFloat()),
                    upperBound = Offset(maxWidthPx, size.height.toFloat())
                )
                launch {
                    if (targetOffset.x.absoluteValue <= maxWidthPx) {
                        // Not enough velocity; Slide back.
                        offset.animateTo(targetValue = Offset.Zero, initialVelocity = Offset(velocity.x, velocity.y))
                    } else {
                        // Enough velocity to slide away the element to the edge.
                        offset.animateDecay(Offset(velocity.x, velocity.y), decay)
                        // The element was swiped away.
                        onDismissed()
                    }
                }
            }
        }
    }
        .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
        .graphicsLayer(
            rotationZ = (offset.value.x / 60).coerceIn(-40f, 40f)
        )
}

private fun Modifier.scaleAndOffset(relativePos: Int, relativeOffset: Float) = composed {
    val scale = 1f - 0.1f * relativePos + 0.1f * relativeOffset
    val offset = 16.dp * (relativePos - relativeOffset) * scale
    Log.d("scaleAndOffset", "relativePos: $relativePos, relativeOffset: $relativeOffset, scale: $scale")
    Modifier.graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        // For scale at center bottom
        transformOrigin = TransformOrigin(0.5f, 1f),
        translationY = with(LocalDensity.current) { offset.toPx() }
    )
}