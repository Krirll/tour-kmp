package ru.krirll.moscowtour.shared.presentation.overview.buy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.buy_ticket
import moscowtour.moscow_tour.client.shared.generated.resources.cancel
import moscowtour.moscow_tour.client.shared.generated.resources.confirm
import moscowtour.moscow_tour.client.shared.generated.resources.no
import moscowtour.moscow_tour.client.shared.generated.resources.success_ticket_desc
import moscowtour.moscow_tour.client.shared.generated.resources.success_ticket_title
import moscowtour.moscow_tour.client.shared.generated.resources.ticket_warning_desc
import moscowtour.moscow_tour.client.shared.generated.resources.unknown_error
import moscowtour.moscow_tour.client.shared.generated.resources.yes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.overview.OverviewDescription
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect
import ru.krirll.ui.applyBlurSource
import ru.krirll.ui.rememberBlurState
import ru.krirll.ui.theme.ComponentDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyScreen(component: BuyComponent) {
    val state by component.state.collectAsState(ScreenState.Idle)
    var showApproveDialog by rememberSaveable { mutableStateOf(false) }
    var showSuccessTicketDialog by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            appBar = { BuyAppBar(component, scrollBehavior) },
            scrollBehavior = scrollBehavior,
            content = {
                if (showApproveDialog) {
                    AlertDialog(
                        onDismissRequest = { showApproveDialog = false },
                        title = { Text(stringResource(Res.string.confirm)) },
                        text = { Text(stringResource(Res.string.ticket_warning_desc)) },
                        confirmButton = {
                            Text(
                                text = stringResource(Res.string.yes),
                                modifier = Modifier.padding(8.dp).clickable {
                                    showApproveDialog = false
                                    component.requestTicket()
                                }
                            )
                        },
                        dismissButton = {
                            Text(
                                text = stringResource(Res.string.cancel),
                                modifier = Modifier.padding(8.dp)
                                    .clickable { showApproveDialog = false }
                            )
                        }
                    )
                }
                if (showSuccessTicketDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showApproveDialog = false
                            component.navTours()
                        },
                        title = { Text(stringResource(Res.string.success_ticket_title)) },
                        text = { Text(stringResource(Res.string.success_ticket_desc)) },
                        confirmButton = {
                            Text(
                                text = stringResource(Res.string.yes),
                                modifier = Modifier.padding(8.dp).clickable {
                                    showApproveDialog = false
                                    component.navTickets()
                                }
                            )
                        },
                        dismissButton = {
                            Text(
                                text = stringResource(Res.string.no),
                                modifier = Modifier.padding(8.dp).clickable {
                                    showApproveDialog = false
                                    component.navTours()
                                }
                            )
                        }
                    )
                }
                when (val s = state) {
                    is ScreenState.Error -> ErrorAndRetry(
                        errorMsg = s.e.message ?: stringResource(Res.string.unknown_error)
                    ) { component.requestTicket() }

                    is ScreenState.Idle -> TicketData(
                        details = component.tour,
                        personData = component.personData,
                        paddingValues = it,
                        onBuyClicked = {
                            showApproveDialog = true
                        }
                    )

                    is ScreenState.Loading -> Loading()

                    is ScreenState.Succeed -> {
                        showSuccessTicketDialog = true
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuyAppBar(
    component: BuyComponent,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val blur = LocalBlurState.current
    TopAppBar(
        title = {
            Text(
                stringResource(Res.string.confirm),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { component.doBack() }) {
                Icon(painterResource(Res.drawable.back), contentDescription = null)
            }
        },
        colors = ComponentDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior,
        modifier = Modifier.applyBlurEffect(blur)
    )
}


@Composable
fun TicketData(
    details: Tour,
    personData: PersonData,
    paddingValues: PaddingValues,
    onBuyClicked: () -> Unit
) {
    val blur = LocalBlurState.current
    LazyColumn(
        modifier = Modifier.applyBlurSource(blur).applyColumnPadding(paddingValues),
        contentPadding = paddingValues.asColumnPadding()
    ) {
        item { OverviewDescription(details, personData) }
        item {
            Button(
                onClick = onBuyClicked,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) { Text(stringResource(Res.string.buy_ticket)) }
        }
    }
}
