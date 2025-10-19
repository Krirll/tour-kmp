package ru.krirll.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextPreference(
    modifier: Modifier = Modifier,
    text: String,
    summary: String? = null
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
