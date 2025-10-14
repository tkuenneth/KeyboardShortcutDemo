package dev.tkuenneth.keyboardshortcutdemo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIKeyCommand
import platform.UIKit.UIKeyModifierCommand
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.addKeyCommand
import platform.UIKit.didMoveToParentViewController

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@ExportObjCClass
class KeyboardHostingController(
    private val onAddAction: () -> Unit,
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
        val cmd = UIKeyCommand.keyCommandWithInput(
            input = "N",
            modifierFlags = UIKeyModifierCommand,
            action = NSSelectorFromString("handleAddCommand:")
        )
        cmd.discoverabilityTitle = "Add hello"
        addKeyCommand(cmd)
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
    fun handleAddCommand(sender: UIKeyCommand?) {
        onAddAction()
    }
}

fun MainViewController(): UIViewController {
    val trigger = Channel<Unit>(CONFLATED)
    val addAction: () -> Unit = { trigger.trySend(Unit) }
    return KeyboardHostingController(
        onAddAction = addAction,
        contentFactory = {
            ComposeUIViewController {
                var hardHidden by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    launch {
                        trigger.receiveAsFlow().collectLatest {
                            hardHidden = !hardHidden
                        }
                    }
                }
                MainScreen(
                    listKeyboardShortcuts = emptyList(),
                    hardKeyboardHidden = hardHidden,
                ) { addAction() }
            }
        }
    )
}
