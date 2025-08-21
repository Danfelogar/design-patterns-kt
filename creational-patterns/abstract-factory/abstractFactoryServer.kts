// Abstract Factory for database providers
interface DatabaseFactory {
    fun createConnection(): DatabaseConnection
    fun createQueryBuilder(): QueryBuilder
    fun createTransactionManager(): TransactionManager
}

// Concrete factories for different database providers
class PostgreSQLFactory : DatabaseFactory {
    override fun createConnection(): DatabaseConnection = PostgreSQLConnection()
    override fun createQueryBuilder(): QueryBuilder = PostgreSQLQueryBuilder()
    override fun createTransactionManager(): TransactionManager = PostgreSQLTransactionManager()
}

class MySQLFactory : DatabaseFactory {
    override fun createConnection(): DatabaseConnection = MySQLConnection()
    override fun createQueryBuilder(): QueryBuilder = MySQLQueryBuilder()
    override fun createTransactionManager(): TransactionManager = MySQLTransactionManager()
}

class SQLServerFactory : DatabaseFactory {
    override fun createConnection(): DatabaseConnection = SQLServerConnection()
    override fun createQueryBuilder(): QueryBuilder = SQLServerQueryBuilder()
    override fun createTransactionManager(): TransactionManager = SQLServerTransactionManager()
}

// Product interfaces
interface DatabaseConnection {
    fun connect()
    fun disconnect()
    fun executeQuery(query: String): List<Map<String, Any>>
}

interface QueryBuilder {
    fun select(table: String, columns: List<String>): String
    fun insert(table: String, values: Map<String, Any>): String
}

interface TransactionManager {
    fun beginTransaction()
    fun commit()
    fun rollback()
}

// PostgreSQL products
class PostgreSQLConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to PostgreSQL database...")
    }

    override fun disconnect() {
        println("Disconnecting from PostgreSQL...")
    }

    override fun executeQuery(query: String): List<Map<String, Any>> {
        println("Executing PostgreSQL query: $query")
        return listOf(mapOf("id" to 1, "name" to "PostgreSQL Result"))
    }
}

class PostgreSQLQueryBuilder : QueryBuilder {
    override fun select(table: String, columns: List<String>): String {
        return "SELECT ${columns.joinToString(", ")} FROM \"$table\""
    }

    override fun insert(table: String, values: Map<String, Any>): String {
        val columns = values.keys.joinToString(", ")
        val placeholders = values.keys.map { "?" }.joinToString(", ")
        val actualValues = values.values.joinToString(", ") {
            if (it is String) "'$it'" else it.toString()
        }
        return "INSERT INTO \"$table\" ($columns) VALUES ($placeholders)"
    }
}

class PostgreSQLTransactionManager : TransactionManager {
    override fun beginTransaction() {
        println("BEGIN TRANSACTION (PostgreSQL)")
    }

    override fun commit() {
        println("COMMIT (PostgreSQL)")
    }

    override fun rollback() {
        println("ROLLBACK (PostgreSQL)")
    }
}

// MySQL products
class MySQLConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to MySQL database...")
    }

    override fun disconnect() {
        println("Disconnecting from MySQL...")
    }

    override fun executeQuery(query: String): List<Map<String, Any>> {
        println("Executing MySQL query: $query")
        return listOf(mapOf("id" to 1, "name" to "MySQL Result"))
    }
}

class MySQLQueryBuilder : QueryBuilder {
    override fun select(table: String, columns: List<String>): String {
        return "SELECT ${columns.joinToString(", ")} FROM `$table`"
    }

    override fun insert(table: String, values: Map<String, Any>): String {
        val columns = values.keys.joinToString(", ")
        val placeholders = values.keys.map { "?" }.joinToString(", ")
        val actualValues = values.values.joinToString(", ") {
            if (it is String) "'$it'" else it.toString()
        }
        return "INSERT INTO `$table` ($columns) VALUES ($placeholders)"
    }
}

class MySQLTransactionManager : TransactionManager {
    override fun beginTransaction() {
        println("START TRANSACTION (MySQL)")
    }

    override fun commit() {
        println("COMMIT (MySQL)")
    }

    override fun rollback() {
        println("ROLLBACK (MySQL)")
    }
}

// SQL Server products
class SQLServerConnection : DatabaseConnection {
    override fun connect() {
        println("Connecting to SQL Server database...")
    }

    override fun disconnect() {
        println("Disconnecting from SQL Server...")
    }

    override fun executeQuery(query: String): List<Map<String, Any>> {
        println("Executing SQL Server query: $query")
        return listOf(mapOf("id" to 1, "name" to "SQL Server Result"))
    }
}

class SQLServerQueryBuilder : QueryBuilder {
    override fun select(table: String, columns: List<String>): String {
        return "SELECT ${columns.joinToString(", ")} FROM [$table]"
    }

    override fun insert(table: String, values: Map<String, Any>): String {
        val columns = values.keys.joinToString(", ")
        val placeholders = values.keys.map { "?" }.joinToString(", ")
        val actualValues = values.values.joinToString(", ") {
            if (it is String) "'$it'" else it.toString()
        }
        return "INSERT INTO [$table] ($columns) VALUES ($placeholders)"
    }
}

class SQLServerTransactionManager : TransactionManager {
    override fun beginTransaction() {
        println("BEGIN TRANSACTION (SQL Server)")
    }

    override fun commit() {
        println("COMMIT (SQL Server)")
    }

    override fun rollback() {
        println("ROLLBACK (SQL Server)")
    }
}

// Server application
class ServerApplication(private val databaseFactory: DatabaseFactory) {
    fun performDatabaseOperations() {
        val connection = databaseFactory.createConnection()
        val queryBuilder = databaseFactory.createQueryBuilder()
        val transactionManager = databaseFactory.createTransactionManager()

        connection.connect()

        try {
            transactionManager.beginTransaction()

            val selectQuery = queryBuilder.select("users", listOf("id", "name"))
            val results = connection.executeQuery(selectQuery)
            println("Query results: $results")

            val insertQuery = queryBuilder.insert("users", mapOf("name" to "John", "email" to "john@example.com"))
            println("Generated insert query: $insertQuery")

            transactionManager.commit()
        } catch (e: Exception) {
            transactionManager.rollback()
            println("Transaction rolled back due to error: ${e.message}")
        } finally {
            connection.disconnect()
        }
    }
}

// Database configuration based on environment or settings
fun main() {
    val databaseType = "postgresql" // Could be "mysql", "sqlserver", etc.

    val databaseFactory: DatabaseFactory = when (databaseType) {
        "postgresql" -> PostgreSQLFactory()
        "mysql" -> MySQLFactory()
        "sqlserver" -> SQLServerFactory()
        else -> throw IllegalArgumentException("Unsupported database type: $databaseType")
    }

    val serverApp = ServerApplication(databaseFactory)
    serverApp.performDatabaseOperations()
}

main()