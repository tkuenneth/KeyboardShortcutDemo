package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.app_name
import dev.tkuenneth.keyboardshortcutdemo.resources.dark_mode
import dev.tkuenneth.keyboardshortcutdemo.resources.hardware_keyboard_hidden
import dev.tkuenneth.keyboardshortcutdemo.resources.hint
import dev.tkuenneth.keyboardshortcutdemo.resources.more_options
import dev.tkuenneth.keyboardshortcutdemo.resources.show_keyboard_shortcuts
import kotlinx.coroutines.launch
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
    var darkMode by remember { mutableStateOf(false) }
    val toggleDarkMode = remember { { darkMode = !darkMode } }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val animatable = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    MaterialTheme(colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .keyboardShortcuts(
                    Pair(
                        Key.S, showKeyboardShortcuts
                    ),
                    Pair(
                        Key.D
                    ) { toggleDarkMode() },
                )
                // Depending on the platform, global shortcuts may not be delivered when a composable
                // has requested focus, so we make sure to handle global shortcuts here, too
                .onPreviewKeyEvent { event ->
                    shortcuts.forEach { shortcut ->
                        if (shortcut.key == event.key &&
                            shortcut.ctrl == event.isCtrlPressed &&
                            shortcut.meta == event.isMetaPressed &&
                            shortcut.alt == event.isAltPressed &&
                            shortcut.shift == event.isShiftPressed
                        ) {
                            shortcut.triggerAction()
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.onKeyEvent {
                            if (it.type == KeyEventType.KeyUp && it.key == Key.Tab) {
                                scope.launch {
                                    animatable.animateTo(359f, animationSpec = tween(durationMillis = 1000))
                                    animatable.snapTo(0F)
                                }
                                true
                            } else {
                                false
                            }
                        }.graphicsLayer {
                            rotationY = animatable.value
                        },
                        placeholder = { Text(stringResource(Res.string.hint)) }
                    )
                    SwitchWithText(active = darkMode, text = {
                        TextWithUnderlinedChar(stringResource(Res.string.dark_mode))
                    }) { toggleDarkMode() }
                    Button(
                        onClick = showKeyboardShortcuts,
                    ) {
                        TextWithUnderlinedChar(stringResource(Res.string.show_keyboard_shortcuts))
                    }
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
                KeyboardShortcut("Cut", Key.X, "X", ctrl = true),
                KeyboardShortcut("Copy", Key.C, "C", ctrl = true),
            ),
            snackbarMessage = "",
            showKeyboardShortcuts = {},
            clearSnackbarMessage = {}
        )
    }
}
