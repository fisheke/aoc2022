import java.io.File

fun main() {
    val input = File({}.javaClass.getResource("/day6/real.input").toURI())
    println(input.forEachLine { findPacketStart(it, 4) })
}

fun findPacketStart(line: String, packetLength: Int): Int {
    val find = line.windowed(packetLength, 1)
            .find { it.toCharArray().distinct().size == packetLength }

    return line.indexOf(find!!)
}

