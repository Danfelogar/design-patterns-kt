// Abstract Factory for UI themes
interface UIThemeFactory {
    fun createButton(): Button
    fun createDialog(): Dialog
    fun createTextField(): TextField
}

// Concrete factories for different themes
class LightThemeFactory : UIThemeFactory {
    override fun createButton(): Button = LightButton()
    override fun createDialog(): Dialog = LightDialog()
    override fun createTextField(): TextField = LightTextField()
}

class DarkThemeFactory : UIThemeFactory {
    override fun createButton(): Button = DarkButton()
    override fun createDialog(): Dialog = DarkDialog()
    override fun createTextField(): TextField = DarkTextField()
}

class HighContrastThemeFactory : UIThemeFactory {
    override fun createButton(): Button = HighContrastButton()
    override fun createDialog(): Dialog = HighContrastDialog()
    override fun createTextField(): TextField = HighContrastTextField()
}

// Product interfaces
interface Button {
    fun render()
    fun onClick(action: () -> Unit)
}

interface Dialog {
    fun show()
    fun setTitle(title: String)
}

interface TextField {
    fun render()
    fun setText(text: String)
}

// Light theme products
class LightButton : Button {
    override fun render() {
        println("Rendering light theme button with soft shadows")
    }

    override fun onClick(action: () -> Unit) {
        println("Light button clicked")
        action()
    }
}

class LightDialog : Dialog {
    override fun show() {
        println("Showing light theme dialog with white background")
    }

    override fun setTitle(title: String) {
        println("Light dialog title: $title")
    }
}

class LightTextField : TextField {
    override fun render() {
        println("Rendering light theme text field")
    }

    override fun setText(text: String) {
        println("Light text field content: $text")
    }
}

// Dark theme products
class DarkButton : Button {
    override fun render() {
        println("Rendering dark theme button with subtle glow")
    }

    override fun onClick(action: () -> Unit) {
        println("Dark button clicked")
        action()
    }
}

class DarkDialog : Dialog {
    override fun show() {
        println("Showing dark theme dialog with dark background")
    }

    override fun setTitle(title: String) {
        println("Dark dialog title: $title")
    }
}

class DarkTextField : TextField {
    override fun render() {
        println("Rendering dark theme text field")
    }

    override fun setText(text: String) {
        println("Dark text field content: $text")
    }
}

// High contrast theme products
class HighContrastButton : Button {
    override fun render() {
        println("Rendering high contrast button with bold borders")
    }

    override fun onClick(action: () -> Unit) {
        println("High contrast button clicked")
        action()
    }
}

class HighContrastDialog : Dialog {
    override fun show() {
        println("Showing high contrast dialog with stark colors")
    }

    override fun setTitle(title: String) {
        println("High contrast dialog title: $title")
    }
}

class HighContrastTextField : TextField {
    override fun render() {
        println("Rendering high contrast text field")
    }

    override fun setText(text: String) {
        println("High contrast text field content: $text")
    }
}

// Client application
class ClientApplication(private val themeFactory: UIThemeFactory) {
    private lateinit var button: Button
    private lateinit var dialog: Dialog
    private lateinit var textField: TextField

    fun buildUI() {
        button = themeFactory.createButton()
        dialog = themeFactory.createDialog()
        textField = themeFactory.createTextField()

        println("Building UI with consistent theme...")
        button.render()
        dialog.show()
        textField.render()
    }

    fun simulateUserInteraction() {
        button.onClick {
            dialog.setTitle("Button was clicked!")
            textField.setText("Action completed")
        }
    }
}

// Theme selector based on user preferences or system settings
fun main() {
    val userPreference = "high-contrast" // Could be "light", "dark", or "high-contrast"

    val themeFactory: UIThemeFactory = when (userPreference) {
        "light" -> LightThemeFactory()
        "dark" -> DarkThemeFactory()
        "high-contrast" -> HighContrastThemeFactory()
        else -> LightThemeFactory() // Default
    }

    val app = ClientApplication(themeFactory)
    app.buildUI()
    app.simulateUserInteraction()
}

main()