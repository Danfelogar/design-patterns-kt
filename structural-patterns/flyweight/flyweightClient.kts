/**
 * CLIENT-SIDE FLYWEIGHT EXAMPLE
 * 
 * Scenario: UI Icon System with Shared Icons
 * Flyweight pattern shares icon graphics and styles across UI components
 * to reduce memory usage in mobile/web applications.
 */

// Intrinsic state (shared)
data class IconType(
    val name: String,
    val svgPath: String,
    val defaultColor: String,
    val defaultSize: Int
) {
    fun render(x: Int, y: Int, color: String? = null, size: Int? = null) {
        val renderColor = color ?: defaultColor
        val renderSize = size ?: defaultSize
        
        println("Rendering icon '$name' at ($x, $y)")
        println("  Color: $renderColor, Size: ${renderSize}px")
        println("  SVG: ${svgPath.take(20)}...")
        println("  ---")
    }
}

// Flyweight factory
object IconFactory {
    private val icons = mutableMapOf<String, IconType>()
    
    fun getIcon(name: String, svgPath: String, defaultColor: String = "#000000", defaultSize: Int = 24): IconType {
        val key = "$name|$svgPath|$defaultColor|$defaultSize"
        return icons.getOrPut(key) {
            println("Loading new icon: $name")
            IconType(name, svgPath, defaultColor, defaultSize)
        }
    }
    
    fun getIconsCount(): Int = icons.size
}

// Extrinsic state (unique)
data class UIIcon(
    val id: String,
    val x: Int,
    val y: Int,
    val color: String?,
    val size: Int?,
    val iconType: IconType
) {
    fun render() {
        iconType.render(x, y, color, size)
    }
}

// Client - UI component system
class UIManager {
    private val icons = mutableListOf<UIIcon>()
    
    fun addIcon(id: String, x: Int, y: Int, color: String? = null, size: Int? = null,
                iconName: String, svgPath: String, defaultColor: String = "#000000", defaultSize: Int = 24) {
        val iconType = IconFactory.getIcon(iconName, svgPath, defaultColor, defaultSize)
        val icon = UIIcon(id, x, y, color, size, iconType)
        icons.add(icon)
    }
    
    fun renderUI() {
        println("=== RENDERING UI ICONS ===")
        icons.take(5).forEach { it.render() } // Show first 5 for demo
    }
    
    fun getTotalIcons(): Int = icons.size
}

// Client-side usage
fun main() {
    println("=== UI Icon System with Flyweight ===\n")
    
    val uiManager = UIManager()
    
    // Common SVG paths (simplified for demo)
    val userIconPath = "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
    val settingsIconPath = "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"
    val searchIconPath = "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"
    
    // Add thousands of UI icons sharing the same graphics
    repeat(5000) { index ->
        uiManager.addIcon(
            id = "user_$index",
            x = index % 100,
            y = index / 100,
            color = if (index % 2 == 0) "#007AFF" else "#FF9500",
            iconName = "user",
            svgPath = userIconPath,
            defaultColor = "#000000"
        )
    }
    
    repeat(3000) { index ->
        uiManager.addIcon(
            id = "settings_$index",
            x = 50 + index % 80,
            y = 30 + index / 80,
            size = if (index % 3 == 0) 32 else 24,
            iconName = "settings",
            svgPath = settingsIconPath,
            defaultColor = "#8E8E93"
        )
    }
    
    repeat(2000) { index ->
        uiManager.addIcon(
            id = "search_$index",
            x = 10 + index % 90,
            y = 60 + index / 90,
            color = "#34C759",
            iconName = "search",
            svgPath = searchIconPath,
            defaultColor = "#000000"
        )
    }
    
    println("Total UI icons: ${uiManager.getTotalIcons()}")
    println("Unique icon types: ${IconFactory.getIconsCount()}")
    
    // Memory savings calculation
    val memoryWithoutFlyweight = uiManager.getTotalIcons() * 4 // name, svgPath, defaultColor, defaultSize per icon
    val memoryWithFlyweight = IconFactory.getIconsCount() * 4 + uiManager.getTotalIcons() // types + references
    
    println("\nMemory savings:")
    println("Without Flyweight: $memoryWithoutFlyweight attribute sets")
    println("With Flyweight: $memoryWithFlyweight (${IconFactory.getIconsCount()} types + ${uiManager.getTotalIcons()} references)")
    println("Memory saved: ${memoryWithoutFlyweight - memoryWithFlyweight} attribute sets")
    
    // Render sample icons
    uiManager.renderUI()
}

main()