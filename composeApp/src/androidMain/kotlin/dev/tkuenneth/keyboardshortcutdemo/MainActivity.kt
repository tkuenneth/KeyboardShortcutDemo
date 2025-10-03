package dev.tkuenneth.keyboardshortcutdemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import keyboardshortcutdemo.composeapp.generated.resources.Res
import keyboardshortcutdemo.composeapp.generated.resources.general
import keyboardshortcutdemo.composeapp.generated.resources.say_hello
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class MainActivity : ComponentActivity() {

    private val channel = Channel<Unit>(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val shortcut = sayHelloKeyboardShortcutInfo()
            setContent {
                KeyboardShortcutDemo(
                    shortcutDisplayString = getDisplayString(shortcut),
                    showKeyboardShortcuts = { requestShowKeyboardShortcuts() },
                    channel = channel,
                    sayHello = ::sayHello
                )
            }
        }
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>,
        menu: Menu?,
        deviceId: Int
    ) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId)
        lifecycleScope.launch {
            mutableListOf<KeyboardShortcutInfo>().apply {
                add(sayHelloKeyboardShortcutInfo())
                data.add(KeyboardShortcutGroup(getString(Res.string.general), this))
            }
        }
    }

    override fun onKeyShortcut(
        keyCode: Int,
        event: KeyEvent
    ): Boolean {
        if (
            keyCode == KeyEvent.KEYCODE_H &&
            event.hasModifiers(KeyEvent.META_CTRL_ON)
        ) {
            sayHello(channel)
            return true
        }
        return super.onKeyShortcut(keyCode, event)
    }
}

fun sayHello(channel: Channel<Unit>) {
    channel.trySend(Unit)
}

fun getDisplayString(shortcutInfo: KeyboardShortcutInfo): String {
    val parts = mutableListOf<String>()
    if (shortcutInfo.modifiers and KeyEvent.META_CTRL_ON != 0) {
        parts.add("Ctrl")
    }
    shortcutInfo.keycode.let { keyCode ->
        if (keyCode in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z) {
            parts.add(('A' + (keyCode - KeyEvent.KEYCODE_A)).toString())
        }
    }
    return parts.joinToString("+")

}

suspend fun sayHelloKeyboardShortcutInfo() = KeyboardShortcutInfo(
    getString(Res.string.say_hello),
    KeyEvent.KEYCODE_H,
    KeyEvent.META_CTRL_ON
)
