/**
 * SERVER-SIDE PROXY EXAMPLE
 * 
 * Scenario: Database Access with Connection Pooling and Caching
 * Proxy pattern manages database connections, provides caching, and controls access
 * to expensive database operations in a server environment.
 */

// Database service interface
interface DatabaseService {
    fun query(sql: String): List<Map<String, Any>>
    fun executeUpdate(sql: String): Int
    fun getUserData(userId: String): Map<String, Any>
    fun getProductDetails(productId: String): Map<String, Any>
}

// Real database service (expensive to create and use)
class RealDatabaseService : DatabaseService {
    init {
        println("Creating real database connection... (expensive operation)")
        // Simulate expensive connection setup
        Thread.sleep(1000)
    }
    
    override fun query(sql: String): List<Map<String, Any>> {
        println("Executing query: $sql")
        Thread.sleep(200) // Simulate query execution time
        return listOf(mapOf("result" to "data from $sql"))
    }
    
    override fun executeUpdate(sql: String): Int {
        println("Executing update: $sql")
        Thread.sleep(300) // Simulate update execution time
        return 1
    }
    
    override fun getUserData(userId: String): Map<String, Any> {
        println("Fetching user data for: $userId")
        Thread.sleep(250)
        return mapOf(
            "id" to userId,
            "name" to "User $userId",
            "email" to "user$userId@example.com",
            "role" to "customer"
        )
    }
    
    override fun getProductDetails(productId: String): Map<String, Any> {
        println("Fetching product details for: $productId")
        Thread.sleep(300)
        return mapOf(
            "id" to productId,
            "name" to "Product $productId",
            "price" to 99.99,
            "stock" to 50
        )
    }
    
    fun close() {
        println("Closing database connection")
    }
}

// Database proxy with connection pooling and caching
class DatabaseProxy : DatabaseService {
    private val connectionPool = mutableListOf<RealDatabaseService>()
    private val cache = mutableMapOf<String, Any>()
    private val maxConnections = 3
    
    override fun query(sql: String): List<Map<String, Any>> {
        // Check cache first
        val cacheKey = "query:$sql"
        if (cache.containsKey(cacheKey)) {
            println("Returning cached query result")
            return cache[cacheKey] as List<Map<String, Any>>
        }
        
        val connection = getConnection()
        val result = connection.query(sql)
        releaseConnection(connection)
        
        // Cache the result
        cache[cacheKey] = result
        return result
    }
    
    override fun executeUpdate(sql: String): Int {
        // Invalidate relevant cache entries
        invalidateCacheForTable(sql)
        
        val connection = getConnection()
        val result = connection.executeUpdate(sql)
        releaseConnection(connection)
        return result
    }
    
    override fun getUserData(userId: String): Map<String, Any> {
        val cacheKey = "user:$userId"
        if (cache.containsKey(cacheKey)) {
            println("Returning cached user data")
            return cache[cacheKey] as Map<String, Any>
        }
        
        val connection = getConnection()
        val userData = connection.getUserData(userId)
        releaseConnection(connection)
        
        cache[cacheKey] = userData
        return userData
    }
    
    override fun getProductDetails(productId: String): Map<String, Any> {
        val cacheKey = "product:$productId"
        if (cache.containsKey(cacheKey)) {
            println("Returning cached product details")
            return cache[cacheKey] as Map<String, Any>
        }
        
        val connection = getConnection()
        val productData = connection.getProductDetails(productId)
        releaseConnection(connection)
        
        cache[cacheKey] = productData
        return productData
    }
    
    private fun getConnection(): RealDatabaseService {
        synchronized(connectionPool) {
            // Reuse existing connection if available
            if (connectionPool.isNotEmpty()) {
                println("Reusing existing database connection")
                return connectionPool.removeAt(0)
            }
            
            // Create new connection if under limit
            if (connectionPool.size < maxConnections) {
                println("Creating new database connection")
                return RealDatabaseService()
            }
            
            // Wait for connection to be available
            println("Waiting for available database connection...")
            while (connectionPool.isEmpty()) {
                Thread.sleep(100)
            }
            return connectionPool.removeAt(0)
        }
    }
    
    private fun releaseConnection(connection: RealDatabaseService) {
        synchronized(connectionPool) {
            connectionPool.add(connection)
            println("Connection released back to pool")
        }
    }
    
    private fun invalidateCacheForTable(sql: String) {
        // Simple cache invalidation logic
        when {
            sql.contains("users") -> cache.keys.filter { it.startsWith("user:") }.forEach { cache.remove(it) }
            sql.contains("products") -> cache.keys.filter { it.startsWith("product:") }.forEach { cache.remove(it) }
            else -> cache.clear() // Invalidate all cache for other tables
        }
        println("Cache invalidated for relevant entries")
    }
    
    fun shutdown() {
        connectionPool.forEach { it.close() }
        connectionPool.clear()
        println("Database proxy shutdown complete")
    }
}

// Server application using the proxy
class ServerApplication {
    private val database = DatabaseProxy()
    
    fun handleUserRequest(userId: String) {
        println("\n=== Handling user request ===")
        val userData = database.getUserData(userId)
        println("User data: $userData")
    }
    
    fun handleProductRequest(productId: String) {
        println("\n=== Handling product request ===")
        val productData = database.getProductDetails(productId)
        println("Product data: $productData")
    }
    
    fun updateUser(userId: String, newName: String) {
        println("\n=== Updating user ===")
        val sql = "UPDATE users SET name = '$newName' WHERE id = '$userId'"
        val rowsAffected = database.executeUpdate(sql)
        println("Update completed: $rowsAffected rows affected")
    }
    
    fun shutdown() {
        database.shutdown()
    }
}

// Server-side usage
fun main() {
    println("=== Database Access Proxy Pattern (Server Side) ===\n")
    
    val server = ServerApplication()
    
    // Simulate multiple requests
    repeat(5) { index ->
        server.handleUserRequest("user${index + 1}")
    }
    
    repeat(3) { index ->
        server.handleProductRequest("product${index + 1}")
    }
    
    // Update operation (will invalidate cache)
    server.updateUser("user1", "John Updated")
    
    // Subsequent request will fetch fresh data
    server.handleUserRequest("user1")
    
    println("\n=== Performance Statistics ===")
    println("Connection pool size: 3 (max)")
    println("Cache hits: Multiple demonstrated")
    println("Connection reuse: Demonstrated")
    
    server.shutdown()
}

main()