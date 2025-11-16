package ru.krirll.moscowtour.shared.presentation.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account_auth
import moscowtour.moscow_tour.client.shared.generated.resources.account_delete
import moscowtour.moscow_tour.client.shared.generated.resources.account_delete_confirm
import moscowtour.moscow_tour.client.shared.generated.resources.account_delete_confirm_long
import moscowtour.moscow_tour.client.shared.generated.resources.account_logout_auth
import moscowtour.moscow_tour.client.shared.generated.resources.account_reg
import moscowtour.moscow_tour.client.shared.generated.resources.account_tickets
import moscowtour.moscow_tour.client.shared.generated.resources.cancel
import moscowtour.moscow_tour.client.shared.generated.resources.edit_password
import moscowtour.moscow_tour.client.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.ExpressiveLazyColumn
import ru.krirll.moscowtour.shared.presentation.base.TextPreference

@Composable
fun AccountContent(comp: AccountComponent, paddingValues: PaddingValues) {
    val tokenInfo by comp.tokenInfo.collectAsState(null)
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.account_delete_confirm)) },
            text = { Text(stringResource(Res.string.account_delete_confirm_long)) },
            confirmButton = {
                Text(
                    text = stringResource(Res.string.yes),
                    modifier = Modifier.padding(8.dp).clickable {
                        showDeleteDialog = false
                        comp.delete()
                    }
                )
            },
            dismissButton = {
                Text(
                    text = stringResource(Res.string.cancel),
                    modifier = Modifier.padding(8.dp).clickable { showDeleteDialog = false }
                )
            }
        )
    }
    Column(modifier = Modifier.applyColumnPadding(paddingValues).fillMaxSize()) {
        val needAuth = tokenInfo == null
        ExpressiveLazyColumn(
            Modifier.padding(top = 8.dp),
            contentPadding = paddingValues.asColumnPadding(),
            mutableListOf<@Composable () -> Unit>().apply {
                if (!needAuth) {
                    add {
                        TextPreference(
                            stringResource(Res.string.account_tickets),
                            modifier = Modifier.fillMaxWidth().clickable {
                                comp.tickets()
                            }
                        )
                    }
                    add {
                        TextPreference(
                            stringResource(Res.string.edit_password),
                            modifier = Modifier.fillMaxWidth().clickable {
                                comp.editPassword()
                            }
                        )
                    }
                }
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
                if (!needAuth) {
                    add {
                        TextPreference(
                            stringResource(Res.string.account_delete),
                            modifier = Modifier.fillMaxWidth().clickable {
                                showDeleteDialog = true
                            },
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
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
            }
        )
    }
}
