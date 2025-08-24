/**
 * BRIDGE PATTERN
 *
 * The Bridge pattern separates an abstraction from its implementation so that
 * the two can vary independently. It uses composition instead of inheritance to
 * decouple the abstraction from its implementation, allowing both to be modified
 * without affecting each other.
 *
 * This pattern is particularly useful when both the abstractions and their
 * implementations need to be extended through inheritance, preventing a
 * combinatorial explosion of classes.
 *
 * Source: https://refactoring.guru/es/design-patterns/bridge
 */

// The "abstraction" defines the interface for the "control" part of the two class hierarchies.
// It maintains a reference to an object of the "implementation" hierarchy and delegates all real work to this object.
open class RemoteControl(protected val device: Device) {
    fun togglePower() {
        if (device.isEnabled()) {
            device.disable()
        } else {
            device.enable()
        }
    }

    fun volumeDown() {
        device.setVolume(device.getVolume() - 10)
    }

    fun volumeUp() {
        device.setVolume(device.getVolume() + 10)
    }

    fun channelDown() {
        device.setChannel(device.getChannel() - 1)
    }

    fun channelUp() {
        device.setChannel(device.getChannel() + 1)
    }
}

// You can extend classes from the abstraction hierarchy independently of device classes.
class AdvancedRemoteControl(device: Device) : RemoteControl(device) {
    fun mute() {
        device.setVolume(0)
    }
}

// The "implementation" interface declares methods common to all concrete implementation classes.
// It doesn't have to match the abstraction's interface. In fact, the two interfaces can be completely different.
// Typically, the implementation interface only provides primitive operations, while the abstraction
// defines higher-level operations based on the primitives.
interface Device {
    fun isEnabled(): Boolean
    fun enable()
    fun disable()
    fun getVolume(): Int
    fun setVolume(percent: Int)
    fun getChannel(): Int
    fun setChannel(channel: Int)
}

// All devices follow the same interface.
class Tv : Device {
    private var enabled = false
    private var volume = 50
    private var channel = 1
    
    override fun isEnabled(): Boolean = enabled
    
    override fun enable() {
        enabled = true
        println("TV enabled")
    }
    
    override fun disable() {
        enabled = false
        println("TV disabled")
    }
    
    override fun getVolume(): Int = volume
    
    override fun setVolume(percent: Int) {
        volume = percent.coerceIn(0, 100)
        println("TV volume set to: $volume")
    }
    
    override fun getChannel(): Int = channel
    
    override fun setChannel(channel: Int) {
        this.channel = channel.coerceAtLeast(1)
        println("TV channel set to: $channel")
    }
}

class Radio : Device {
    private var enabled = false
    private var volume = 30
    private var channel = 88
    
    override fun isEnabled(): Boolean = enabled
    
    override fun enable() {
        enabled = true
        println("Radio enabled")
    }
    
    override fun disable() {
        enabled = false
        println("Radio disabled")
    }
    
    override fun getVolume(): Int = volume
    
    override fun setVolume(percent: Int) {
        volume = percent.coerceIn(0, 100)
        println("Radio volume set to: $volume")
    }
    
    override fun getChannel(): Int = channel
    
    override fun setChannel(channel: Int) {
        this.channel = channel.coerceIn(88, 108)
        println("Radio channel set to: $channel")
    }
}

// Client code
fun main() {
    val tv = Tv()
    val remote = RemoteControl(tv)
    remote.togglePower() // Turns TV on
    remote.volumeUp()    // Increases TV volume
    
    val radio = Radio()
    val advancedRemote = AdvancedRemoteControl(radio)
    advancedRemote.togglePower() // Turns radio on
    advancedRemote.mute()        // Mutes radio
}

main()