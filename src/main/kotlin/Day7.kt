import java.io.File

fun main(args: Array<String>) {
    println("day 7")
    solve("/test.input")
    solve("/real.input")
}

var cwd = "/";
val tree:MutableMap<String, Long> = HashMap()

private fun solve(inputFile: String) {
    cwd = "/";
    tree.clear();

    File({}.javaClass.getResource(inputFile).toURI()).forEachLine { process(it) }

    val totalSpace = 70_000_000
    val requiredSpace = 30_000_000
    val usedSpace = getSize("/", tree)
    val freespace = totalSpace - usedSpace
    val missingspace = requiredSpace - freespace
    println("total space: $totalSpace")
    println("used space: $usedSpace")
    println("free space: $freespace")
    println("missing space: $missingspace")

    val first = getDirectories(tree)
            .map { it to getSize(it, tree) }.toMap()
            .filter { it.value >= missingspace }
            .toList()
            .sortedBy { it.second }
            .first()

    println("best matching dir: $first")
}

fun getSize(dir: String, tree: MutableMap<String, Long>): Long {
    return tree
            .entries
            .filter { it.key.startsWith(dir) }
            .sumOf { it.value }
}


fun getDirectories(tree: MutableMap<String, Long>): List<String> {
    return tree.entries
            .map { it.key }
            .map { it.substringBeforeLast("/") }
            .flatMap { split(it) }
            .map { "$it/" }
            .distinct()
}

fun split(it: String): List<String> {
    val parts = it.split("/")

    val parents = mutableListOf<String>()
    for (idx in 0..parts.size) {
        parents.add(parts.subList(0, idx).joinToString("/"))
    }
    return parents;
}

fun process(it: String) {
    if (it.startsWith("$")) {
        val command = it.removePrefix("$ ")
        if (command.startsWith("cd ")) {
            val dir = command.removePrefix("cd ")
            if (dir.equals("/")) {
                cwd = "/";
            } else if (dir.equals("..")) {
                cwd = cwd.substring(0, cwd.removeSuffix("/").lastIndexOf("/")) + "/";
            } else {
                cwd = cwd + dir + "/"
            }
        }
    } else {
        if (!it.startsWith("dir")) {
            val size = it.split(" ")[0].toLong();
            val name = it.split(" ")[1];

            tree.put(cwd + name, size);
        }
    }
}

