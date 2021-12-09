package tools.forma.target

class FormaTarget(val path: String) {

    val name: String = path.substringAfterLast(":")

}