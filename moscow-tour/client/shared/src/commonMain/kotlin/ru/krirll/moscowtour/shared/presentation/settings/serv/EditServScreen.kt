package ru.krirll.moscowtour.shared.presentation.settings.serv

import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.check
import moscowtour.moscow_tour.client.shared.generated.resources.edit_custom_serv_addr
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.BackButton
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.list.Loading

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditServScreen(comp: EditServComponent) {
    val url by comp.serverUrl.collectAsState(null)
    LaunchedEffect(null) {
        comp.state.collect {
            if (it is EditServState.Succeed) {
                comp.doBack()
            }
        }
    }
    val state by comp.state.collectAsState(null)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    BaseScreen(
        appBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.edit_custom_serv_addr)) },
                navigationIcon = {
                    BackButton(doBack = comp.doBack)
                },
                actions = {
                    if (state !is EditServState.Error) {
                        IconButton(onClick = { comp.done() }) {
                            Icon(painterResource(Res.drawable.check), contentDescription = null)
                        }
                    }
                }
            )
    }, content = {
        if (url != null) {
            var tfv by remember {
                mutableStateOf(TextFieldValue(url!!, TextRange(url!!.length)))
            }
            Box(modifier = Modifier.padding(it).fillMaxSize()) {
                OutlinedTextField(
                    tfv,
                    onValueChange = {
                        comp.onUrlChanged(it.text)
                        tfv = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { comp.done() }),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.show()
                            }
                        }.padding(4.dp),
                )
            }
            LaunchedEffect("focus") {
                delay(500)
                focusRequester.requestFocus()
            }
        } else {
            Loading()
        }
    })
}
