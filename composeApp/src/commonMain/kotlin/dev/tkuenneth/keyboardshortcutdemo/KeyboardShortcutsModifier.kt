package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

/**
 * A modifier that listens for specific key presses.
 * Besides the specified key, Alt must also be pressed.
 *
 * @param shortcuts Pairs of keys and their corresponding actions
 */
fun Modifier.keyboardShortcuts(
    vararg shortcuts: Pair<Key, () -> Unit>
): Modifier = this.then(KeyboardShortcutElement(shortcuts.toList()))

private data class KeyboardShortcutElement(
    private val shortcuts: List<Pair<Key, () -> Unit>>
) : ModifierNodeElement<KeyboardShortcutNode>() {

    override fun create(): KeyboardShortcutNode {
        return KeyboardShortcutNode(shortcuts)
    }

    override fun update(node: KeyboardShortcutNode) {
        node.updateShortcuts(shortcuts)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "keyboardShortcuts"
        properties["shortcuts"] = shortcuts.map { it.first }
    }
}

private class KeyboardShortcutNode(
    private var shortcuts: List<Pair<Key, () -> Unit>>
) : Modifier.Node(), KeyInputModifierNode {

    fun updateShortcuts(newShortcuts: List<Pair<Key, () -> Unit>>) {
        if (shortcuts != newShortcuts) {
            shortcuts = newShortcuts
        }
    }

    override fun onPreKeyEvent(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyDown) {
            shortcuts.forEach { (key, action) ->
                if (event.key == key && event.isAltPressed) {
                    action()
                    return true
                }
            }
        }
        return false
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        return false
    }
}
