// Product interface
interface DatabaseConnection {
    fun connect()
    fun disconnect()
    fun query(sql: String): List<Map<String, Any>>
}

// Concrete products
class MySQLConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to MySQL database...")
        // Actual connection logic would go here
    }

    override fun disconnect() {
        println("Disconnecting from MySQL database...")
        // Actual disconnection logic would go here
    }

    override fun query(sql: String): List<Map<String, Any>> {
        println("Executing MySQL query: $sql")
        // Actual query execution would go here
        return listOf(mapOf("id" to 1, "name" to "Example"))
    }
}

class PostgreSQLConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to PostgreSQL database...")
        // Actual connection logic would go here
    }

    override fun disconnect() {
        println("Disconnecting from PostgreSQL database...")
        // Actual disconnection logic would go here
    }

    override fun query(sql: String): List<Map<String, Any>> {
        println("Executing PostgreSQL query: $sql")
        // Actual query execution would go here
        return listOf(mapOf("id" to 1, "name" to "Example"))
    }
}

class MongoDBConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to MongoDB database...")
        // Actual connection logic would go here
    }

    override fun disconnect() {
        println("Disconnecting from MongoDB database...")
        // Actual disconnection logic would go here
    }

    override fun query(sql: String): List<Map<String, Any>> {
        println("Executing MongoDB query: $sql")
        // Actual query execution would go here
        return listOf(mapOf("_id" to "507f1f77bcf86cd799439011", "name" to "Example"))
    }
}

// Creator
abstract class DatabaseFactory {
    // Factory method
    abstract fun createConnection(): DatabaseConnection

    // Business logic that uses the product
    fun executeQuery(sql: String): List<Map<String, Any>> {
        val connection = createConnection()
        connection.connect()
        val result = connection.query(sql)
        connection.disconnect()
        return result
    }
}

// Concrete creators
class MySQLFactory : DatabaseFactory() {
    override fun createConnection(): DatabaseConnection = MySQLConnection()
}

class PostgreSQLFactory : DatabaseFactory() {
    override fun createConnection(): DatabaseConnection = PostgreSQLConnection()
}

class MongoDBFactory : DatabaseFactory() {
    override fun createConnection(): DatabaseConnection = MongoDBConnection()
}

// Server-side client code
fun main() {
    // This would typically come from configuration
    val databaseType = "postgresql" // Could be "postgresql", "mongodb", etc.

    val factory: DatabaseFactory = when (databaseType) {
        "mysql" -> MySQLFactory()
        "postgresql" -> PostgreSQLFactory()
        "mongodb" -> MongoDBFactory()
        else -> throw IllegalArgumentException("Unknown database type: $databaseType")
    }

    val results = factory.executeQuery("SELECT * FROM users")
    println("Query results: $results")
}

main()