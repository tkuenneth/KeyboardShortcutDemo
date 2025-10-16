package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.app_name
import dev.tkuenneth.keyboardshortcutdemo.resources.hardware_keyboard_hidden
import dev.tkuenneth.keyboardshortcutdemo.resources.more_options
import dev.tkuenneth.keyboardshortcutdemo.resources.show_keyboard_shortcuts
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardShortcutDemo(
    hardwareKeyboardHidden: Boolean,
    shortcuts: List<KeyboardShortcut>,
    snackbarMessage: String,
    showKeyboardShortcuts: () -> Unit,
    clearSnackbarMessage: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .keyboardShortcuts(
                    Pair(
                        Key.S, showKeyboardShortcuts
                    )
                ),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(Res.string.more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            shortcuts.forEach { shortcut ->
                                DropdownMenuItemWithShortcut(
                                    text = shortcut.label,
                                    shortcut = shortcut.shortcutAsText,
                                    onClick = {
                                        showMenu = false
                                        shortcut.triggerAction()
                                    }
                                )
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
            Box(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LaunchedEffect(focusRequester) {
                    focusRequester.requestFocus()
                }
                Button(
                    onClick = showKeyboardShortcuts,
                ) {
                    TextWithUnderlinedChar(stringResource(Res.string.show_keyboard_shortcuts))
                }
                if (hardwareKeyboardHidden) {
                    Text(
                        text = stringResource(Res.string.hardware_keyboard_hidden),
                        modifier = Modifier.align(Alignment.BottomCenter).safeContentPadding(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            LaunchedEffect(snackbarMessage) {
                if (snackbarMessage.isNotBlank()) {
                    snackBarHostState.showSnackbar(snackbarMessage)
                    clearSnackbarMessage()
                }
            }
        }
    }
}

@Preview
@Composable
private fun KeyboardShortcutDemoPreview() {
    MaterialTheme {
        KeyboardShortcutDemo(
            hardwareKeyboardHidden = false,
            shortcuts = listOf(
                KeyboardShortcut("Cut", "Ctrl+X"),
                KeyboardShortcut("Copy", "Ctrl+C")
            ),
            snackbarMessage = "",
            showKeyboardShortcuts = {},
            clearSnackbarMessage = {}
        )
    }
}
