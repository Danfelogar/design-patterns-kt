/**
 * Configuration Manager Singleton - Client side example
 * Manages application configuration settings throughout the app
 * Reference: https://refactoring.guru/es/design-patterns/singleton
 */
class ConfigManager private constructor() {
    private val settings = mutableMapOf<String, Any>()
    
    companion object {
        private var instance: ConfigManager? = null
        
        fun getInstance(): ConfigManager {
            if (instance == null) {
                instance = ConfigManager()
            }
            return instance!!
        }
    }
    
    fun setSetting(key: String, value: Any) {
        settings[key] = value
    }
    
    fun getSetting(key: String): Any? {
        return settings[key]
    }
    
    fun getAllSettings(): Map<String, Any> {
        return settings.toMap()
    }
}

// Example usage
fun main() {
    // Get the singleton instance
    val config = ConfigManager.getInstance()
    
    // Set some configuration values
    config.setSetting("theme", "dark")
    config.setSetting("language", "en")
    config.setSetting("notifications", true)
    
    // Access the same instance from another part of the code
    val sameConfig = ConfigManager.getInstance()
    
    // Retrieve and print settings
    println("Current theme: ${sameConfig.getSetting("theme")}")
    println("All settings: ${sameConfig.getAllSettings()}")
    
    // Verify it's the same instance
    println("Same instance: ${config === sameConfig}")
}

main()