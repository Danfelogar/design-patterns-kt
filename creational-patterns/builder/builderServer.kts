/**
 * Builder Pattern for API Response Construction - Server Side
 * Reference: https://refactoring.guru/es/design-patterns/builder
 */

// Product: API Response
data class ApiResponse(
    val statusCode: Int,
    val success: Boolean,
    val data: Any?,
    val message: String,
    val errors: List<String>,
    val metadata: Map<String, Any>,
    val timestamp: String
) {
    fun toJson(): String {
        return """{
            "status": $statusCode,
            "success": $success,
            "data": ${if (data != null) "\"$data\"" else "null"},
            "message": "$message",
            "errors": [${errors.joinToString { "\"$it\"" }}],
            "metadata": {${metadata.entries.joinToString { "\"${it.key}\": \"${it.value}\"" }}},
            "timestamp": "$timestamp"
        }""".trimIndent()
    }
}

// Builder Interface
interface ApiResponseBuilder {
    fun reset()
    fun setStatusCode(code: Int): ApiResponseBuilder
    fun setSuccess(success: Boolean): ApiResponseBuilder
    fun setData(data: Any?): ApiResponseBuilder
    fun setMessage(message: String): ApiResponseBuilder
    fun addError(error: String): ApiResponseBuilder
    fun addMetadata(key: String, value: Any): ApiResponseBuilder
    fun build(): ApiResponse
}

// Concrete Builder
class RestApiResponseBuilder : ApiResponseBuilder {
    private lateinit var response: ApiResponse
    private val errors = mutableListOf<String>()
    private val metadata = mutableMapOf<String, Any>()

    override fun reset() {
        errors.clear()
        metadata.clear()
        val currentTime = java.time.LocalDateTime.now().toString()
        response = ApiResponse(200, true, null, "", emptyList(), emptyMap(), currentTime)
    }

    init {
        reset()
    }

    override fun setStatusCode(code: Int): ApiResponseBuilder {
        response = response.copy(statusCode = code)
        return this
    }

    override fun setSuccess(success: Boolean): ApiResponseBuilder {
        response = response.copy(success = success)
        return this
    }

    override fun setData(data: Any?): ApiResponseBuilder {
        response = response.copy(data = data)
        return this
    }

    override fun setMessage(message: String): ApiResponseBuilder {
        response = response.copy(message = message)
        return this
    }

    override fun addError(error: String): ApiResponseBuilder {
        errors.add(error)
        return this
    }

    override fun addMetadata(key: String, value: Any): ApiResponseBuilder {
        metadata[key] = value
        return this
    }

    override fun build(): ApiResponse {
        val finalResponse = response.copy(
            errors = errors.toList(),
            metadata = metadata.toMap()
        )
        reset()
        return finalResponse
    }
}

// Director for common response types
class ApiResponseDirector {
    fun createSuccessResponse(builder: ApiResponseBuilder, data: Any?, message: String = "Success"): ApiResponse {
        return builder
            .setStatusCode(200)
            .setSuccess(true)
            .setData(data)
            .setMessage(message)
            .addMetadata("version", "1.0")
            .build()
    }

    fun createErrorResponse(builder: ApiResponseBuilder, message: String, errors: List<String> = emptyList()): ApiResponse {
        return builder
            .setStatusCode(400)
            .setSuccess(false)
            .setData(null)
            .setMessage(message)
            .apply { errors.forEach { addError(it) } }
            .addMetadata("version", "1.0")
            .build()
    }

    fun createNotFoundResponse(builder: ApiResponseBuilder, resource: String): ApiResponse {
        return builder
            .setStatusCode(404)
            .setSuccess(false)
            .setData(null)
            .setMessage("$resource not found")
            .addError("Resource $resource does not exist")
            .addMetadata("version", "1.0")
            .build()
    }
}

// Server-side usage
fun main() {
    println("=== API Response Builder Example ===\n")
    
    val director = ApiResponseDirector()
    val builder = RestApiResponseBuilder()

    // Success response
    val successResponse = director.createSuccessResponse(
        builder,
        mapOf("user" to mapOf("id" to 123, "name" to "John Doe")),
        "User created successfully"
    )
    println("Success Response:")
    println(successResponse.toJson())
    println()

    // Error response
    val errorResponse = director.createErrorResponse(
        builder,
        "Validation failed",
        listOf("Email is required", "Name must be at least 3 characters")
    )
    println("Error Response:")
    println(errorResponse.toJson())
    println()

    // Not found response
    val notFoundResponse = director.createNotFoundResponse(
        builder,
        "user"
    )
    println("Not Found Response:")
    println(notFoundResponse.toJson())
    println()

    // Custom response
    val customResponse = builder
        .setStatusCode(201)
        .setSuccess(true)
        .setData(mapOf("orderId" to 456, "status" to "created"))
        .setMessage("Order created successfully")
        .addMetadata("processedBy", "server-1")
        .addMetadata("responseTime", "150ms")
        .build()
    
    println("Custom Created Response:")
    println(customResponse.toJson())
}

// Run both examples
fun runAllExamples() {
    println("🚀 Running HTTP Client Builder Example\n")
    main() // This runs the first example
    
    println("\n" + "=".repeat(50) + "\n")
    
    println("🚀 Running API Server Builder Example\n")
    // We need to call the second main function differently
    val serverMain: () -> Unit = {
        println("=== API Response Builder Example ===\n")
        
        val director = ApiResponseDirector()
        val builder = RestApiResponseBuilder()

        val successResponse = director.createSuccessResponse(
            builder,
            mapOf("user" to mapOf("id" to 123, "name" to "John Doe")),
            "User created successfully"
        )
        println("Success Response:")
        println(successResponse.toJson())
    }
    serverMain()
}

// Uncomment to run both examples
runAllExamples()