package day10

import load
import java.lang.Math.floor

val red = "\u001b[31m"
val reset = "\u001b[0m"

fun main() {
    println("Day 10")

    val input = load("/day10/real.input")
    var current = 1;

    val values = arrayListOf<Pair<Int, Int>>()

    input.readLines().flatMap(::map).toList()
        .forEachIndexed { index, op ->
            val cycle = index + 1;
            val old = current
            current = (current + op)
            values.add(Pair(cycle, old))
        }

    values.forEach { println(it) }

    values.forEach {
        val cycle = it.first
        val register = it.second

        val row = floor((cycle / 40).toDouble()).toInt()
        var col = (it.first) % 40
        if (col == 0) { col = 40 }
//        if (abs((it.first)-1 - it.second) < 2)Ï

        val isVisible = isVisible(col-1, register)

        if (isVisible)
            print(red + "#" + reset)
        else
            print(".")

        if (col == 40) {
            println(" <= row " + row)
        }
    }
}

private fun isVisible(col: Int, value: Int): Boolean {
    return col == value || col == value-1 || col == value+1;
}

private fun map(s: String): List<Int> {
    return if (s.startsWith("addx")) {
        val second: Int = s.substring(5).toInt()
        listOf(0, second)
    } else {
        listOf(0)
    }
}

