package ru.krirll.moscowtour.multiplatform.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.list.Loading

@Preview
@Composable
fun ErrorPreview() {
    ErrorAndRetry(
        errorMsg = "Неизвестная ошибка"
    ) {}
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}
