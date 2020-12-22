package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLBoundVariable(name: String) : FOLFormula(
    type = FOLType.Variable,
    name = name
) {
    val uuid: UUID = UUID.randomUUID()
    private var quantorSymbolUuid: UUID? = null

    fun withQuantorSymbol(quantorSymbol: FOLFormula): FOLBoundVariable {
        if (quantorSymbol is FOLBoundVariable) {
            quantorSymbolUuid = quantorSymbol.uuid
        }
        return this
    }
}
