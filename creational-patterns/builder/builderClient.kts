/**
 * Builder Pattern for HTTP Request Construction - Client Side
 * Reference: https://refactoring.guru/es/design-patterns/builder
 */

// Product: HTTP Request
data class HttpRequest(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String?,
    val timeout: Int,
    val followRedirects: Boolean
) {
    override fun toString(): String {
        return "HTTP $method $url\nHeaders: $headers\nBody: $body\nTimeout: ${timeout}ms\nFollow redirects: $followRedirects"
    }
}

// Builder Interface
interface HttpRequestBuilder {
    fun reset()
    fun setMethod(method: String): HttpRequestBuilder
    fun setUrl(url: String): HttpRequestBuilder
    fun addHeader(key: String, value: String): HttpRequestBuilder
    fun setBody(body: String): HttpRequestBuilder
    fun setTimeout(timeout: Int): HttpRequestBuilder
    fun setFollowRedirects(follow: Boolean): HttpRequestBuilder
    fun build(): HttpRequest
}

// Concrete Builder
class HttpClientRequestBuilder : HttpRequestBuilder {
    private lateinit var request: HttpRequest
    private val headers = mutableMapOf<String, String>()

    override fun reset() {
        headers.clear()
        request = HttpRequest("GET", "", emptyMap(), null, 30000, true)
    }

    init {
        reset()
    }

    override fun setMethod(method: String): HttpRequestBuilder {
        request = request.copy(method = method.uppercase())
        return this
    }

    override fun setUrl(url: String): HttpRequestBuilder {
        request = request.copy(url = url)
        return this
    }

    override fun addHeader(key: String, value: String): HttpRequestBuilder {
        headers[key] = value
        return this
    }

    override fun setBody(body: String): HttpRequestBuilder {
        request = request.copy(body = body)
        return this
    }

    override fun setTimeout(timeout: Int): HttpRequestBuilder {
        request = request.copy(timeout = timeout)
        return this
    }

    override fun setFollowRedirects(follow: Boolean): HttpRequestBuilder {
        request = request.copy(followRedirects = follow)
        return this
    }

    override fun build(): HttpRequest {
        val finalRequest = request.copy(headers = headers.toMap())
        reset()
        return finalRequest
    }
}

// Director for common request types
class HttpRequestDirector {
    fun createJsonPostRequest(builder: HttpRequestBuilder, url: String, jsonBody: String): HttpRequest {
        return builder
            .setMethod("POST")
            .setUrl(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .setBody(jsonBody)
            .setTimeout(5000)
            .build()
    }

    fun createGetRequest(builder: HttpRequestBuilder, url: String): HttpRequest {
        return builder
            .setMethod("GET")
            .setUrl(url)
            .addHeader("Accept", "application/json")
            .setFollowRedirects(true)
            .build()
    }
}

// Client-side usage
fun main() {
    println("=== HTTP Request Builder Example ===\n")
    
    val director = HttpRequestDirector()
    val builder = HttpClientRequestBuilder()

    // Build a POST request for API call
    val postRequest = director.createJsonPostRequest(
        builder, 
        "https://api.example.com/users",
        """{"name": "John Doe", "email": "john@example.com"}"""
    )
    println("POST Request:")
    println(postRequest)
    println()

    // Build a GET request
    val getRequest = director.createGetRequest(
        builder,
        "https://api.example.com/users/123"
    )
    println("GET Request:")
    println(getRequest)
    println()

    // Custom request without director
    val customRequest = builder
        .setMethod("PUT")
        .setUrl("https://api.example.com/users/123")
        .addHeader("Authorization", "Bearer token123")
        .addHeader("Content-Type", "application/json")
        .setBody("""{"name": "John Updated"}""")
        .setTimeout(10000)
        .build()
    
    println("Custom PUT Request:")
    println(customRequest)
}

main()