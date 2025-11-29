package ru.krirll.moscowtour.shared.presentation.overview

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.broken_image
import org.jetbrains.compose.resources.painterResource
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullscreenImageCarouselScreen(
    images: List<String>,
    startIndex: Int = 0,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState { images.size }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(startIndex)
    }
    BaseScreen(
        content = { paddingValues ->
            FullscreenImagesContent(scope, paddingValues, pagerState, images)
        },
        appBar = {
            FullscreenImagesAppBar( scrollBehavior) { onBack() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenImagesAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit
) {
    val blur = LocalBlurState.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.9f),
                        Color.Black.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        painterResource(Res.drawable.back),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
            ),
            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .fillMaxSize()
                .applyBlurEffect(blur)
        )
    }
}

@Composable
private fun FullscreenImagesContent(
    scope: CoroutineScope,
    paddingValues: PaddingValues,
    pagerState: PagerState,
    images: List<String>
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            var offset by remember { mutableStateOf(Offset.Zero) }
            val scaleAnim = remember { Animatable(1f) }

            val transform = Modifier.pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scope.launch {
                        val newScale = (scaleAnim.value * zoom).coerceIn(1f, 5f)
                        scaleAnim.snapTo(newScale)

                        val maxX = (size.width * (scaleAnim.value - 1)) / 2
                        val maxY = (size.height * (scaleAnim.value - 1)) / 2

                        val newOffsetX = (offset.x + pan.x).coerceIn(-maxX, maxX)
                        val newOffsetY = (offset.y + pan.y).coerceIn(-maxY, maxY)

                        offset = Offset(newOffsetX, newOffsetY)
                    }
                }
            }.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scope.launch {
                            if (scaleAnim.value == 1f) {
                                scaleAnim.animateTo(2.5f, animationSpec = tween(300))
                            } else {
                                scaleAnim.animateTo(1f, animationSpec = tween(300))
                                offset = Offset.Zero
                            }
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberImagePainter(
                        url = images[page],
                        errorPainter = { painterResource(Res.drawable.broken_image) }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scaleAnim.value,
                            scaleY = scaleAnim.value,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .then(transform),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
