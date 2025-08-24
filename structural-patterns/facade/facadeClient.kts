/**
 * CLIENT-SIDE FACADE EXAMPLE
 *
 * Scenario: Mobile App User Profile Management
 * Facade simplifies complex user profile operations involving data fetching,
 * caching, validation, and UI updates across multiple screens.
 */

// Complex subsystem classes
class ApiService {
    fun fetchUserProfile(userId: String): Map<String, Any> {
        println("Fetching user profile from API for user: $userId")
        return mapOf(
            "id" to userId,
            "name" to "John Doe",
            "email" to "john@example.com",
            "avatar" to "https://api.com/avatars/john.jpg",
            "preferences" to mapOf("theme" to "dark", "notifications" to true)
        )
    }
    
    fun updateUserProfile(userId: String, data: Map<String, Any>): Boolean {
        println("Updating user profile via API: $userId")
        return true
    }
}

class CacheService {
    private val cache = mutableMapOf<String, Any>()
    
    fun getFromCache(key: String): Any? {
        println("Checking cache for key: $key")
        return cache[key]
    }
    
    fun saveToCache(key: String, data: Any) {
        println("Saving to cache: $key")
        cache[key] = data
    }
    
    fun invalidateCache(key: String) {
        println("Invalidating cache: $key")
        cache.remove(key)
    }
}

class ValidationService {
    fun validateEmail(email: String): Boolean {
        println("Validating email: $email")
        return email.contains("@") && email.contains(".")
    }
    
    fun validateName(name: String): Boolean {
        println("Validating name: $name")
        return name.length >= 2 && name.isNotBlank()
    }
    
    fun validateProfileData(data: Map<String, Any>): Map<String, String> {
        println("Validating profile data")
        val errors = mutableMapOf<String, String>()
        
        if (!validateName(data["name"] as? String ?: "")) {
            errors["name"] = "Invalid name"
        }
        
        if (!validateEmail(data["email"] as? String ?: "")) {
            errors["email"] = "Invalid email"
        }
        
        return errors
    }
}

class ImageService {
    fun loadImage(url: String): String {
        println("Loading image from: $url")
        return "Image data for $url"
    }
    
    fun cacheImage(url: String, imageData: String) {
        println("Caching image: $url")
    }
}

class AnalyticsService {
    fun trackEvent(eventName: String, data: Map<String, Any>) {
        println("Tracking event: $eventName with data: $data")
    }
}

// Facade - Simplifies user profile management
class UserProfileFacade {
    private val api = ApiService()
    private val cache = CacheService()
    private val validation = ValidationService()
    private val image = ImageService()
    private val analytics = AnalyticsService()
    
    fun getUserProfile(userId: String): Map<String, Any> {
        println("👤 Loading user profile...")
        
        // Check cache first
        val cacheKey = "user_$userId"
        val cachedData = cache.getFromCache(cacheKey)
        if (cachedData != null) {
            analytics.trackEvent("profile_load_cached", mapOf("userId" to userId))
            return cachedData as Map<String, Any>
        }
        
        // Fetch from API
        val profileData = api.fetchUserProfile(userId)
        
        // Cache the result
        cache.saveToCache(cacheKey, profileData)
        
        // Load profile image
        val avatarUrl = profileData["avatar"] as String
        image.loadImage(avatarUrl)
        image.cacheImage(avatarUrl, "cached_image_data")
        
        analytics.trackEvent("profile_load_api", mapOf("userId" to userId))
        
        return profileData
    }
    
    fun updateUserProfile(userId: String, updates: Map<String, Any>): Map<String, Any> {
        println("✏️ Updating user profile...")
        
        // Validate data
        val errors = validation.validateProfileData(updates)
        if (errors.isNotEmpty()) {
            analytics.trackEvent("profile_update_validation_failed", mapOf("userId" to userId, "errors" to errors))
            return mapOf("success" to false, "errors" to errors)
        }
        
        // Update via API
        val success = api.updateUserProfile(userId, updates)
        
        if (success) {
            // Invalidate cache
            cache.invalidateCache("user_$userId")
            
            // Track success
            analytics.trackEvent("profile_update_success", mapOf("userId" to userId))
            
            return mapOf("success" to true, "message" to "Profile updated successfully")
        }
        
        analytics.trackEvent("profile_update_failed", mapOf("userId" to userId))
        return mapOf("success" to false, "message" to "Update failed")
    }
}

// Client code - Simple interface for UI components
class ProfileScreen {
    private val profileFacade = UserProfileFacade()
    
    fun loadProfile(userId: String) {
        println("📱 Loading profile for UI...")
        val profile = profileFacade.getUserProfile(userId)
        println("Profile loaded: ${profile["name"]} - ${profile["email"]}")
    }
    
    fun updateProfile(userId: String, newName: String, newEmail: String) {
        println("📱 Updating profile from UI...")
        val updates = mapOf("name" to newName, "email" to newEmail)
        val result = profileFacade.updateUserProfile(userId, updates)
        println("Update result: $result")
    }
}

// Client-side usage
fun main() {
    val profileScreen = ProfileScreen()
    
    println("=== Mobile App Profile Management ===\n")
    
    // Load profile
    profileScreen.loadProfile("user123")
    
    println("\n=== Updating Profile ===\n")
    
    // Update profile
    profileScreen.updateProfile("user123", "John Smith", "john.smith@example.com")
    
    println("\n=== Reloading Profile ===\n")
    
    // Reload to see cached result
    profileScreen.loadProfile("user123")
}

main()