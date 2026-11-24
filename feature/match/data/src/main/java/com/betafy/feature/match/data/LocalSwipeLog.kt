package com.betafy.feature.match.data

import android.content.Context
import com.betafy.feature.match.domain.SwipeLog
import com.betafy.feature.match.domain.swipeStillHidden

class LocalSwipeLog(
    context: Context
) : SwipeLog {
    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    override fun remember(userId: String, key: String, atMillis: Long) {
        if (userId.isBlank() || key.isBlank()) return
        prefs.edit().putLong(entry(userId, key), atMillis).apply()
    }

    override fun hidden(userId: String, nowMillis: Long): Set<String> {
        if (userId.isBlank()) return emptySet()
        val prefix = "$userId$SEPARATOR"
        val editor = prefs.edit()
        val fresh = mutableSetOf<String>()
        var dirty = false
        prefs.all.forEach { (name, value) ->
            if (!name.startsWith(prefix) || value !is Long) return@forEach
            if (swipeStillHidden(value, nowMillis)) {
                fresh += name.removePrefix(prefix)
            } else {
                editor.remove(name)
                dirty = true
            }
        }
        if (dirty) editor.apply()
        return fresh
    }

    private fun entry(userId: String, key: String) = "$userId$SEPARATOR$key"

    private companion object {
        const val FILE = "betafy_swipes"
        const val SEPARATOR = "\u0000"
    }
}
