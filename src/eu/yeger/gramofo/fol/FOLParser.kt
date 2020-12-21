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
    private var errorMessageHTMLMode = false
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
     * Parse a term given as string
     * @param sTerm string to be parsed
     * @param symbolList list with already assigned symbols. If a symbol is already in use with a different meaning,
     * the parser will stop with an error message.
     * @return a ParseResult containing either the result or an error message
     */
    fun parseTerm(sTerm: String, symbolList: HashMap<String?, String?>?): ParseResult<FOLFormula> {
        symbolTable.clear()
        curBoundedVars = HashSet()
        val errorMessage: String? = null
        errorMessageHTMLMode = false
        return try {
            checkSettings()
            addOperatorsToSymbolTable()
            val scanner = FOLScanner(sTerm, settings)
            val termNode = parseInfixTerm(scanner)
            if (scanner.curType() != FOLToken.END_OF_SOURCE) {
                throw createParseException(
                    getString("FOP_INPUT_ERROR_1", scanner.curValue(), scanner.restOfText),
                    scanner
                )
            }
            ParseResult(termNode)
        } catch (e: ParseException) {
            ParseResult(e.message)
        }
    }

    /**
     * Parses one formula given as string to FormulaSet
     * @param sFormula string to be parsed
     * @param errorAsHTML format error messages as html
     * @return a ParseResult containing either the result or an error message
     */
    fun parseFormula(sFormula: String, errorAsHTML: Boolean): ParseResult<FOLFormulaHead> {
        symbolTable.clear()
        curBoundedVars = HashSet()
        var errorMessage: String? = null
        errorMessageHTMLMode = errorAsHTML
        return try {
            checkSettings()
            addOperatorsToSymbolTable()
            val scanner = FOLScanner(sFormula, settings)
            val formula = parseFormula(scanner)
            if (scanner.curType() != FOLToken.END_OF_SOURCE) {
                throw createParseException(
                    getString("FOP_INPUT_ERROR_1", scanner.curValue(), scanner.restOfText),
                    scanner
                )
            }
            ParseResult(FOLFormulaHead(formula, symbolTable))
        } catch (e: ParseException) {
            errorMessage = if (errorAsHTML) surroundWithHTMLHeader(e.message) else e.message
            ParseResult(errorMessage)
        }
    }

    @Throws(ParseException::class)
    private fun checkSettings() {
        if (settings == null) {
            throw ParseException(getString("MISSING_SETTINGS"))
        } else if (settings.errorMessage != null) {
            throw ParseException(settings.errorMessage)
        }
    }

    @Throws(ParseException::class)
    private fun parseFormulaSet(source: String): LinkedHashSet<FOLFormulaHead> {
        val formulaSet = LinkedHashSet<FOLFormulaHead>()
        val scanner = FOLScanner(source, settings)
        curBoundedVars = HashSet()
        if (scanner.curType() == FOLToken.END_OF_SOURCE) {
            return formulaSet
        }
        if (scanner.curType() == FOLToken.COMMA) {
            throw createParseException(getString("FOP_INPUT_ERROR_2"), scanner)
        }
        while (scanner.curType() != FOLToken.END_OF_SOURCE) {
            formulaSet.add(FOLFormulaHead(parseFormula(scanner), symbolTable))
            if (scanner.curType() == FOLToken.COMMA) {
                if (scanner.lookAHeadType() == FOLToken.END_OF_SOURCE) {
                    throw createParseException(getString("FOP_INPUT_ERROR_3"), scanner)
                } else {
                    scanner.nextToken()
                }
            } else if (scanner.curType() == FOLToken.END_OF_SOURCE) {
                break
            } else {
                if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == ")") {
                    throw createParseException(getString("FOP_INPUT_ERROR_4"), scanner)
                } // else
                throw createParseException(getString("FOP_INPUT_ERROR_5"), scanner)
            }
        }
        return formulaSet
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
            biimpl = FOLFactory.createOperatorBiimplication(biimpl, impl, false, false)
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
            impl = FOLFactory.createOperatorImplication(impl, or, false, false)
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
            or = FOLFactory.createOperatorOr(or, and, false, false)
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
            and = FOLFactory.createOperatorAnd(and, unaryOperator, false, false)
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
                FOLFactory.createOperatorNot(unaryOperator, false, false)
            }
            FOLToken.FOR_ALL -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLFactory.createQuantifierForall(variable, unaryOperator, false, false)
            }
            FOLToken.EXISTS -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLFactory.createQuantifierExists(variable, unaryOperator, false, false)
            }
            else -> parseOperand(scanner)
        }
    }

    // VarSymbol: Symbol with first lower case letter
    @Throws(ParseException::class)
    private fun parseVarSymbol(scanner: FOLScanner): FOLBoundVariable {
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw createParseException(getString("FOP_INPUT_ERROR_6", scanner.curValue()), scanner)
        } // else
        val symbol = scanner.curValue()
        if (!(symbol[0] >= 'a' && symbol[0] <= 'z')) {
            throw createParseException(getString("FOP_INPUT_ERROR_7"), scanner)
        }
        checkSymbolInfo(symbol, "V", scanner)
        if (containsSymbol(curBoundedVars, symbol)) {
            throw createParseException(getString("FOP_INPUT_ERROR_8", symbol), scanner)
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
                    throw createParseException(getString("FOP_INPUT_ERROR_4"), scanner)
                } // else
                scanner.nextToken()
                formula = parseFormula(scanner)
                if (scanner.curType() != FOLToken.BRACKET || scanner.curValue() != ")") {
                    throw createParseException(getString("FOP_INPUT_ERROR_9"), scanner)
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
            throw createParseException(getString("FOP_INPUT_ERROR_10", scanner.curValue()), scanner)
        }
        val symbol = scanner.curValue()
        // its not yet read, because scanner.nextToken isn't called

        // if it starts with a big letter it is a Predicate
        // else it is a term and must be followed by an infix-Predicate
        return if (symbol[0] >= 'A' && symbol[0] <= 'Z') {
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
                    throw createParseException(getString("FOP_INPUT_ERROR_10", symbol), scanner)
                }
                scanner.nextToken()
            }
        } // else no opening bracket -> no term		
        val symbolType = "P-" + termChildren.size
        checkSymbolInfo(symbol, symbolType, scanner)
        return FOLFactory.createPredicate(termChildren, false, false, symbol)
    }

    // InfixPredicate ::= Term InfixPred Term
    @Throws(ParseException::class)
    private fun parseInfixPredicate(scanner: FOLScanner): FOLFormula {
        val leftOperand = parseInfixTerm(scanner)
        if (!(scanner.curType() == FOLToken.INFIX_PRED || scanner.curType() == FOLToken.EQUAL_SIGN)) {
            throw createParseException(
                getString("FOP_INPUT_ERROR_12", leftOperand.name, scanner.curValue()),
                scanner
            )
        } // else
        val symbol = scanner.curValue()
        val symbolType = "P-" + 2
        checkSymbolInfo(symbol, symbolType, scanner)
        scanner.nextToken()
        val rightOperand = parseInfixTerm(scanner)
        return FOLFactory.createInfixPredicate(leftOperand, rightOperand, false, false, symbol)
    }

    // InfixTerm ::= NormalTerm [infixFunc NormalTerm]* 
    @Throws(ParseException::class)
    private fun parseInfixTerm(scanner: FOLScanner): FOLFormula {
        var infixTerm = parseNormalTerm(scanner)
        while (scanner.curType() == FOLToken.INFIX_FUNC) {
            val symbol = scanner.curValue()
            checkSymbolInfo(symbol, "F-2", scanner)
            scanner.nextToken()
            val normalTerm = parseNormalTerm(scanner)
            infixTerm = FOLFactory.createInfixFunction(infixTerm, normalTerm, false, false, symbol)
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
                throw createParseException(
                    getString("FOP_INPUT_ERROR_13", scanner.curValue()),
                    scanner
                )
            }
        } // else
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw createParseException(getString("FOP_INPUT_ERROR_14", scanner.curValue()), scanner)
        } // else
        val symbol = scanner.curValue()
        if (!(symbol[0] >= 'a' && symbol[0] <= 'z')) {
            throw createParseException(getString("FOP_INPUT_ERROR_15"), scanner)
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
                    throw createParseException(getString("FOP_INPUT_ERROR_11", symbol), scanner)
                }
                scanner.nextToken()
            }
        } // else not opening bracket -> no term		
        return if (!containsSymbol(curBoundedVars, symbol)) {
            checkSymbolInfo(symbol, "F-" + termChildren.size, scanner)
            FOLFactory.createFunction(termChildren, false, false, symbol)
        } else if (termChildren.size == 0) {
            val forSymbol = getForSymbol(curBoundedVars, symbol)
            if (forSymbol != null) {
                FOLFactory.createBoundVariable(symbol, forSymbol)
            } else FOLFactory.createBoundVariable(symbol)
        } else {
            throw createParseException(getString("FOP_INPUT_ERROR_16", symbol), scanner)
        }
    }

    /**
     * throws a ParseException, if the symbol is already in use
     *
     * (adds a a info-string for printing)
     * @param symbol the symbol to be checked
     */
    @Throws(ParseException::class)
    private fun checkSymbolInfo(symbol: String, symbolType: String, scanner: FOLScanner) {
        if (!symbolTable.containsKey(symbol) || symbolTable[symbol] == symbolType) {
            symbolTable[symbol] = symbolType
            return // everything all right
        } // else

        // error handling:
        val infos = symbolTable[symbol]!!.split("-".toRegex()).toTypedArray()
        val info: String
        info = when (infos[0]) {
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
        throw createParseException(getString("FOP_INPUT_ERROR_17", symbol, info), scanner)
    }

    private fun containsSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): Boolean {
        return if (getForSymbol(curBoundedVars, symbol) != null) {
            true
        } else false
    }

    private fun getForSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): FOLFormula? {
        for (curBoundedVar in curBoundedVars!!) {
            if (curBoundedVar.name == symbol) {
                return curBoundedVar
            }
        }
        return null
    }

    /**
     * @param scanner
     * @return
     */
    private fun createParseException(message: String, scanner: FOLScanner): ParseException {
        val sb = StringBuilder()
        sb.append(message)
        if (errorMessageHTMLMode) {
            sb.append("\n<br>\n")
            sb.append("<font class=\"error\">")
            sb.append(scanner.curValue())
            sb.append("</font>")
            sb.append(scanner.restOfText)
        }
        return ParseException(sb.toString())
    }

    companion object {
        fun surroundWithHTMLHeader(content: String?): String {
            val errorMessage: String
            val sb = StringBuilder()
            sb.append("<html>").append("\n")
            sb.append("  <head>").append("\n")
            sb.append("    <style type=\"text/css\">").append("\n")
            sb.append("      html {").append("\n")
            sb.append("        font-family: Noto Sans;").append("\n")
            sb.append("        font-size: 15px;").append("\n")
            sb.append("        font-weight: bold;").append("\n")
            sb.append("      }").append("\n")
            sb.append("      .error {").append("\n")
            sb.append("        color: red;").append("\n")
            sb.append("        font-weight: bold;").append("\n")
            sb.append("      }").append("\n")
            sb.append("    </style>").append("\n")
            sb.append("  </head>").append("\n")
            sb.append("  <body>").append("\n")
            sb.append(content).append("\n")
            sb.append("  </body>").append("\n")
            sb.append("</html>").append("\n")
            errorMessage = sb.toString()
            return errorMessage
        }
    }
}
