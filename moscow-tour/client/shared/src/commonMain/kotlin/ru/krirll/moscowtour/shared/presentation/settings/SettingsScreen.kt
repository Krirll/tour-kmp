package ru.krirll.moscowtour.shared.presentation.settings

import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account_auth
import moscowtour.moscow_tour.client.shared.generated.resources.account_logout_auth
import moscowtour.moscow_tour.client.shared.generated.resources.account_reg
import moscowtour.moscow_tour.client.shared.generated.resources.custom_serv_addr
import moscowtour.moscow_tour.client.shared.generated.resources.edit_password
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.ExpressiveLazyColumn
import ru.krirll.moscowtour.shared.presentation.base.TextPreference

@Composable
fun SettingsContent(comp: SettingsComponent, paddingValues: PaddingValues) {
    val servAddr by comp.serverInfo.collectAsState(null)
    val tokenInfo by comp.tokenInfo.collectAsState(null)
    Column(modifier = Modifier.applyColumnPadding(paddingValues)) {
        ExpressiveLazyColumn(
            Modifier,
            contentPadding = PaddingValues(0.dp),
            mutableListOf<@Composable () -> Unit>().apply {
                if (servAddr != null && comp.isDebug) {
                    add {
                        TextPreference(
                            stringResource(Res.string.custom_serv_addr),
                            servAddr!!,
                            modifier = Modifier.fillMaxWidth().clickable { comp.editServAddr() }
                        )
                    }
                }
            }
        )
        val needAuth = tokenInfo == null
        ExpressiveLazyColumn(
            Modifier.padding(top = 8.dp),
            contentPadding = paddingValues.asColumnPadding(),
            mutableListOf<@Composable () -> Unit>().apply {
                add {
                    val msg = if (needAuth) {
                        Res.string.account_auth
                    } else {
                        Res.string.account_logout_auth
                    }
                    TextPreference(
                        stringResource(msg),
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (needAuth) {
                                comp.doAuth()
                            } else {
                                comp.logout()
                            }
                        }
                    )
                }
                if (needAuth) {
                    add {
                        TextPreference(
                            stringResource(Res.string.account_reg),
                            modifier = Modifier.fillMaxWidth().clickable {
                                comp.doRegister()
                            }
                        )
                    }
                }
                if (!needAuth) {
                    add {
                        TextPreference(
                            stringResource(Res.string.edit_password),
                            modifier = Modifier.fillMaxWidth().clickable {
                                comp.editPassword()
                            }
                        )
                    }
                }
            }
        )
    }
}
