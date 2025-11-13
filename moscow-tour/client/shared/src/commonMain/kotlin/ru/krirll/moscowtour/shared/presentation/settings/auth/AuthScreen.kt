package ru.krirll.moscowtour.shared.presentation.settings.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account_auth
import moscowtour.moscow_tour.client.shared.generated.resources.account_login
import moscowtour.moscow_tour.client.shared.generated.resources.account_password
import moscowtour.moscow_tour.client.shared.generated.resources.account_reg_hint
import moscowtour.moscow_tour.client.shared.generated.resources.empty_login
import moscowtour.moscow_tour.client.shared.generated.resources.empty_pass
import moscowtour.moscow_tour.client.shared.generated.resources.error_title
import moscowtour.moscow_tour.client.shared.generated.resources.okay
import moscowtour.moscow_tour.client.shared.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.EmptyLoginException
import ru.krirll.moscowtour.shared.domain.model.EmptyPasswordException
import ru.krirll.moscowtour.shared.domain.model.LoginException
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.SimpleAppBar
import ru.krirll.moscowtour.shared.presentation.list.Loading
import ru.krirll.moscowtour.shared.presentation.base.PasswordTextField

@Composable
fun AuthScreen(comp: AuthComponent) {
    val login = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    BaseLogin(
        login,
        password,
        comp.state,
        stringResource(Res.string.account_auth),
        doBack = comp.doBack,
        onDone = { comp.login(login.value, password.value) },
        onFinish = { comp.finish() },
        buttonAction = {
            OutlinedButton(
                onClick = { comp.doRegister() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.account_reg_hint))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseLogin(
    login: MutableState<String>,
    password: MutableState<String>,
    stateFlow: Flow<AuthState>,
    title: String,
    doBack: (() -> Unit)?,
    onDone: () -> Unit,
    onFinish: () -> Unit,
    buttonAction: (@Composable () -> Unit)? = null
) {
    val state by stateFlow.collectAsState(AuthState.Idle)
    var errorInfo by remember { mutableStateOf<LoginException?>(null) }
    LaunchedEffect(state) {
        when (val s = state) {
            is AuthState.Error -> { errorInfo = s.e }
            AuthState.Succeed -> onFinish()
            else -> {}
        }
    }
    BaseScreen(
        appBar = { SimpleAppBar(title, doBack) },
        content = {
            if (state is AuthState.Loading) {
                Loading()
            } else {
                Box(Modifier.padding(it).fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (errorInfo != null && state is AuthState.Error) {
                            AlertDialog(
                                onDismissRequest = { errorInfo = null },
                                title = { Text(stringResource(Res.string.error_title)) },
                                text = { Text(errorInfo!!.toMessage()) },
                                confirmButton = {
                                    Text(
                                        text = stringResource(Res.string.okay),
                                        modifier = Modifier.padding(8.dp).clickable { errorInfo = null }
                                    )
                                },
                            )
                        }
                        OutlinedTextField(
                            login.value,
                            onValueChange = { login.value = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text(stringResource(Res.string.account_login)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        PasswordTextField(
                            password.value,
                            onValueChange = { password.value = it },
                            onDone = onDone,
                            labelRes = Res.string.account_password
                        )
                        Button(
                            onClick = { onDone() },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text(title)
                        }
                        buttonAction?.invoke()
                    }
                }
            }
        }
    )
}

@Composable
private fun LoginException.toMessage(): String {
    return when (this) {
        is EmptyLoginException -> stringResource(Res.string.empty_login)
        is EmptyPasswordException -> stringResource(Res.string.empty_pass)
        else -> message ?: stringResource(Res.string.unknown_error)
    }
}
