package com.example.smartfit.ui

import android.util.Log
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

/**
 * Simple composable that plays a video resource placed in res/raw (or drawable) using Android VideoView.
 * Use `RawVideoPlayerById` with a compile-time id (recommended: R.raw.my_video).
 */
@Composable
fun RawVideoPlayerById(
    resId: Int,
    visible: Boolean,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    onFinished: (() -> Unit)? = null
) {
    if (!visible) return

    val context = LocalContext.current
    val uri = remember(resId) { ("android.resource://${context.packageName}/$resId").toUri() }

    Box(modifier = modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
        AndroidView(factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(uri)
                setOnCompletionListener {
                    onFinished?.invoke()
                    if (loop) start() else stopPlayback()
                }
                setOnErrorListener { _, what, extra ->
                    Log.d("RawVideoPlayerById", "Video error what=$what extra=$extra")
                    onFinished?.invoke()
                    true
                }
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                start()
            }
        }, update = { view ->
            // if view was recreated or paused, ensure it's playing
            if (!view.isPlaying) {
                try { view.start() } catch (_: Exception) { }
            }
        })
    }
}
