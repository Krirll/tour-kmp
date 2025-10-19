package ru.krirll.moscowtour.shared.domain

data class ServerConfiguration(
    val uriString: String = "https://tour.krirll.ru/api"
) {
    fun asWebSocketStr(): String {
        val useTls = asHttpStr().startsWith("https")
        val withoutScheme = asHttpStr()
            .removePrefix("https")
            .removePrefix("http")
        val newPrefix = if (useTls) "wss" else "ws"
        return "$newPrefix$withoutScheme"
    }

    fun asHttpStr(): String {
        val builder = StringBuilder(uriString)
        if (uriString.endsWith("/")) {
            builder.removeSuffix("/")
        }
        return builder.toString()
    }
}
