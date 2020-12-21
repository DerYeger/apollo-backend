package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLBoundVariable internal constructor(name: String?) :
    FOLFormula(FOLType.Variable, LinkedHashSet(), false, false, name) {
    val uuid: UUID = UUID.randomUUID()
    var quantorSymbolUuid: UUID? = null
        private set

    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        return name
    }

    override fun toString(): String {
        return name
    }

    fun withQuantorSymbol(quantorSymbol: FOLFormula?): FOLBoundVariable {
        if (quantorSymbol is FOLBoundVariable) {
            quantorSymbolUuid = quantorSymbol.uuid
        }
        return this
    }
}
