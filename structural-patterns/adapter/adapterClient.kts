/**
 * CLIENT-SIDE ADAPTER EXAMPLE
 * 
 * Scenario: Mobile app supporting multiple data formats from different APIs
 * The app needs to display user data consistently regardless of API response format
 */

// Unified user data model our UI expects
data class UserViewData(
    val fullName: String,
    val email: String,
    val avatarUrl: String,
    val joinDate: String
)

// Legacy API returns data in different format
class LegacyUserApi {
    fun getUserData(userId: String): LegacyUserResponse {
        return LegacyUserResponse(
            "John Doe",
            "john@email.com",
            "https://api.com/avatars/john.jpg",
            "2023-01-15T10:30:00Z"
        )
    }
}

data class LegacyUserResponse(
    val name: String,
    val emailAddress: String,
    val profileImage: String,
    val createdAt: String
)

// New GraphQL API returns different structure
class GraphQLUserService {
    fun fetchUser(userId: String): GraphQLUser {
        return GraphQLUser(
            UserNode(
                "Jane Smith",
                "jane@email.com",
                "https://graphql.com/users/jane/avatar",
                "2022-05-20"
            )
        )
    }
}

data class GraphQLUser(val user: UserNode)
data class UserNode(
    val displayName: String,
    val contactEmail: String,
    val imageUrl: String,
    val memberSince: String
)

// Adapter for Legacy API
class LegacyUserAdapter(private val api: LegacyUserApi) {
    fun getUserViewData(userId: String): UserViewData {
        val response = api.getUserData(userId)
        return UserViewData(
            fullName = response.name,
            email = response.emailAddress,
            avatarUrl = response.profileImage,
            joinDate = response.createdAt.substring(0, 10) // Extract just the date
        )
    }
}

// Adapter for GraphQL API
class GraphQLUserAdapter(private val service: GraphQLUserService) {
    fun getUserViewData(userId: String): UserViewData {
        val response = service.fetchUser(userId)
        return UserViewData(
            fullName = response.user.displayName,
            email = response.user.contactEmail,
            avatarUrl = response.user.imageUrl,
            joinDate = response.user.memberSince
        )
    }
}

// Client code - UI components work with consistent data format
fun loadClientUserData() {
    val legacyAdapter = LegacyUserAdapter(LegacyUserApi())
    val graphqlAdapter = GraphQLUserAdapter(GraphQLUserService())
    
    val user1 = legacyAdapter.getUserViewData("user123")
    val user2 = graphqlAdapter.getUserViewData("user456")
    
    println("Legacy user: ${user1.fullName}, GraphQL user: ${user2.fullName}")
    
    // Both can be used interchangeably in UI components
    displayUserProfile(user1)
    displayUserProfile(user2)
}

fun displayUserProfile(user: UserViewData) {
    // UI component that expects consistent data structure
    println("Displaying: ${user.fullName} - ${user.email}")
}

// Run examples
fun main() {
    println("\nClient-side Adapter Example:")
    loadClientUserData()
}

main()