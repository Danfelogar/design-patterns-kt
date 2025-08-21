/**
 * Prototype Pattern for Server Configuration - Server Side
 * Reference: https://refactoring.guru/es/design-patterns/prototype
 */

// Server configuration prototype
abstract class ServerConfig {
    var host: String = "localhost"
    var port: Int = 8080
    var timeout: Int = 30000
    var maxConnections: Int = 1000
    var enabled: Boolean = true
    var properties: Map<String, Any> = emptyMap()

    constructor()

    constructor(source: ServerConfig) {
        this.host = source.host
        this.port = source.port
        this.timeout = source.timeout
        this.maxConnections = source.maxConnections
        this.enabled = source.enabled
        this.properties = source.properties.toMutableMap()
    }

    abstract fun clone(): ServerConfig

    open fun getConfigSummary(): String {
        return """
        |Host: $host
        |Port: $port
        |Timeout: ${timeout}ms
        |Max Connections: $maxConnections
        |Enabled: $enabled
        |Properties: ${properties.size}
        """.trimMargin()
    }
}

// Concrete server configurations
class DatabaseConfig : ServerConfig {
    var databaseName: String = ""
    var username: String = ""
    var password: String = ""
    var poolSize: Int = 10
    var sslEnabled: Boolean = false

    constructor() : super()

    constructor(source: DatabaseConfig) : super(source) {
        this.databaseName = source.databaseName
        this.username = source.username
        this.password = source.password
        this.poolSize = source.poolSize
        this.sslEnabled = source.sslEnabled
    }

    override fun clone(): ServerConfig {
        return DatabaseConfig(this)
    }

    override fun getConfigSummary(): String {
        return """
        |DATABASE CONFIG:
        |Host: $host:$port
        |Database: $databaseName
        |User: $username
        |Pool Size: $poolSize
        |SSL: $sslEnabled
        |Timeout: ${timeout}ms
        """.trimMargin()
    }
}

class ApiConfig : ServerConfig {
    var basePath: String = "/api"
    var version: String = "v1"
    var rateLimit: Int = 100
    var corsEnabled: Boolean = true
    var authenticationRequired: Boolean = true

    constructor() : super()

    constructor(source: ApiConfig) : super(source) {
        this.basePath = source.basePath
        this.version = source.version
        this.rateLimit = source.rateLimit
        this.corsEnabled = source.corsEnabled
        this.authenticationRequired = source.authenticationRequired
    }

    override fun clone(): ServerConfig {
        return ApiConfig(this)
    }

    override fun getConfigSummary(): String {
        return """
        |API CONFIG:
        |Host: $host:$port
        |Base Path: $basePath/$version
        |Rate Limit: $rateLimit req/min
        |CORS: $corsEnabled
        |Auth Required: $authenticationRequired
        |Max Connections: $maxConnections
        """.trimMargin()
    }
}

class CacheConfig : ServerConfig {
    var cacheType: String = "redis"
    var expirationTime: Int = 3600
    var clusterMode: Boolean = false
    var replicationEnabled: Boolean = false

    constructor() : super()

    constructor(source: CacheConfig) : super(source) {
        this.cacheType = source.cacheType
        this.expirationTime = source.expirationTime
        this.clusterMode = source.clusterMode
        this.replicationEnabled = source.replicationEnabled
    }

    override fun clone(): ServerConfig {
        return CacheConfig(this)
    }
}

// Server Configuration Registry
class ServerConfigRegistry {
    private val configPrototypes = mutableMapOf<String, ServerConfig>()

    init {
        initializeDefaultConfigs()
    }

    private fun initializeDefaultConfigs() {
        // Default database config
        val dbConfig = DatabaseConfig().apply {
            host = "db.example.com"
            port = 5432
            databaseName = "production_db"
            username = "admin"
            password = "secure_password"
            poolSize = 20
            timeout = 60000
            sslEnabled = true
        }
        configPrototypes["production_db"] = dbConfig

        // Development database config
        val devDbConfig = DatabaseConfig().apply {
            host = "localhost"
            port = 5432
            databaseName = "development_db"
            username = "dev"
            password = "dev_password"
            poolSize = 5
            timeout = 30000
            sslEnabled = false
        }
        configPrototypes["development_db"] = devDbConfig

        // API config
        val apiConfig = ApiConfig().apply {
            host = "api.example.com"
            port = 443
            basePath = "/api"
            version = "v2"
            rateLimit = 1000
            maxConnections = 5000
            timeout = 15000
            corsEnabled = true
            authenticationRequired = true
        }
        configPrototypes["production_api"] = apiConfig

        // Cache config
        val cacheConfig = CacheConfig().apply {
            host = "cache.example.com"
            port = 6379
            cacheType = "redis"
            expirationTime = 7200
            clusterMode = true
            replicationEnabled = true
            maxConnections = 10000
        }
        configPrototypes["production_cache"] = cacheConfig
    }

    fun registerConfig(key: String, config: ServerConfig) {
        configPrototypes[key] = config
    }

    fun getConfig(key: String): ServerConfig? {
        return configPrototypes[key]?.clone()
    }

    fun listConfigs(): List<String> {
        return configPrototypes.keys.toList()
    }
}

// Server Management System
class ServerManagementSystem {
    private val configRegistry = ServerConfigRegistry()

    fun demonstrateServerConfigPrototypes() {
        println("🖥️ === Server Configuration Prototype Demonstration - Server Side ===")
        
        println("\n1. 🗄️ Creating Database Configurations:")
        val productionDbConfig = configRegistry.getConfig("production_db") as? DatabaseConfig
        var stagingDbConfig: DatabaseConfig? = null
        
        if (productionDbConfig != null) {
            stagingDbConfig = productionDbConfig.clone() as DatabaseConfig
            stagingDbConfig.host = "staging-db.example.com"
            stagingDbConfig.databaseName = "staging_db"
            stagingDbConfig.username = "staging_user"
            stagingDbConfig.poolSize = 10
            stagingDbConfig.sslEnabled = false
        }

        println("Production DB:")
        println(productionDbConfig?.getConfigSummary())
        println("\nStaging DB (cloned from production):")
        println(stagingDbConfig?.getConfigSummary())

        println("\n2. 🌐 Creating API Configurations:")
        val apiConfig = configRegistry.getConfig("production_api") as? ApiConfig
        var internalApiConfig: ApiConfig? = null
        
        if (apiConfig != null) {
            internalApiConfig = apiConfig.clone() as ApiConfig
            internalApiConfig.host = "internal-api.example.com"
            internalApiConfig.port = 8080
            internalApiConfig.rateLimit = 5000
            internalApiConfig.authenticationRequired = false
            internalApiConfig.corsEnabled = false
        }

        println("External API:")
        println(apiConfig?.getConfigSummary())
        println("\nInternal API (cloned from external):")
        println(internalApiConfig?.getConfigSummary())

        println("\n3. ⚙️ Environment-Specific Configurations:")
        val environments = listOf("development", "testing", "staging", "production")
        val baseDbConfig = configRegistry.getConfig("production_db") as? DatabaseConfig
        
        environments.forEach { env ->
            var envConfig: DatabaseConfig? = null
            if (baseDbConfig != null) {
                envConfig = baseDbConfig.clone() as DatabaseConfig
                envConfig.host = "$env-db.example.com"
                envConfig.databaseName = "${env}_database"
                envConfig.username = "${env}_user"
                envConfig.poolSize = when (env) {
                    "production" -> 50
                    "staging" -> 20
                    else -> 5
                }
            }
            println("$env environment: ${envConfig?.host} (pool: ${envConfig?.poolSize})")
        }
    }

    fun demonstrateConfigModification() {
        println("\n" + "=".repeat(60))
        println("🔄 Configuration Modification Safety Check")
        
        val original = configRegistry.getConfig("production_api") as? ApiConfig
        var modified: ApiConfig? = null
        
        if (original != null) {
            modified = original.clone() as ApiConfig
            modified.rateLimit = 2000
            modified.maxConnections = 10000
            modified.properties = modified.properties + mapOf("newFeature" to true)
        }

        println("Original rate limit: ${original?.rateLimit}")
        println("Modified rate limit: ${modified?.rateLimit}")
        println("Original properties: ${original?.properties?.size}")
        println("Modified properties: ${modified?.properties?.size}")
        println("Different objects: ${original !== modified}")
    }

    fun demonstrateEmergencyConfig() {
        println("\n" + "=".repeat(60))
        println("🚨 Emergency Configuration Scenario")
        
        // Create emergency config from production but with reduced limits
        val productionConfig = configRegistry.getConfig("production_api") as? ApiConfig
        var emergencyConfig: ApiConfig? = null
        
        if (productionConfig != null) {
            emergencyConfig = productionConfig.clone() as ApiConfig
            emergencyConfig.rateLimit = 100  // Drastically reduce rate limit
            emergencyConfig.maxConnections = 100  // Severely limit connections
            emergencyConfig.properties = emergencyConfig.properties + mapOf("emergencyMode" to true, "maintenance" to true)
            emergencyConfig.timeout = 5000  // Shorter timeout
        }

        println("Emergency configuration activated:")
        println(emergencyConfig?.getConfigSummary())
    }
}

// Main function
fun main() {
    println("🔷 Server-Side Prototype Pattern: Configuration Management")
    println("=".repeat(60))
    
    val serverSystem = ServerManagementSystem()
    serverSystem.demonstrateServerConfigPrototypes()
    serverSystem.demonstrateConfigModification()
    serverSystem.demonstrateEmergencyConfig()
    
    println("\n" + "=".repeat(60))
    println("✅ Server demonstration completed!")
    println("=".repeat(60))
}

// Execute
main()