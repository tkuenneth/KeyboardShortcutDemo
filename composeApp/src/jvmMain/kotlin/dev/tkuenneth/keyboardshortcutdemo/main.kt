package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KeyboardShortcutDemo",
    ) {
        MainScreen(
            listKeyboardShortcuts = emptyList(),
            hardKeyboardHidden = false,
        ) {}
    }
}