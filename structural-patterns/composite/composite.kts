/**
 * COMPOSITE PATTERN
 * 
 * The Composite pattern lets you compose objects into tree structures and then work with 
 * these structures as if they were individual objects. It allows clients to treat individual 
 * objects and compositions of objects uniformly.
 * 
 * This pattern is useful when you need to represent part-whole hierarchies of objects and 
 * want clients to be able to ignore the difference between compositions of objects and 
 * individual objects.
 * 
 * Source: https://refactoring.guru/es/design-patterns/composite
 */

// The component interface declares common operations for both simple and complex objects in a composition.
interface Graphic {
    fun move(x: Int, y: Int)
    fun draw()
}

// Base class for graphics with common properties
open class BaseGraphic(protected var x: Int, protected var y: Int) : Graphic {
    override fun move(x: Int, y: Int) {
        this.x += x
        this.y += y
    }

    override fun draw() {
        println("Drawing at ($x, $y)")
    }
}

// The leaf class represents end objects of a composition.
// A leaf object cannot have any sub-objects. Typically,
// leaf objects do the actual work, while composite objects
// only delegate to their subcomponents.
class Dot(x: Int, y: Int) : BaseGraphic(x, y) {
    override fun draw() {
        println("Drawing a dot at ($x, $y)")
    }
}

// All component classes can extend other components.
class Circle(x: Int, y: Int, private val radius: Int) : BaseGraphic(x, y) {
    override fun draw() {
        println("Drawing a circle at ($x, $y) with radius $radius")
    }
}

// The composite class represents complex components that can have children.
// Usually, composite objects delegate the actual work to their children and then
// "summarize" the result.
class CompoundGraphic : Graphic {
    private val children = mutableListOf<Graphic>()

    // A composite object can add or remove other components
    // (both simple and complex) to or from its child list.
    fun add(child: Graphic) {
        children.add(child)
    }

    fun remove(child: Graphic) {
        children.remove(child)
    }

    override fun move(x: Int, y: Int) {
        for (child in children) {
            child.move(x, y)
        }
    }

    // A composite executes its primary logic in a particular way.
    // It recursively traverses all its children, collecting and
    // summarizing their results. Because the composite's children
    // pass these calls to their own children and so on, the entire
    // object tree is traversed as a result.
    override fun draw() {
        println("Starting compound graphic drawing:")
        for (child in children) {
            child.draw()
        }
        println("Finished compound graphic drawing")
        println("Drawing dotted rectangle around bounding box")
    }
}

// The client code works with all components through their base interface.
// This way, the client code can support both simple leaf components
// and complex composites.
class ImageEditor {
    private val all = CompoundGraphic()

    fun load() {
        all.add(Dot(1, 2))
        all.add(Circle(5, 3, 10))
        all.add(Dot(8, 4))
    }

    // Combine selected components to form a complex composite component.
    fun groupSelected(components: List<Graphic>) {
        val group = CompoundGraphic()
        for (component in components) {
            group.add(component)
            all.remove(component)
        }
        all.add(group)
        // All components will be drawn
        all.draw()
    }
}

// Client code
fun main() {
    val editor = ImageEditor()
    editor.load()
    
    // Create some individual components for grouping
    val dot1 = Dot(10, 20)
    val dot2 = Dot(15, 25)
    val circle = Circle(20, 30, 5)
    
    val componentsToGroup = listOf(dot1, dot2, circle)
    
    println("Drawing individual components:")
    dot1.draw()
    dot2.draw()
    circle.draw()
    
    println("\nGrouping components:")
    val group = CompoundGraphic()
    group.add(dot1)
    group.add(dot2)
    group.add(circle)
    group.draw()
}

main()