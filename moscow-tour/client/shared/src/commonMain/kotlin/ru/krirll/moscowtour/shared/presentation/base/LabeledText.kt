package ru.krirll.moscowtour.shared.presentation.base

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun LabeledText(
    label: String,
    value: String
) {
    val primary = MaterialTheme.colorScheme.primary
    val labelStyle = MaterialTheme.typography.titleMedium
    val valueStyle = MaterialTheme.typography.bodyLarge

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = primary,
                    fontSize = labelStyle.fontSize,
                    fontWeight = labelStyle.fontWeight
                )
            ) {
                append(label)
                append(": ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = valueStyle.fontSize,
                    fontWeight = valueStyle.fontWeight
                )
            ) {
                append(value)
            }
        },
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
