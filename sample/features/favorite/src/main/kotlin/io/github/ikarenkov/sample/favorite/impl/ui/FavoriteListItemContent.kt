package io.github.ikarenkov.sample.favorite.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FavoriteListItemContent(
    item: FavoriteListItem.Item?,
    removeFavoriteClick: (() -> Unit)?,
    itemClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { itemClick?.invoke() },
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
                .padding(start = 8.dp)
                .then(if (item == null) Modifier.shimmer() else Modifier)
        ) {
            Text(
                text = item?.title ?: ITEM_PLACEHOLDER_TEXT,
                style = MaterialTheme.typography.subtitle1,
                color = if (item == null) Color.Transparent else Color.Unspecified,
                modifier = if (item == null) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                } else Modifier
            )
            Box(Modifier.weight(1f))
            IconButton(
                onClick = removeFavoriteClick ?: {},
            ) {
                if (item != null) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Favorite),
                        contentDescription = "remove from favorite",
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewListItem() {
    MaterialTheme {
        FavoriteListItemContent(
            item = FavoriteListItem.Item("83135", "Item 12345"),
            removeFavoriteClick = {},
            itemClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PreviewSkeletonListItem() {
    MaterialTheme {
        FavoriteListItemContent(
            item = null,
            removeFavoriteClick = {},
            itemClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(widthDp = 500)
@Composable
private fun PreviewFuvWithSkeletonListItem() {
    MaterialTheme {
        Row {
            FavoriteListItemContent(
                item = FavoriteListItem.Item("83135", ITEM_PLACEHOLDER_TEXT),
                removeFavoriteClick = {},
                itemClick = {},
                modifier = Modifier.weight(1f)
            )
            FavoriteListItemContent(
                item = null,
                removeFavoriteClick = {},
                itemClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private const val ITEM_PLACEHOLDER_TEXT = "Item 12345678"