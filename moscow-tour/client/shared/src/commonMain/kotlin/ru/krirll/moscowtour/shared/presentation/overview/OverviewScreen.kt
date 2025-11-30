package ru.krirll.moscowtour.shared.presentation.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.broken_image
import moscowtour.moscow_tour.client.shared.generated.resources.buy_disabled
import moscowtour.moscow_tour.client.shared.generated.resources.buy_ticket
import moscowtour.moscow_tour.client.shared.generated.resources.buy_tour_warning_desc
import moscowtour.moscow_tour.client.shared.generated.resources.cancel
import moscowtour.moscow_tour.client.shared.generated.resources.city
import moscowtour.moscow_tour.client.shared.generated.resources.country
import moscowtour.moscow_tour.client.shared.generated.resources.date_begin
import moscowtour.moscow_tour.client.shared.generated.resources.date_end
import moscowtour.moscow_tour.client.shared.generated.resources.desc
import moscowtour.moscow_tour.client.shared.generated.resources.first_name
import moscowtour.moscow_tour.client.shared.generated.resources.last_name
import moscowtour.moscow_tour.client.shared.generated.resources.middle_name
import moscowtour.moscow_tour.client.shared.generated.resources.passport_number
import moscowtour.moscow_tour.client.shared.generated.resources.passport_series
import moscowtour.moscow_tour.client.shared.generated.resources.phone_number
import moscowtour.moscow_tour.client.shared.generated.resources.price
import moscowtour.moscow_tour.client.shared.generated.resources.star_checked
import moscowtour.moscow_tour.client.shared.generated.resources.star_unchecked
import moscowtour.moscow_tour.client.shared.generated.resources.warning
import moscowtour.moscow_tour.client.shared.generated.resources.yes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.LabeledText
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.base.formatDate
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect
import ru.krirll.ui.applyBlurSource
import ru.krirll.ui.rememberBlurState
import ru.krirll.ui.theme.ComponentDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(component: OverviewComponent) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val errorState by component.errorCode.collectAsState(initial = null)
    val tour by component.details.collectAsState()
    val token by component.token.collectAsState(null)
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        val needAuth = token == null
        var showAuthDialog by rememberSaveable { mutableStateOf(false) }
        BaseScreen(
            appBar = { OverviewAppBar(tour, component, scrollBehavior) },
            scrollBehavior = scrollBehavior,
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
                                modifier = Modifier.padding(8.dp)
                                    .clickable { showAuthDialog = false }
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
                        },
                        onImageClick = { index ->
                            tour?.imagesUrls?.let { t -> component.onImageClicked(index, t) }
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
private fun OverviewAppBar(
    details: Tour?,
    component: OverviewComponent,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isSaved by component.isSaved.collectAsState(null)
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
        actions = {
            details?.let {
                isSaved?.let { saved ->
                    IconButton(onClick = if (saved) component::remove else component::save) {
                        if (saved) {
                            Icon(painterResource(Res.drawable.star_checked), null)
                        } else {
                            Icon(painterResource(Res.drawable.star_unchecked), null)
                        }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.applyBlurEffect(blur)
    )
}

@Composable
fun DetailsInfo(
    details: Tour,
    paddingValues: PaddingValues,
    onBuyClicked: () -> Unit,
    onImageClick: (Int) -> Unit
) {
    val blur = LocalBlurState.current
    LazyColumn(
        modifier = Modifier.applyBlurSource(blur).applyColumnPadding(paddingValues),
        contentPadding = paddingValues.asColumnPadding()
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { ImageCarousel(details.imagesUrls) { onImageClick(it) } }
        item { OverviewDescription(details) }
        item {
            Button(
                onClick = onBuyClicked,
                enabled = details.canBuy,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(
                    stringResource(
                        if (details.canBuy) {
                            Res.string.buy_ticket
                        } else {
                            Res.string.buy_disabled
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun OverviewDescription(
    details: Tour,
    personData: PersonData? = null,
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
            overflow = TextOverflow.Ellipsis
        )
        LabeledText(stringResource(Res.string.city), details.city)
        LabeledText(stringResource(Res.string.country), details.country)
        LabeledText(stringResource(Res.string.date_begin), formatDate(details.dateBegin))
        LabeledText(stringResource(Res.string.date_end), formatDate(details.dateEnd))
        LabeledText(stringResource(Res.string.price), details.price.toString() + "₽")

        personData?.let {
            LabeledText(stringResource(Res.string.last_name), it.lastName)
            LabeledText(stringResource(Res.string.first_name), it.firstName)
            LabeledText(stringResource(Res.string.middle_name), it.middleName)
            LabeledText(stringResource(Res.string.passport_series), it.passportSeries.toString())
            LabeledText(stringResource(Res.string.passport_number), it.passportNumber.toString())
            LabeledText(stringResource(Res.string.phone_number), it.phone)
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    images: List<String>,
    onClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState { images.size }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 8.dp)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberImagePainter(
                            url = images[page],
                            errorPainter = { painterResource(Res.drawable.broken_image) }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable { onClick(page) },
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(if (selected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}
