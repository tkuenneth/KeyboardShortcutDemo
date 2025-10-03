package dev.tkuenneth.keyboardshortcutdemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform