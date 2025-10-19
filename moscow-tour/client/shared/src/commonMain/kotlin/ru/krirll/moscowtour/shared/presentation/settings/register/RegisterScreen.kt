package ru.krirll.moscowtour.shared.presentation.settings.register

import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account_reg
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.settings.auth.BaseLogin

@Composable
fun RegisterScreen(comp: RegisterComponent) {
    val login = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    BaseLogin(
        login,
        password,
        comp.state,
        stringResource(Res.string.account_reg),
        doBack = comp.doBack,
        onDone = { comp.register(login.value, password.value) },
        onFinish = comp.doBack
    )
}
