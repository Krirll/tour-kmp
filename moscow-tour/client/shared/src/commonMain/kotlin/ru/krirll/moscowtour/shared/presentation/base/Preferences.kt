package ru.krirll.moscowtour.shared.presentation.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val PREF_ITEM_PADDING = 16

@Composable
fun PreferenceCategory(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(
            start = PREF_ITEM_PADDING.dp,
            end = PREF_ITEM_PADDING.dp,
            top = PREF_ITEM_PADDING.dp,
            bottom = 4.dp
        ),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun TextPreference(
    text: String,
    summary: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
