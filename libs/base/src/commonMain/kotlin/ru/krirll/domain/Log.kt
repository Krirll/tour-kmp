package ru.krirll.domain

interface Log {
    fun d(tag: String, msg: String? = null, e: Throwable? = null)
    fun e(tag: String, e: Throwable? = null, msg: String? = null)
}
