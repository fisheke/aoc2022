package day9

import java.io.File
import kotlin.math.abs
import kotlin.math.sign

fun main(args: Array<String>) {
    println("Hello world!")

//    solve("/test.input")
    solve("/day9/real.input")
}

private fun solve(inputFile: String) {
    val input = File({}.javaClass.getResource(inputFile).toURI())

    var headPos = Position(0, 0);
    var knot1Pos = Position(0, 0);
    var knot2Pos = Position(0, 0);
    var knot3Pos = Position(0, 0);
    var knot4Pos = Position(0, 0);
    var knot5Pos = Position(0, 0);
    var knot6Pos = Position(0, 0);
    var knot7Pos = Position(0, 0);
    var knot8Pos = Position(0, 0);
    var knot9Pos = Position(0, 0);

    val frames = mutableListOf<Frame>()
    frames.add(Frame(headPos, knot1Pos, knot2Pos, knot3Pos, knot4Pos, knot5Pos, knot6Pos, knot7Pos, knot8Pos, knot9Pos))

    input.readLines()
            .flatMap { l -> 1.rangeTo(l.split(" ")[1].toInt()).map { l.split(" ")[0] } }
            .map { Direction.valueOf(it) }
            .map {
                headPos = move(it, headPos)
                knot1Pos = follow(knot1Pos, headPos);
                knot2Pos = follow(knot2Pos, knot1Pos);
                knot3Pos = follow(knot3Pos, knot2Pos);
                knot4Pos = follow(knot4Pos, knot3Pos);
                knot5Pos = follow(knot5Pos, knot4Pos);
                knot6Pos = follow(knot6Pos, knot5Pos);
                knot7Pos = follow(knot7Pos, knot6Pos);
                knot8Pos = follow(knot8Pos, knot7Pos);
                knot9Pos = follow(knot9Pos, knot8Pos);
                Frame(headPos, knot1Pos, knot2Pos, knot3Pos, knot4Pos, knot5Pos, knot6Pos, knot6Pos, knot8Pos, knot9Pos)
            }
            .forEach { frames.add(it) }

    frames.forEach { println(it) }

    val distinct = frames.distinctBy { it.knot9 }.count();
    println("Distinct tail positions: $distinct");
}

fun printFrame(frame: Frame, minX: Int, maxX: Int, minY: Int, maxY: Int) {
    println("=====")
    maxY.downTo(minY).forEach {
        val y = it
        minX.rangeTo(maxX).forEach {
            val x = it
            if (frame.headPos.on(x, y)) {
                print("H")
            } else if (frame.knot1.on(x, y)) {
                print("1")
            } else if (frame.knot2.on(x, y)) {
                print("2")
            } else if (frame.knot3.on(x, y)) {
                print("3")
            } else if (frame.knot4.on(x, y)) {
                print("4")
            } else if (frame.knot5.on(x, y)) {
                print("5")
            } else if (frame.knot6.on(x, y)) {
                print("6")
            } else if (frame.knot7.on(x, y)) {
                print("7")
            } else if (frame.knot8.on(x, y)) {
                print("8")
            } else if (frame.knot9.on(x, y)) {
                print("9")
            } else {
                print(".")
            }
        }
        println()
    }
}

fun follow(tailPos: Position, headPos: Position): Position {
    val xDiff = headPos.x - tailPos.x;
    val yDiff = headPos.y - tailPos.y;

    if (xDiff==0 && abs(yDiff) > 1) {
        return Position(tailPos.x, tailPos.y + yDiff.sign)
    }
    if (yDiff==0 && abs(xDiff) > 1) {
        return Position(tailPos.x + xDiff.sign, tailPos.y)
    }
    if (abs(xDiff) > 1 || abs(yDiff) > 1) {
        return Position(tailPos.x + xDiff.sign, tailPos.y + yDiff.sign)
    }

    return tailPos
}

fun move(it: Direction, headPos: Position): Position {
    return it.mutator.invoke(headPos)
}

data class Frame(val headPos: Position, val knot1: Position, val knot2: Position, val knot3: Position, val knot4: Position, val knot5: Position, val knot6: Position, val knot7: Position, val knot8: Position, val knot9: Position)

data class Position(val x: Int, val y: Int) {
    fun on(x: Int, y: Int): Boolean {
        return x == this.x && y == this.y
    }
}

enum class Direction(val mutator: (Position) -> Position) {
    U({ Position(it.x, it.y + 1) }),
    R({ Position(it.x + 1, it.y) }),
    D({ Position(it.x, it.y - 1) }),
    L({ Position(it.x - 1, it.y) })
}


