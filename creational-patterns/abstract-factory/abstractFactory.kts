// The Abstract Factory pattern provides an interface for creating families of related
// or dependent objects without specifying their concrete classes. It allows a client
// to create objects that follow a common theme without being coupled to their specific implementations.

//link doc: http://refactoring.guru/es/design-patterns/abstract-factory

// Abstract Factory interface declares a set of methods that return different abstract products.
// These products are called a family and are related by a high-level theme or concept.
interface GUIFactory {
    fun createButton(): Button
    fun createCheckbox(): Checkbox
}

// Concrete factories produce a family of products that belong to a single variant.
// The factory guarantees that the resulting products are compatible.
class WinFactory : GUIFactory {
    override fun createButton(): Button = WinButton()
    override fun createCheckbox(): Checkbox = WinCheckbox()
}

// Each concrete factory has a corresponding product variant.
class MacFactory : GUIFactory {
    override fun createButton(): Button = MacButton()
    override fun createCheckbox(): Checkbox = MacCheckbox()
}

// Each product of a product family must have a base interface.
// All variants of the product must implement this interface.
interface Button {
    fun paint()
}

// Concrete products are created by corresponding concrete factories.
class WinButton : Button {
    override fun paint() {
        println("Rendering a button in Windows style")
    }
}

class MacButton : Button {
    override fun paint() {
        println("Rendering a button in macOS style")
    }
}

// Here's the base interface of another product.
interface Checkbox {
    fun paint()
}

class WinCheckbox : Checkbox {
    override fun paint() {
        println("Rendering a checkbox in Windows style")
    }
}

class MacCheckbox : Checkbox {
    override fun paint() {
        println("Rendering a checkbox in macOS style")
    }
}

// Client code works with factories and products only through abstract types.
// This allows passing any factory or product subclass to the client code without breaking it.
class Application(private val factory: GUIFactory) {
    private lateinit var button: Button
    private lateinit var checkbox: Checkbox

    fun createUI() {
        button = factory.createButton()
        checkbox = factory.createCheckbox()
    }

    fun paint() {
        button.paint()
        checkbox.paint()
    }
}

// Application configuration that chooses the factory type based on configuration
class ApplicationConfigurator {
    fun main() {
        val config = readApplicationConfigFile()

        val factory: GUIFactory = when (config.os) {
            "Windows" -> WinFactory()
            "Mac" -> MacFactory()
            else -> throw Exception("Error! Unknown operating system.")
        }

        val app = Application(factory)
        app.createUI()
        app.paint()
    }

    private fun readApplicationConfigFile(): AppConfig {
        // In a real application, this would read from a config file
        return AppConfig(os = "Windows") // Change to "Mac" to see different behavior
    }
}

// Simple data class to hold configuration
data class AppConfig(val os: String)

// Entry point
fun main() {
    val configurator = ApplicationConfigurator()
    configurator.main()
}

main()