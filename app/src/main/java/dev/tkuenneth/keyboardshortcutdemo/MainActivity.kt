package dev.tkuenneth.keyboardshortcutdemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class MainActivity : ComponentActivity() {

    private val channel = Channel<Unit>(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeyboardShortcutDemo(
                showKeyboardShortcuts = { requestShowKeyboardShortcuts() },
                channel = channel
            )
        }
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>,
        menu: Menu?,
        deviceId: Int
    ) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId)
        mutableListOf<KeyboardShortcutInfo>().apply {
            add(
                KeyboardShortcutInfo(
                    getString(R.string.say_hello),
                    KeyEvent.KEYCODE_H,
                    KeyEvent.META_CTRL_ON
                )
            )
            data.add(KeyboardShortcutGroup(getString(R.string.general), this))
        }
    }

    override fun onKeyShortcut(
        keyCode: Int,
        event: KeyEvent
    ): Boolean {
        if (
            keyCode == KeyEvent.KEYCODE_H &&
            event.hasModifiers(KeyEvent.META_CTRL_ON)
        ) {
            sayHello(channel)
            return true
        }
        return super.onKeyShortcut(keyCode, event)
    }
}

fun sayHello(channel: Channel<Unit>) {
    channel.trySend(Unit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardShortcutDemo(
    showKeyboardShortcuts: () -> Unit,
    channel: Channel<Unit>
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.say_hello)) },
                                onClick = {
                                    showMenu = false
                                    sayHello(channel)
                                }
                            )
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
                    Text(stringResource(R.string.show_keyboard_shortcuts))
                }
            }
            stringResource(R.string.hello).let { hello ->
                LaunchedEffect(channel) {
                    channel.receiveAsFlow().collect {
                        snackBarHostState.showSnackbar(hello)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardShortcutDemoPreview() {
    KeyboardShortcutDemo(showKeyboardShortcuts = {}, channel = Channel())
}
