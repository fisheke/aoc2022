import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Query
import org.neo4j.driver.Result
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName

fun main() {
    val grid = loadGrid()

    val createStatements = grid.map { "(:Square {x: ${it.x}, y: ${it.y}, z: ${it.height}})" }
        .joinToString(", ", "CREATE ")

    println(createStatements)

    val listStatement = "MATCH (n:Square {x:4, y:3}) RETURN n"
    val neo = startNeo()
    writeInNeo(createStatements, neo.first) {println(it.consume()) }
    readFromNeo(listStatement, neo.first) { it.list().map { r -> r.toString() }}.forEach { println(it) }
    grid.forEach { createRelationsFor(it, grid, neo) }

    readFromNeo("MATCH (from:Square {x:0, y:0}), (to:Square {x:5, y:2}), p = shortestPath((from)-[]-(to)) RETURN p", neo.first) { println(it.consume()) }

    neo.second.stop()

}

fun getNodeFor(x: Int, y: Int, grid: List<Node>): Node? {
    return grid.find { it.x == x && it.y == y }
}

fun createRelationsFor(it: Node, grid: List<Node>, neo: Pair<Driver, Neo4jContainer<*>>) {
    // can I go up?
    createRelationshipFromTo(grid, neo, it.x, it.y, it.x, it.y - 1, it.height, "NORTH")

    // can I go down?
    createRelationshipFromTo(grid, neo, it.x, it.y, it.x, it.y + 1, it.height, "SOUTH")

    // can I go left?
    createRelationshipFromTo(grid, neo, it.x, it.y, it.x - 1, it.y, it.height, "WEST")

    // can I go right?
    createRelationshipFromTo(grid, neo, it.x, it.y, it.x + 1, it.y, it.height, "EAST")
}

private fun createRelationshipFromTo(grid: List<Node>, neo: Pair<Driver, Neo4jContainer<*>>, fromX: Int, fromY: Int, toX: Int, toY: Int, fromHeight: Int, type: String) {
    val up = getNodeFor(toX, toY, grid)?.height
    if (up != null && up <= fromHeight + 1) {
        writeInNeo("MATCH (from:Square {x: $fromX, y:$fromY}), (to:Square {x: $toX, y:$toY}) CREATE (from)-[:$type]->(to)", neo.first) { println(it.consume()) };
    }
}

data class Node(val x: Int, val y: Int, val height: Int, val isStart: Boolean, val isEnd: Boolean)

fun loadGrid(): List<Node> {
    val input = load("day12/test.input")
    val inputLines = input.readLines()

    val rows = inputLines.size
    val cols = inputLines[0].length

    println("Grid of " + cols + "x" + rows)

    val nodes = mutableListOf<Node>()

    (0 until rows).forEach { row ->
        (0 until cols).forEach { col ->
            val height = height(inputLines[row][col])
            print(("" + height).padEnd(3, ' '))
            nodes.add(Node(col, row, height, inputLines[row][col] == 'S', inputLines[row][col] == 'E'))

        }
        println()
    }
    return nodes
}

fun height(input: Char): Int {
    if (input == 'S') {
        return 'a'.code - 97
    }
    if (input == 'E') {
        return 'z'.code - 97
    }
    return input.code - 97
}

fun startNeo() : Pair<Driver, Neo4jContainer<*>> {

    var neo4jContainer = Neo4jContainer(DockerImageName.parse("neo4j:4.4"))
        .withoutAuthentication()

    neo4jContainer.start()
    neo4jContainer.followOutput { println(it.utf8String) }

    var driver = GraphDatabase.driver(neo4jContainer.getBoltUrl());

    return Pair(driver, neo4jContainer)
}

fun <T> writeInNeo(stmt: String, driver: Driver, extractor: (Result) -> T): T {
    return driver.session().use { session ->
        session.writeTransaction { tx ->
//            val query = Query("CREATE (a:Greeting) SET a.message = \$message RETURN a.message + ', from node ' + id(a)", parameters("message", "testing"))
            val result = tx.run(Query(stmt))
            extractor(result)
        }
    }
}
fun <T> readFromNeo(stmt: String, driver: Driver, extractor: (Result) -> T): T {
    return driver.session().use { session ->
        session.readTransaction() { tx ->
            val result = tx.run(Query(stmt))
            extractor(result)
        }
    }
}
