package ru.krirll.moscowtour.shared.presentation

import android.content.Context
import android.content.Intent
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.domain.model.Tour

// adb shell am start -W -a android.intent.action.VIEW -d "https://tour.krirll.ru/deep-link/overview/191" ru.krirll.moscowtour.multiplatform
actual class ShareManager actual constructor() {
    private val context: Context by lazy { koin.get() }

    actual fun shareDetails(details: Tour) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "https://tour.krirll.ru/overview/${details.id}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    actual fun canShare(): Boolean = true
}
