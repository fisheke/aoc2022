import kotlin.math.sqrt

fun main() {
    println("Day 22")

    val load = load("day22/test.input")
        .readText().split("\n\n")

    val world = World22(toMap(load[0]))
    val commands = toCommands(load[1])

    val startingPos = Day2Position(world.findStart(), Day22Direction.RIGHT)

    world.segments()

//    startWaling(startingPos, commands, world)
}

private fun startWaling(startingPos: Day2Position, commands: List<Command>, world: World22) {
    var pos = startingPos
    println(pos)
    commands.forEach {
        println(it)
        if (it.move != null) {
            pos = Day2Position(world.move(pos.xy, it.move, pos.dir), pos.dir)
        } else if (it.left) {
            println("Turning Left")
            pos = Day2Position(pos.xy, pos.dir.left())
        } else if (it.right) {
            println("Turning Right")
            pos = Day2Position(pos.xy, pos.dir.right())
        }
        println(pos)
        println()
    }

    println(pos)

    println(pos.xy.second * 1000 + pos.xy.first * 4 + pos.dir.endVal)
}

fun toCommands(s: String): List<Command> {
    return s.split("((?<=(L|R)|(?=(L|R))))".toRegex())
        .map { it.trim() }
        .map {
            if (it.all { it.isDigit() }) {
                Command(it.toInt(), left = false, right = false)
            } else if (it == "R") {
                Command(null, left = false, right = true)
            } else if (it == "L") {
                Command(null, left = true, right = false)
            } else {
                throw UnsupportedOperationException(s)
            }
        }
}

data class Command(val move: Int?, val left: Boolean, val right: Boolean)

data class Day2Position(val xy: Pair<Int, Int>, val dir: Day22Direction)

enum class Day22Direction(val mod: (Pair<Int, Int>) -> Pair<Int, Int>, val endVal: Int) {
    RIGHT( { Pair(it.first + 1, it.second)}, 0 ),
    DOWN( { Pair(it.first, it.second + 1)}, 1 ),
    LEFT( { Pair(it.first - 1, it.second)}, 2 ),
    UP( { Pair(it.first, it.second - 1)}, 3 );

    fun right() : Day22Direction {
        return when(this) {
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
            UP -> RIGHT
        }
    }

    fun left() : Day22Direction {
        return when(this) {
            RIGHT -> UP
            DOWN -> RIGHT
            LEFT -> DOWN
            UP -> LEFT
        }
    }

    fun resetFilter(from: Pair<Int, Int>): (Pair<Int, Int>) -> Boolean {
        when(this) {
            RIGHT, LEFT -> return { it.second == from.second }
            DOWN, UP -> return { it.first == from.first }
        }
    }

    fun resetSearch(from: Pair<Int, Int>): kotlin.Comparator<Map.Entry<Pair<Int, Int>, Block>> {
        return when(this) {
            RIGHT -> compareBy { it.key.first }
            DOWN -> compareBy { it.key.second }
            LEFT -> compareBy<Map.Entry<Pair<Int, Int>, Block>> { it.key.first }.reversed()
            UP -> compareBy<Map.Entry<Pair<Int, Int>, Block>> { it.key.second }.reversed()
        }

    }
}

class World22(val grid: Map<Pair<Int, Int>, Block>) {
    fun findStart(): Pair<Int, Int> {
        return grid.filter { it.value == Block.OPEN }
            .map { it.key }
            .minWith(compareBy<Pair<Int, Int>> { it.second }.thenBy { it.first })
    }

    fun move(xy: Pair<Int, Int>, move: Int,
             dir: Day22Direction,
             ): Pair<Int, Int> {
        var pos = xy;
        0.until(move).forEach {
            println("Moving 1 to $dir from $pos")
            var nextPos = dir.mod.invoke(pos)

            val nextBlock: Block
            if (grid.containsKey(nextPos)) {
                nextBlock = grid[nextPos]!!
            } else {
                val nextEntry = grid.filter { dir.resetFilter(pos).invoke(it.key) }
                    .minWith(dir.resetSearch(pos))
                println("Wrap from $pos to ${nextEntry.key}")
                nextBlock = nextEntry.value
                nextPos = nextEntry.key
            }

            if (nextBlock == Block.WALL) {
                return pos
            } else {
                pos = nextPos
            }
        }
        return pos
    }

    fun segments(): Map<Pair<IntRange, IntRange>, String> {
        val faceSize = grid.size / 6
        println("$faceSize blocks per face");
        val ribSize = sqrt(faceSize.toDouble()).toInt()
        println("That's $ribSize per rib")

        val maxX = grid.maxOf { it.key.first }
        val maxY = grid.maxOf { it.key.second }

        val maxHorizontal = maxX / ribSize
        val maxVertical = maxY / ribSize
        println("MaxX = $maxX -> $maxHorizontal faces")
        println("MaxY = $maxY -> $maxVertical faces")

        return emptyMap()
    }

}

fun toMap(input: String): Map<Pair<Int, Int>, Block> {
    val map = mutableMapOf<Pair<Int, Int>, Block>()
    input.lines()
        .forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                val value = blockOf(c)
                if (value !== Block.VOID) {
                    map.put(Pair(x + 1, y + 1), value)
                }
            }
        }
    return map
}

fun blockOf(input: Char): Block {
    return when(input) {
        ' ' -> Block.VOID
        '.' -> Block.OPEN
        '#' -> Block.WALL
        else -> throw UnsupportedOperationException("$input is not a valid map block")
    }
}
enum class Block {
    VOID,
    OPEN,
    WALL
}
