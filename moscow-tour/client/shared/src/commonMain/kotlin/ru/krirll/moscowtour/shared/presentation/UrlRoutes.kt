package ru.krirll.moscowtour.shared.presentation

import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import ru.krirll.moscowtour.shared.presentation.nav.Route

object UrlRoutes {

    object Seg {
        const val ROOT = "/"
        const val VIDEOS = "videos"
        const val SEARCH = "search"
        const val SAVED = "saved"

        const val OVERVIEW = "overview"
        const val SELECT_SEASON = "selectSeason"

        const val SETTINGS = "settings"
        const val AUTH = "auth"
        const val REGISTER = "register"
        const val EDIT_PASSWORD = "editPassword"
    }

    object Param {
        const val QUERY = "q"
    }

    data class Built(
        val path: String,
        val params: Map<String, String> = emptyMap()
    )

    fun build(route: Route): Built {
        val r = rules.firstOrNull { it.matches(route) }
            ?: error("No URL rule for route: $route")
        return r.build(route)
    }

    fun parseUrl(url: String, basePath: String = Seg.ROOT): Route? {
        val u = Url(url)

        val cleanPath = removeBase(normalize(u.encodedPath), basePath)

        val segs = cleanPath
            .trim('/')
            .split('/')
            .filter { it.isNotEmpty() }
            .map { it.decodeURLPart() }

        val params = u.parameters

        if (segs.isEmpty()) return Route.default

        for (rule in rules) {
            rule.parse(segs, params)?.let { return it }
        }
        return null
    }

    private interface Rule {
        fun matches(route: Route): Boolean
        fun build(route: Route): Built
        fun parse(segs: List<String>, params: Parameters): Route?
    }

    private val rules: List<Rule> = listOf(

        // "/" -> Loading
        rule(
            match = { it is Route.Loading },
            build = { Built(Seg.ROOT) },
            parse = { segs, _ ->
                if (segs.isEmpty()) Route.Loading() else null
            }
        ),

        // "/videos[?q=...]"
        rule(
            match = { it is Route.Videos },
            build = {
                val r = it as Route.Videos
                Built("/${Seg.VIDEOS}", r.request?.let { q -> mapOf(Param.QUERY to q) } ?: emptyMap())
            },
            parse = { segs, params ->
                if (segs.size == 1 && segs[0] == Seg.VIDEOS) {
                    Route.Videos(request = params[Param.QUERY])
                } else null
            }
        ),

        // "/search"
        rule(
            match = { it === Route.SearchVideos },
            build = { Built("/${Seg.SEARCH}") },
            parse = { segs, _ ->
                if (segs.size == 1 && segs[0] == Seg.SEARCH) Route.SearchVideos else null
            }
        ),

        // "/saved"
        rule(
            match = { it === Route.Saved },
            build = { Built("/${Seg.SAVED}") },
            parse = { segs, _ ->
                if (segs.size == 1 && segs[0] == Seg.SAVED) Route.Saved else null
            }
        ),

        // "/overview/{id}"
        rule(
            match = { it is Route.Overview },
            build = {
                val r = it as Route.Overview
                Built("/${Seg.OVERVIEW}/${r.id}")
            },
            parse = { segs, _ ->
                if (segs.size == 2 && segs[0] == Seg.OVERVIEW) {
                    segs[1].toLongOrNull()?.let { id -> Route.Overview(id) }
                } else null
            }
        ),

        // "/overview/{id}/selectSeason"
        rule(
            match = { it is Route.Overview.Season },
            build = {
                val r = it as Route.Overview.Season
                Built("/${Seg.OVERVIEW}/${r.id}/${Seg.SELECT_SEASON}")
            },
            parse = { segs, _ ->
                if (segs.size == 3 && segs[0] == Seg.OVERVIEW && segs[2] == Seg.SELECT_SEASON) {
                    segs[1].toLongOrNull()?.let { id -> Route.Overview.Season(id) }
                } else null
            }
        ),

        // "/settings"
        rule(
            match = { it === Route.Settings },
            build = { Built("/${Seg.SETTINGS}") },
            parse = { segs, _ ->
                if (segs.size == 1 && segs[0] == Seg.SETTINGS) Route.Settings else null
            }
        ),

        // "/settings/auth"  (required=true по умолчанию)
        rule(
            match = { it is Route.Settings.Auth },
            build = { Built("/${Seg.SETTINGS}/${Seg.AUTH}") },
            parse = { segs, _ ->
                if (segs.size == 2 && segs[0] == Seg.SETTINGS && segs[1] == Seg.AUTH) {
                    Route.Settings.Auth()
                } else null
            }
        ),

        // "/settings/register"
        rule(
            match = { it === Route.Settings.Register },
            build = { Built("/${Seg.SETTINGS}/${Seg.REGISTER}") },
            parse = { segs, _ ->
                if (segs.size == 2 && segs[0] == Seg.SETTINGS && segs[1] == Seg.REGISTER) Route.Settings.Register else null
            }
        ),

        // "/settings/editPassword"
        rule(
            match = { it === Route.Settings.EditPassword },
            build = { Built("/${Seg.SETTINGS}/${Seg.EDIT_PASSWORD}") },
            parse = { segs, _ ->
                if (segs.size == 2 && segs[0] == Seg.SETTINGS && segs[1] == Seg.EDIT_PASSWORD) Route.Settings.EditPassword else null
            }
        ),
    )

    private fun rule(
        match: (Route) -> Boolean,
        build: (Route) -> Built,
        parse: (List<String>, Parameters) -> Route?
    ): Rule = object : Rule {
        override fun matches(route: Route) = match(route)
        override fun build(route: Route) = build(route)
        override fun parse(segs: List<String>, params: Parameters) = parse(segs, params)
    }

    private fun normalize(path: String): String =
        path.replace(Regex("/+"), "/")

    private fun removeBase(path: String, base: String): String {
        val p = normalize(path)
        val b = normalize(base).trimEnd('/')
        return if (b.isNotEmpty() && b != "/" && p.startsWith(b)) p.removePrefix(b) else p
    }
}
