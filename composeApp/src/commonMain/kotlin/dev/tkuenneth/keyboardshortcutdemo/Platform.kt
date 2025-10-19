package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key

/**
 * A modifier that listens for specific key presses.
 * Besides the specified key, Alt must also be pressed.
 *
 * @param shortcuts Pairs of keys and their corresponding actions
 */
expect fun Modifier.keyboardShortcuts(
    vararg shortcuts: Pair<Key, () -> Unit>
): Modifier
