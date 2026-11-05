package com.betafy.feature.profile.data

import android.content.Context
import android.util.Log
import com.betafy.feature.profile.domain.ProfileTrace
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileProfileTrace(
    context: Context
) : ProfileTrace {
    private val file = File(context.filesDir, "logs/profile.log")
    private val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    override fun event(message: String) {
        val line = "${stamp.format(Date())} $message"
        Log.i(TAG, line)
        synchronized(file) {
            file.parentFile?.mkdirs()
            if (file.exists() && file.length() > MAX_BYTES) {
                val tail = file.readText().takeLast(MAX_BYTES / 2)
                file.writeText(tail)
            }
            file.appendText(line + "\n")
        }
    }

    private companion object {
        const val TAG = "BetafyProfile"
        const val MAX_BYTES = 200_000
    }
}
