/**
 * SERVER-SIDE COMPOSITE EXAMPLE
 * 
 * Scenario: E-commerce order management system
 * Orders can contain individual products or product bundles (composite products)
 * Both are treated uniformly for price calculation and processing
 */

// Component interface
interface OrderItem {
    fun getPrice(): Double
    fun getDescription(): String
    fun process()
}

// Leaf - Individual product
class Product(
    private val name: String,
    private val price: Double,
    private val sku: String
) : OrderItem {
    override fun getPrice(): Double = price
    override fun getDescription(): String = "Product: $name (SKU: $sku)"
    override fun process() {
        println("Processing product: $name - Inventory checked, ready to ship")
    }
}

// Leaf - Service item
class Service(
    private val name: String,
    private val price: Double,
    private val duration: Int // in hours
) : OrderItem {
    override fun getPrice(): Double = price
    override fun getDescription(): String = "Service: $name ($duration hours)"
    override fun process() {
        println("Processing service: $name - Scheduling and resource allocation completed")
    }
}

// Composite - Product bundle
class ProductBundle(private val name: String) : OrderItem {
    private val items = mutableListOf<OrderItem>()
    
    fun addItem(item: OrderItem) {
        items.add(item)
    }
    
    fun removeItem(item: OrderItem) {
        items.remove(item)
    }
    
    override fun getPrice(): Double {
        var total = 0.0
        for (item in items) {
            total += item.getPrice()
        }
        return total * 0.9 // 10% discount for bundles
    }
    
    override fun getDescription(): String {
        val descriptions = items.joinToString("\n  ") { it.getDescription() }
        return "Bundle: $name\n  $descriptions\n  Total with discount: $${getPrice()}"
    }
    
    override fun process() {
        println("Processing bundle: $name")
        for (item in items) {
            item.process()
        }
        println("Bundle processing complete")
    }
}

// Order processor (client code)
class OrderProcessor {
    fun processOrder(items: List<OrderItem>) {
        println("=== PROCESSING ORDER ===")
        var total = 0.0
        
        for (item in items) {
            println("\n${item.getDescription()}")
            total += item.getPrice()
            item.process()
        }
        
        println("\nOrder total: $${"%.2f".format(total)}")
        println("=== ORDER PROCESSING COMPLETE ===\n")
    }
}

// Server-side usage
fun main() {
    val processor = OrderProcessor()
    
    // Individual products
    val laptop = Product("Laptop", 999.99, "LP123")
    val mouse = Product("Wireless Mouse", 49.99, "WM456")
    val warranty = Service("Extended Warranty", 99.99, 24)
    
    // Composite bundle
    val gamingBundle = ProductBundle("Gaming Bundle")
    gamingBundle.addItem(Product("Gaming Laptop", 1499.99, "GL789"))
    gamingBundle.addItem(Product("Gaming Mouse", 79.99, "GM012"))
    gamingBundle.addItem(Service("Premium Support", 199.99, 48))
    
    // Process order with both individual items and bundles
    val orderItems = listOf(laptop, mouse, warranty, gamingBundle)
    processor.processOrder(orderItems)
}

main()