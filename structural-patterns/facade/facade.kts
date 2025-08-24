/**
 * FACADE PATTERN
 * 
 * The Facade pattern provides a simplified interface to a complex subsystem of classes, 
 * library, or framework. It doesn't add new functionality but makes the existing 
 * functionality easier to use by providing a higher-level interface.
 * 
 * This pattern is useful when you need to integrate with complex systems, hide 
 * implementation details, or reduce dependencies between client code and subsystems.
 * 
 * Source: https://refactoring.guru/es/design-patterns/facade
 */

// These are some classes from a third-party video conversion framework.
// We don't control that code, so we can't simplify it.

class VideoFile(private val filename: String) {
    fun getFilename(): String = filename
}

class OggCompressionCodec {
    fun compress(data: ByteArray): ByteArray {
        println("Compressing with OGG codec")
        return data.copyOf()
    }
}

class MPEG4CompressionCodec {
    fun compress(data: ByteArray): ByteArray {
        println("Compressing with MPEG4 codec")
        return data.copyOf()
    }
}

class CodecFactory {
    fun extract(file: VideoFile): String {
        println("Extracting codec from file: ${file.getFilename()}")
        return if (file.getFilename().endsWith(".ogg")) "ogg" else "mpeg4"
    }
}

class BitrateReader {
    companion object {
        fun read(filename: String, codecType: String): ByteArray {
            println("Reading file: $filename with codec: $codecType")
            return "Video data from $filename".toByteArray()
        }
        
        fun convert(buffer: ByteArray, codec: Any): ByteArray {
            println("Converting video data")
            return when (codec) {
                is MPEG4CompressionCodec -> codec.compress(buffer)
                is OggCompressionCodec -> codec.compress(buffer)
                else -> buffer
            }
        }
    }
}

class AudioMixer {
    fun fix(audioData: ByteArray): ByteArray {
        println("Fixing audio synchronization")
        return audioData.copyOf()
    }
}

class File(private val data: ByteArray) {
    fun save() {
        println("Saving file with ${data.size} bytes")
    }
}

// We create a facade class to hide the complexity of the framework
// behind a simple interface. It's a trade-off between functionality and simplicity.
class VideoConverter {
    fun convert(filename: String, format: String): File {
        val file = VideoFile(filename)
        val sourceCodec = CodecFactory().extract(file)
        
        val destinationCodec = if (format == "mp4") {
            MPEG4CompressionCodec()
        } else {
            OggCompressionCodec()
        }
        
        val buffer = BitrateReader.read(filename, sourceCodec)
        var result = BitrateReader.convert(buffer, destinationCodec)
        result = AudioMixer().fix(result)
        
        return File(result)
    }
}

// Application classes don't depend on a million classes provided by the complex framework.
// Also, if you decide to change frameworks, you'll only need to rewrite the facade class.
class Application {
    fun main() {
        val converter = VideoConverter()
        val mp4 = converter.convert("funny-cats-video.ogg", "mp4")
        mp4.save()
    }
}

// Client code
fun main() {
    println("=== Video Conversion using Facade Pattern ===\n")
    
    val app = Application()
    app.main()
    
    println("\n=== Alternative usage without facade ===")
    // Demonstrating the complexity without the facade
    val file = VideoFile("another-video.ogg")
    val codecType = CodecFactory().extract(file)
    val buffer = BitrateReader.read(file.getFilename(), codecType)
    val codec = OggCompressionCodec()
    val converted = BitrateReader.convert(buffer, codec)
    val fixed = AudioMixer().fix(converted)
    val resultFile = File(fixed)
    resultFile.save()
}

main()