/**
 * CHAIN OF RESPONSIBILITY PATTERN
 * 
 * The Chain of Responsibility pattern allows an object to pass a request along a chain of potential handlers 
 * until one of them handles the request. It decouples the sender of a request from its receivers by giving 
 * multiple objects a chance to handle the request.
 * 
 * This pattern is useful when you need to process requests in a specific order through multiple handlers, 
 * or when the handler isn't known in advance and needs to be determined dynamically at runtime.
 * 
 * Source: https://refactoring.guru/es/design-patterns/chain-of-responsibility
 */

// The handler interface declares a method for executing a request.
interface ComponentWithContextualHelp {
    fun showHelp()
}

// The base class for simple components.
abstract class Component : ComponentWithContextualHelp {
    protected var tooltipText: String? = null
    
    // The component's container acts as the next link in the chain of handlers.
    // We use internal setter to allow Container class to set this property
    var container: Container? = null
        internal set
    
    // The component shows a hint if it has help text assigned.
    // Otherwise, it forwards the call to the container, if it exists.
    override fun showHelp() {
        if (!tooltipText.isNullOrEmpty()) {
            // Show the tooltip
            println("Showing tooltip: $tooltipText")
        } else {
            // Forward to container
            container?.showHelp() ?: println("No help available")
        }
    }
    
    fun setTooltip(text: String) {
        tooltipText = text
    }
}

// Containers can contain simple components and other containers as children.
// The chain relationships are established here. The class inherits the showHelp
// behavior from its parent.
abstract class Container : Component() {
    protected val children = mutableListOf<Component>()
    
    fun add(child: Component) {
        children.add(child)
        child.container = this  // Now this works with internal setter
    }
}

// Primitive components may be fine with the default help implementation...
class Button : Component() {
    // Additional button-specific functionality can be added here
}

// But complex components can override the default implementation.
// If the help text cannot be provided in a new way, the component can
// always invoke the base implementation (see Component class).
class Panel : Container() {
    private var modalHelpText: String? = null
    
    fun setModalHelpText(text: String) {
        modalHelpText = text
    }
    
    override fun showHelp() {
        if (!modalHelpText.isNullOrEmpty()) {
            // Show a modal window with the help text
            println("Showing modal help: $modalHelpText")
        } else {
            super.showHelp()
        }
    }
}

// Same as above...
class Dialog : Container() {
    private var wikiPageURL: String? = null
    
    fun setWikiPageURL(url: String) {
        wikiPageURL = url
    }
    
    override fun showHelp() {
        if (!wikiPageURL.isNullOrEmpty()) {
            // Open the wiki help page
            println("Opening wiki page: $wikiPageURL")
        } else {
            super.showHelp()
        }
    }
}

// Client code.
class Application {
    private var currentComponent: Component? = null
    
    // Each application configures the chain differently.
    fun createUI() {
        val dialog = Dialog()
        dialog.setWikiPageURL("http://wiki.example.com/budget-reports")
        
        val panel = Panel()
        panel.setModalHelpText("This panel contains form controls for budget reports")
        
        val ok = Button()
        ok.setTooltip("This is an OK button that saves the current budget report")
        
        val cancel = Button()
        cancel.setTooltip("This is a Cancel button that discards changes")
        
        // Build the component hierarchy
        panel.add(ok)
        panel.add(cancel)
        dialog.add(panel)
        
        // Set the current component for demonstration
        currentComponent = ok
    }
    
    // Simulate what happens when F1 is pressed
    fun onF1KeyPress() {
        println("\nF1 key pressed - seeking help...")
        currentComponent?.showHelp()
    }
    
    fun setComponentAtMouseCoords(component: Component) {
        currentComponent = component
    }
}

// Demonstration
fun main() {
    println("=== Chain of Responsibility Pattern: Help System ===\n")
    
    val app = Application()
    app.createUI()
    
    // Test help for different components in the chain
    println("1. Testing help for OK button (has tooltip):")
    app.onF1KeyPress()
    
    println("\n2. Testing help for Cancel button (has tooltip):")
    val cancelButton = Button()
    cancelButton.setTooltip("Cancel button tooltip")
    app.setComponentAtMouseCoords(cancelButton)
    app.onF1KeyPress()
    
    println("\n3. Testing help for Panel (has modal help):")
    val panel = Panel()
    panel.setModalHelpText("Panel modal help text")
    app.setComponentAtMouseCoords(panel)
    app.onF1KeyPress()
    
    println("\n4. Testing help for Dialog (has wiki URL):")
    val dialog = Dialog()
    dialog.setWikiPageURL("http://wiki.example.com/help")
    app.setComponentAtMouseCoords(dialog)
    app.onF1KeyPress()
    
    println("\n5. Testing help for component without help (forwards to parent):")
    val buttonWithoutHelp = Button() // No tooltip set
    val parentPanel = Panel() // No modal help set
    parentPanel.add(buttonWithoutHelp)
    app.setComponentAtMouseCoords(buttonWithoutHelp)
    app.onF1KeyPress()
    
    println("\n6. Testing help for isolated component (no parent chain):")
    val isolatedButton = Button() // No tooltip, no container
    app.setComponentAtMouseCoords(isolatedButton)
    app.onF1KeyPress()
    
    println("\n=== Chain Behavior Summary ===")
    println("• Each component tries to handle the help request itself")
    println("• If it can't handle it, it passes the request to its container")
    println("• The chain continues until a handler is found or the chain ends")
    println("• This allows flexible help system organization")
}

main()