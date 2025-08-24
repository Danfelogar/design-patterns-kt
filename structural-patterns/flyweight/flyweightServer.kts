/**
 * SERVER-SIDE FLYWEIGHT EXAMPLE
 * 
 * Scenario: E-commerce Product Catalog with Shared Attributes
 * Flyweight pattern shares common product attributes (category, brand, specs) 
 * across millions of product instances to reduce memory usage.
 */

// Intrinsic state (shared)
data class ProductType(
    val category: String,
    val brand: String,
    val specifications: Map<String, String>,
    val warranty: String
) {
    fun displayInfo(sku: String, price: Double) {
        println("Product: $sku")
        println("Category: $category")
        println("Brand: $brand")
        println("Price: $$price")
        println("Warranty: $warranty")
        println("Specs: $specifications")
        println("---")
    }
}

// Flyweight factory
object ProductTypeFactory {
    private val productTypes = mutableMapOf<String, ProductType>()
    
    fun getProductType(category: String, brand: String, specs: Map<String, String>, warranty: String): ProductType {
        val key = "$category|$brand|${specs.hashCode()}|$warranty"
        return productTypes.getOrPut(key) {
            println("Creating new ProductType: $category - $brand")
            ProductType(category, brand, specs, warranty)
        }
    }
    
    fun getTypesCount(): Int = productTypes.size
}

// Extrinsic state (unique)
data class Product(
    val sku: String,
    val price: Double,
    val stock: Int,
    val type: ProductType
) {
    fun display() {
        type.displayInfo(sku, price)
    }
}

// Client - Product catalog managing millions of products
class ProductCatalog {
    private val products = mutableListOf<Product>()
    
    fun addProduct(sku: String, price: Double, stock: Int, category: String, 
                  brand: String, specs: Map<String, String>, warranty: String) {
        val productType = ProductTypeFactory.getProductType(category, brand, specs, warranty)
        val product = Product(sku, price, stock, productType)
        products.add(product)
    }
    
    fun displayCatalog() {
        println("=== PRODUCT CATALOG ===")
        products.take(5).forEach { it.display() } // Show first 5 for demo
    }
    
    fun getTotalProducts(): Int = products.size
}

// Server-side usage
fun main() {
    println("=== E-commerce Product Catalog with Flyweight ===\n")
    
    val catalog = ProductCatalog()
    
    // Add millions of products with shared types
    val laptopSpecs = mapOf("RAM" to "16GB", "Storage" to "512GB SSD", "CPU" to "Intel i7")
    val phoneSpecs = mapOf("RAM" to "8GB", "Storage" to "128GB", "Battery" to "4000mAh")
    
    // Add products sharing the same types
    repeat(10000) { index ->
        catalog.addProduct(
            sku = "LP${1000 + index}",
            price = 999.99 + index,
            stock = 50,
            category = "Laptop",
            brand = "Dell",
            specs = laptopSpecs,
            warranty = "2 years"
        )
    }
    
    repeat(10000) { index ->
        catalog.addProduct(
            sku = "PH${2000 + index}",
            price = 699.99 + index,
            stock = 100,
            category = "Smartphone",
            brand = "Samsung",
            specs = phoneSpecs,
            warranty = "1 year"
        )
    }
    
    repeat(10000) { index ->
        catalog.addProduct(
            sku = "LP${3000 + index}",
            price = 1299.99 + index,
            stock = 30,
            category = "Laptop",
            brand = "Apple",
            specs = laptopSpecs,
            warranty = "3 years"
        )
    }
    
    println("Total products: ${catalog.getTotalProducts()}")
    println("Unique product types: ${ProductTypeFactory.getTypesCount()}")
    
    // Memory savings calculation
    val memoryWithoutFlyweight = catalog.getTotalProducts() * 4 // category, brand, specs, warranty per product
    val memoryWithFlyweight = ProductTypeFactory.getTypesCount() * 4 + catalog.getTotalProducts() // types + references
    
    println("\nMemory savings:")
    println("Without Flyweight: $memoryWithoutFlyweight attribute sets")
    println("With Flyweight: $memoryWithFlyweight (${ProductTypeFactory.getTypesCount()} types + ${catalog.getTotalProducts()} references)")
    println("Memory saved: ${memoryWithoutFlyweight - memoryWithFlyweight} attribute sets")
    
    // Display sample products
    catalog.displayCatalog()
}

main()