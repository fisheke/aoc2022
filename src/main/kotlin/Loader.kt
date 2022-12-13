import java.io.File

fun load(input: String): File {
    return File({}.javaClass.getResource(input).toURI());
}
