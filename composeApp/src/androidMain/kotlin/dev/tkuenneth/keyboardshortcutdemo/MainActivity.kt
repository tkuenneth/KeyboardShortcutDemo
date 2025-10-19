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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private lateinit var listKeyboardShortcutInfo: List<KeyboardShortcutInfo>

    private var hardKeyboardHiddenFlow = MutableStateFlow(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        listKeyboardShortcutInfo = globalShortcuts.map { shortcut ->
            KeyboardShortcutInfo(
                shortcut.label,
                shortcut.keyAsString.first(),
                shortcut.modifiers()
            )
        }
        updateFlows()
        setContent {
            val hardKeyboardHidden by hardKeyboardHiddenFlow.collectAsStateWithLifecycle()
            MainScreen(
                listKeyboardShortcuts = globalShortcuts,
                hardKeyboardHidden = hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES,
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
        listKeyboardShortcutInfo.forEachIndexed { index, info ->
            if (info.keycode == keyCode && event.hasModifiers(info.modifiers)) {
                globalShortcuts[index].triggerAction()
                return true
            }
        }
        return super.onKeyShortcut(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFlows()
    }

    private fun updateFlows() {
        hardKeyboardHiddenFlow.update {
            resources.configuration.hardKeyboardHidden
        }
    }
}

private fun KeyboardShortcut.modifiers(): Int {
    var mask = 0
    if (ctrl) mask = mask or KeyEvent.META_CTRL_ON
    if (meta) mask = mask or KeyEvent.META_META_ON
    if (alt) mask = mask or KeyEvent.META_ALT_ON
    if (shift) mask = mask or KeyEvent.META_SHIFT_ON
    return mask
}
