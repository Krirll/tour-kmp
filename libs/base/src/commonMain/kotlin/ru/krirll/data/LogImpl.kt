package ru.krirll.data

import ru.krirll.domain.Log

expect class LogImpl() : Log {
    override fun d(tag: String, msg: String?, e: Throwable?)
    override fun e(tag: String, e: Throwable?, msg: String?)
}