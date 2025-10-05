package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    MainScreen(
        listKeyboardShortcuts = emptyList(),
        hardKeyboardHidden = false,
    ) {}
}
