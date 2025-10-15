package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.say_hello
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController {
    val sayHello = runBlocking { getString(Res.string.say_hello) }
    val shortcuts = listOf(KeyboardShortcut(sayHello, "^H"))
    return KeyboardShortcutViewController(
        shortcuts = shortcuts,
        contentFactory = {
            ComposeUIViewController {
                var hardHidden by remember { mutableStateOf(false) }
                var showKeyboardShortcuts by remember { mutableStateOf(false) }
                MainScreen(
                    listKeyboardShortcuts = shortcuts,
                    hardKeyboardHidden = hardHidden,
                ) { showKeyboardShortcuts = true }
                KeyboardShortcuts(
                    enabled = showKeyboardShortcuts,
                    shortcuts = shortcuts
                ) { showKeyboardShortcuts = false }
            }
        }
    )
}
