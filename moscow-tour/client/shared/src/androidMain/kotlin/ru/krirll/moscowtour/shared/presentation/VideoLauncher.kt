package ru.krirll.moscowtour.shared.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.cant_open_video_viewer
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual open class VideoLauncher : KoinComponent {
    private val context by lazy { get<Context>() }

    actual open suspend fun launch(uri: String) {
        if (!launchInternal(uri, true)) {
            if (!launchInternal(uri, false)) {
                Toast.makeText(
                    context,
                    getString(Res.string.cant_open_video_viewer),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun launchInternal(uri: String, withType: Boolean): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (withType) {
            intent.setDataAndType(Uri.parse(uri), "video/*")
        } else {
            intent.data = Uri.parse(uri)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
