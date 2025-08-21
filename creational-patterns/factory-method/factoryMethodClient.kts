// Product interface
interface Button {
    fun render()
    fun onClick(action: () -> Unit)
}

// Concrete products
class AndroidButton : Button {
    override fun render() {
        println("Rendering Material Design button")
    }

    override fun onClick(action: () -> Unit) {
        println("Android button clicked - executing action")
        action()
    }
}

class IOSButton : Button {
    override fun render() {
        println("Rendering Cupertino style button")
    }

    override fun onClick(action: () -> Unit) {
        println("iOS button clicked - executing action")
        action()
    }
}

class WebButton : Button {
    override fun render() {
        println("Rendering responsive HTML/CSS button")
    }

    override fun onClick(action: () -> Unit) {
        println("Web button clicked - executing action")
        action()
    }
}

// Creator
abstract class UIFactory {
    // Factory method
    abstract fun createButton(): Button
    
    // Business logic that uses the product
    fun renderUI() {
        val button = createButton()
        button.render()
        button.onClick {
            println("Default click action performed")
        }
    }
}

// Concrete creators
class AndroidUIFactory : UIFactory() {
    override fun createButton(): Button = AndroidButton()
}

class IOSUIFactory : UIFactory() {
    override fun createButton(): Button = IOSButton()
}

class WebUIFactory : UIFactory() {
    override fun createButton(): Button = WebButton()
}

// Client code
fun main() {
    // This would typically be determined by the runtime environment
    val platform = "android" // Could be "ios", "web", etc.

    val factory: UIFactory = when (platform) {
        "android" -> AndroidUIFactory()
        "ios" -> IOSUIFactory()
        "web" -> WebUIFactory()
        else -> throw IllegalArgumentException("Unknown platform: $platform")
    }

    factory.renderUI()
}

main()