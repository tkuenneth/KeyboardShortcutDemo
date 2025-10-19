package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

actual fun Modifier.keyboardShortcuts(
    vararg shortcuts: Pair<Key, () -> Unit>
): Modifier = then(
    Modifier.onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown && event.isAltPressed) {
            shortcuts.forEach { (key, action) ->
                if (event.key == key) {
                    action()
                    true
                }
            }
        }
        event.isAltPressed
    }
)
