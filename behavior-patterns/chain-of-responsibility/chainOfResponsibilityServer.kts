/**
 * SERVER-SIDE CHAIN OF RESPONSIBILITY EXAMPLE
 * 
 * Scenario: API Request Processing Pipeline
 * Chain of handlers processes HTTP requests with authentication, validation,
 * rate limiting, and business logic in a specific order.
 */

// Handler interface
interface RequestHandler {
    fun setNext(handler: RequestHandler): RequestHandler
    fun handle(request: HttpRequest): HttpResponse
}

// Abstract base handler
abstract class AbstractHandler : RequestHandler {
    private var nextHandler: RequestHandler? = null
    
    override fun setNext(handler: RequestHandler): RequestHandler {
        nextHandler = handler
        return handler
    }
    
    override fun handle(request: HttpRequest): HttpResponse {
        return nextHandler?.handle(request) ?: throw Exception("No handler available")
    }
}

// Concrete handlers
class AuthenticationHandler : AbstractHandler() {
    override fun handle(request: HttpRequest): HttpResponse {
        println("🔐 Authenticating request...")
        
        val authToken = request.headers["Authorization"]
        if (authToken == null || !isValidToken(authToken)) {
            return HttpResponse(401, "Unauthorized")
        }
        
        println("✅ Authentication successful")
        return super.handle(request)
    }
    
    private fun isValidToken(token: String): Boolean {
        return token.startsWith("Bearer ") && token.length > 20
    }
}

class ValidationHandler : AbstractHandler() {
    override fun handle(request: HttpRequest): HttpResponse {
        println("📋 Validating request...")
        
        when (request.path) {
            "/api/users" -> {
                if (request.method != "GET") {
                    return HttpResponse(400, "Invalid method for users endpoint")
                }
            }
            "/api/orders" -> {
                if (request.method == "POST" && request.body.isNullOrEmpty()) {
                    return HttpResponse(400, "Order data required")
                }
            }
        }
        
        println("✅ Validation passed")
        return super.handle(request)
    }
}

class RateLimitHandler : AbstractHandler() {
    private val requestCounts = mutableMapOf<String, Int>()
    
    override fun handle(request: HttpRequest): HttpResponse {
        println("⚡ Checking rate limit...")
        
        val clientIp = request.headers["X-Forwarded-For"] ?: "unknown"
        val currentCount = requestCounts.getOrDefault(clientIp, 0)
        
        if (currentCount >= 100) {
            return HttpResponse(429, "Rate limit exceeded")
        }
        
        requestCounts[clientIp] = currentCount + 1
        println("✅ Rate limit check passed for IP: $clientIp")
        return super.handle(request)
    }
}

class BusinessLogicHandler : AbstractHandler() {
    override fun handle(request: HttpRequest): HttpResponse {
        println("💼 Executing business logic...")
        
        return when (request.path) {
            "/api/users" -> HttpResponse(200, "User data: [user1, user2, user3]")
            "/api/orders" -> {
                if (request.method == "POST") {
                    HttpResponse(201, "Order created successfully")
                } else {
                    HttpResponse(200, "Orders: [order1, order2]")
                }
            }
            "/api/products" -> HttpResponse(200, "Products: [product1, product2]")
            else -> HttpResponse(404, "Endpoint not found")
        }
    }
}

class LoggingHandler : AbstractHandler() {
    override fun handle(request: HttpRequest): HttpResponse {
        println("📝 Logging request: ${request.method} ${request.path}")
        
        val response = super.handle(request)
        
        println("📝 Logging response: ${response.statusCode} ${response.body}")
        return response
    }
}

// Data classes
data class HttpRequest(
    val method: String,
    val path: String,
    val headers: Map<String, String>,
    val body: String?
)

data class HttpResponse(
    val statusCode: Int,
    val body: String
)

// Server application
class ApiServer {
    private val requestHandler: RequestHandler
    
    init {
        // Build the chain of responsibility
        val authentication = AuthenticationHandler()
        val validation = ValidationHandler()
        val rateLimit = RateLimitHandler()
        val logging = LoggingHandler()
        val businessLogic = BusinessLogicHandler()
        
        authentication.setNext(validation)
            .setNext(rateLimit)
            .setNext(logging)
            .setNext(businessLogic)
        
        requestHandler = authentication
    }
    
    fun processRequest(request: HttpRequest): HttpResponse {
        println("\n🚀 Processing request: ${request.method} ${request.path}")
        return requestHandler.handle(request)
    }
}

// Server-side usage
fun main() {
    println("=== API Server with Chain of Responsibility ===\n")
    
    val server = ApiServer()
    
    val requests = listOf(
        HttpRequest("GET", "/api/users", mapOf("Authorization" to "Bearer invalid"), null),
        HttpRequest("GET", "/api/users", mapOf("Authorization" to "Bearer valid_token_123456"), null),
        HttpRequest("POST", "/api/orders", mapOf("Authorization" to "Bearer valid_token_123456"), "order data"),
        HttpRequest("GET", "/api/products", mapOf("Authorization" to "Bearer valid_token_123456"), null),
        HttpRequest("GET", "/api/nonexistent", mapOf("Authorization" to "Bearer valid_token_123456"), null)
    )
    
    requests.forEachIndexed { index, request ->
        println("\n--- Request ${index + 1} ---")
        val response = server.processRequest(request)
        println("Response: ${response.statusCode} - ${response.body}")
    }
    
    println("\n=== Chain Benefits ===")
    println("• Each handler has single responsibility")
    println("• Easy to add/remove/reorder handlers")
    println("• Handlers can stop the chain (e.g., authentication failure)")
    println("• Better separation of concerns")
}

main()