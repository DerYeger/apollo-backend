package eu.yeger.gramofo.fol

import eu.yeger.gramofo.fol.Lang.getString
import eu.yeger.gramofo.fol.formula.*
import java.util.*

/**
 * This class provides a singleton object, which can parse input strings into
 * data structures. It works like a recursive descent parser.
 */
class FOLParser {
    val settings: Settings = Settings()
    private val symbolTable: HashMap<String, String> = HashMap()
    private var curBoundedVars: HashSet<FOLBoundVariable>? = null
    private fun addOperatorsToSymbolTable() {
        for (infixFunc in settings.getSetting(Settings.TRUE)) {
            symbolTable[infixFunc] = "TT"
        }
        for (infixFunc in settings.getSetting(Settings.FALSE)) {
            symbolTable[infixFunc] = "FF"
        }
        for (infixFunc in settings.getSetting(Settings.OR)) {
            symbolTable[infixFunc] = "OR"
        }
        for (infixFunc in settings.getSetting(Settings.AND)) {
            symbolTable[infixFunc] = "AND"
        }
        for (infixFunc in settings.getSetting(Settings.NOT)) {
            symbolTable[infixFunc] = "NOT"
        }
        for (infixFunc in settings.getSetting(Settings.IMPLICATION)) {
            symbolTable[infixFunc] = "IMP"
        }
        for (infixFunc in settings.getSetting(Settings.BIIMPLICATION)) {
            symbolTable[infixFunc] = "BIIMP"
        }
        for (infixFunc in settings.getSetting(Settings.EXISTS)) {
            symbolTable[infixFunc] = "EX"
        }
        for (infixFunc in settings.getSetting(Settings.FORALL)) {
            symbolTable[infixFunc] = "FOR"
        }
        for (infixPred in settings.getSetting(Settings.INFIX_PRED)) {
            symbolTable[infixPred] = "P-2"
        }
        for (infixFunc in settings.getSetting(Settings.INFIX_FUNC)) {
            symbolTable[infixFunc] = "F-2"
        }
        for (infixFunc in settings.getSetting(Settings.EQUAL_SIGN)) {
            symbolTable[infixFunc] = "P-2"
        }
    }

    /**
     * Parses one formula given as string to FormulaSet
     * @param sFormula string to be parsed
     * @return a ParseResult containing either the result or an error message
     */
    fun parseFormula(sFormula: String): ParseResult<FOLFormulaHead> {
        symbolTable.clear()
        curBoundedVars = HashSet()
        return try {
            checkSettings()
            addOperatorsToSymbolTable()
            val scanner = FOLScanner(sFormula, settings)
            val formula = parseFormula(scanner)
            if (scanner.curType() != FOLToken.END_OF_SOURCE) {
                throw ParseException(getString("FOP_END_OF_INPUT", scanner.curValue(), scanner.restOfText))
            }
            ParseResult(FOLFormulaHead(formula, symbolTable))
        } catch (e: ParseException) {
            ParseResult(e.message)
        }
    }

    @Throws(ParseException::class)
    private fun checkSettings() {
        if (settings.errorMessage != null) {
            throw ParseException(settings.errorMessage)
        }
    }

    // Formula ::= Biimpl
    @Throws(ParseException::class)
    private fun parseFormula(scanner: FOLScanner): FOLFormula {
        return parseBiImplication(scanner)
    }

    // Biimpl ::= Impl ['<->' Impl]* 
    @Throws(ParseException::class)
    private fun parseBiImplication(scanner: FOLScanner): FOLFormula {
        var biimpl = parseImplication(scanner)
        while (scanner.curType() == FOLToken.BI_IMPLICATION) {
            scanner.nextToken()
            val impl = parseImplication(scanner)
            biimpl = FOLFactory.createOperatorBiimplication(biimpl, impl, hasBrackets = false, hasDot = false)
        }
        return biimpl
    }

    // Impl ::= Or ['->' Or]* 
    @Throws(ParseException::class)
    private fun parseImplication(scanner: FOLScanner): FOLFormula {
        var impl = parseOr(scanner)
        while (scanner.curType() == FOLToken.IMPLICATION) {
            scanner.nextToken()
            val or = parseOr(scanner)
            impl = FOLFactory.createOperatorImplication(impl, or, hasBrackets = false, hasDot = false)
        }
        return impl
    }

    // Or ::= And ['||' And]* 
    @Throws(ParseException::class)
    private fun parseOr(scanner: FOLScanner): FOLFormula {
        var or = parseAnd(scanner)
        while (scanner.curType() == FOLToken.OR) {
            scanner.nextToken()
            val and = parseAnd(scanner)
            or = FOLFactory.createOperatorOr(or, and, hasBrackets = false, hasDot = false)
        }
        return or
    }

    // And ::= UnaryOperator ['&&' UnaryOperator]* 
    @Throws(ParseException::class)
    private fun parseAnd(scanner: FOLScanner): FOLFormula {
        var and = parseUnaryOperator(scanner)
        while (scanner.curType() == FOLToken.AND) {
            scanner.nextToken()
            val unaryOperator = parseUnaryOperator(scanner)
            and = FOLFactory.createOperatorAnd(and, unaryOperator, hasBrackets = false, hasDot = false)
        }
        return and
    }

    // UnaryOperator ::= Operand | ['-' | 'forall' VarSymbol | 'exists' VarSymbol] UnaryOperator
    @Throws(ParseException::class)
    private fun parseUnaryOperator(scanner: FOLScanner): FOLFormula {
        val unaryOperator: FOLFormula
        val variable: FOLBoundVariable
        return when (scanner.curType()) {
            FOLToken.NOT -> {
                scanner.nextToken()
                unaryOperator = parseUnaryOperator(scanner)
                FOLFactory.createOperatorNot(unaryOperator, hasBrackets = false, hasDot = false)
            }
            FOLToken.FOR_ALL -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLFactory.createQuantifierForall(variable, unaryOperator, hasBrackets = false, hasDot = false)
            }
            FOLToken.EXISTS -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLFactory.createQuantifierExists(variable, unaryOperator, hasBrackets = false, hasDot = false)
            }
            else -> parseOperand(scanner)
        }
    }

    // VarSymbol: Symbol with first lower case letter
    @Throws(ParseException::class)
    private fun parseVarSymbol(scanner: FOLScanner): FOLBoundVariable {
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw ParseException(getString("FOP_VARIABLE_EXPECTED", scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        if (symbol[0] !in 'a'..'z') {
            throw ParseException(getString("FOP_VARIABLE_LOWER_CASE"))
        }
        checkSymbolInfo(symbol, "V")
        if (containsSymbol(curBoundedVars, symbol)) {
            throw ParseException(getString("FOP_VARIABLE_ALREADY_BOUND", symbol))
        }
        scanner.nextToken()
        return FOLFactory.createBoundVariable(symbol)
    }

    // Operand ::= Predicate | Constant | '(' Formula ')' | '.' Formula
    // Constant ::= True | False
    @Throws(ParseException::class)
    private fun parseOperand(scanner: FOLScanner): FOLFormula {
        val formula: FOLFormula
        return when (scanner.curType()) {
            FOLToken.TRUE -> {
                scanner.nextToken()
                FOLFactory.createTrueConstant()
            }
            FOLToken.FALSE -> {
                scanner.nextToken()
                FOLFactory.createFalseConstant()
            }
            FOLToken.BRACKET -> {
                if (scanner.curValue() == ")") {
                    throw ParseException(getString("FOP_EXTRA_CLOSING_BRACKET"))
                } // else
                scanner.nextToken()
                formula = parseFormula(scanner)
                if (scanner.curType() != FOLToken.BRACKET || scanner.curValue() != ")") {
                    throw ParseException(getString("FOP_MISSING_CLOSING_BRACKET"))
                } // else
                formula.hasBrackets = true
                scanner.nextToken()
                formula
            }
            FOLToken.DOT -> {
                scanner.nextToken()
                formula = parseFormula(scanner)
                formula.hasDot = true
                formula
            }
            else -> parsePredicate(scanner)
        }
    }

    // Predicate ::= NormalPredicate | InfixPredicate
    @Throws(ParseException::class)
    private fun parsePredicate(scanner: FOLScanner): FOLFormula {
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw ParseException(getString("FOP_MISSING_OPERATOR", scanner.curValue()))
        }
        val symbol = scanner.curValue()
        // its not yet read, because scanner.nextToken isn't called

        // if it starts with a big letter it is a Predicate
        // else it is a term and must be followed by an infix-Predicate
        return if (symbol[0] in 'A'..'Z') {
            parseNormalPredicate(scanner)
        } else {
            parseInfixPredicate(scanner)
        }
    }

    // NormalPredicate ::= (  PredSymbol '(' ((Term) [',' Term]* )?  ')'  ) 
    @Throws(ParseException::class)
    private fun parseNormalPredicate(scanner: FOLScanner): FOLFormula {
        val symbol = scanner.curValue()
        scanner.nextToken()
        val termChildren = LinkedHashSet<FOLFormula>()
        if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == "(") {
            scanner.nextToken()
            if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == ")") {
                // OK predicate end
                scanner.nextToken()
            } else {
                // parse terms
                termChildren.add(parseInfixTerm(scanner))
                while (scanner.curType() == FOLToken.COMMA) {
                    scanner.nextToken()
                    termChildren.add(parseInfixTerm(scanner))
                }
                if (scanner.curType() != FOLToken.BRACKET || scanner.curValue() != ")") {
                    throw ParseException(getString("FOP_MISSING_OPERATOR", symbol))
                }
                scanner.nextToken()
            }
        } // else no opening bracket -> no term		
        val symbolType = "P-" + termChildren.size
        checkSymbolInfo(symbol, symbolType)
        return FOLFactory.createPredicate(termChildren, hasBrackets = false, hasDot = false, name = symbol)
    }

    // InfixPredicate ::= Term InfixPred Term
    @Throws(ParseException::class)
    private fun parseInfixPredicate(scanner: FOLScanner): FOLFormula {
        val leftOperand = parseInfixTerm(scanner)
        if (!(scanner.curType() == FOLToken.INFIX_PRED || scanner.curType() == FOLToken.EQUAL_SIGN)) {
            throw ParseException(
                getString("FOP_INFIX_EXPECTED", leftOperand.name, scanner.curValue())
            )
        } // else
        val symbol = scanner.curValue()
        val symbolType = "P-" + 2
        checkSymbolInfo(symbol, symbolType)
        scanner.nextToken()
        val rightOperand = parseInfixTerm(scanner)
        return FOLFactory.createInfixPredicate(
            leftOperand,
            rightOperand,
            hasBrackets = false,
            hasDot = false,
            name = symbol
        )
    }

    // InfixTerm ::= NormalTerm [infixFunc NormalTerm]* 
    @Throws(ParseException::class)
    private fun parseInfixTerm(scanner: FOLScanner): FOLFormula {
        var infixTerm = parseNormalTerm(scanner)
        while (scanner.curType() == FOLToken.INFIX_FUNC) {
            val symbol = scanner.curValue()
            checkSymbolInfo(symbol, "F-2")
            scanner.nextToken()
            val normalTerm = parseNormalTerm(scanner)
            infixTerm = FOLFactory.createInfixFunction(
                infixTerm,
                normalTerm,
                hasBrackets = false,
                hasDot = false,
                name = symbol
            )
        }
        return infixTerm
    }

    // NormalTerm ::='(' infixTerm ')' | Variable | FuncSymbol '(' ((InfixTerm) [',' InfixTerm]* )? ')'
    @Throws(ParseException::class)
    private fun parseNormalTerm(scanner: FOLScanner): FOLFormula {
        if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == "(") {
            scanner.nextToken()
            val termNode = parseInfixTerm(scanner)
            return if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == ")") {
                scanner.nextToken()
                termNode.hasBrackets = true
                termNode
            } else {
                throw ParseException(getString("FOP_CLOSING_BRACKET_EXPECTED", scanner.curValue()))
            }
        } // else
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw ParseException(getString("FOP_FUNCTION_EXPECTED", scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        if (symbol[0] !in 'a'..'z') {
            throw ParseException(getString("FOP_FUNCTION_LOWER_CASE"))
        }
        scanner.nextToken()
        val termChildren = LinkedHashSet<FOLFormula>()
        if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == "(") {
            scanner.nextToken()
            if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == ")") {
                // OK Function end
                scanner.nextToken()
            } else {
                // parse terms
                termChildren.add(parseInfixTerm(scanner))
                while (scanner.curType() == FOLToken.COMMA) {
                    scanner.nextToken()
                    termChildren.add(parseInfixTerm(scanner))
                }
                if (scanner.curType() != FOLToken.BRACKET || scanner.curValue() != ")") {
                    throw ParseException(getString("FOP_MISSING_PARAMETER_LIST_CLOSING_BRACKET", symbol))
                }
                scanner.nextToken()
            }
        } // else not opening bracket -> no term		
        return if (!containsSymbol(curBoundedVars, symbol)) {
            checkSymbolInfo(symbol, "F-" + termChildren.size)
            FOLFactory.createFunction(termChildren, hasBrackets = false, hasDot = false, name = symbol)
        } else if (termChildren.size == 0) {
            val forSymbol = getForSymbol(curBoundedVars, symbol)
            if (forSymbol != null) {
                FOLFactory.createBoundVariable(symbol, forSymbol)
            } else FOLFactory.createBoundVariable(symbol)
        } else {
            throw ParseException(getString("FOP_SYMBOL_ALREADY_IN_USE_AS_BOUND_VARIABLE", symbol))
        }
    }

    /**
     * throws a ParseException, if the symbol is already in use
     *
     * (adds a a info-string for printing)
     * @param symbol the symbol to be checked
     */
    @Throws(ParseException::class)
    private fun checkSymbolInfo(symbol: String, symbolType: String) {
        if (!symbolTable.containsKey(symbol) || symbolTable[symbol] == symbolType) {
            symbolTable[symbol] = symbolType
            return // everything all right
        } // else

        // error handling:
        val infos = symbolTable[symbol]!!.split("-".toRegex()).toTypedArray()
        val info: String = when (infos[0]) {
            "TT" -> getString("FOP_OPERATOR_TT")
            "FF" -> getString("FOP_OPERATOR_FF")
            "OR" -> getString("FOP_OPERATOR_OR")
            "AND" -> getString("FOP_OPERATOR_AND")
            "NOT" -> getString("FOP_OPERATOR_NOT")
            "IMP" -> getString("FOP_OPERATOR_IMP")
            "BIIMP" -> getString("FOP_OPERATOR_BIIMP")
            "EX" -> getString("FOP_OPERATOR_EX")
            "FOR" -> getString("FOP_OPERATOR_FOR")
            "P" -> getString("FOP_OPERATOR_P", infos[1])
            "F" -> getString("FOP_OPERATOR_F", infos[1])
            "V" -> getString("FOP_OPERATOR_V")
            else -> getString("FOP_OPERATOR_ERROR")
        }
        throw ParseException(getString("FOP_SYMBOL_ALREADY_IN_USE_AS", symbol, info))
    }

    private fun containsSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): Boolean {
        return getForSymbol(curBoundedVars, symbol) !== null
    }

    private fun getForSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): FOLFormula? {
        for (curBoundedVar in curBoundedVars!!) {
            if (curBoundedVar.name == symbol) {
                return curBoundedVar
            }
        }
        return null
    }
}
