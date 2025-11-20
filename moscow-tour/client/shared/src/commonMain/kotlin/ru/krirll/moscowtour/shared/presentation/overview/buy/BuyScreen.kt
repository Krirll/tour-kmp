package ru.krirll.moscowtour.shared.presentation.overview.buy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.buy_ticket
import moscowtour.moscow_tour.client.shared.generated.resources.buy_tour_warning_desc
import moscowtour.moscow_tour.client.shared.generated.resources.cancel
import moscowtour.moscow_tour.client.shared.generated.resources.desc
import moscowtour.moscow_tour.client.shared.generated.resources.warning
import moscowtour.moscow_tour.client.shared.generated.resources.yes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.overview.DetailsInfo
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect
import ru.krirll.ui.applyBlurSource
import ru.krirll.ui.rememberBlurState
import ru.krirll.ui.theme.ComponentDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyScreen(component: BuyComponent) {
    //todo тут будет подтверждение, данные пользователя и данные о туре
    //  чел нажал подтвердить, вылезает диалог мол ты уверен?
    //  если да то идем скачивать и тд
    //  если нет то возвращаемся назад, на экран ввода данных юзера
    val tour by component.details.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            appBar = { BuyAppBar(tour, snackbarState, component, scrollBehavior) },
            scrollBehavior = scrollBehavior,
            snackbarState = snackbarState,
            content = {
                if (showAuthDialog) {
                    AlertDialog(
                        onDismissRequest = { showAuthDialog = false },
                        title = { Text(stringResource(Res.string.warning)) },
                        text = { Text(stringResource(Res.string.buy_tour_warning_desc)) },
                        confirmButton = {
                            Text(
                                text = stringResource(Res.string.yes),
                                modifier = Modifier.padding(8.dp).clickable {
                                    showAuthDialog = false
                                    component.onAuth()
                                }
                            )
                        },
                        dismissButton = {
                            Text(
                                text = stringResource(Res.string.cancel),
                                modifier = Modifier.padding(8.dp).clickable { showAuthDialog = false }
                            )
                        }
                    )
                }
                when {
                    errorState != null -> ErrorAndRetry(
                        errorMsg = errorState!!
                    ) { component.loadIfNeeded() }

                    tour != null -> DetailsInfo(
                        details = tour!!,
                        paddingValues = it,
                        onBuyClicked = {
                            tour?.let { t ->
                                if (needAuth) {
                                    showAuthDialog = true
                                } else {
                                    component.buy(t)
                                }
                            }
                        }
                    )

                    else -> Loading()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuyAppBar(
    details: Tour?,
    personData: PersonData?,
    component: BuyComponent,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val blur = LocalBlurState.current
    TopAppBar(
        title = {
            details?.title?.let {
                Text(
                    it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
    val showDetails = rememberSaveable { mutableStateOf(false) }
    val blur = LocalBlurState.current
    LazyColumn(
        modifier = Modifier.applyBlurSource(blur).applyColumnPadding(paddingValues),
        contentPadding = paddingValues.asColumnPadding()
    ) {
        item { TicketInfo(details, personData, showDetails) }
        item {
            Button(
                onClick = onBuyClicked,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) { Text(stringResource(Res.string.buy_ticket)) }
        }
    }
}

@Composable
private fun TicketInfo(
    details: Tour,
    personData: PersonData,
    showDetails: MutableState<Boolean>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Text(
            text = stringResource(Res.string.desc),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = details.description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (showDetails.value) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
