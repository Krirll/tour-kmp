package ru.krirll.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val ROUNDED_MAX = 24
private const val ROUNDED_MIN = 6

@Composable
fun ExpressiveLazyColumn(
    modifier: Modifier,
    contentPadding: PaddingValues,
    items: List<@Composable () -> Unit>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items.forEachIndexed { index, callback ->
            val topRadius = if (index == 0) {
                ROUNDED_MAX.dp
            } else {
                ROUNDED_MIN.dp
            }
            val bottomRadius = if (index == items.lastIndex) {
                ROUNDED_MAX.dp
            } else {
                ROUNDED_MIN.dp
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    shape = RoundedCornerShape(
                        topStart = topRadius,
                        topEnd = topRadius,
                        bottomStart = bottomRadius,
                        bottomEnd = bottomRadius
                    ),
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 1.dp
                    )
                ) {
                    callback()
                }
            }
        }
    }
}
