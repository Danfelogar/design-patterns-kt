/**
 * Builder Pattern Implementation in Kotlin
 * Reference: https://refactoring.guru/es/design-patterns/builder
 *
 * The Builder pattern is used to construct complex objects step by step.
 * It allows producing different types and representations of an object
 * using the same construction code.
 */

// The Builder pattern only makes sense when your products are quite complex
// and require extensive configuration. The following two products are
// related, although they don't have a common interface.
interface Engine
class SportEngine : Engine
class SUVEngine : Engine

class Car {
    // A car can have GPS, a trip computer, and a certain number of seats.
    // Different car models (sports car, SUV, convertible) can have
    // different features installed or enabled.
    var seats: Int = 0
    var engine: Engine? = null
    var hasTripComputer: Boolean = false
    var hasGPS: Boolean = false
    
    override fun toString(): String {
        return "Car(seats=$seats, engine=${engine?.javaClass?.simpleName}, " +
               "hasTripComputer=$hasTripComputer, hasGPS=$hasGPS)"
    }
}

class Manual {
    // Each car should have a user manual that corresponds to the car's
    // configuration and explains all its features.
    var content: String = ""
    
    override fun toString(): String {
        return "Manual:\n$content"
    }
}

// The builder interface specifies methods for creating the different parts
// of the product objects.
interface Builder {
    fun reset()
    fun setSeats(seats: Int)
    fun setEngine(engine: Engine)
    fun setTripComputer(hasTripComputer: Boolean)
    fun setGPS(hasGPS: Boolean)
}

// Concrete builder classes follow the builder interface and provide
// specific implementations of the construction steps. Your program can
// have many variations of builders, each implemented differently.
class CarBuilder : Builder {
    private lateinit var car: Car

    // A new instance of the builder class should contain a blank product
    // object that it uses in further assembly.
    init {
        reset()
    }

    // The reset method clears the object being built.
    override fun reset() {
        car = Car()
    }

    // All production steps work with the same product instance.
    override fun setSeats(seats: Int) {
        car.seats = seats
    }

    override fun setEngine(engine: Engine) {
        car.engine = engine
    }

    override fun setTripComputer(hasTripComputer: Boolean) {
        car.hasTripComputer = hasTripComputer
    }

    override fun setGPS(hasGPS: Boolean) {
        car.hasGPS = hasGPS
    }

    // Concrete builders should provide their own methods for retrieving
    // results. This is because various types of builders may create
    // completely different products that don't all follow the same interface.
    // Therefore, such methods cannot be declared in the builder interface.
    //
    // Typically, after returning the final result to the client, a builder
    // instance should be ready to start producing another product. That's why
    // it's common practice to call the reset method at the end of the
    // getProduct method body.
    fun getProduct(): Car {
        val product = car
        reset()
        return product
    }
}

// Unlike other creational patterns, Builder allows you to build products
// that don't follow a common interface.
class CarManualBuilder : Builder {
    private lateinit var manual: Manual

    init {
        reset()
    }

    override fun reset() {
        manual = Manual()
    }

    override fun setSeats(seats: Int) {
        manual.content += "Car has $seats seats.\n"
    }

    override fun setEngine(engine: Engine) {
        manual.content += "Car has a ${engine.javaClass.simpleName} engine.\n"
    }

    override fun setTripComputer(hasTripComputer: Boolean) {
        if (hasTripComputer) {
            manual.content += "Car has a trip computer.\n"
        }
    }

    override fun setGPS(hasGPS: Boolean) {
        if (hasGPS) {
            manual.content += "Car has GPS.\n"
        }
    }

    fun getProduct(): Manual {
        val product = manual
        reset()
        return product
    }
}

// The director is only responsible for executing the building steps in a
// particular sequence. It's useful when creating products according to a
// specific order or configuration. Strictly speaking, the director class
// is optional, since the client can control builders directly.
class Director {
    // The director works with any builder instance passed to it by the client
    // code. This way, the client code can alter the final type of the newly
    // assembled product. The director can construct many variations of the
    // product using the same building steps.
    fun constructSportsCar(builder: Builder) {
        builder.reset()
        builder.setSeats(2)
        builder.setEngine(SportEngine())
        builder.setTripComputer(true)
        builder.setGPS(true)
    }

    fun constructSUV(builder: Builder) {
        builder.reset()
        builder.setSeats(7)
        builder.setEngine(SUVEngine())
        builder.setTripComputer(true)
        builder.setGPS(true)
        // Additional SUV-specific configuration can be added here
    }
}

// Client code creates a builder object, passes it to the director, and then
// initiates the construction process. The final result is retrieved from
// the builder object.
class Application {
    fun makeCar() {
        val director = Director()

        // Build a car
        val carBuilder = CarBuilder()
        director.constructSportsCar(carBuilder)
        val car: Car = carBuilder.getProduct()

        // Build a manual for the car
        val manualBuilder = CarManualBuilder()
        director.constructSportsCar(manualBuilder)

        // The final product is often retrieved from a builder object, since
        // the director doesn't know about and doesn't depend on concrete
        // builders and products.
        val manual: Manual = manualBuilder.getProduct()

        println("Car built: $car")
        println("Manual created: $manual")
    }
}

// Usage example
fun main() {
    val app = Application()
    app.makeCar()

    // Additional demonstration
    println("\n=== Building an SUV ===")
    val director = Director()
    val suvBuilder = CarBuilder()
    director.constructSUV(suvBuilder)
    val suv = suvBuilder.getProduct()
    println("SUV built: $suv")
}

// Run the main function
main()