import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    println("Day 15")

    val inputFile = "day15/real.input"
    val range = 4_000_000L;

    val input = load(inputFile)
        .readLines()
        .map { toSensor(it) }

    val minX = input.minOf { min(min(it.x, it.xBeacon), it.x - it.manhattan) }
    val maxX = input.maxOf { max(max(it.x, it.xBeacon), it.x + it.manhattan) }
    val minY = input.minOf { min(min(it.y, it.yBeacon), it.y - it.manhattan) }
    val maxY = input.maxOf { max(max(it.y, it.yBeacon), it.y + it.manhattan) }
    val minHattsan = input.minOf { it.manhattan }
    val maxHattsan = input.maxOf { it.manhattan }

    println("x range from $minX to $maxX")
    println("y range from $minY to $maxY")
    println("manhattan min $minHattsan")
    println("manhattan max $maxHattsan")


    val first = 0L.until(range)
        .map { y ->
            val randes = ranges(y, input)
            val merged = merge(randes)
            if (y.mod(1000) == 0) {
                println("$y -> $randes -> $merged")
            }
            Pair(y, merged)
        }
        .filter { it.first in 0L..range }
        .find { p ->
            p.second.size > 1
        }

    println(first)
    println(first!!.first + (first.second[0].last + 1) * range)
}

fun ranges(y: Long, input: List<Sensor>): List<LongRange> {
    return input.filter { abs(it.y - y) <= it.manhattan }
        .map { (it.x - (it.manhattan - abs(it.y - y)))..(it.x + (it.manhattan - abs(it.y - y))) }
        .sortedBy { it.first };
}

// geen .intersect gebruiken - waaay too slow
private fun merge(ranges: List<LongRange>): List<LongRange> {
    val newRanges = mutableListOf<LongRange>()

    var current: LongRange? = null
    ranges.forEach {
        current = if (current == null) {
            it
        } else {
            if (current!!.last < it.first - 1) { // current ligt volledig in het verleden, toevoegen
                newRanges.add(current!!);
                it
            } else if (current!!.last >= it.first - 1 && current!!.last <= it.last) { // current verlengen
                LongRange(current!!.first, it.last)
            } else {
                current
            }
        }
    }
    if (current != null) {
        newRanges.add(current!!)
    }
    return newRanges
}

val regex = "Sensor at x=(-?[0-9]*), y=(-?[0-9]*): closest beacon is at x=(-?[0-9]*), y=(-?[0-9]*)".toRegex()

fun toSensor(it: String): Sensor {
    val values = regex.matchEntire(it)!!.groupValues.drop(1).map { value -> value.toLong() }
    return Sensor(values[0], values[1], values[2], values[3])
}

fun manhattan(x1: Long, x2: Long, y1: Long, y2: Long): Long {
    return abs(x1 - x2) + abs(y1 - y2);
}

data class Sensor(val x: Long, val y: Long, val xBeacon: Long, val yBeacon: Long, val manhattan: Long) {
    constructor(x: Long, y: Long, xBeacon: Long, yBeacon: Long) : this(x, y, xBeacon, yBeacon, manhattan(x, xBeacon, y, yBeacon))
}
