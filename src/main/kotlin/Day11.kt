import java.math.BigInteger

fun main() {
    println("Day 11")

    val input = load("/day11/real.input")

    val monkeys = input.readLines().windowed(6, 7)
        .map { makeMonkey(it) }
        .associateBy { it.name }

    monkeys.forEach { println(it) }

    var holdings = monkeys.mapValues { it.value.items.toMutableList() }.toMutableMap()

    val fold = monkeys.values.map { it.test.divisor }.fold(BigInteger.ONE) { a, b -> a.multiply(b) }
    println(fold)


    val inspections = mutableMapOf<String, Long>()

    (0..10_000).forEach {round ->
        println(round + 1)
        monkeys.values.forEach {monkey ->
            val throwTo = mutableMapOf<String, MutableList<BigInteger>>()
            holdings.get(monkey.name)?.forEach {item ->
                val afterInspection = monkey.operation.apply(item)
//                val afterGettingBored = afterInspection / 3

                val afetFold = afterInspection % fold

                val eval = monkey.test.test(afetFold)

                val recipient = if (eval) {
                    monkey.test.recipientWhenTrue
                } else {
                    monkey.test.recipientWhenFalse
                }

                throwTo.putIfAbsent(recipient, mutableListOf())
                throwTo.get(recipient)?.add(afetFold)

                inspections.set(monkey.name, inspections.getOrDefault(monkey.name, 0) + 1);

            }
            val throws = throwTo
            holdings.put(monkey.name, mutableListOf())
            throws.forEach {
                holdings.get(it.key)?.addAll(it.value)
            }
        }

        holdings.forEach { println(it) }


        inspections.forEach { println(it) }

        println("Monkey Business: " + inspections.values.sortedDescending().take(2)[0] * inspections.values.sortedDescending().take(2)[1])
    }


    val highest = inspections.values.sortedDescending().take(2)

    println(highest[0] * highest[1])


}

data class Monkey(val name: String, var items: List<BigInteger>, val operation: Operation, val test: Test)

fun operationOf(input: String): Operation {
    if (input.startsWith("old * old")) {
        return Operation { it * it };
    }
    if (input.startsWith("old * ")) {
        return Operation { it.times(input.removePrefix("old * ").toBigInteger()) };
    }
    if (input.startsWith("old + ")) {
        return Operation { it.plus(input.removePrefix("old + ").toBigInteger()) };
    }
    throw UnsupportedOperationException("Cannot parse " + input)
}

data class Operation(val apply: (input: BigInteger) -> BigInteger)

data class Test(val divisor: BigInteger, val recipientWhenTrue: String, val recipientWhenFalse: String) {
    constructor(string: String, whenTrue: String, whenFalse: String) : this(
        string.removePrefix("divisible by ").toBigInteger(),
        whenTrue,
        whenFalse
    )

    fun test(input: BigInteger): Boolean {
        return input.mod(divisor) == BigInteger.ZERO
    }
}

fun makeMonkey(inputs: List<String>): Monkey {
    return Monkey(
        inputs[0].removeSuffix(":"),
        inputs[1].substringAfter("Starting items: ").split(",")
            .map { it.trim() }
            .map { it.toBigInteger() },
        operationOf(
            inputs[2].substringAfter("Operation: ").removePrefix("new = ")
        ),
        Test(
            inputs[3].substringAfter("Test: "),
            "Monkey " + inputs[4].substringAfter("monkey ").toInt(),
            "Monkey " + inputs[5].substringAfter("monkey ").toInt()
        )
    )
}

