package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.app_name
import dev.tkuenneth.keyboardshortcutdemo.resources.more_options
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val title = stringResource(Res.string.app_name)
    Window(
        onCloseRequest = ::exitApplication,
        title = title,
    ) {
        var showKeyboardShortcuts by remember { mutableStateOf(false) }
        var darkMode by remember { mutableStateOf(false) }
        MaterialTheme(colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()) {
            MainScreen(
                listKeyboardShortcuts = globalShortcuts,
                hardKeyboardHidden = false,
                darkMode = darkMode,
                showKeyboardShortcuts = { showKeyboardShortcuts = true },
                toggleDarkMode = { darkMode = !darkMode }
            )
            MenuBar {
                Menu(text = stringResource(Res.string.more_options)) {
                    globalShortcuts.forEachIndexed { index, shortcut ->
                        Item(
                            text = shortcut.label,
                            shortcut = KeyShortcut(
                                shortcut.key,
                                shortcut.ctrl,
                                shortcut.meta,
                                shortcut.alt,
                                shortcut.shift,
                            ),
                            onClick = {
                                globalShortcuts[index].triggerAction()
                            })
                    }
                }
            }
            KeyboardShortcuts(
                enabled = showKeyboardShortcuts, shortcuts = globalShortcuts
            ) { showKeyboardShortcuts = false }
        }
    }
}
