package ru.krirll.moscowtour.shared.presentation.search

import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.clear
import moscowtour.moscow_tour.client.shared.generated.resources.clear_text
import moscowtour.moscow_tour.client.shared.generated.resources.search
import moscowtour.moscow_tour.client.shared.generated.resources.search_empty_now
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.ui.collectAsStateWithLifecycle
import ru.krirll.moscowtour.shared.presentation.base.ExpressiveLazyColumn
import ru.krirll.moscowtour.shared.presentation.imePaddingInternal
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry

@Composable
fun SearchVideoContent(
    component: SearchScreenComponent,
    paddingValues: PaddingValues,
    focusRequester: FocusRequester,
    onUpdateTextField: (TextFieldValue) -> Unit
) {
    val oldSearch by component.oldSearch.collectAsState()
    val errorMsg by component.errorMsg.collectAsState(null)
    val isLoading by component.isLoading.collectAsState()
    val searchEvent by component.searchEvent.collectAsStateWithLifecycle(component, null)
    LaunchedEffect(searchEvent) { component.refresh() }
    Column(
        modifier = Modifier.focusGroup().focusRequester(focusRequester).fillMaxSize()
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    .fillMaxWidth()
            )
        }
        if (errorMsg != null) {
            ErrorAndRetry(errorMsg!!) { component.restart() }
        } else if (oldSearch.isNotEmpty()) {
            OldSearchInfo(
                info = oldSearch,
                if (isLoading) PaddingValues(0.dp) else paddingValues,
                onClick = { value ->
                    component.onValueChange(value)
                    onUpdateTextField(TextFieldValue(value, TextRange(value.length)))
                    component.done()
                },
                onDeleteRequested = {
                    component.removeFromSearch(it)
                })
        } else if (!isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(4.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.search_empty_now),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OldSearchInfo(
    info: List<String>,
    contentPaddingValues: PaddingValues,
    onClick: (String) -> Unit,
    onDeleteRequested: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember { HideKeyboardConnection { keyboardController } }
    ExpressiveLazyColumn(
        modifier = Modifier.nestedScroll(nestedScrollConnection).padding(contentPaddingValues)
            .imePaddingInternal(), contentPadding = PaddingValues(0.dp), items = info.map {
            {
                OldSearchInfo(it, onDeleteRequested, onClick)
            }
        })
}

private class HideKeyboardConnection(
    private val keyboardControllerCallback: () -> SoftwareKeyboardController?
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        keyboardControllerCallback()?.hide()
        return super.onPreScroll(available, source)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OldSearchInfo(
    info: String, onDeleteRequested: (String) -> Unit, onClick: (String) -> Unit
) {
    Column(modifier = Modifier.combinedClickable { onClick(info) }) {
        Row {
            Text(
                text = info,
                modifier = Modifier.padding(16.dp).weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { onDeleteRequested(info) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(painterResource(Res.drawable.clear), null)
            }
        }
    }
}

//todo сделать для ios
expect fun Modifier.requestFocusOnDownEvent(focusRequester: FocusRequester): Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    textFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onDone: () -> Unit,
    onValueChange: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            inputFocusRequester.freeFocus()
        }
    }
    Box {
        TopAppBar(
            title = {
                TextField(
                    value = textFieldValue,
                    onValueChange = {
                        onValueChange(it)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onDone() }),
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(inputFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.show()
                            }
                        }.requestFocusOnDownEvent(focusRequester),
                    trailingIcon = {
                        if (textFieldValue.text.isNotEmpty()) {
                            IconButton(onClick = {
                                onValueChange(TextFieldValue(""))
                            }) {
                                Icon(painterResource(Res.drawable.clear_text), null)
                            }
                        }
                    },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.search)) }
                )
            }, scrollBehavior = scrollBehavior
        )
        LaunchedEffect("focus") {
            delay(500)
            inputFocusRequester.requestFocus()
        }
    }

}

@Composable
private fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithSearch(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String,
    isDefault: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: (() -> Unit)
) {
    TopAppBar(actions = {
        actions(this)
    }, title = {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable(!isDefault) { onBack() })
    }, navigationIcon = {
        if (!isDefault) {
            IconButton(onClick = { onBack() }) {
                Icon(painterResource(Res.drawable.back), null)
            }
        }
    }, scrollBehavior = scrollBehavior)
}
