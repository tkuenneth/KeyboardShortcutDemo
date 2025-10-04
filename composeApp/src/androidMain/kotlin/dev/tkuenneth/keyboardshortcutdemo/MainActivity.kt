package dev.tkuenneth.keyboardshortcutdemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private lateinit var listKeyboardShortcutInfo: List<KeyboardShortcutInfo>
    private lateinit var mapKeyboardShortcuts: Map<KeyboardShortcutInfo, KeyboardShortcut>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        listKeyboardShortcutInfo = listOf(
            KeyboardShortcutInfo(
                getString(R.string.say_hello),
                KeyEvent.KEYCODE_H,
                KeyEvent.META_CTRL_ON
            )
        )
        mapKeyboardShortcuts =
            listKeyboardShortcutInfo.associateWith { shortcutInfo ->
                KeyboardShortcut(
                    label = shortcutInfo.label.toString(),
                    shortcut = shortcutInfo.getDisplayString(),
                )
            }
        setContent {
            var snackbarMessage by remember { mutableStateOf("") }
            val listKeyboardShortcuts = remember {
                mapKeyboardShortcuts.values.toList()
            }
            key(listKeyboardShortcuts) {
                listKeyboardShortcuts.forEach { shortcut ->
                    LaunchedEffect(shortcut) {
                        shortcut.events.collect {
                            snackbarMessage = getString(R.string.hello)
                        }
                    }
                }
            }
            KeyboardShortcutDemo(
                snackbarMessage = snackbarMessage,
                shortcuts = listKeyboardShortcuts,
                showKeyboardShortcuts = { requestShowKeyboardShortcuts() },
                clearSnackbarMessage = { snackbarMessage = "" })
        }
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>,
        menu: Menu?,
        deviceId: Int
    ) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId)
        data.add(KeyboardShortcutGroup(getString(R.string.general), listKeyboardShortcutInfo))
    }

    override fun onKeyShortcut(
        keyCode: Int,
        event: KeyEvent
    ): Boolean {
        listKeyboardShortcutInfo.find { it.keycode == keyCode && event.hasModifiers(it.modifiers) }
            ?.let {
                val shortcut = mapKeyboardShortcuts[it]
                shortcut?.triggerAction()
                return true
            }
        return super.onKeyShortcut(keyCode, event)
    }

    private fun KeyboardShortcutInfo.getDisplayString(): String {
        val parts = mutableListOf<String>()
        if (modifiers and KeyEvent.META_CTRL_ON != 0) {
            parts.add("Ctrl")
        }
        keycode.let { keyCode ->
            if (keyCode in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z) {
                parts.add(('A' + (keyCode - KeyEvent.KEYCODE_A)).toString())
            }
        }
        return parts.joinToString("+")
    }
}
