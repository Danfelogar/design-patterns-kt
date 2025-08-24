/**
 * SERVER-SIDE FACADE EXAMPLE
 * 
 * Scenario: E-commerce Order Processing System
 * Facade simplifies complex order processing involving inventory, payment, 
 * shipping, and notification subsystems.
 */

// Complex subsystem classes
class InventoryService {
    fun checkStock(productId: String, quantity: Int): Boolean {
        println("Checking stock for product: $productId, quantity: $quantity")
        return true // Simulate stock available
    }
    
    fun reserveStock(productId: String, quantity: Int) {
        println("Reserving stock: $productId, quantity: $quantity")
    }
    
    fun updateStock(productId: String, quantity: Int) {
        println("Updating stock: $productId, quantity: -$quantity")
    }
}

class PaymentService {
    fun processPayment(orderId: String, amount: Double, cardToken: String): Boolean {
        println("Processing payment for order: $orderId, amount: $$amount")
        return true // Simulate successful payment
    }
    
    fun refundPayment(orderId: String, amount: Double) {
        println("Refunding payment for order: $orderId, amount: $$amount")
    }
}

class ShippingService {
    fun createShippingLabel(orderId: String, address: Map<String, String>): String {
        println("Creating shipping label for order: $orderId")
        return "SHIP-$orderId-${System.currentTimeMillis()}"
    }
    
    fun schedulePickup(orderId: String, labelId: String) {
        println("Scheduling pickup for order: $orderId, label: $labelId")
    }
}

class NotificationService {
    fun sendOrderConfirmation(orderId: String, email: String) {
        println("Sending order confirmation to: $email for order: $orderId")
    }
    
    fun sendShippingNotification(orderId: String, email: String, trackingNumber: String) {
        println("Sending shipping notification to: $email, tracking: $trackingNumber")
    }
}

class DatabaseService {
    fun saveOrder(orderData: Map<String, Any>): String {
        val orderId = "ORD-${System.currentTimeMillis()}"
        println("Saving order to database: $orderId")
        return orderId
    }
    
    fun updateOrderStatus(orderId: String, status: String) {
        println("Updating order status: $orderId -> $status")
    }
}

// Facade - Simplifies the complex order processing
class OrderProcessingFacade {
    private val inventory = InventoryService()
    private val payment = PaymentService()
    private val shipping = ShippingService()
    private val notification = NotificationService()
    private val database = DatabaseService()
    
    fun placeOrder(
        products: List<Map<String, Any>>,
        totalAmount: Double,
        cardToken: String,
        shippingAddress: Map<String, String>,
        customerEmail: String
    ): Map<String, Any> {
        println("🚀 Starting order processing...")
        
        // 1. Check and reserve inventory
        products.forEach { product ->
            val productId = product["id"] as String
            val quantity = product["quantity"] as Int
            if (!inventory.checkStock(productId, quantity)) {
                throw Exception("Insufficient stock for product: $productId")
            }
            inventory.reserveStock(productId, quantity)
        }
        
        // 2. Process payment
        if (!payment.processPayment("temp-order", totalAmount, cardToken)) {
            throw Exception("Payment processing failed")
        }
        
        // 3. Save order to database
        val orderData = mapOf(
            "products" to products,
            "totalAmount" to totalAmount,
            "shippingAddress" to shippingAddress,
            "customerEmail" to customerEmail
        )
        val orderId = database.saveOrder(orderData)
        
        // 4. Create shipping
        val shippingLabel = shipping.createShippingLabel(orderId, shippingAddress)
        shipping.schedulePickup(orderId, shippingLabel)
        
        // 5. Update inventory
        products.forEach { product ->
            val productId = product["id"] as String
            val quantity = product["quantity"] as Int
            inventory.updateStock(productId, quantity)
        }
        
        // 6. Send notifications
        notification.sendOrderConfirmation(orderId, customerEmail)
        notification.sendShippingNotification(orderId, customerEmail, shippingLabel)
        
        // 7. Update order status
        database.updateOrderStatus(orderId, "COMPLETED")
        
        println("✅ Order processing completed successfully!")
        
        return mapOf(
            "orderId" to orderId,
            "status" to "COMPLETED",
            "shippingLabel" to shippingLabel,
            "totalAmount" to totalAmount
        )
    }
}

// Client code - Simple interface
class ECommerceServer {
    private val orderFacade = OrderProcessingFacade()
    
    fun processOrderRequest() {
        val products = listOf(
            mapOf("id" to "PROD-001", "name" to "Laptop", "quantity" to 1, "price" to 999.99),
            mapOf("id" to "PROD-002", "name" to "Mouse", "quantity" to 2, "price" to 25.99)
        )
        
        val totalAmount = 1051.97
        val cardToken = "card_tok_123456"
        val shippingAddress = mapOf(
            "street" to "123 Main St",
            "city" to "New York",
            "zip" to "10001"
        )
        val customerEmail = "customer@example.com"
        
        try {
            val result = orderFacade.placeOrder(
                products, totalAmount, cardToken, shippingAddress, customerEmail
            )
            println("\nOrder result: $result")
        } catch (e: Exception) {
            println("❌ Order failed: ${e.message}")
        }
    }
}

// Server-side usage
fun main() {
    val server = ECommerceServer()
    server.processOrderRequest()
}

main()