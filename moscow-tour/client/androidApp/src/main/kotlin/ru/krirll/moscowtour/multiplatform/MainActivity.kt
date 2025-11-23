package ru.krirll.moscowtour.multiplatform

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.retainedComponent
import kotlinx.coroutines.launch
import ru.krirll.moscowtour.shared.data.FileSaverAndroid
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.presentation.Nav
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.RootFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class MainActivity : ComponentActivity() {
    private lateinit var root: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        edgeToEdge(false)
        super.onCreate(savedInstanceState)
        FileSaverAndroid.register(this, this.application)
        root = retainedComponent { componentContext ->
            val route = listOf(Route.default)
            koin.get<RootFactory>().create(componentContext, route)
        }
        setContent {
            Nav(rootComponent = root)
            edgeToEdge(isSystemInDarkTheme())
        }
        lifecycleScope.launch {
            root.onFinish.collect {
                finishAndRemoveTask()
            }
        }
    }

    private fun edgeToEdge(isDarkTheme: Boolean) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                { isDarkTheme }
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
    }
}
