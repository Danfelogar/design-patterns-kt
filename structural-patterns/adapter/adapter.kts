/**
 * ADAPTER PATTERN
 * 
 * The Adapter pattern allows objects with incompatible interfaces to collaborate.
 * It acts as a bridge between two incompatible interfaces by converting the interface
 * of one class into an interface expected by the clients.
 * 
 * This example demonstrates how to make SquarePeg objects work with RoundHole
 * that expects RoundPeg objects, using an adapter that converts square pegs to
 * round pegs mathematically.
 * 
 * Source: https://refactoring.guru/es/design-patterns/adapter
 */

// Interface for round pegs
interface RoundPeg {
    fun getRadius(): Double
}

// RoundHole class that works with RoundPeg interface
class RoundHole(private val radius: Double) {
    fun getRadius(): Double = radius

    fun fits(peg: RoundPeg): Boolean {
        return this.getRadius() >= peg.getRadius()
    }
}

// Concrete implementation of RoundPeg
class DefaultRoundPeg(private val radius: Double) : RoundPeg {
    override fun getRadius(): Double = radius
}

// Incompatible class: SquarePeg
class SquarePeg(private val width: Double) {
    fun getWidth(): Double = width
}

// Adapter class allows square pegs to fit into round holes
// Implements RoundPeg interface to act as a round peg
class SquarePegAdapter(private val peg: SquarePeg) : RoundPeg {
    override fun getRadius(): Double {
        // The adapter simulates being a round peg with a radius
        // that can accommodate the square peg it wraps
        // Formula: radius = width * √2 / 2 (circumradius of a square)
        return peg.getWidth() * Math.sqrt(2.0) / 2
    }
}

// Client code
fun main() {
    val hole = RoundHole(5.0)
    val rpeg = DefaultRoundPeg(5.0)
    println(hole.fits(rpeg)) // true

    val smallSqpeg = SquarePeg(5.0)
    val largeSqpeg = SquarePeg(10.0)
    // hole.fits(smallSqpeg) // This doesn't compile (incompatible types)

    val smallSqpegAdapter = SquarePegAdapter(smallSqpeg)
    val largeSqpegAdapter = SquarePegAdapter(largeSqpeg)
    println(hole.fits(smallSqpegAdapter)) // true
    println(hole.fits(largeSqpegAdapter)) // false
}

main()