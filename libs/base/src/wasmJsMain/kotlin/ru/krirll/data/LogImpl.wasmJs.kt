package ru.krirll.data

actual class LogImpl actual constructor() : ru.krirll.domain.Log {
    actual override fun d(tag: String, msg: String?, e: Throwable?) {
        println("$tag $msg")
        e?.printStackTrace()
    }

    actual override fun e(tag: String, e: Throwable?, msg: String?) {
        println("$tag $msg")
        e?.printStackTrace()
    }
}