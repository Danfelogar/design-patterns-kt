/**
 * CLIENT-SIDE COMPOSITE EXAMPLE
 * 
 * Scenario: UI layout system for mobile/desktop applications
 * UI components can be individual elements or containers holding multiple elements
 * Both are treated uniformly for rendering and layout calculations
 */

// Component interface
interface UIComponent {
    fun render(): String
    fun getWidth(): Int
    fun getHeight(): Int
    fun calculateLayout(): Map<String, Any>
}

// Leaf - Basic UI elements
class Button(
    private val text: String,
    private val width: Int,
    private val height: Int
) : UIComponent {
    override fun render(): String {
        return "<button style='width:${width}px;height:${height}px'>$text</button>"
    }
    
    override fun getWidth(): Int = width
    override fun getHeight(): Int = height
    
    override fun calculateLayout(): Map<String, Any> {
        return mapOf(
            "type" to "button",
            "width" to width,
            "height" to height,
            "text" to text
        )
    }
}

class TextField(
    private val placeholder: String,
    private val width: Int,
    private val height: Int
) : UIComponent {
    override fun render(): String {
        return "<input type='text' placeholder='$placeholder' style='width:${width}px;height:${height}px'>"
    }
    
    override fun getWidth(): Int = width
    override fun getHeight(): Int = height
    
    override fun calculateLayout(): Map<String, Any> {
        return mapOf(
            "type" to "textfield",
            "width" to width,
            "height" to height,
            "placeholder" to placeholder
        )
    }
}

// Composite - Container components
class Container(private val name: String) : UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val padding = 10
    
    fun addComponent(component: UIComponent) {
        children.add(component)
    }
    
    fun removeComponent(component: UIComponent) {
        children.remove(component)
    }
    
    override fun render(): String {
        val childrenHtml = children.joinToString("\n") { it.render() }
        return """
        <div class='container' style='padding:${padding}px'>
            <h3>$name</h3>
            $childrenHtml
        </div>
        """.trimIndent()
    }
    
    override fun getWidth(): Int {
        return children.maxOfOrNull { it.getWidth() }?.plus(padding * 2) ?: 0
    }
    
    override fun getHeight(): Int {
        return children.sumOf { it.getHeight() } + padding * 2 + 30 // 30 for header
    }
    
    override fun calculateLayout(): Map<String, Any> {
        val childrenLayouts = children.map { it.calculateLayout() }
        return mapOf(
            "type" to "container",
            "name" to name,
            "width" to getWidth(),
            "height" to getHeight(),
            "children" to childrenLayouts,
            "childrenCount" to children.size
        )
    }
}

// Client-side UI renderer
class UIRenderer {
    fun renderScreen(components: List<UIComponent>) {
        println("=== RENDERING UI SCREEN ===")
        
        for (component in components) {
            println("\n${component.render()}")
            val layout = component.calculateLayout()
            println("Layout info: $layout")
        }
        
        val totalWidth = components.maxOfOrNull { it.getWidth() } ?: 0
        val totalHeight = components.sumOf { it.getHeight() }
        
        println("\nTotal screen dimensions: ${totalWidth}x$totalHeight")
        println("=== UI RENDERING COMPLETE ===\n")
    }
}

// Client-side usage
fun main() {
    val renderer = UIRenderer()
    
    // Individual UI components
    val loginButton = Button("Login", 100, 40)
    val signupButton = Button("Sign Up", 120, 40)
    val emailField = TextField("Enter email", 200, 35)
    val passwordField = TextField("Enter password", 200, 35)
    
    // Composite container
    val loginForm = Container("Login Form")
    loginForm.addComponent(emailField)
    loginForm.addComponent(passwordField)
    loginForm.addComponent(loginButton)
    
    val authPanel = Container("Authentication Panel")
    authPanel.addComponent(loginForm)
    authPanel.addComponent(signupButton)
    
    // Render UI with both individual components and containers
    val uiComponents = listOf(authPanel, loginButton) // Mix of composite and leaf
    renderer.renderScreen(uiComponents)
}

main()