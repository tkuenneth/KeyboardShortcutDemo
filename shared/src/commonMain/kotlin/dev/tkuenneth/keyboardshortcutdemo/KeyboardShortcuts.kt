package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.keyboard_shortcuts
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardShortcuts(
    enabled: Boolean,
    shortcuts: List<KeyboardShortcut>,
    onDismissRequest: () -> Unit
) {
    if (enabled) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.keyboard_shortcuts),
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                shortcuts.forEach { shortcut ->
                    item {
                        ShortcutText(text = shortcut.label, shortcut = shortcut.shortcutAsText)
                    }
                }
            }
        }
    }
}
