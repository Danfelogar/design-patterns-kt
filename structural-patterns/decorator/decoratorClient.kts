/**
 * CLIENT-SIDE DECORATOR EXAMPLE
 * 
 * Scenario: UI Component Enhancement
 * Decorators add visual enhancements, behaviors, and interactions 
 * to UI components without modifying their core rendering logic.
 */

// Component interface
interface UIComponent {
    fun render(): String
    fun getWidth(): Int
    fun getHeight(): Int
}

// Concrete component - Basic button
class BasicButton(private val text: String, private val width: Int = 100, private val height: Int = 40) : UIComponent {
    override fun render(): String {
        return "<button style='width:${width}px;height:${height}px'>$text</button>"
    }
    
    override fun getWidth(): Int = width
    override fun getHeight(): Int = height
}

// Base decorator
abstract class UIComponentDecorator(private val wrappee: UIComponent) : UIComponent {
    override fun render(): String {
        return wrappee.render()
    }
    
    override fun getWidth(): Int = wrappee.getWidth()
    override fun getHeight(): Int = wrappee.getHeight()
}

// Concrete decorator - Styling
class StyledDecorator(wrappee: UIComponent, private val style: String) : UIComponentDecorator(wrappee) {
    override fun render(): String {
        val original = super.render()
        return original.replace("<button", "<button style='$style'")
    }
}

// Concrete decorator - Hover effects
class HoverDecorator(wrappee: UIComponent) : UIComponentDecorator(wrappee) {
    override fun render(): String {
        val original = super.render()
        return original.replace(">", " onmouseover='this.style.opacity=0.8' onmouseout='this.style.opacity=1'>")
    }
}

// Concrete decorator - Tooltip
class TooltipDecorator(wrappee: UIComponent, private val tooltip: String) : UIComponentDecorator(wrappee) {
    override fun render(): String {
        val original = super.render()
        return original.replace(">", " title='$tooltip'>")
    }
}

// Concrete decorator - Loading state
class LoadingDecorator(wrappee: UIComponent) : UIComponentDecorator(wrappee) {
    override fun render(): String {
        val original = super.render()
        return original.replace(">", " onclick='this.innerHTML=\"Loading...\";this.disabled=true'>")
    }
}

// UI Renderer (Client code)
class UIRenderer {
    fun createEnhancedButton(): UIComponent {
        val basicButton = BasicButton("Submit", 120, 45)
        
        // Build enhanced button with decorators
        var button: UIComponent = basicButton
        button = StyledDecorator(button, "background: #007bff; color: white; border: none; border-radius: 5px;")
        button = HoverDecorator(button)
        button = TooltipDecorator(button, "Click to submit the form")
        button = LoadingDecorator(button)
        
        return button
    }
    
    fun createPrimaryButton(): UIComponent {
        val basicButton = BasicButton("Primary Action", 140, 50)
        
        var button: UIComponent = basicButton
        button = StyledDecorator(button, "background: #28a745; color: white; font-weight: bold; border-radius: 8px;")
        button = HoverDecorator(button)
        
        return button
    }
    
    fun createDisabledButton(): UIComponent {
        val basicButton = BasicButton("Disabled", 100, 40)
        
        var button: UIComponent = basicButton
        button = StyledDecorator(button, "background: #6c757d; color: white; opacity: 0.6; cursor: not-allowed;")
        
        return button
    }
    
    fun renderUI() {
        println("🎨 Rendering Enhanced UI Components...\n")
        
        val buttons = listOf(
            createEnhancedButton(),
            createPrimaryButton(),
            createDisabledButton()
        )
        
        buttons.forEachIndexed { index, button ->
            println("Button ${index + 1}:")
            println("Rendered HTML: ${button.render()}")
            println("Dimensions: ${button.getWidth()}x${button.getHeight()}px")
            println()
        }
    }
}

// Client-side usage
fun main() {
    val renderer = UIRenderer()
    renderer.renderUI()
}

main()