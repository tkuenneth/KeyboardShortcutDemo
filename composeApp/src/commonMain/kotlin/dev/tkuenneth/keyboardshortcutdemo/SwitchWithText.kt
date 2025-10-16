package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwitchWithText(
    active: Boolean,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onActiveChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                // we don't want a background
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onActiveChange(!active) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Switch(checked = active, onCheckedChange = onActiveChange)
        text()
    }
}
