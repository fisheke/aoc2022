package day6

import java.io.File
fun main(args: Array<String>) {
    File({ }.javaClass.getResource("/test.input").toURI()).forEachLine { parse(it) }
}

private fun parse(input: String) {
    var result = input.windowed(4, 1)
        .find { it.toCharArray().distinct().size == 4 }

    println(input.indexOf(result!!) + 4);
}
