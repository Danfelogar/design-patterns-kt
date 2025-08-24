/**
 * PROXY PATTERN
 * 
 * The Proxy pattern provides a surrogate or placeholder for another object to control 
 * access to it. It allows you to add additional behavior around the actual object, 
 * such as caching, access control, logging, or lazy initialization.
 * 
 * This pattern is useful when you need to control access to an object, add functionality 
 * before/after the main logic, or when the actual object is resource-intensive to create.
 * 
 * Source: https://refactoring.guru/es/design-patterns/proxy
 */

// The interface of a remote service.
interface ThirdPartyYouTubeLib {
    fun listVideos(): List<String>
    fun getVideoInfo(id: String): Map<String, Any>
    fun downloadVideo(id: String)
}

// The concrete implementation of a service connector. Methods of this class
// can request information from YouTube. The request speed depends on the user's
// internet connection and YouTube. The application will slow down if many
// requests are made at the same time, even if they all request the same information.
class ThirdPartyYouTubeClass : ThirdPartyYouTubeLib {
    override fun listVideos(): List<String> {
        println("Fetching video list from YouTube API...")
        // Simulate network delay
        Thread.sleep(500)
        return listOf("video1", "video2", "video3", "video4", "video5")
    }
    
    override fun getVideoInfo(id: String): Map<String, Any> {
        println("Fetching video info for $id from YouTube API...")
        // Simulate network delay
        Thread.sleep(300)
        return mapOf(
            "id" to id,
            "title" to "Video $id Title",
            "duration" to "10:30",
            "views" to 10000,
            "likes" to 500
        )
    }
    
    override fun downloadVideo(id: String) {
        println("Downloading video $id from YouTube...")
        // Simulate network delay
        Thread.sleep(1000)
        println("Download completed for video $id")
    }
}

// To save bandwidth, we can cache request results for some time, but
// we can't place this code directly inside the service class. For example,
// it might have been provided as part of a third-party library and/or
// defined as `final`. That's why we place the caching code inside a new
// proxy class that implements the same interface as the service class.
// It delegates to the service object only when actual requests must be sent.
class CachedYouTubeClass(private val service: ThirdPartyYouTubeLib) : ThirdPartyYouTubeLib {
    private var listCache: List<String>? = null
    private val videoCache = mutableMapOf<String, Map<String, Any>>()
    private var needReset = false
    
    override fun listVideos(): List<String> {
        if (listCache == null || needReset) {
            listCache = service.listVideos()
            println("Cached video list")
        } else {
            println("Returning cached video list")
        }
        return listCache!!
    }
    
    override fun getVideoInfo(id: String): Map<String, Any> {
        if (!videoCache.containsKey(id) || needReset) {
            videoCache[id] = service.getVideoInfo(id)
            println("Cached video info for $id")
        } else {
            println("Returning cached video info for $id")
        }
        return videoCache[id]!!
    }
    
    override fun downloadVideo(id: String) {
        if (needReset) {
            service.downloadVideo(id)
        } else {
            println("Video $id already downloaded (cached)")
        }
    }
    
    fun resetCache() {
        needReset = true
        listCache = null
        videoCache.clear()
        println("Cache reset")
    }
}

// The GUI class, which used to work directly with a service object, remains
// unchanged as long as it works with the service object through an interface.
// We can safely pass a proxy object instead of a real service object, since
// both implement the same interface.
class YouTubeManager(private val service: ThirdPartyYouTubeLib) {
    fun renderVideoPage(id: String) {
        val info = service.getVideoInfo(id)
        println("Rendering video page: ${info["title"]} (${info["views"]} views)")
    }
    
    fun renderListPanel() {
        val list = service.listVideos()
        println("Rendering list panel with ${list.size} videos: $list")
    }
    
    fun reactOnUserInput() {
        println("\n--- User interaction started ---")
        renderListPanel()
        renderVideoPage("video2")
        renderVideoPage("video3")
        println("--- User interaction completed ---\n")
    }
}

// The application can configure proxies on the fly.
class Application {
    fun init() {
        println("=== YouTube Manager with Proxy Pattern ===\n")
        
        val aYouTubeService = ThirdPartyYouTubeClass()
        val aYouTubeProxy = CachedYouTubeClass(aYouTubeService)
        val manager = YouTubeManager(aYouTubeProxy)
        
        // First interaction - will cache results
        println("First user interaction (will cache results):")
        manager.reactOnUserInput()
        
        // Second interaction - will use cached results
        println("Second user interaction (using cached results):")
        manager.reactOnUserInput()
        
        // Reset cache and demonstrate again
        println("Resetting cache and third interaction:")
        aYouTubeProxy.resetCache()
        manager.reactOnUserInput()
    }
}

// Client code
fun main() {
    val app = Application()
    app.init()
    
    // Demonstrate without proxy for comparison
    println("\n=== Performance comparison: With vs Without Proxy ===")
    
    val service = ThirdPartyYouTubeClass()
    val proxy = CachedYouTubeClass(service)
    val managerWithProxy = YouTubeManager(proxy)
    val managerWithoutProxy = YouTubeManager(service)
    
    // Time with proxy (cached)
    val startTimeWithProxy = System.currentTimeMillis()
    managerWithProxy.reactOnUserInput()
    val endTimeWithProxy = System.currentTimeMillis()
    
    // Time without proxy (always fresh)
    val startTimeWithoutProxy = System.currentTimeMillis()
    managerWithoutProxy.reactOnUserInput()
    val endTimeWithoutProxy = System.currentTimeMillis()
    
    println("Time with proxy: ${endTimeWithProxy - startTimeWithProxy}ms")
    println("Time without proxy: ${endTimeWithoutProxy - startTimeWithoutProxy}ms")
    println("Time saved with proxy: ${(endTimeWithoutProxy - startTimeWithoutProxy) - (endTimeWithProxy - startTimeWithProxy)}ms")
}

main()