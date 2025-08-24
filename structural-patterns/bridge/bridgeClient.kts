/**
 * CLIENT-SIDE BRIDGE EXAMPLE
 *
 * Scenario: UI Theme system with different components and themes
 * The abstraction (UI components) and implementation (themes) can vary independently.
 */

// Implementation interface - Theme
interface Theme {
    fun getBackgroundColor(): String
    fun getTextColor(): String
    fun getButtonColor(): String
    fun getBorderColor(): String
}

// Concrete implementations - Different themes
class DarkTheme : Theme {
    override fun getBackgroundColor(): String = "#2C2C2C"
    override fun getTextColor(): String = "#FFFFFF"
    override fun getButtonColor(): String = "#4A4A4A"
    override fun getBorderColor(): String = "#555555"
}

class LightTheme : Theme {
    override fun getBackgroundColor(): String = "#FFFFFF"
    override fun getTextColor(): String = "#000000"
    override fun getButtonColor(): String = "#F0F0F0"
    override fun getBorderColor(): String = "#DDDDDD"
}

// Abstraction - UI Component
abstract class UIComponent(protected val theme: Theme) {
    abstract fun render(): String
    abstract fun getStyles(): Map<String, String>
}

// Refined abstractions - Different UI components
class Button(theme: Theme, private val text: String) : UIComponent(theme) {
    override fun render(): String {
        return "<button style='${getStyles().map { "${it.key}:${it.value}" }.joinToString(";")}'>$text</button>"
    }
    
    override fun getStyles(): Map<String, String> {
        return mapOf(
            "background-color" to theme.getButtonColor(),
            "color" to theme.getTextColor(),
            "border" to "1px solid ${theme.getBorderColor()}"
        )
    }
}

class Card(theme: Theme, private val content: String) : UIComponent(theme) {
    override fun render(): String {
        return "<div style='${getStyles().map { "${it.key}:${it.value}" }.joinToString(";")}'>$content</div>"
    }
    
    override fun getStyles(): Map<String, String> {
        return mapOf(
            "background-color" to theme.getBackgroundColor(),
            "color" to theme.getTextColor(),
            "border" to "1px solid ${theme.getBorderColor()}",
            "padding" to "16px",
            "border-radius" to "8px"
        )
    }
}

// Client code examples
fun main() {
    println("\n=== CLIENT-SIDE EXAMPLE ===")
    
    // Different combinations of UI components and themes
    val darkTheme = DarkTheme()
    val lightTheme = LightTheme()
    
    val darkButton = Button(darkTheme, "Dark Button")
    val lightCard = Card(lightTheme, "Light Card Content")
    val darkCard = Card(darkTheme, "Dark Card Content")
    
    println("Dark Button: ${darkButton.render()}")
    println("Light Card: ${lightCard.render()}")
    println("Dark Card: ${darkCard.render()}")
}

main()