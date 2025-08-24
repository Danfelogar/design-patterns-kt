/**
 * CLIENT-SIDE PROXY EXAMPLE
 * 
 * Scenario: Image Loading with Lazy Loading and Caching
 * Proxy pattern handles image loading in a client application with
 * lazy loading, caching, and placeholder display for better UX.
 */

// Image service interface
interface ImageService {
    fun loadImage(url: String): String
    fun displayImage(url: String)
    fun getImageInfo(url: String): Map<String, Any>
}

// Real image loader (expensive network operations)
class RealImageLoader : ImageService {
    override fun loadImage(url: String): String {
        println("📡 Loading image from network: $url")
        // Simulate network latency
        Thread.sleep(800)
        // Simulate image data
        return "Image data for $url (${(1000..5000).random()}KB)"
    }
    
    override fun displayImage(url: String) {
        val imageData = loadImage(url)
        println("🖼️  Displaying image: ${imageData.take(30)}...")
    }
    
    override fun getImageInfo(url: String): Map<String, Any> {
        println("ℹ️  Fetching image metadata: $url")
        Thread.sleep(200)
        return mapOf(
            "url" to url,
            "size" to "${(500..2000).random()}KB",
            "dimensions" to "${(800..4000).random()}x${(600..3000).random()}",
            "format" to listOf("JPEG", "PNG", "WEBP", "GIF").random()
        )
    }
}

// Image proxy with lazy loading and caching
class ImageProxy : ImageService {
    private val realLoader = RealImageLoader()
    private val imageCache = mutableMapOf<String, String>()
    private val metadataCache = mutableMapOf<String, Map<String, Any>>()
    private val loadingQueue = mutableSetOf<String>()

    override fun loadImage(url: String): String {
        // Return cached image if available
        if (imageCache.containsKey(url)) {
            println("♻️  Returning cached image: $url")
            return imageCache[url]!!
        }

        // Load from network
        val imageData = realLoader.loadImage(url)
        imageCache[url] = imageData
        return imageData
    }

    override fun displayImage(url: String) {
        // Show placeholder immediately
        showPlaceholder(url)
        
        // Check cache first
        if (imageCache.containsKey(url)) {
            println("🖼️  Displaying cached image: $url")
            return
        }
        
        // Prevent duplicate loading
        if (loadingQueue.contains(url)) {
            println("⏳ Image already loading: $url")
            return
        }
        
        loadingQueue.add(url)
        
        // Load in background (simulated with thread)
        Thread {
            try {
                val imageData = realLoader.loadImage(url)
                imageCache[url] = imageData
                loadingQueue.remove(url)
                println("✅ Image loaded successfully: $url")
                // In real app, this would trigger UI update
            } catch (e: Exception) {
                loadingQueue.remove(url)
                showError(url, e.message ?: "Unknown error")
            }
        }.start()
    }
    
    override fun getImageInfo(url: String): Map<String, Any> {
        // Return cached metadata if available
        if (metadataCache.containsKey(url)) {
            println("📊 Returning cached metadata: $url")
            return metadataCache[url]!!
        }
        
        val info = realLoader.getImageInfo(url)
        metadataCache[url] = info
        return info
    }
    
    private fun showPlaceholder(url: String) {
        val metadata = getImageInfo(url)
        val dimensions = metadata["dimensions"] as String
        val format = metadata["format"] as String
        
        println("⏳ Showing placeholder for: $url")
        println("   📐 Dimensions: $dimensions")
        println("   📄 Format: $format")
        println("   🎨 Loading full image in background...")
    }
    
    private fun showError(url: String, error: String) {
        println("❌ Failed to load image: $url")
        println("   Error: $error")
        // In real app, this would show error UI
    }
    
    fun preloadImages(urls: List<String>) {
        println("\n🔮 Preloading images...")
        urls.forEach { url ->
            Thread {
                if (!imageCache.containsKey(url)) {
                    loadImage(url) // Cache the image
                }
            }.start()
        }
    }
    
    fun clearCache() {
        imageCache.clear()
        metadataCache.clear()
        println("🗑️  Image cache cleared")
    }
}

// Client UI component using the proxy
class ImageGallery {
    private val imageLoader = ImageProxy()
    
    fun openGallery() {
        println("\n=== Opening Image Gallery ===")
        
        val imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.png",
            "https://example.com/image3.webp",
            "https://example.com/image4.gif"
        )
        
        // Preload some images
        imageLoader.preloadImages(imageUrls.take(2))
        
        // Display images (some will show placeholders initially)
        imageUrls.forEachIndexed { index, url ->
            println("\n--- Image ${index + 1} ---")
            imageLoader.displayImage(url)
            
            // Simulate user scrolling delay
            if (index < imageUrls.size - 1) {
                Thread.sleep(300)
            }
        }
    }
    
    fun showImageInfo(url: String) {
        println("\n📋 Image Information:")
        val info = imageLoader.getImageInfo(url)
        info.forEach { (key, value) ->
            println("   $key: $value")
        }
    }
}

// Client-side usage
fun main() {
    println("=== Image Loading Proxy Pattern (Client Side) ===\n")
    
    val gallery = ImageGallery()
    
    // Open gallery with multiple images
    gallery.openGallery()
    
    // Show detailed info for one image
    gallery.showImageInfo("https://example.com/image1.jpg")
    
    // Demonstrate cache benefits
    println("\n=== Performance Benefits ===")
    println("• Lazy loading: Images load only when needed")
    println("• Caching: Subsequent loads are instant")
    println("• Background loading: UI remains responsive")
    println("• Metadata preloading: Placeholders show immediately")
    println("• Error handling: Graceful failure management")
}

main()