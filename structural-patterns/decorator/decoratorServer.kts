/**
 * SERVER-SIDE DECORATOR EXAMPLE
 *
 * Scenario: API Request Processing Pipeline
 * Decorators add cross-cutting concerns like logging, authentication,
 * rate limiting, and caching to API endpoints without modifying core logic.
 */

// Component interface
interface ApiHandler {
    fun handleRequest(request: Map<String, Any>): Map<String, Any>
}

// Concrete component - Core API logic
class UserApiHandler : ApiHandler {
    override fun handleRequest(request: Map<String, Any>): Map<String, Any> {
        val userId = request["userId"] as? String ?: throw IllegalArgumentException("User ID required")
        
        // Simulate database query
        val userData = mapOf(
            "id" to userId,
            "name" to "John Doe",
            "email" to "john@example.com",
            "role" to "user"
        )
        
        return mapOf(
            "status" to 200,
            "data" to userData,
            "message" to "User data retrieved successfully"
        )
    }
}

// Base decorator
abstract class ApiHandlerDecorator(private val wrappee: ApiHandler) : ApiHandler {
    override fun handleRequest(request: Map<String, Any>): Map<String, Any> {
        return wrappee.handleRequest(request)
    }
}

// Concrete decorator - Authentication
class AuthenticationDecorator(wrappee: ApiHandler) : ApiHandlerDecorator(wrappee) {
    override fun handleRequest(request: Map<String, Any>): Map<String, Any> {
        val authToken = request["authToken"] as? String
        if (authToken == null || !isValidToken(authToken)) {
            return mapOf("status" to 401, "message" to "Unauthorized")
        }
        
        println("✅ Authentication successful for token: ${authToken.take(8)}...")
        return super.handleRequest(request)
    }
    
    private fun isValidToken(token: String): Boolean {
        return token.length >= 10 // Simple validation
    }
}

// Concrete decorator - Logging
class LoggingDecorator(wrappee: ApiHandler) : ApiHandlerDecorator(wrappee) {
    override fun handleRequest(request: Map<String, Any>): Map<String, Any> {
        println("📝 Request received: ${request.keys}")
        val startTime = System.currentTimeMillis()
        
        val response = super.handleRequest(request)
        
        val duration = System.currentTimeMillis() - startTime
        println("⏱️  Request processed in ${duration}ms - Status: ${response["status"]}")
        
        return response
    }
}

// Concrete decorator - Rate Limiting
class RateLimitDecorator(wrappee: ApiHandler) : ApiHandlerDecorator(wrappee) {
    private val requestCounts = mutableMapOf<String, Int>()
    
    override fun handleRequest(request: Map<String, Any>): Map<String, Any> {
        val clientIp = request["clientIp"] as? String ?: "unknown"
        val currentCount = requestCounts.getOrDefault(clientIp, 0)
        
        if (currentCount >= 5) {
            return mapOf("status" to 429, "message" to "Rate limit exceeded")
        }
        
        requestCounts[clientIp] = currentCount + 1
        println("🔒 Rate limit check passed for IP: $clientIp")
        return super.handleRequest(request)
    }
}

// API Server (Client code)
class ApiServer {
    fun createHandler(): ApiHandler {
        val coreHandler = UserApiHandler()
        
        // Build processing pipeline with decorators
        var handler: ApiHandler = coreHandler
        handler = AuthenticationDecorator(handler)
        handler = LoggingDecorator(handler)
        handler = RateLimitDecorator(handler)
        
        return handler
    }
    
    fun processRequests() {
        val handler = createHandler()
        
        val requests = listOf(
            mapOf("userId" to "123", "authToken" to "valid_token_123", "clientIp" to "192.168.1.1"),
            mapOf("userId" to "456", "authToken" to "invalid", "clientIp" to "192.168.1.2"),
            mapOf("userId" to "789", "authToken" to "valid_token_456", "clientIp" to "192.168.1.1")
        )
        
        println("🚀 Starting API request processing...\n")
        
        requests.forEachIndexed { index, request ->
            println("--- Request ${index + 1} ---")
            val response = handler.handleRequest(request)
            println("Response: $response\n")
        }
    }
}

// Server-side usage
fun main() {
    val server = ApiServer()
    server.processRequests()
}

main()