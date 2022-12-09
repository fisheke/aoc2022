package day8

import java.io.File

val red = "\u001b[31m"
val reset = "\u001b[0m"

fun main() {
    println("Day 8")

//    solve("/othertest.input")
    solve("/test.input")
    solve("/real.input")
}

private fun solve(input: String) {
    val lines = File({}.javaClass.getResource(input).toURI()).readLines()

    val maxX = lines.size - 1
    val maxY = lines.map { it.length - 1 }.max()

    val trees: Array<Array<Tree>> = Array(maxX + 1) { x ->
        Array(maxY + 1) { y ->
            Tree(x, y, Integer.parseInt(lines[x].substring(y, y + 1)))
        }
    }

    println(trees.flatten().count { isVisible(it, trees, maxX, maxY) })

    println(trees.flatten()
            .map { score(it, trees, maxX, maxY, false) }.max())

}

data class Tree(val x: Int, val y: Int, val height: Int) {}

fun printTree(tree: Tree, trees: Array<Array<Tree>>, maxX: Int, maxY: Int) {
    if (isVisible(tree, trees, maxX, maxY)) {
        print(red + tree.height + reset + " (" + score(tree, trees, maxX, maxY, false) + ") ");
    } else {
        print(tree.height.toString() + "     ")
    }
}

fun isVisible(tree: Tree, trees: Array<Array<Tree>>, maxX: Int, maxY: Int): Boolean {
    if (tree.x == 0 || tree.y == 0 || tree.x == maxX || tree.y == maxY) {
        return true
    }
    if (trees[tree.x].filter { it.y > tree.y }.all { it.height < tree.height }) {
        return true;
    }
    if (trees[tree.x].filter { it.y < tree.y }.all { it.height < tree.height }) {
        return true;
    }
    if (trees.map { it[tree.y] }.filter { it.x > tree.x }.all { it.height < tree.height }) {
        return true;
    }
    if (trees.map { it[tree.y] }.filter { it.x < tree.x }.all { it.height < tree.height }) {
        return true;
    }
    return false
}

fun score(tree: Tree, trees: Array<Array<Tree>>, maxX: Int, maxY: Int, print: Boolean): Int {
    if (print) println("Eval " + tree)

    val col = trees.map { it[tree.y] }.map { it.height }
    if (print) print("Looking on the col - " + col)
    // lookup up
    val up = score(tree.height, col.slice(tree.x - 1 downTo 0).toIntArray())
    // looking down
    val down = score(tree.height, col.slice(tree.x + 1..maxX).toIntArray())

    if (print) println(" -> up: " + up + " down " + down);

    val row = trees[tree.x].map { it.height }
    if (print) print("Looking on the row - " + row)
    // looking left
    val left = score(tree.height, row.slice(tree.y - 1 downTo 0).toIntArray())
    // looking right
    val right = score(tree.height, row.slice(tree.y + 1..maxY).toIntArray())
    if (print) println(" -> left: " + left + " right " + right);


    return up * down * left * right;
}

fun score(myHeight: Int, lookingAt: IntArray): Int {
    var counter = 0;
    for (lookingAtNow in lookingAt) {
        if (lookingAtNow >= myHeight) {
            counter++
            return counter;
        } else {
            counter++;
        }
    }
    return counter;
}


