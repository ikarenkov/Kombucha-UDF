package tools.forma.target

open class FormaTarget(val path: String) {

    open val name: String = path.substringAfterLast(":")

}

class FormaMimicTarget(path: String, mimicTarget: TargetTemplate) : FormaTarget(path) {

    override val name: String = "${super.name}-${mimicTarget.suffix}"

}