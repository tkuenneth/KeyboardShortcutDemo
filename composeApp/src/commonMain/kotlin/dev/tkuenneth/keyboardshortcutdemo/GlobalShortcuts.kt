package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.input.key.Key
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.say_hello
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

val globalShortcuts = runBlocking {
    listOf(
        KeyboardShortcut(
            label = getString(Res.string.say_hello),
            key = Key.H,
            keyAsString = "H",
            ctrl = true
        )
    )
}
