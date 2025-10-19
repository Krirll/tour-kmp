package ru.krirll.moscowtour.multiplatform

import android.content.Intent
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
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.presentation.Nav
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.RootFactory
import ru.krirll.moscowtour.shared.presentation.UrlRoutes
import ru.krirll.moscowtour.shared.presentation.nav.Route

class MainActivity : ComponentActivity() {
    private lateinit var root: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        edgeToEdge(false)
        super.onCreate(savedInstanceState)
        root = retainedComponent { componentContext ->
            val routeFromIntent = obtainRouteOrNull(intent)
            val route = if (routeFromIntent == null) {
                listOf(Route.default)
            } else {
                listOf(Route.default, routeFromIntent)
            }
            koin.get<RootFactory>().create(componentContext, route)
        }
        if (openSavedIfNeeded(intent)) {
            intent = Intent(intent)
                .setData(null)
                .setAction(null)
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

    private fun openSavedIfNeeded(intent: Intent?): Boolean {
        if (intent?.action == "ru.krirll.moscowtour.action.OPEN_SAVED_MOVIES") {
            root.nav(Route.Saved, true)
            return true
        }
        return false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (!openSavedIfNeeded(intent)) {
            val route = obtainRouteOrNull(intent)
            if (route != null) {
                root.nav(route, true)
            }
        }
    }

    private fun obtainRouteOrNull(intent: Intent): Route? {
        val uri = intent.data ?: return null
        return UrlRoutes.parseUrl(uri.toString(), basePath = "/")
    }
}
