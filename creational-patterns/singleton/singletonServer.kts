/**
 * Logger Singleton - Server side example
 * Centralized logging system for server applications
 * Reference: https://refactoring.guru/es/design-patterns/singleton
 */
class Logger private constructor() {
    private val logs = mutableListOf<String>()
    
    companion object {
        private var instance: Logger? = null
        
        fun getInstance(): Logger {
            if (instance == null) {
                instance = Logger()
            }
            return instance!!
        }
    }
    
    fun log(message: String) {
        val timestamp = java.time.LocalDateTime.now()
        val logEntry = "[$timestamp] $message"
        logs.add(logEntry)
        println(logEntry) // Also print to console
    }
    
    fun getLogs(): List<String> {
        return logs.toList()
    }
    
    fun clearLogs() {
        logs.clear()
    }
}

// Example usage
fun main() {
    // Get logger instance from different parts of the application
    val logger1 = Logger.getInstance()
    val logger2 = Logger.getInstance()
    
    // Log messages from different components
    logger1.log("Application started")
    logger2.log("Database connection established")
    logger1.log("User login successful")
    
    // Verify it's the same instance
    println("Same logger instance: ${logger1 === logger2}")
    
    // Display all logs
    println("\nAll log entries:")
    logger1.getLogs().forEachIndexed { index, log -> 
        println("${index + 1}. $log")
    }
}

main()