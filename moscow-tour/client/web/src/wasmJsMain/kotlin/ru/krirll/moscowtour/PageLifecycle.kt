package ru.krirll.moscowtour

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.w3c.dom.events.Event
import ru.krirll.domain.Log
import ru.krirll.koin

@JsFun("""
    function hideElementById(id) { 
        var el = document.getElementById(id);
        el.style.display = 'none';
    }
""")
external fun hideElementById(id: String)
//todo есть lifecycle от Decompose - смотри как сделано в QrGenerator

@JsFun("(function(){ return document.visibilityState; })")
external fun pageVisibilityState(): String

fun hookPageVisibility(lifecycle: LifecycleRegistry) {
    val log = koin.get<Log>()

    fun bootstrap() {
        lifecycle.resume()
        log.d("moscowtourApplication", "bootstrap -> ${pageVisibilityState()} / ${lifecycle.state}")
    }

    fun onVisibilityChange() {
        when (pageVisibilityState()) {
            "visible" -> lifecycle.resume()
            else -> lifecycle.stop()
        }
        log.d("moscowtourApplication", "visibility -> ${pageVisibilityState()} / ${lifecycle.state}")
    }

    fun onPageHideShow(hidden: Boolean) {
        if (hidden) {
            // уходим глубже: STARTED → CREATED
            when (lifecycle.state) {
                Lifecycle.State.RESUMED -> { lifecycle.pause(); lifecycle.stop() }
                Lifecycle.State.STARTED -> { lifecycle.stop() }
                else -> Unit
            }
        } else {
            // pageshow: поднимемся обратно
            when (lifecycle.state) {
                Lifecycle.State.INITIALIZED -> { lifecycle.create(); lifecycle.start(); if (pageVisibilityState() == "visible") lifecycle.resume() }
                Lifecycle.State.CREATED     -> { lifecycle.start(); if (pageVisibilityState() == "visible") lifecycle.resume() }
                Lifecycle.State.STARTED     -> { if (pageVisibilityState() == "visible") lifecycle.resume() }
                else -> Unit
            }
        }
        log.d("moscowtourApplication", "page ${if (hidden) "hide" else "show"} -> ${lifecycle.state}")
    }

    bootstrap()

    val visHandler: (Event) -> Unit = { onVisibilityChange() }
    document.addEventListener("visibilitychange", visHandler)

    document.addEventListener("pagehide", { onPageHideShow(true) })
    document.addEventListener("pageshow", { onPageHideShow(false) })
}
