# KeyboardShortcutDemo

Shows how to use keyboard shortcuts on Android, iOS, and the Desktop.

## Project structure

| Module | Role |
| --- | --- |
| `shared` | Kotlin Multiplatform library with shared Compose UI and logic |
| `androidApp` | Android application entry point |
| `desktopApp` | Desktop (JVM) application entry point |
| `iosApp` | Xcode iOS application entry point |

## Run

- **Android:** run the `androidApp` configuration
- **Desktop:** `./gradlew :desktopApp:run`
- **iOS:** open `iosApp` in Xcode and run
