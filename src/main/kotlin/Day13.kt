import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.math.max

fun main() {
    println("Day 13")
    val inputfile = "day13/real.input"
    val extrasFile = "day13/extra.input"

    var indexes = arrayListOf<Int>()

    load(inputfile).readLines().windowed(2,3)
        .forEachIndexed { index, strings ->
            val left = strings[0]
            val right = strings[1]
//            println(left)
//            println(right)
            val message = comparePackets(packetOf(left), packetOf(right))
//            println("" + index + " = " + message)
//            println()

//            if (message == CompareResult.RIGHT_ORDER)
//                indexes.add(index+1)
        }

    val extras = load(extrasFile).readLines()
        .map { packetOf(it) }

    val originals = load(inputfile).readLines()
        .filter { it.isNotBlank() }
        .map { packetOf(it) }

    val sorted = originals
        .union(extras)
        .sortedWith(Comparator())

    sorted
        .forEachIndexed { index, packet ->
//            println(packet.pretty())
            extras.forEach { extra ->
                if (extra.pretty().equals(packet.pretty())) {
                    indexes.add(index+1)
                }
            }
        }

    println(indexes)

    println(indexes.fold(1) { a, b -> a * b })

}

class Comparator(): java.util.Comparator<Packet> {
    override fun compare(o1: Packet?, o2: Packet?): Int {
        return comparePackets(o1!!, o2!!).value
    }

}

class Packet(val values: List<Packet>?, val value: Int?) {

    constructor(value: Int) : this(emptyList(), value)
    constructor(values: List<Packet>) : this(values, null)

    fun hasValue(): Boolean {
        return value != null;
    }

    override fun toString(): String {
        return "Packet(values=$values, value=$value)"
    }

    fun pretty(): String {
        if (hasValue()) {
            return value.toString()
        } else {
            return "[" + values!!.joinToString(",") { it.pretty() } + "]"
        }
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

fun comparePackets(left: Packet, right: Packet): CompareResult {
    if (left.hasValue() && right.hasValue()) {
        return comparePackets(left.value!!, right.value!!);
    } else if (left.hasValue()) {
        return comparePackets(listOf(left), right.values!!)
    } else if (right.hasValue()) {
        return comparePackets(left.values!!, listOf(right))
    } else {
        return comparePackets(left.values!!, right.values!!);
    }

}

fun comparePackets(left: Int, right: Int): CompareResult {
//    println("Compare $left vs $right")
    return if (left == right) {
        CompareResult.DUNNO
    } else if (left < right) {
//        println("left < right")
        CompareResult.RIGHT_ORDER
    } else {
//        println("left > right")
        CompareResult.WRONG_ORDER
    }
}
fun comparePackets(left: List<Packet>, right: List<Packet>): CompareResult {
//    println("Compare $left vs $right")
    (0 until max(left.size, right.size)).forEach {
        if (left.size <= it) {
//            println("left ran out of items first")
            return CompareResult.RIGHT_ORDER // left ran out of items first
        } else if (right.size <= it) {
//            println("right ran out of items first")
            return CompareResult.WRONG_ORDER // right ran out of items first
        } else {
            val leftVal = left.get(it)
            val rightVal = right.get(it)
            val compareResult = comparePackets(leftVal, rightVal)
            if (compareResult !== CompareResult.DUNNO) { // left and right item are different
                return compareResult
            }
        }
    }
    return CompareResult.DUNNO // everything is the same - dunno
}


enum class CompareResult(val value: Int) {
    RIGHT_ORDER(-1),
    WRONG_ORDER(1),
    DUNNO(0)
}

