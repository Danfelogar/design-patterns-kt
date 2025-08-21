/**
 * Prototype Pattern Implementation in Kotlin
 * Reference: https://refactoring.guru/es/design-patterns/prototype
 * 
 * The Prototype pattern lets you copy existing objects without making your code
 * dependent on their classes. It creates new objects by copying an existing object,
 * known as the prototype.
 */

// Base prototype
abstract class Shape {
    var x: Int = 0
    var y: Int = 0
    var color: String = ""

    // A normal constructor
    constructor()

    // The prototype constructor. A new object is initialized
    // with values from the existing object.
    constructor(source: Shape) {
        this.x = source.x
        this.y = source.y
        this.color = source.color
    }

    // The clone operation returns one of the Shape subclasses.
    abstract fun clone(): Shape

    override fun toString(): String {
        return "${this::class.simpleName}(x=$x, y=$y, color='$color')"
    }
}

// Concrete prototype. The cloning method creates a new object
// and passes it to the constructor. Until the constructor
// finishes, it has a reference to a new clone. This way
// nobody has access to a half-finished clone. This ensures
// the consistency of the cloning result.
class Rectangle : Shape {
    var width: Int = 0
    var height: Int = 0

    // Normal constructor
    constructor() : super()

    // Prototype constructor
    constructor(source: Rectangle) : super(source) {
        this.width = source.width
        this.height = source.height
    }

    override fun clone(): Shape {
        return Rectangle(this)
    }

    override fun toString(): String {
        return "${super.toString()}, width=$width, height=$height"
    }
}

class Circle : Shape {
    var radius: Int = 0

    // Normal constructor
    constructor() : super()

    // Prototype constructor
    constructor(source: Circle) : super(source) {
        this.radius = source.radius
    }

    override fun clone(): Shape {
        return Circle(this)
    }

    override fun toString(): String {
        return "${super.toString()}, radius=$radius"
    }
}

// Client code that uses prototypes
class Application {
    private val shapes = mutableListOf<Shape>()

    init {
        initializeShapes()
    }

    private fun initializeShapes() {
        // Create a circle prototype
        val circle = Circle()
        circle.x = 10
        circle.y = 10
        circle.radius = 20
        circle.color = "red"
        shapes.add(circle)

        // Clone the circle
        val anotherCircle = circle.clone() as Circle
        shapes.add(anotherCircle)
        // The variable `anotherCircle` contains an exact copy of the `circle` object.

        // Create a rectangle
        val rectangle = Rectangle()
        rectangle.x = 30
        rectangle.y = 40
        rectangle.width = 10
        rectangle.height = 20
        rectangle.color = "blue"
        shapes.add(rectangle)

        println("Original shapes created:")
        shapes.forEachIndexed { index, shape -> 
            println("${index + 1}. $shape")
        }
    }

    fun businessLogic() {
        println("\n=== Cloning Shapes using Prototype Pattern ===")
        
        // Prototype is great because it lets you produce a
        // copy of an object without knowing anything about its type.
        val shapesCopy = mutableListOf<Shape>()

        // For example, we don't know the exact elements of the
        // shapes list. All we know is that they are all shapes.
        // But thanks to polymorphism, when we invoke the `clone`
        // method on a shape, the program checks its actual class
        // and executes the appropriate cloning method defined
        // in that class. That's why we get the proper clones
        // instead of a bunch of simple Shape objects.
        shapes.forEach { shape ->
            shapesCopy.add(shape.clone())
        }

        println("Cloned shapes:")
        shapesCopy.forEachIndexed { index, shape ->
            println("${index + 1}. $shape")
        }

        // Demonstrate that clones are independent objects
        println("\n=== Modifying Clones (Original shapes remain unchanged) ===")
        
        // Modify the first clone
        val firstClone = shapesCopy[0] as Circle
        firstClone.x = 100
        firstClone.color = "green"
        
        // Modify the second clone
        val secondClone = shapesCopy[1] as Circle
        secondClone.radius = 50
        
        // Modify the third clone
        val thirdClone = shapesCopy[2] as Rectangle
        thirdClone.width = 100
        thirdClone.height = 200

        println("After modifying clones:")
        println("Original shapes:")
        shapes.forEachIndexed { index, shape -> 
            println("${index + 1}. $shape")
        }
        
        println("\nModified clones:")
        shapesCopy.forEachIndexed { index, shape -> 
            println("${index + 1}. $shape")
        }
    }

    fun demonstrateDeepCloning() {
        println("\n=== Deep Cloning Demonstration ===")
        
        // Create a complex shape with nested properties
        val originalCircle = Circle()
        originalCircle.x = 5
        originalCircle.y = 5
        originalCircle.radius = 15
        originalCircle.color = "purple"
        
        // Clone it multiple times
        val clone1 = originalCircle.clone() as Circle
        val clone2 = originalCircle.clone() as Circle
        val clone3 = originalCircle.clone() as Circle
        
        // Modify each clone differently
        clone1.x += 10
        clone2.y += 10
        clone3.radius *= 2
        clone3.color = "orange"
        
        println("Original: $originalCircle")
        println("Clone 1: $clone1")
        println("Clone 2: $clone2")
        println("Clone 3: $clone3")
        
        // Verify they are different objects
        println("\nObject references comparison:")
        println("Original == Clone 1: ${originalCircle === clone1}")
        println("Clone 1 == Clone 2: ${clone1 === clone2}")
        println("Clone 2 == Clone 3: ${clone2 === clone3}")
    }
}

// Additional example: Prototype Registry
class ShapeRegistry {
    private val prototypes = mutableMapOf<String, Shape>()
    
    init {
        loadDefaultPrototypes()
    }
    
    private fun loadDefaultPrototypes() {
        // Predefine some common shapes as prototypes
        val defaultCircle = Circle()
        defaultCircle.radius = 10
        defaultCircle.color = "black"
        prototypes["default_circle"] = defaultCircle
        
        val defaultRectangle = Rectangle()
        defaultRectangle.width = 20
        defaultRectangle.height = 10
        defaultRectangle.color = "gray"
        prototypes["default_rectangle"] = defaultRectangle
        
        val largeCircle = Circle()
        largeCircle.radius = 50
        largeCircle.color = "blue"
        prototypes["large_circle"] = largeCircle
    }
    
    fun addPrototype(key: String, prototype: Shape) {
        prototypes[key] = prototype
    }
    
    fun getPrototype(key: String): Shape? {
        return prototypes[key]?.clone()
    }
    
    fun demonstrateRegistry() {
        println("\n=== Prototype Registry Demonstration ===")
        
        // Get prototypes from registry and customize them
        val circle1 = getPrototype("default_circle")?.apply {
            x = 100
            y = 100
        }
        
        val circle2 = getPrototype("large_circle")?.apply {
            x = 200
            y = 200
            color = "red"
        }
        
        val rectangle = getPrototype("default_rectangle")?.apply {
            x = 300
            y = 300
            if (this is Rectangle) {
                width = 30
                height = 15
            }
        }
        
        println("Registry-generated shapes:")
        println("Circle 1: $circle1")
        println("Circle 2: $circle2")
        println("Rectangle: $rectangle")
    }
}

// Main function to demonstrate the prototype pattern
fun main() {
    println("🔷 Prototype Pattern Implementation in Kotlin")
    println("Reference: https://refactoring.guru/es/design-patterns/prototype")
    println("=".repeat(60))
    
    val app = Application()
    app.businessLogic()
    app.demonstrateDeepCloning()
    
    // Demonstrate prototype registry
    val registry = ShapeRegistry()
    registry.demonstrateRegistry()
    
    println("\n" + "=".repeat(60))
    println("✅ Prototype pattern demonstration completed!")
    println("=".repeat(60))
}

// Run the main function
main()