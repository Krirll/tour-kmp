package ru.krirll.data

import ru.krirll.domain.Log

actual class LogImpl : Log {
    actual override fun d(tag: String, msg: String?, e: Throwable?) {
        android.util.Log.d(tag, msg, e)
    }

    actual override fun e(tag: String, e: Throwable?, msg: String?) {
        android.util.Log.e(tag, msg, e)
    }
}
