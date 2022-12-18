import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = load("day14/real.input")

    val source = Point(500, 0, Fill.SAND)

    val map = input.readLines()
        .map { line ->
            line.split(" -> ")
                .map { point ->
                    val coords = point.split(",")
                    Point(coords[0].toInt(), coords[1].toInt(), Fill.ROCK)
                }
                .windowed(2, 1)
                .map { Segment(it[0], it[1]) }
        }
        .flatten()
        .map {
            it.a.rangeTo(it.b)
        }
        .flatten()
        .distinct()
        .union(listOf(source))

    val coords = map
        .groupBy { it.y }
        .mapValues { it.value.groupBy { it.x }.mapValues { it.value[0] }.toMutableMap() }
        .toMutableMap()

    val world = World(coords, mutableMapOf())

    println(world)

    var counter = 0
    while (drop(world, source) == DropResult.PLACED) {
        counter++
    }

    counter++ // last drop is ON the source

    println(world)
    println(counter)

}

fun drop(world: World, source: Point): DropResult {
    var point = source
    while (true) {
        if (world.isBottom(point.y + 1)) {
            world.add(point)
            world.add(Point(point.x, point.y+1, Fill.ROCK))
            println("Placed on bottom: $point")
            return DropResult.PLACED
        } else {
            val nextLine = world.get(point.x - 1..point.x + 1, point.y + 1)

            // can I drop down?
            if (nextLine.get(1)?.fill == null) {
                point = Point(point.x, point.y + 1, Fill.SAND)
            }

            // can I drop down-left?
            else if (nextLine.get(0)?.fill == null) {
                point = Point(point.x - 1, point.y + 1, Fill.SAND)
            }

            // can I drop down-right?
            else if (nextLine.get(2)?.fill == null) {
                point = Point(point.x + 1, point.y + 1, Fill.SAND)
            } else {
                if (point.y == 0) {
                    return DropResult.ON_SOURCE;
                } else {
                    world.add(point)
                    println("Placed to $point")
                    return DropResult.PLACED
                }
            }
        }
    }
}

fun xRange(maps: List<MutableMap<Int, MutableMap<Int, Point>>>): IntRange {
    return maps.map {
        it.map {
            it.value
        }.minOf {
            it.keys.min()
        }
    }.min()
        .rangeTo(maps.map { it.map { it.value }.maxOf { it.keys.max() } }.max())
}

fun yRange(maps: List<MutableMap<Int, MutableMap<Int, Point>>>): IntRange {
    return maps.minOf { it.keys.min() }..maps.maxOf { it.keys.max() }
}

class World(
    val rock: MutableMap<Int, MutableMap<Int, Point>>,
    val sand: MutableMap<Int, MutableMap<Int, Point>>,
    var xRange: IntRange = xRange(listOf(rock)),
    var yRange: IntRange = yRange(listOf(rock)),
    val bottom : Int = yRange.last + 2) {

    fun isBottom(y: Int): Boolean {
        return y == bottom
    }

    fun get(xs: IntRange, y: Int): List<Point?> {
        if (isBottom(y)) {
            return xs.map { Point(it, y, Fill.BOTTOM) }
        }
        val rocks = rock.getOrDefault(y, emptyMap())
        val sands = sand.getOrDefault(y, emptyMap())

        return xs.map { rocks.getOrDefault(it, sands[it]) }
    }

    fun add(point: Point) {
        if (point.fill == Fill.SAND) {
            sand.putIfAbsent(point.y, mutableMapOf())
            sand[point.y]!![point.x] = point
        }
        if (point.fill == Fill.ROCK) {
            sand.putIfAbsent(point.y, mutableMapOf())
            sand[point.y]!![point.x] = point
        }

        this.xRange = xRange(listOf( rock, sand))
        this.yRange = yRange(listOf( rock, sand))

        print(xRange)
        print("/")
        print(yRange)
        println()
    }

    override fun toString(): String {
        var out = "\b"
        yRange.forEach { y ->
            out += "$y\t"
            get(xRange, y).forEach {
                if (it == null) {
                    out += Fill.NILL.s
                } else {
                    out += it.fill.s
                }
            }
            out += "\n"
        }
        return out
    }
}

enum class DropResult {
    PLACED,
    ON_SOURCE
}

enum class Fill(val s: String) {
    ROCK("#"),
    SAND("o"),
    NILL("."),
    BOTTOM("%")
}

data class Point(val x: Int, val y: Int, val fill: Fill) {
    operator fun rangeTo(other: Point): List<Point> {
        val xFrom = min(x, other.x)
        val xTo = max(x, other.x)
        val yFrom = min(y, other.y)
        val yTo = max(y, other.y)

        return (xFrom..xTo).map { x ->
            (yFrom..yTo).map { y ->
                Point(x, y, fill)
            }
        }.flatten()
    }
}

data class Segment(val a: Point, val b: Point)
