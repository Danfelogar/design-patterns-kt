/**
 * FLYWEIGHT PATTERN
 * 
 * The Flyweight pattern minimizes memory usage by sharing as much data as possible 
 * with similar objects. It's used when you need to create a large number of similar 
 * objects that would otherwise consume too much memory.
 * 
 * This pattern separates intrinsic state (shared) from extrinsic state (unique). 
 * The intrinsic state is stored in flyweight objects, while extrinsic state is 
 * stored or computed by client objects and passed to the flyweight when needed.
 * 
 * Source: https://refactoring.guru/es/design-patterns/flyweight
 */

// The Flyweight class contains a portion of the tree's state.
// These fields store values that are unique for each particular tree.
// For example, you won't find tree coordinates here. But the texture and colors
// shared by many trees are here. Since this amount of data is usually LARGE,
// you would spend a lot of memory keeping it in each tree object. Instead,
// we can extract the texture, color, and other repeated data and place it
// in a separate object that many individual tree objects can reference.
class TreeType(
    private val name: String,
    private val color: String,
    private val texture: String
) {
    fun draw(canvas: String, x: Int, y: Int) {
        // 1. Create a bitmap of a specific type, color, and texture.
        // 2. Draw the bitmap on the canvas with coordinates X and Y.
        println("Drawing $name tree (color: $color, texture: $texture) at position ($x, $y) on $canvas")
    }
    
    override fun toString(): String {
        return "TreeType(name='$name', color='$color', texture='$texture')"
    }
}

// The Flyweight factory decides whether to reuse an existing flyweight
// or create a new object.
object TreeFactory {
    private val treeTypes = mutableMapOf<String, TreeType>()
    
    fun getTreeType(name: String, color: String, texture: String): TreeType {
        val key = "$name|$color|$texture"
        return treeTypes.getOrPut(key) {
            println("Creating new TreeType: $name, $color, $texture")
            TreeType(name, color, texture)
        }
    }
    
    fun getTreeTypesCount(): Int {
        return treeTypes.size
    }
}

// The contextual object contains the extrinsic part of the tree's state.
// An application can create millions of these, as they are very small:
// two integer coordinates and a reference field.
class Tree(
    private val x: Int,
    private val y: Int,
    private val type: TreeType
) {
    fun draw(canvas: String) {
        type.draw(canvas, x, y)
    }
    
    override fun toString(): String {
        return "Tree at ($x, $y) of type: $type"
    }
}

// The Tree and Forest classes are the flyweight clients.
// You can merge them if you don't plan to develop the Tree class further.
class Forest {
    private val trees = mutableListOf<Tree>()
    
    fun plantTree(x: Int, y: Int, name: String, color: String, texture: String) {
        val type = TreeFactory.getTreeType(name, color, texture)
        val tree = Tree(x, y, type)
        trees.add(tree)
    }
    
    fun draw(canvas: String) {
        println("\nDrawing forest on $canvas:")
        for (tree in trees) {
            tree.draw(canvas)
        }
    }
    
    fun getTreeCount(): Int {
        return trees.size
    }
}

// Client code
fun main() {
    println("=== Flyweight Pattern Demo: Forest Simulation ===\n")
    
    val forest = Forest()
    
    // Plant many trees with shared types
    forest.plantTree(1, 1, "Oak", "Green", "Rough")
    forest.plantTree(2, 3, "Oak", "Green", "Rough")  // Same type as above
    forest.plantTree(4, 5, "Oak", "Green", "Rough")  // Same type as above
    forest.plantTree(6, 7, "Pine", "Dark Green", "Smooth")
    forest.plantTree(8, 9, "Pine", "Dark Green", "Smooth")  // Same type as above
    forest.plantTree(10, 11, "Maple", "Red", "Medium")
    forest.plantTree(12, 13, "Maple", "Red", "Medium")  // Same type as above
    forest.plantTree(14, 15, "Birch", "White", "Smooth")
    
    println("\nTotal trees planted: ${forest.getTreeCount()}")
    println("Unique tree types created: ${TreeFactory.getTreeTypesCount()}")
    
    // Draw the forest
    forest.draw("Main Canvas")
    
    // Demonstrate memory savings
    println("\n=== Memory Savings Demonstration ===")
    println("Without Flyweight: ${forest.getTreeCount()} trees × 3 fields = ${forest.getTreeCount() * 3} field instances")
    println("With Flyweight: ${TreeFactory.getTreeTypesCount()} types + ${forest.getTreeCount()} trees × 1 reference = ${TreeFactory.getTreeTypesCount() + forest.getTreeCount()} total objects")
    
    val memorySaved = (forest.getTreeCount() * 3) - (TreeFactory.getTreeTypesCount() + forest.getTreeCount())
    println("Memory saved: $memorySaved field references")
}

main()