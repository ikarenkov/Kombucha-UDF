package com.github.ikarenkov.kombucha.game.impl.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.ikarenkov.sample.game.R
import com.github.ikarenkov.kombucha.game.impl.details.ui.theme.Typography
import java.util.Locale

@Composable
fun DetailsBottomContent(
    state: DetailsScreenState,
    modifier: Modifier = Modifier,
    bottomOffset: Dp = 0.dp
) {
    LazyColumn(modifier) {
        item {
            GenresRow(state.genres)
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                text = state.description,
                modifier = Modifier.contentPadding(),
                style = Typography.body1
            )
            Spacer(modifier = Modifier.size(21.dp))
            MediaRow(state.media)
            Spacer(modifier = Modifier.size(20.dp))
            ReviewHeader(state.rating, state.reviewsCount)
            Spacer(modifier = Modifier.size(30.dp))
        }
        itemsIndexed(state.topReviews) { pos, item ->
            ReviewItem(review = item, modifier = Modifier.contentPadding())
            if (pos != state.topReviews.lastIndex) {
                Divider(
                    modifier = Modifier
                        .padding(24.dp)
                        .contentPadding()
                        .fillParentMaxWidth()
                )
            }
        }
        item {
            Spacer(modifier = Modifier.size(bottomOffset))
        }
    }
}

@Composable
private fun MediaRow(items: List<DetailsMedia>) {
    LazyRow {
        itemsIndexed(items) { pos, item ->
            DecorateListItem(
                pos = pos,
                size = items.size,
                innerOffset = 15.dp,
                outerOffset = LocalContentPadding.current
            ) {
                DetailsMediaContent(item)
            }
        }
    }
}

@Composable
private fun GenresRow(items: List<String>) {
    LazyRow(Modifier.fillMaxWidth()) {
        itemsIndexed(items) { pos, item ->
            DecorateListItem(
                pos = pos,
                size = items.size,
                innerOffset = 10.dp,
                outerOffset = LocalContentPadding.current
            ) {
                GenreCell(text = item)
            }
        }
    }
}

@Composable
private fun ReviewHeader(rating: Float, reviewsCount: Int) {
    Column(Modifier.contentPadding()) {
        Text(text = "Review & Ratings", style = Typography.h5)
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = String.format(Locale.US, "%.1f", rating),
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.h3,
            )
            Column(
                modifier = Modifier
                    .alignByBaseline()
                    .padding(start = 16.dp),
            ) {
                StarsRating(rating = rating)
                Spacer(modifier = Modifier.size(8.dp))
                @Suppress("MagicNumber")
                Text(
                    text = reviewsCount.formatReviewCount() + " Reviews",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color(0xFFA8ADB7)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewReviewHeader() {
    ReviewHeader(rating = 3f, reviewsCount = 10_000_000)
}

@Composable
fun DecorateListItem(
    pos: Int,
    size: Int,
    innerOffset: Dp = 0.dp,
    outerOffset: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Spacer(modifier = Modifier.size(if (pos == 0) outerOffset else innerOffset))
    content()
    if (pos == size - 1) {
        Spacer(modifier = Modifier.size(outerOffset))
    }
}

@Preview
@Composable
fun PreviewDetailsBottomContent() {
    DetailsBottomContent(state = mockModel)
}

@Composable
fun DetailsMediaContent(media: DetailsMedia) {
    Box(
        modifier = Modifier
            .size(height = 135.dp, width = 240.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        Image(
            painter = painterResource(id = media.previewImage),
            contentDescription = when (media) {
                is DetailsMedia.Image -> "Game image"
                is DetailsMedia.Video -> "Game video"
            },
            contentScale = ContentScale.Crop,
        )
        if (media is DetailsMedia.Video) {
            Icon(
                painter = painterResource(id = R.drawable.icon_play),
                contentDescription = "Play",
                modifier = Modifier
                    .align(Center)
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.24f), CircleShape)
                    .padding(12.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewDetailsImage() {
    DetailsMediaContent(mockDetailsMediaImage)
}

@Preview
@Composable
fun PreviewDetailsVideo() {
    DetailsMediaContent(mockDetailsMediaVideo)
}

@Suppress("MagicNumber")
@Composable
fun GenreCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(22.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color(0x3D44A9F4))
            .padding(horizontal = 10.dp),
        contentAlignment = Center
    ) {
        Text(
            text = text.uppercase(),
            style = Typography.caption,
        )
    }
}

@Preview
@Composable
fun PreviewGenreCell() {
    Row {
        GenreCell(text = "moba")
    }
}

@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(Modifier.height(40.dp)) {
            Image(
                painter = painterResource(id = review.reviewerImage),
                contentDescription = "Reviewer image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .align(CenterVertically)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = review.reviewerName, style = Typography.h6)
                Text(text = review.reviewDate, style = Typography.subtitle1)
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = review.review)
    }
}

@Preview
@Composable
fun PreviewReviewItem() {
    ReviewItem(review = mockReview1)
}