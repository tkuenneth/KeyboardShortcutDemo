package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import dev.tkuenneth.keyboardshortcutdemo.resources.Res
import dev.tkuenneth.keyboardshortcutdemo.resources.say_hello
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIKeyCommand
import platform.UIKit.UIKeyModifierControl
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.addKeyCommand
import platform.UIKit.didMoveToParentViewController

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@ExportObjCClass
class KeyboardHostingController(
    private val shortcuts: List<KeyboardShortcut>,
    private val contentFactory: () -> UIViewController,
) : UIViewController(nibName = null, bundle = null) {

    private lateinit var composeController: UIViewController

    override fun viewDidLoad() {
        super.viewDidLoad()
        composeController = contentFactory()
        addChildViewController(composeController)
        view.addSubview(composeController.view)
        composeController.view.autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        composeController.didMoveToParentViewController(this)
        shortcuts.forEach { shortcut ->
            UIKeyCommand.keyCommandWithInput(
                input = shortcut.shortcutAsText,
                modifierFlags = UIKeyModifierControl,
                action = NSSelectorFromString("handleCommand:")
            ).apply {
                discoverabilityTitle = shortcut.label
                addKeyCommand(this)
            }
        }
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        becomeFirstResponder()
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        composeController.view.setFrame(view.bounds)
    }

    override fun canBecomeFirstResponder(): Boolean = true

    @ObjCAction
    fun handleCommand(sender: UIKeyCommand?) {
        shortcuts.find { it.shortcutAsText == sender?.input }?.triggerAction()
    }
}

fun MainViewController(): UIViewController {
    val sayHello = runBlocking { getString(Res.string.say_hello) }
    val shortcuts = listOf(KeyboardShortcut(sayHello, "H"))
    return KeyboardHostingController(
        shortcuts = shortcuts,
        contentFactory = {
            ComposeUIViewController {
                var hardHidden by remember { mutableStateOf(false) }
                MainScreen(
                    listKeyboardShortcuts = shortcuts,
                    hardKeyboardHidden = hardHidden,
                ) { }
            }
        }
    )
}
