package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import keyboardshortcutdemo.composeapp.generated.resources.Res
import keyboardshortcutdemo.composeapp.generated.resources.app_name
import keyboardshortcutdemo.composeapp.generated.resources.more_options
import keyboardshortcutdemo.composeapp.generated.resources.show_keyboard_shortcuts
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.compose.resources.stringResource

data class KeyboardShortcut(
    val label: String,
    val shortcut: String,
    val snackbarMessage: String,
    val channel: Channel<Unit>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardShortcutDemo(
    shortcuts: List<KeyboardShortcut>,
    showKeyboardShortcuts: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
                            shortcuts.forEach {
                                with(it) {
                                    DropdownMenuItemWithShortcut(
                                        text = label,
                                        shortcut = shortcut,
                                        onClick = {
                                            showMenu = false
                                            channel.trySend(Unit)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Button(onClick = showKeyboardShortcuts) {
                    Text(stringResource(Res.string.show_keyboard_shortcuts))
                }
            }
            key(shortcuts) {
                shortcuts.forEach {
                    with(it) {
                        LaunchedEffect(channel) {
                            channel.receiveAsFlow().collect {
                                snackBarHostState.showSnackbar(snackbarMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}
