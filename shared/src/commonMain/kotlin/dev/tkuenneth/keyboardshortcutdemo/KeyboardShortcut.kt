package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.input.key.Key
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


data class KeyboardShortcut(
    val label: String,
    val key: Key,
    val keyAsString: String,
    val ctrl: Boolean = false,
    val meta: Boolean = false,
    val alt: Boolean = false,
    val shift: Boolean = false,
) {
    private val channel = Channel<Unit>(Channel.CONFLATED)
    val flow = channel.receiveAsFlow()

    fun triggerAction() {
        channel.trySend(Unit)
    }

    val shortcutAsText: String
        get() {
            val parts = mutableListOf<String>()
            if (ctrl) {
                parts.add("Ctrl")
            }
            if (meta) {
                parts.add("Meta")
            }
            if (alt) {
                parts.add("Alt")
            }
            if (shift) {
                parts.add("Shift")
            }
            parts.add(keyAsString)
            return parts.joinToString("+")
        }
}
