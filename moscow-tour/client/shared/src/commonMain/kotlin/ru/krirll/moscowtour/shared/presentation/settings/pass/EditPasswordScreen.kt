package ru.krirll.moscowtour.shared.presentation.settings.pass

import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.edit_password
import moscowtour.moscow_tour.client.shared.generated.resources.error_title
import moscowtour.moscow_tour.client.shared.generated.resources.new_password
import moscowtour.moscow_tour.client.shared.generated.resources.okay
import moscowtour.moscow_tour.client.shared.generated.resources.old_password
import moscowtour.moscow_tour.client.shared.generated.resources.password_changed
import moscowtour.moscow_tour.client.shared.generated.resources.unknown_error
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.new_password_repeat
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.base.PasswordTextField
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.SimpleAppBar
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(component: EditPasswordComponent) {
    val state by component.state.collectAsState()
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    BaseScreen(
        appBar = {
            SimpleAppBar(
                stringResource(Res.string.edit_password),
                doBack = { component.onBack() }
            )
        },
        snackbarState = snackbarState,
        content = {
            var old by rememberSaveable { mutableStateOf("") }
            var new by rememberSaveable { mutableStateOf("") }
            var repeat by rememberSaveable { mutableStateOf("") }
            val onDone: () -> Unit = { component.changePassword(old, new, repeat) }
            Box(modifier = Modifier.padding(it)) {
                when (val s = state) {
                    is AuthState.Loading -> Loading()
                    else -> {
                        if (s is AuthState.Succeed) {
                            val message = stringResource(Res.string.password_changed)
                            scope.launch {
                                snackbarState.showSnackbar(message)
                                component.resetState()
                            }
                        } else if (s is AuthState.Error) {
                            AlertDialog(
                                onDismissRequest = { component.resetState() },
                                title = { Text(stringResource(Res.string.error_title)) },
                                text = { Text(s.e.message ?: stringResource(Res.string.unknown_error)) },
                                confirmButton = {
                                    Text(
                                        text = stringResource(Res.string.okay),
                                        modifier = Modifier.padding(8.dp).clickable { component.resetState() }
                                    )
                                }
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            item {
                                PasswordTextField(
                                    old,
                                    onValueChange = { old = it },
                                    labelRes = Res.string.old_password
                                )
                            }
                            item {
                                PasswordTextField(
                                    new,
                                    onValueChange = { new = it },
                                    labelRes = Res.string.new_password,
                                    onDone = onDone
                                )
                            }
                            item {
                                PasswordTextField(
                                    repeat,
                                    onValueChange = { repeat = it },
                                    labelRes = Res.string.new_password_repeat,
                                    onDone = onDone
                                )
                            }
                            item {
                                Button(
                                    onClick = { onDone() },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Text(stringResource(Res.string.edit_password))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
