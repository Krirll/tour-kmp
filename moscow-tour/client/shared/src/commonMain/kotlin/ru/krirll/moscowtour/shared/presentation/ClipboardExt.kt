package ru.krirll.moscowtour.shared.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.method_not_supported
import moscowtour.moscow_tour.client.shared.generated.resources.url_copied
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.OsType
import ru.krirll.moscowtour.shared.domain.getCurrentOsType

fun SnackbarHostState.clipboardUrl(
    url: String,
    scope: CoroutineScope,
    clipboardManager: ClipboardManager,
    text: String
) {
    scope.launch {
        if (getCurrentOsType() != OsType.JS) {
            clipboardManager.setText(AnnotatedString(url))
        }
        showSnackbar(
            message = text,
            duration = SnackbarDuration.Short
        )
    }
}

@Composable
fun getClipboardText(): String {
    return if (getCurrentOsType() == OsType.JS) {
        stringResource(Res.string.method_not_supported)
    } else {
        stringResource(Res.string.url_copied)
    }
}
