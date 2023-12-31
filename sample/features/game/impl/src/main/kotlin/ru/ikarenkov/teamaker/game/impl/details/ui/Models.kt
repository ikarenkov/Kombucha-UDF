package ru.ikarenkov.teamaker.game.impl.details.ui

import androidx.annotation.DrawableRes
import com.github.ikarenkov.sample.game.R


data class DetailsScreenState(
    val name: String,
    val rating: Float,
    val reviewsCount: Int,
    val topReviews: List<Review>,
    @DrawableRes
    val imageRes: Int,
    val iconRes: Int,
    val genres: List<String>,
    val description: String,
    val media: List<DetailsMedia>,
)

sealed interface DetailsMedia {

    val previewImage: Int

    data class Video(
        @DrawableRes
        override val previewImage: Int,
        val videoUrl: String
    ) : DetailsMedia

    data class Image(
        @DrawableRes
        override val previewImage: Int
    ) : DetailsMedia

}

data class Review(
    val reviewerImage: Int,
    val reviewerName: String,
    val reviewDate: String,
    val review: String
)

val mockDetailsMediaVideo = DetailsMedia.Video(R.drawable.image_dota_video_preview, "")
val mockDetailsMediaImage = DetailsMedia.Image(R.drawable.image_dota_preview)

val mockReview1 = Review(
    R.drawable.image_person_1,
    "Auguste Conte",
    "February 14, 2019",
    "“Once you start to learn its secrets, there’s a wild and exciting variety of play here that’s unmatched, even by its peers.”"
)

val mockReview2 = Review(
    R.drawable.image_person_2,
    "Jang Marcelino",
    "February 14, 2019",
    "“Once you start to learn its secrets, there’s a wild and exciting variety of play here that’s unmatched, even by its peers.”"
)

val mockModel = DetailsScreenState(
    name = "DoTA 2",
    rating = 4.7f,
    reviewsCount = 70_000_000,
    genres = listOf("MOBA", "MULTIPLAYER", "STRATEGY", "action", "battle arena", "MOBA", "MULTIPLAYER", "STRATEGY", "action", "battle arena"),
    description = "Dota 2 is a multiplayer online battle arena (MOBA) game which has two teams of five players compete to collectively destroy a " +
        "large structure defended by the opposing team known as the \"Ancient\", whilst defending their own.",
    imageRes = R.drawable.image_dota,
    iconRes = R.drawable.icon_dota,
    media = listOf(
        mockDetailsMediaVideo,
        mockDetailsMediaImage,
        mockDetailsMediaVideo,
        mockDetailsMediaImage,
        mockDetailsMediaVideo,
        mockDetailsMediaImage,
    ),
    topReviews = listOf(
        mockReview1,
        mockReview2,
        mockReview1,
        mockReview2,
        mockReview1,
        mockReview2,
        mockReview1,
        mockReview2,
        mockReview1,
        mockReview2,
    )
)