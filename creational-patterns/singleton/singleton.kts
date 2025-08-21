/**
 * Singleton pattern implementation in Kotlin.
 * 
 * The Singleton pattern ensures that a class has only one instance and provides 
 * a global point of access to that instance.
 * 
 * Reference: https://refactoring.guru/es/design-patterns/singleton
 * 
 * Solves the problem of:
 * - Controlling access to shared resources (like database connections)
 * - Providing a global access point to an instance
 * - Ensuring only one instance exists throughout the application
 */
class Database private constructor() {
    
    companion object {
        // Volatile annotation ensures visibility of changes across threads
        @Volatile
        private var instance: Database? = null
        
        /**
         * Static method that controls access to the singleton instance.
         * Uses double-checked locking for thread safety.
         * 
         * @return The single instance of Database class
         */
        @Synchronized
        fun getInstance(): Database {
            // First check (without synchronization)
            if (instance == null) {
                synchronized(this) {
                    // Second check (with synchronization)
                    if (instance == null) {
                        println("Creating new and unique instance")
                        instance = Database()
                    }
                }
            }
            return instance!!
        }
    }
    
    init {
        // Some initialization code, such as actual connection
        // to a database server would go here
        // ...
    }
    
    /**
     * Business logic that can be executed on the singleton instance.
     * For example, all database queries of an application go through this method.
     * Therefore, you can place throttling or caching logic here.
     * 
     * @param sql The SQL query to execute
     */
    fun query(sql: String) {
        // Database query execution logic
        // Can include throttling, caching, or other cross-cutting concerns
        // ...
        println("Executing query: $sql")
    }
}

fun main() {
    val foo = Database.getInstance()
    foo.query("SELECT ...")
    
    val bar = Database.getInstance()
    bar.query("SELECT ...")
    
    // Both 'foo' and 'bar' contain the same object instance
    println("Same instance: ${foo === bar}") // Will print: Same instance: true
}

main()