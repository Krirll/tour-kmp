@file:OptIn(ExperimentalMaterial3Api::class)

package ru.krirll.moscowtour.multiplatform.preview

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.krirll.moscowtour.shared.presentation.search.AppBarWithSearch
import ru.krirll.moscowtour.shared.presentation.search.OldSearchInfo
import ru.krirll.moscowtour.shared.presentation.search.SearchAppBar

@Preview
@Composable
fun AppBarWithSearchPreview() {
    AppBarWithSearch(
        title = "TitleWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
        isDefault = false,
        onBack = {}
    )
}

@Preview
@Composable
fun AppBarWithSearchWithBackPreview() {
    AppBarWithSearch(
        title = "TitleWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
        onBack = {}
    )
}

@SuppressLint("RememberInComposition")
@Preview
@Composable
fun InternalAppBarPreview() {
    SearchAppBar(
        null,
        TextFieldValue(""),
        FocusRequester(),
        {},
        {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchInfoPreview() {
    OldSearchInfo(
        info = listOf(
            "Почему дора дура?",
            "клиинка",
            "симпсоны",
            "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"
        ),
        PaddingValues(0.dp),
        onClick = {},
        onDeleteRequested = {}
    )
}
