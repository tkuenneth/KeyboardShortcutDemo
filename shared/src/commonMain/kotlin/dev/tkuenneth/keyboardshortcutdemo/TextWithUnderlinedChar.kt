package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun TextWithUnderlinedChar(text: String) {
    Text(buildAnnotatedString {
        val result = Regex("\\[([a-zA-Z])").find(text)
        val pos = result?.range?.first ?: -1
        if (pos >= 0) {
            append(text.substring(0, pos))
            result?.groupValues?.get(1)?.let { char ->
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(char)
                }
            }
            append(text.substring(pos + 2))
        } else {
            append(text)
        }
    })
}
