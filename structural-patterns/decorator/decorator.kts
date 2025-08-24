/**
 * DECORATOR PATTERN
 * 
 * The Decorator pattern allows behavior to be added to individual objects, 
 * either statically or dynamically, without affecting the behavior of other 
 * objects from the same class. It provides a flexible alternative to subclassing 
 * for extending functionality.
 * 
 * This pattern is useful when you need to add responsibilities to objects 
 * without modifying their code, or when subclassing would lead to an explosion 
 * of subclasses to cover every combination of features.
 * 
 * Source: https://refactoring.guru/es/design-patterns/decorator
 */

// The component interface defines operations that decorators can alter.
interface DataSource {
    fun writeData(data: String)
    fun readData(): String
}

// Concrete components provide default implementations for the operations.
// There can be many variations of these classes in a program.
class FileDataSource(private val filename: String) : DataSource {
    private var data: String = ""
    
    override fun writeData(data: String) {
        this.data = data
        println("Writing data to file '$filename': $data")
    }
    
    override fun readData(): String {
        println("Reading data from file '$filename': $data")
        return data
    }
}

// The base decorator class follows the same interface as other components.
// The main purpose of this class is to define the wrapping interface for all
// concrete decorators. The default implementation of the wrapping code might
// include a field for storing a wrapped component and means to initialize it.
open class DataSourceDecorator(protected val wrappee: DataSource) : DataSource {
    // The base decorator simply delegates all work to the wrapped component.
    // Concrete decorators can add additional behaviors.
    override fun writeData(data: String) {
        wrappee.writeData(data)
    }
    
    // Concrete decorators can call the parent implementation of the operation
    // instead of calling the wrapped object directly. This approach simplifies
    // extending decorator classes.
    override fun readData(): String {
        return wrappee.readData()
    }
}

// Concrete decorators must call methods on the wrapped object, but can add
// something of their own to the result. Decorators can execute the added behavior
// either before or after the call to a wrapped object.
class EncryptionDecorator(wrappee: DataSource) : DataSourceDecorator(wrappee) {
    override fun writeData(data: String) {
        // 1. Encrypt the passed data
        val encryptedData = "ENCRYPTED($data)"
        // 2. Pass the encrypted data to the writeData method of the wrapped object
        super.writeData(encryptedData)
    }
    
    override fun readData(): String {
        // 1. Get data from the readData method of the wrapped object
        val data = super.readData()
        // 2. Try to decrypt it if it's encrypted
        return if (data.startsWith("ENCRYPTED(")) {
            data.removePrefix("ENCRYPTED(").removeSuffix(")")
        } else {
            data
        }
    }
}

// You can wrap objects in multiple layers of decorators.
class CompressionDecorator(wrappee: DataSource) : DataSourceDecorator(wrappee) {
    override fun writeData(data: String) {
        // 1. Compress the passed data
        val compressedData = "COMPRESSED($data)"
        // 2. Pass the compressed data to the writeData method of the wrapped object
        super.writeData(compressedData)
    }
    
    override fun readData(): String {
        // 1. Get data from the readData method of the wrapped object
        val data = super.readData()
        // 2. Try to decompress it if it's compressed
        return if (data.startsWith("COMPRESSED(")) {
            data.removePrefix("COMPRESSED(").removeSuffix(")")
        } else {
            data
        }
    }
}

// Option 1: A simple example of assembling a decorator.
class Application {
    fun dumbUsageExample() {
        val salaryRecords = "Salary records: Alice=1000, Bob=1500"
        
        var source: DataSource = FileDataSource("somefile.dat")
        source.writeData(salaryRecords)
        // The target file has been written with raw data.
        
        source = CompressionDecorator(source)
        source.writeData(salaryRecords)
        // The target file has been written with compressed data.
        
        source = EncryptionDecorator(source)
        // The source variable now contains this:
        // Encryption > Compression > FileDataSource
        source.writeData(salaryRecords)
        // The file has been written with compressed and encrypted data.
        
        // Reading back the data
        println("\nReading data through decorators:")
        val result = source.readData()
        println("Final result: $result")
    }
}

// Option 2: Client code that uses an external data source.
// SalaryManager objects don't know or care about data storage specifics.
// They work with a preconfigured data source received from the application configurator.
class SalaryManager(private val source: DataSource) {
    private var salaryRecords: String = ""
    
    fun load(): String {
        salaryRecords = source.readData()
        return salaryRecords
    }
    
    fun save() {
        source.writeData(salaryRecords)
    }
    
    fun setSalaryRecords(records: String) {
        salaryRecords = records
    }
}

// The application can assemble different decorator stacks at runtime,
// depending on configuration or environment.
class ApplicationConfigurator {
    fun configurationExample() {
        val enabledEncryption = true
        val enabledCompression = true
        
        var source: DataSource = FileDataSource("salary.dat")
        
        if (enabledEncryption) {
            source = EncryptionDecorator(source)
        }
        if (enabledCompression) {
            source = CompressionDecorator(source)
        }
        
        val logger = SalaryManager(source)
        logger.setSalaryRecords("Salary records: Charlie=2000, Dana=1800")
        logger.save()
        
        val salary = logger.load()
        println("Loaded salary data: $salary")
    }
}

// Client code
fun main() {
    println("=== Option 1: Simple Decorator Example ===")
    val app = Application()
    app.dumbUsageExample()
    
    println("\n=== Option 2: Configurable Decorator Example ===")
    val configurator = ApplicationConfigurator()
    configurator.configurationExample()
}

main()