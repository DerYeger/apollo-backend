package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLBoundVariable(name: String) : FOLFormula(FOLType.Variable, false, false, name) {
    val uuid: UUID = UUID.randomUUID()
    var quantorSymbolUuid: UUID? = null
        private set

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
