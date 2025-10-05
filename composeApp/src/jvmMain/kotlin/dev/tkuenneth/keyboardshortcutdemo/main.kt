package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.app_name
import dev.tkuenneth.keyboardshortcutdemo.resources.more_options
import dev.tkuenneth.keyboardshortcutdemo.resources.say_hello
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val title = stringResource(Res.string.app_name)
    Window(
        onCloseRequest = ::exitApplication,
        title = title,
    ) {
        val nativeShortcuts = remember {
            runBlocking {
                listOf(
                    Pair(getString(Res.string.say_hello), KeyShortcut(Key.H, ctrl = true)),
                )
            }
        }
        val shortcuts = remember(nativeShortcuts) {
            nativeShortcuts.map {
                KeyboardShortcut(it.first, it.second.toString())
            }
        }
        var showKeyboardShortcuts by remember { mutableStateOf(false) }
        MainScreen(
            listKeyboardShortcuts = shortcuts,
            hardKeyboardHidden = false,
        ) {}
        MenuBar {
            Menu(text = stringResource(Res.string.more_options)) {
                nativeShortcuts.forEachIndexed { index, nativeShortcut ->
                    Item(
                        text = nativeShortcut.first,
                        shortcut = nativeShortcut.second,
                        onClick = { shortcuts[index].triggerAction() }
                    )
                }
            }
        }
    }
}
