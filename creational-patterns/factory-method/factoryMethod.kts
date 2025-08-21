// The Factory Method pattern defines an interface for creating an object,
// but lets subclasses alter the type of objects that will be created.
// It provides a way to delegate the instantiation logic to child classes.

//link: https://refactoring.guru/es/design-patterns/factory-method

// The Product interface declares the operations that all concrete products must implement.
interface Button {
    fun render()
    fun onClick(f: () -> Unit)
}

// Concrete Products provide various implementations of the Product interface.
class WindowsButton : Button {
    override fun render() {
        // Render a button in a Windows style.
        println("Rendering a Windows button.")
    }
    override fun onClick(f: () -> Unit) {
        // Bind a native OS click event.
        println("Windows button clicked. Closing dialog.")
        f()
    }
}

class HTMLButton : Button {
    override fun render() {
        // Return an HTML representation of a button.
        println("Rendering an HTML button.")
    }
    override fun onClick(f: () -> Unit) {
        // Bind a web browser click event.
        println("HTML button clicked. Closing dialog.")
        f()
    }
}

// The Creator class declares the factory method that must return an object of a Product class.
// Usually, the creator's subclasses provide the implementation of this method.
abstract class Dialog {

    // The factory method. Subclasses will override this to create specific buttons.
    abstract fun createButton(): Button

    // Note that the creator's primary responsibility isn't creating products.
    // It often contains core business logic that relies on product objects returned by the factory method.
    fun render() {
        // Invoke the factory method to create a product object.
        val okButton = createButton()
        // Now, use the product.
        okButton.onClick { closeDialog() }
        okButton.render()
    }

    fun closeDialog() {
        println("Dialog closed.")
    }
}

// Concrete Creators override the factory method to change the resulting product's type.
class WindowsDialog : Dialog() {
    override fun createButton(): Button {
        return WindowsButton()
    }
}

class WebDialog : Dialog() {
    override fun createButton(): Button {
        return HTMLButton()
    }
}

// Client code. It works with an instance of a concrete creator through its base interface.
class Application {
    private lateinit var dialog: Dialog

    // The application picks a creator's type depending on the configuration or environment.
    fun initialize() {
        val config = readApplicationConfigFile()

        dialog = when (config.os) {
            "Windows" -> WindowsDialog()
            "Web" -> WebDialog()
            else -> throw Exception("Error! Unknown operating system.")
        }
    }

    // Simulate reading a config file
    private fun readApplicationConfigFile(): AppConfig {
        // In a real application, this would read from a file or environment variable.
        // For this example, we'll hardcode it to "Windows". Change it to "Web" to see the difference.
        return AppConfig(os = "Windows")
    }

    fun main() {
        initialize()
        dialog.render()
    }
}

// Simple data class to hold configuration
data class AppConfig(val os: String)

// Entry point
fun main() {
    println("=== Probando Windows Dialog ===")
    val windowsDialog = WindowsDialog()
    windowsDialog.render()

    println("\n=== Probando Web Dialog ===")
    val webDialog = WebDialog()
    webDialog.render()
}