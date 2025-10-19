package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController {
    return KeyboardShortcutViewController(
        shortcuts = globalShortcuts,
        contentFactory = {
            ComposeUIViewController {
                var hardHidden by remember { mutableStateOf(false) }
                var showKeyboardShortcuts by remember { mutableStateOf(false) }
                MainScreen(
                    listKeyboardShortcuts = globalShortcuts,
                    hardKeyboardHidden = hardHidden,
                ) { showKeyboardShortcuts = true }
                KeyboardShortcuts(
                    enabled = showKeyboardShortcuts,
                    shortcuts = globalShortcuts
                ) { showKeyboardShortcuts = false }
            }
        }
    )
}
