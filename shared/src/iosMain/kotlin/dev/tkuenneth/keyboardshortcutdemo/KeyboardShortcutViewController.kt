package dev.tkuenneth.keyboardshortcutdemo

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIKeyCommand
import platform.UIKit.UIKeyModifierAlternate
import platform.UIKit.UIKeyModifierCommand
import platform.UIKit.UIKeyModifierControl
import platform.UIKit.UIKeyModifierShift
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.addKeyCommand
import platform.UIKit.didMoveToParentViewController

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@ExportObjCClass
class KeyboardShortcutViewController(
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
            var mask = 0L
            if (shortcut.ctrl) mask = mask or UIKeyModifierControl
            if (shortcut.meta) mask = mask or UIKeyModifierCommand
            if (shortcut.alt) mask = mask or UIKeyModifierAlternate
            if (shortcut.shift) mask = mask or UIKeyModifierShift
            UIKeyCommand.keyCommandWithInput(
                input = shortcut.keyAsString,
                modifierFlags = mask,
                action = NSSelectorFromString("handleCommand:")
            ).apply {
                discoverabilityTitle = shortcut.label
                addKeyCommand(this)
            }
        }
        becomeFirstResponder()
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        composeController.view.setFrame(view.bounds)
    }

    override fun canBecomeFirstResponder(): Boolean = true

    @ObjCAction
    fun handleCommand(sender: UIKeyCommand?) {
        shortcuts.find { it.shortcutAsText.endsWith(sender?.input ?: "") }?.triggerAction()
    }
}
