package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.hello
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    listKeyboardShortcuts: List<KeyboardShortcut>,
    hardKeyboardHidden: Boolean,
    showKeyboardShortcuts: () -> Unit,
) {
    var snackbarMessage by remember { mutableStateOf("") }
    val helloMessage = stringResource(Res.string.hello)
    LaunchedEffect(listKeyboardShortcuts) {
        listKeyboardShortcuts.forEach { shortcut ->
            launch {
                shortcut.flow.collectLatest {
                    snackbarMessage = helloMessage
                }
            }
        }
    }
    KeyboardShortcutDemo(
        hardwareKeyboardHidden = hardKeyboardHidden,
        snackbarMessage = snackbarMessage,
        shortcuts = listKeyboardShortcuts,
        showKeyboardShortcuts = showKeyboardShortcuts,
        clearSnackbarMessage = { snackbarMessage = "" })
}
