package ru.krirll.backend.data

import org.koin.core.annotation.Single
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Single
class Logger {
    private val loggers = hashMapOf<String, Logger>()

    fun info(tag: String, message: String) = obtainLogger(tag).info(message)
    fun debug(tag: String, message: String) = obtainLogger(tag).debug(message)
    fun error(tag: String, e: Exception) = obtainLogger(tag).error("error occurred", e)

    private fun obtainLogger(tag: String): Logger {
        loggers[tag]?.let { return it }
        loggers[tag] = LoggerFactory.getLogger(tag)
        return loggers[tag]!!
    }
}
