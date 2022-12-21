import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    println("Day 21")

    val inputValues = mutableMapOf<String, Long>()
    val inputDependencies = mutableMapOf<String, Set<String>>()
    val inputOperations = mutableMapOf<String, Triple<String, Calc, String>>()

    load("day21/real.input")
        .forEachLine {
            val parts = it.split(": ")
            if (parts[1].all { it.isDigit() }) {
                inputValues[parts[0]] = parts[1].toLong()
            } else {
                val operationParts = parts[1].split(" ")
                inputDependencies[parts[0]] = hashSetOf(operationParts[0], operationParts[2])
                inputOperations[parts[0]] = Triple(operationParts[0], calcOf(operationParts[1]), operationParts[2])
            }
        }

    println(inputValues)
    println(inputDependencies)
    println(inputOperations)

    val values = inputValues.toMap()
    val dependencies = inputDependencies.toMap()
    val operations = inputOperations.toMap()

//    solveRange(0L..100000L, values, dependencies, operations)

//    solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), 1_844_674_407_370)
//    solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), 2_000_000_000_000)
//    solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), 3_000_000_000_000)
//    solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), 3_379_022_190_350)
//
//    (3_379_022_190_300..3_379_022_190_399).forEach { solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), it) }

    println(Long.MAX_VALUE / 2)

//    runBinarySearchIteratively(0L, 0, Long.MAX_VALUE) { solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), it) }
//    binary search doesn't work -> non-linear solution?
}

fun runBinarySearchIteratively(key: Long, low: Long, high: Long, solve: (Long) -> Long): Long {
    var low = low
    var high = high
    var index = Long.MAX_VALUE
    while (low <= high) {
        val mid = low + (high - low) / 2
        val solution = solve(mid)
        if (solution < key) {
            low = mid + 1
        } else if (solution > key) {
            high = mid - 1
        } else if (solution == key) {
            index = mid
            break
        }
    }
    return index
}

private fun solveRange(
    range: LongRange,
    values: Map<String, Long>,
    dependencies: Map<String, Set<String>>,
    operations: Map<String, Triple<String, Calc, String>>
) {
    if (range.first == range.last) {
        throw UnsupportedOperationException("$range is empty")
    }

    println(range)
    val parts = splitRange(range)

    val pickPoint = pickPoint(
        parts.first,
        parts.second,
        solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), middle(parts.first)),
        solveAndPrint(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), middle(parts.second)),
        0L
    )

    solveRange(pickPoint, values, dependencies, operations);
}

private fun solveAndPrint(
    values: Map<String, Long>,
    dependencies: Map<String, Set<String>>,
    operations: Map<String, Triple<String, Calc, String>>,
    it: Long
): Long {
    val solutions = solveFor(values.toMutableMap(), dependencies.toMutableMap(), operations.toMutableMap(), it)
    val diff = solutions.first - solutions.second
    println("$it -> ${"%,d".format(diff)} (${solutions.third})")

    if (diff == 0L) {
        throw RuntimeException("Solution is $it")
    }

    return diff
}


fun splitRange(originalRange: LongRange): Pair<LongRange, LongRange> {
    val new = middle(originalRange)

    return Pair(originalRange.first..new, new..originalRange.last)
}

fun middle(originalRange: LongRange) = (originalRange.first + originalRange.last) / 2

fun pickPoint(range1: LongRange, range2: LongRange, range1Result: Long, range2Result: Long, target: Long): LongRange {
    if (abs(range1Result - target) >= abs(range2Result - target)) {
        return range1
    } else {
        return range2
    }

}

private fun solveFor(
    values: MutableMap<String, Long>,
    dependencies: MutableMap<String, Set<String>>,
    operations: MutableMap<String, Triple<String, Calc, String>>,
    human: Long
): Triple<Long, Long, Long> {
    var solution: Pair<Long, Long>
    val tome = measureTimeMillis {

        val rootOperation = operations.get("root")!!
        val first = rootOperation.first
        val second = rootOperation.third

        values["humn"] = human

        //    var iteration = 0
        while (!values.containsKey("root")) {
            val solvable = dependencies.filter { it.value.all { values.containsKey(it) } }.map { it.key }

            //        iteration++
            //        println("Iteration $iteration: ${solvable.size} solvable")

            solvable.forEach {
                val operation = operations[it]!!
                val result = operation.second.execute(values[operation.first]!!, values[operation.third]!!)
                values[it] = result
            }

            solvable.forEach { dependencies.remove(it) }
            solvable.forEach { operations.remove(it) }
        }

//        println(values["humn"])
//        println(values[first])
//        println(values[second])

        solution = Pair(values[first]!!, values[second]!!)
    }

    return Triple(solution.first, solution.second, tome)
}

fun calcOf(input: String): Calc {
    return when (input) {
        "*" -> Calc.MULTIPLY
        "+" -> Calc.ADD
        "-" -> Calc.SUBSTRACT
        "/" -> Calc.DIVIDE
        else -> throw UnsupportedOperationException("$input is not a valid Calc operation")
    }
}

enum class Calc(val execute: (i1: Long, i2: Long) -> Long) {
    ADD({ a, b -> a + b }),
    SUBSTRACT({ a, b -> a - b }),
    MULTIPLY({ a, b -> a * b }),
    DIVIDE({ a, b -> a / b })
}
