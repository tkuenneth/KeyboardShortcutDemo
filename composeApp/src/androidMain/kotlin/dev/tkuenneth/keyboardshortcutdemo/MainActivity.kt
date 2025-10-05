package dev.tkuenneth.keyboardshortcutdemo

import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private lateinit var listKeyboardShortcutInfo: List<KeyboardShortcutInfo>
    private lateinit var mapKeyboardShortcuts: Map<KeyboardShortcutInfo, KeyboardShortcut>

    private var hardKeyboardHiddenFlow = MutableStateFlow(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        listKeyboardShortcutInfo = listOf(
            KeyboardShortcutInfo(
                getString(R.string.say_hello), KeyEvent.KEYCODE_H, KeyEvent.META_CTRL_ON
            )
        )
        mapKeyboardShortcuts = listKeyboardShortcutInfo.associateWith { shortcutInfo ->
            KeyboardShortcut(
                label = shortcutInfo.label.toString(),
                shortcutAsText = shortcutInfo.getDisplayString(),
            )
        }
        updateFlows()
        setContent {
            val listKeyboardShortcuts = remember {
                mapKeyboardShortcuts.values.toList()
            }
            val hardKeyboardHidden by hardKeyboardHiddenFlow.collectAsStateWithLifecycle()
            MainScreen(
                listKeyboardShortcuts = listKeyboardShortcuts,
                hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES,
            ) { requestShowKeyboardShortcuts() }
        }
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>, menu: Menu?, deviceId: Int
    ) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId)
        data.add(KeyboardShortcutGroup(getString(R.string.general), listKeyboardShortcutInfo))
    }

    override fun onKeyShortcut(
        keyCode: Int, event: KeyEvent
    ): Boolean {
        val keyboardShortcutInfo = listKeyboardShortcutInfo.find {
            it.keycode == keyCode && event.hasModifiers(it.modifiers)
        }
        val shortcut = mapKeyboardShortcuts[keyboardShortcutInfo]
        if (shortcut != null) {
            shortcut.triggerAction()
            return true
        }
        return super.onKeyShortcut(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFlows()
    }

    private fun KeyboardShortcutInfo.getDisplayString(): String {
        val parts = mutableListOf<String>()
        if (modifiers and KeyEvent.META_CTRL_ON != 0) {
            parts.add("Ctrl")
        }
        if (keycode in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z) {
            parts.add(('A' + (keycode - KeyEvent.KEYCODE_A)).toString())
        }
        return parts.joinToString("+")
    }

    private fun updateFlows() {
        hardKeyboardHiddenFlow.update {
            resources.configuration.hardKeyboardHidden
        }
    }
}
