package ru.ikarenkov.teamaker.game.impl.details.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import com.google.accompanist.insets.LocalWindowInsets
import ru.ikarenkov.teamaker.game.impl.details.ui.theme.BackgroundColor

val contentPadding = 24.dp

// For tablets support
val LocalContentPadding = staticCompositionLocalOf { contentPadding }

fun Modifier.contentPadding(): Modifier = composed { padding(horizontal = LocalContentPadding.current) }

@OptIn(ExperimentalMotionApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Preview
@Composable
fun DetailsScreen(state: DetailsScreenState = mockModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.toFloat()

    val swipeableState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)

    val animateMotionLayoutProgress by animateFloatAsState(
        targetValue = if (swipeableState.progress.to == SwipingStates.COLLAPSED) {
            swipeableState.progress.fraction
        } else {
            1f - swipeableState.progress.fraction
        },
        animationSpec = spring()
    )

    val connection = remember { createNestedScrollConnection(swipeableState) }


    val topInsets = LocalDensity.current.run { LocalWindowInsets.current.systemBars.top.toDp() }
    val topContentOffset = topInsets + 26.dp
    val headerHeight = 88.dp

    val buttonHeight = 64.dp
    val bottomInsets = LocalDensity.current.run { LocalWindowInsets.current.systemBars.bottom.toDp() }
    val bottomButtonOffset = bottomInsets + LocalContentPadding.current

    val listTopOffset = buttonHeight + topContentOffset + headerHeight + 23.dp
    val listContentBottomOffset = bottomButtonOffset + listTopOffset + 40.dp

    MotionLayout(
        start = expandedConstraintSet(LocalContentPadding.current, buttonHeight, bottomButtonOffset),
        end = collapsedConstraintSet(LocalContentPadding.current, buttonHeight, bottomButtonOffset),
        progress = animateMotionLayoutProgress,
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to SwipingStates.COLLAPSED,
                    screenHeight to SwipingStates.EXPANDED,
                )
            )
            .nestedScroll(connection)
    ) {
        Image(
            painter = painterResource(id = mockModel.imageRes),
            contentDescription = "Game image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .layoutId(constraints.image)
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(BackgroundColor, Color(0x00050B18)),
                        endY = 126.dp.toPx()
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                }
        )
        val cornersRadius = 22.dp * animateMotionLayoutProgress.reversed
        Box(
            modifier = Modifier
                .layoutId(constraints.contentBackground)
                .clip(RoundedCornerShape(topStart = cornersRadius, topEnd = cornersRadius))
                .background(BackgroundColor)
        )
        DetailsBottomContent(
            state,
            Modifier.layoutId(constraints.content),
            listContentBottomOffset
        )
        GameHeader(
            name = mockModel.name,
            rating = mockModel.rating,
            reviewsCount = mockModel.reviewsCount,
            iconRes = mockModel.iconRes,
            modifier = Modifier
                .layoutId(constraints.header)
                .contentPadding()
                .height(headerHeight)
        )
        Spacer(
            modifier = Modifier
                .layoutId(constraints.topSpacer)
                .height(topContentOffset)
        )
        TopBarButton(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = {},
            animateMotionLayoutProgress = animateMotionLayoutProgress,
            modifier = Modifier.layoutId(constraints.back)
        )
        TopBarButton(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More",
            onClick = {},
            animateMotionLayoutProgress = animateMotionLayoutProgress,
            modifier = Modifier.layoutId(constraints.more),
            iconModifier = Modifier.rotate(90f)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .layoutId(constraints.installButton)
                .height(64.dp),
        ) {
            Text(text = "Install")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun createNestedScrollConnection(swipeableState: SwipeableState<SwipingStates>) = object : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        return if (delta < 0) {
            swipeableState.performDrag(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll( // If there is any leftover sroll from childern, let's try to use it on parent swipe
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        return swipeableState.performDrag(delta).toOffset()
    }

    override suspend fun onPostFling( // Lets's try to use fling on parent and pass all leftover to childern
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        swipeableState.performFling(velocity = available.y)
        return super.onPostFling(consumed, available)
    }

    private fun Float.toOffset() = Offset(0f, this)
}

val Float.reversed get() = (this * -1) + 1

private fun expandedConstraintSet(
    horizontalMargin: Dp,
    installButtonHeight: Dp,
    installButtonBottomOffset: Dp
) = ConstraintSet {
    val back = createRefFor(constraints.back)
    val contentBackground = createRefFor(constraints.contentBackground)
    val content = createRefFor(constraints.content)
    val header = createRefFor(constraints.header)
    val more = createRefFor(constraints.more)
    val image = createRefFor(constraints.image)
    val topSpacer = createRefFor(constraints.topSpacer)
    val installButton = createRefFor(constraints.installButton)

    constrain(image) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        height = Dimension.value(327.dp)
        width = Dimension.matchParent
    }

    constrain(topSpacer) {
        top.linkTo(parent.top)
    }

    constrain(content) {
        top.linkTo(header.bottom, margin = 23.dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }

    constrain(back) {
        top.linkTo(topSpacer.bottom)
        start.linkTo(parent.start, horizontalMargin)
    }

    constrain(more) {
        top.linkTo(topSpacer.bottom)
        end.linkTo(parent.end, horizontalMargin)
    }

    constrain(contentBackground) {
        top.linkTo(image.bottom, margin = (-42).dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(header) {
        top.linkTo(contentBackground.top, margin = (-22).dp)
        start.linkTo(contentBackground.start)
    }

    constrain(installButton) {
        start.linkTo(parent.start, horizontalMargin)
        end.linkTo(parent.end, horizontalMargin)
        top.linkTo(parent.bottom)
        height = Dimension.value(installButtonHeight)
        width = Dimension.fillToConstraints
    }
}

private fun collapsedConstraintSet(
    horizontalMargin: Dp,
    installButtonHeigh: Dp,
    installButtonBottomOffset: Dp
) = ConstraintSet {
    val back = createRefFor(constraints.back)
    val contentBackground = createRefFor(constraints.contentBackground)
    val content = createRefFor(constraints.content)
    val header = createRefFor(constraints.header)
    val more = createRefFor(constraints.more)
    val image = createRefFor(constraints.image)
    val topSpacer = createRefFor(constraints.topSpacer)
    val installButton = createRefFor(constraints.installButton)

    constrain(image) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        height = Dimension.value(0.dp)
        width = Dimension.matchParent
    }

    constrain(topSpacer) {
        top.linkTo(parent.top)
    }

    constrain(content) {
        top.linkTo(header.bottom, margin = 23.dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }

    constrain(back) {
        top.linkTo(topSpacer.bottom)
        end.linkTo(parent.start, horizontalMargin)
    }

    constrain(more) {
        top.linkTo(topSpacer.bottom)
        end.linkTo(parent.end, horizontalMargin)
    }

    constrain(contentBackground) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(header) {
        top.linkTo(topSpacer.bottom)
        start.linkTo(parent.start)
    }

    constrain(installButton) {
        start.linkTo(parent.start, horizontalMargin)
        end.linkTo(parent.end, horizontalMargin)
        bottom.linkTo(parent.bottom, installButtonBottomOffset)
        height = Dimension.value(installButtonHeigh)
        width = Dimension.fillToConstraints
    }
}

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

enum class constraints {
    contentBackground,
    content,
    back,
    more,
    header,
    topSpacer,
    image,
    installButton,
}

@Composable
private fun TopBarButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    animateMotionLayoutProgress: Float,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier
            .size(36.dp + 20.dp * animateMotionLayoutProgress.reversed)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.28f * animateMotionLayoutProgress.reversed)),
        onClick = onClick
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription, tint = Color.White, modifier = iconModifier)
    }
}