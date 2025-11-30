package ru.krirll.moscowtour.shared.presentation.account.tickets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account_tickets
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.date_buy
import moscowtour.moscow_tour.client.shared.generated.resources.not_found
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.Ticket
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.LabeledText
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.base.formatDate
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.list.NotFound
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect
import ru.krirll.ui.applyBlurSource
import ru.krirll.ui.rememberBlurState
import ru.krirll.ui.theme.ComponentDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(component: TicketsComponent) {
    val error by component.errorMsg.collectAsState(null)
    val items by component.all.collectAsState(null)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            content = { paddingValues ->
                TicketsScreenContent(
                    paddingValues,
                    error,
                    items,
                    onRefresh = { component.load() },
                    onShowOverview = { component.onOverview(it) },
                    emptyResource = Res.string.not_found,
                    onLoad = { component.load() }
                )
            },
            appBar = {
                TicketsAppBar(component, scrollBehavior)
            },
            scrollBehavior = scrollBehavior
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketsAppBar(
    component: TicketsComponent,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val blur = LocalBlurState.current
    TopAppBar(
        title = {
            Text(
                stringResource(Res.string.account_tickets),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { component.onBack() }) {
                Icon(painterResource(Res.drawable.back), contentDescription = null)
            }
        },
        colors = ComponentDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior,
        modifier = Modifier.applyBlurEffect(blur)
    )
}

@Composable
fun TicketsScreenContent(
    paddingValues: PaddingValues,
    error: String?,
    items: List<Ticket>?,
    emptyResource: StringResource,
    onRefresh: () -> Unit,
    onShowOverview: (Long) -> Unit,
    onLoad: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoad()
    }
    if (error != null) {
        ErrorAndRetry(error) {
            onRefresh()
        }
    } else if (items != null) {
        TicketInfo(items = items, paddingValues, emptyResource) {
            onShowOverview(it.tourId)
        }
    } else {
        Loading()
    }
}

@Composable
fun TicketInfo(
    items: List<Ticket>,
    paddingValues: PaddingValues,
    emptyResource: StringResource,
    onClick: (Ticket) -> Unit
) {
    val blur = LocalBlurState.current
    if (items.isNotEmpty()) {
        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier
                .fillMaxSize()
                .applyBlurSource(blur),
            contentPadding = paddingValues,
            state = gridState
        ) {
            items(items) { item ->
                TicketItem(
                    ticket = item,
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        NotFound(emptyResource)
    }
}


@Composable
fun TicketItem(
    ticket: Ticket,
    modifier: Modifier = Modifier,
    onClick: (Ticket) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = { onClick(ticket) }),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = ticket.tourTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                LabeledText(
                    label = stringResource(Res.string.date_buy),
                    value = formatDate(ticket.date)
                )
            }
        }
    }
}
