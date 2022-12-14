import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.math.max

fun main() {
    println("Day 13")

    var indexes = arrayListOf<Int>()

    load("day13/real.input").readLines().windowed(2,3)
        .forEachIndexed { index, strings ->
            val left = strings[0]
            val right = strings[1]
            println(left)
            println(right)
            val message = compare(packetOf(left), packetOf(right))
            println("" + index + " = " + message)
            println()

            if (message == CompareResult.RIGHT_ORDER)
                indexes.add(index+1)
        }

    println(indexes.sum())
}

class Packet(val values: List<Packet>?, val value: Int?) {

    constructor(value: Int) : this(emptyList(), value)
    constructor(values: List<Packet>) : this(values, null)

    fun hasValue(): Boolean {
        return value != null;
    }
}

fun packetOf(input: String): Packet {
    val tree = jacksonObjectMapper().readTree(input)

    if (tree.isInt) {
        return Packet(tree.intValue())
    } else if (tree.isArray) {
        return Packet(tree.map {
            packetOf(it.toString())
        })
    } else {
        throw UnsupportedOperationException(tree.toString())
    }
}

fun compare(left: Packet, right: Packet): CompareResult {
    if (left.hasValue() && right.hasValue()) {
        return compare(left.value!!, right.value!!);
    } else if (left.hasValue()) {
        return compare(listOf(left), right.values!!)
    } else if (right.hasValue()) {
        return compare(left.values!!, listOf(right))
    } else {
        return compare(left.values!!, right.values!!);
    }

}

fun compare(left: Int, right: Int): CompareResult {
    return if (left == right) {
        CompareResult.DUNNO
    } else if (left < right) {
        CompareResult.RIGHT_ORDER
    } else {
        CompareResult.WRONG_ORDER
    }
}
fun compare(left: List<Packet>, right: List<Packet>): CompareResult {
    (0..max(left.size, right.size)).forEach {
        if (left.size <= it) {
            return CompareResult.RIGHT_ORDER // left ran out of items first
        } else if (right.size <= it) {
            return CompareResult.WRONG_ORDER // right ran out of items first
        } else {
            val leftVal = left.get(it)
            val rightVal = right.get(it)
            val compareResult = compare(leftVal, rightVal)
            if (compareResult !== CompareResult.DUNNO) { // left and right item are different
                return compareResult
            }
        }
    }
    return CompareResult.DUNNO // everything is the same - dunno
}


enum class CompareResult {
    RIGHT_ORDER,
    WRONG_ORDER,
    DUNNO
}

