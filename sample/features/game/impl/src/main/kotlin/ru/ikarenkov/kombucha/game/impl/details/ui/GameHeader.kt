package ru.ikarenkov.kombucha.game.impl.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ikarenkov.sample.game.R
import ru.ikarenkov.kombucha.game.impl.details.ui.theme.Typography

@Suppress("MagicNumber")
@Composable
fun GameHeader(
    name: String,
    rating: Float,
    reviewsCount: Int,
    iconRes: Int,
    modifier: Modifier = Modifier.height(88.dp)
) {
    Row(modifier.height(IntrinsicSize.Max)) {
        val imageShape = RoundedCornerShape(18.dp)
        Image(
            painter = painterResource(iconRes),
            contentDescription = "Game icon",
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .border(2.dp, Color(0xFF1F2430), imageShape)
                .clip(imageShape)
                .background(Color.Black)
                .padding(15.dp)
        )
        Spacer(Modifier.size(12.dp))
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
            // TODO: fix text color
            Text(text = name, style = Typography.h4)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarsRating(rating = rating)
                Spacer(modifier = Modifier.size(10.dp))
                // TODO: fix text color
                Text(
                    text = reviewsCount.formatReviewCount(),
                    style = Typography.subtitle1,
                    color = Color(0xFF45454D)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
fun PreviewGameHeader() {
    GameHeader(
        name = mockModel.name,
        rating = mockModel.rating,
        reviewsCount = mockModel.reviewsCount,
        iconRes = mockModel.iconRes
    )
}

private const val STARS_COUNT = 5

// TODO: support float
@Composable
fun StarsRating(rating: Float, modifier: Modifier = Modifier) {
    val actualRating = rating.coerceIn(0f, STARS_COUNT.toFloat())
    Row(
        modifier
            .height(14.dp)
            .padding(vertical = 1.dp)
    ) {
        for (i in 0 until STARS_COUNT) {
            Image(
                painter = painterResource(id = R.drawable.icon_star),
                contentDescription = null,
                modifier = Modifier
                    .drawWithContent {
                        drawContext.canvas.withSaveLayer(size.toRect(), Paint()) {
                            drawContent()
                            if (i >= actualRating.toInt()) {
                                val fraction = (actualRating - i).let { if (it >= 1f) 0f else it }
                                @Suppress("MagicNumber")
                                drawRect(
                                    color = Color(0xFF282E3E),
                                    topLeft = Offset(x = fraction * size.width, y = 0f),
                                    blendMode = BlendMode.SrcIn
                                )
                            }
                        }
                    }
            )
            if (i != STARS_COUNT - 1) {
                Spacer(modifier = Modifier.size(4.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewStars() {
    StarsRating(rating = 2f)
}