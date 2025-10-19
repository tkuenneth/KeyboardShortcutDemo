package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
                var darkMode by remember { mutableStateOf(false) }
                MaterialTheme(colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()) {
                    MainScreen(
                        listKeyboardShortcuts = globalShortcuts,
                        hardKeyboardHidden = hardHidden,
                        darkMode = darkMode,
                        showKeyboardShortcuts = { showKeyboardShortcuts = true },
                        toggleDarkMode = { darkMode = !darkMode }
                    )
                    KeyboardShortcuts(
                        enabled = showKeyboardShortcuts,
                        shortcuts = globalShortcuts
                    ) { showKeyboardShortcuts = false }
                }
            }
        }
    )
}
