import kotlin.math.ceil
import kotlin.math.pow

fun main() {
    val sum = load("day25/real.input")
        .readLines()
        .map { Pair(it, snafuToDecimal(it)) }
        .map { Triple(it.first, it.second, decimalToSnafu(it.second)) }
        .sumOf { it.second }

    println(sum)
    println(decimalToSnafu(sum))
}

fun decimalToSnafu(input: Long): String {
    var decimal = input
    val digits = ceil(decimal.toDouble().pow(0.2)).toInt()
    return digits.downTo(0)
        .map {
            val snafu = decimalToSnafu(it, decimal)
            decimal -= snafuToDecimal(snafu) * 5.0.pow(it).toLong()
            snafu
        }
        .joinToString(separator = "")
        .trimStart('0')
}

fun decimalToSnafu(position: Int, decimal: Long): String {
    val unit = 5.0.pow(position)
    if (decimal > 0) {
        if ((decimal > unit / 2) && (decimal < unit * 1.5)) {
            return "1"
        } else if (decimal > unit * 1.5) {
            return "2"
        } else {
            return "0"
        }
    } else {
        if (decimal < -unit / 2 && decimal > -unit * 1.5) {
            return "-"
        } else if (decimal < -unit * 1.5) {
            return "="
        } else {
            return "0"
        }
    }
}

fun snafuToDecimal(snafu: String, verbose: Boolean = false): Long {
    return snafu.reversed()
        .mapIndexed { index, c ->
            val pow = (5.0).pow(index).toLong()
            val powString = pow.toString().padStart(snafu.length, ' ')
            val snafuToDecimal = snafuToDecimal(c)
            val snafuToDecimalString = snafuToDecimal.toString().padStart(2, ' ')
            val total = snafuToDecimal * pow

            if (verbose) {
                println("$snafuToDecimalString * $powString = $total")
            }
            total
        }
        .sum()
}

fun snafuToDecimal(snafu: Char): Int {
    return when (snafu) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> throw UnsupportedOperationException("$snafu is not a valid SNAFU digit")
    }
}
