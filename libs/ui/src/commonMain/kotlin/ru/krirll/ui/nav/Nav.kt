package ru.krirll.ui.nav

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun Nav(
    rootComponent: BaseRootComponent<*>,
    process: @Composable (child: Child, route: Route) -> Unit
) {
    Children(
        stack = rootComponent.childStack,
        animation = predictiveBackAnimation(
            backHandler = rootComponent.backHandler,
            fallbackAnimation = stackAnimation(fade()),
            onBack = rootComponent::onBack,
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
        ),
    ) {
        process(it.instance, it.configuration as Route)
    }
}
