package dev.tkuenneth.keyboardshortcutdemo

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


data class KeyboardShortcut(
    val label: String,
    val shortcutAsText: String
) {
    private val channel = Channel<Unit>(Channel.CONFLATED)
    val flow = channel.receiveAsFlow()

    fun triggerAction() {
        channel.trySend(Unit)
    }
}
