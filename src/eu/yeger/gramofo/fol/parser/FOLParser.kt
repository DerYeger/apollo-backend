package eu.yeger.gramofo.fol.parser

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.Settings
import eu.yeger.gramofo.fol.formula.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

typealias ParserResult = Result<FOLFormulaHead, String>

fun parseFormula(formula: String, language: Language = English): ParserResult {
    return FOLParser(language).parseFormula(formula)
}

/**
 * This class provides a singleton object, which can parse input strings into
 * data structures. It works like a recursive descent parser.
 */
private class FOLParser(private val language: Language) {
    private val symbolTable: HashMap<String, String> = HashMap()
    private var curBoundedVars: HashSet<FOLBoundVariable>? = null

    private fun addOperatorsToSymbolTable() {
        for (infixFunc in Settings[Settings.TRUE]) {
            symbolTable[infixFunc] = "TT"
        }
        for (infixFunc in Settings[Settings.FALSE]) {
            symbolTable[infixFunc] = "FF"
        }
        for (infixFunc in Settings[Settings.OR]) {
            symbolTable[infixFunc] = "OR"
        }
        for (infixFunc in Settings[Settings.AND]) {
            symbolTable[infixFunc] = "AND"
        }
        for (infixFunc in Settings[Settings.NOT]) {
            symbolTable[infixFunc] = "NOT"
        }
        for (infixFunc in Settings[Settings.IMPLICATION]) {
            symbolTable[infixFunc] = "IMP"
        }
        for (infixFunc in Settings[Settings.BI_IMPLICATION]) {
            symbolTable[infixFunc] = "BIIMP"
        }
        for (infixFunc in Settings[Settings.EXISTS]) {
            symbolTable[infixFunc] = "EX"
        }
        for (infixFunc in Settings[Settings.FOR_ALL]) {
            symbolTable[infixFunc] = "FOR"
        }
        for (infixPred in Settings[Settings.INFIX_PRED]) {
            symbolTable[infixPred] = "P-2"
        }
        for (infixFunc in Settings[Settings.INFIX_FUNC]) {
            symbolTable[infixFunc] = "F-2"
        }
        for (infixFunc in Settings[Settings.EQUAL_SIGN]) {
            symbolTable[infixFunc] = "P-2"
        }
    }

    /**
     * Parses one formula given as string to FormulaSet
     * @param sFormula string to be parsed
     * @return a ParseResult containing either the result or an error message
     */
    fun parseFormula(sFormula: String): Result<FOLFormulaHead, String> {
        symbolTable.clear()
        curBoundedVars = HashSet()
        return try {
            addOperatorsToSymbolTable()
            val scanner = FOLScanner(sFormula, language)
            val formula = parseFormula(scanner)
            if (scanner.curType() != FOLToken.END_OF_SOURCE) {
                throw ParseException(language.getString("FOP_END_OF_INPUT", scanner.curValue(), scanner.restOfText))
            }
            Ok(FOLFormulaHead(formula, symbolTable))
        } catch (e: ParseException) {
            Err(e.message)
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
            biimpl = FOLOperator.Binary.BiImplication(biimpl, impl)
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
            impl = FOLOperator.Binary.Implication(impl, or)
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
            or = FOLOperator.Binary.Or(or, and)
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
            and = FOLOperator.Binary.And(and, unaryOperator)
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
                FOLOperator.Unary.Not(unaryOperator)
            }
            FOLToken.FOR_ALL -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLQuantifier.Universal(variable, unaryOperator)
            }
            FOLToken.EXISTS -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                FOLQuantifier.Existential(variable, unaryOperator)
            }
            else -> parseOperand(scanner)
        }
    }

    // VarSymbol: Symbol with first lower case letter
    @Throws(ParseException::class)
    private fun parseVarSymbol(scanner: FOLScanner): FOLBoundVariable {
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw ParseException(language.getString("FOP_VARIABLE_EXPECTED", scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        if (symbol[0] !in 'a'..'z') {
            throw ParseException(language.getString("FOP_VARIABLE_LOWER_CASE"))
        }
        checkSymbolInfo(symbol, "V")
        if (containsSymbol(curBoundedVars, symbol)) {
            throw ParseException(language.getString("FOP_VARIABLE_ALREADY_BOUND", symbol))
        }
        scanner.nextToken()
        return FOLBoundVariable(symbol)
    }

    // Operand ::= Predicate | Constant | '(' Formula ')' | '.' Formula
    // Constant ::= True | False
    @Throws(ParseException::class)
    private fun parseOperand(scanner: FOLScanner): FOLFormula {
        val formula: FOLFormula
        return when (scanner.curType()) {
            FOLToken.TRUE -> {
                scanner.nextToken()
                FOLConstant.True()
            }
            FOLToken.FALSE -> {
                scanner.nextToken()
                FOLConstant.False()
            }
            FOLToken.BRACKET -> {
                if (scanner.curValue() == ")") {
                    throw ParseException(language.getString("FOP_EXTRA_CLOSING_BRACKET"))
                } // else
                scanner.nextToken()
                formula = parseFormula(scanner)
                if (scanner.curType() != FOLToken.BRACKET || scanner.curValue() != ")") {
                    throw ParseException(language.getString("FOP_MISSING_CLOSING_BRACKET"))
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
            throw ParseException(language.getString("FOP_MISSING_OPERATOR", scanner.curValue()))
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
        val termChildren = mutableSetOf<Term>()
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
                    throw ParseException(language.getString("FOP_MISSING_OPERATOR", symbol))
                }
                scanner.nextToken()
            }
        } // else no opening bracket -> no term		
        val symbolType = "P-" + termChildren.size
        checkSymbolInfo(symbol, symbolType)
        val children = termChildren.toList()
        return when (children.size) {
            1 -> FOLPredicate.Unary(name = symbol, term = children[0])
            2 -> FOLPredicate.Binary(name = symbol, firstTerm = children[0], secondTerm = children[1], isInfix = false)
            else -> throw ParseException(language.getString("FOP_RELATION_INVALID_CHILDREN", symbol, children.size))
        }
    }

    // InfixPredicate ::= Term InfixPred Term
    @Throws(ParseException::class)
    private fun parseInfixPredicate(scanner: FOLScanner): FOLFormula {
        val leftOperand = parseInfixTerm(scanner)
        if (!(scanner.curType() == FOLToken.INFIX_PRED || scanner.curType() == FOLToken.EQUAL_SIGN)) {
            throw ParseException(language.getString("FOP_INFIX_EXPECTED", leftOperand.name, scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        val symbolType = "P-" + 2
        checkSymbolInfo(symbol, symbolType)
        scanner.nextToken()
        val rightOperand = parseInfixTerm(scanner)
        return FOLPredicate.Binary(
            name = symbol,
            firstTerm = leftOperand,
            secondTerm = rightOperand,
            isInfix = true
        )
    }

    // InfixTerm ::= NormalTerm [infixFunc NormalTerm]*
    @Throws(ParseException::class)
    private fun parseInfixTerm(scanner: FOLScanner): Term {
        var infixTerm = parseNormalTerm(scanner)
        while (scanner.curType() == FOLToken.INFIX_FUNC) {
            val symbol = scanner.curValue()
            checkSymbolInfo(symbol, "F-2")
            scanner.nextToken()
            val normalTerm = parseNormalTerm(scanner)
            infixTerm = FOLFunction.Infix(
                name = symbol,
                leftOperand = infixTerm,
                rightOperand = normalTerm
            )
        }
        return infixTerm
    }

    // NormalTerm ::='(' infixTerm ')' | Variable | FuncSymbol '(' ((InfixTerm) [',' InfixTerm]* )? ')'
    @Throws(ParseException::class)
    private fun parseNormalTerm(scanner: FOLScanner): Term {
        if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == "(") {
            scanner.nextToken()
            val termNode = parseInfixTerm(scanner)
            return if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == ")") {
                scanner.nextToken()
                termNode.hasBrackets = true
                termNode
            } else {
                throw ParseException(language.getString("FOP_CLOSING_BRACKET_EXPECTED", scanner.curValue()))
            }
        } // else
        if (scanner.curType() != FOLToken.SYMBOL) {
            throw ParseException(language.getString("FOP_FUNCTION_EXPECTED", scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        if (symbol[0] !in 'a'..'z') {
            throw ParseException(language.getString("FOP_FUNCTION_LOWER_CASE"))
        }
        scanner.nextToken()
        val termChildren = mutableSetOf<Term>()
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
                    throw ParseException(language.getString("FOP_MISSING_PARAMETER_LIST_CLOSING_BRACKET", symbol))
                }
                scanner.nextToken()
            }
        } // else not opening bracket -> no term		
        return if (!containsSymbol(curBoundedVars, symbol)) {
            checkSymbolInfo(symbol, "F-" + termChildren.size)
            when (termChildren.size) {
                0 -> FOLFunction.Constant(symbol)
                1 -> FOLFunction.Unary(symbol, termChildren.first())
                else -> throw ParseException(language.getString("FOP_FUNCTION_TOO_MANY_CHILDREN", symbol, termChildren.size))
            }
        } else if (termChildren.size == 0) {
            FOLBoundVariable(symbol)
        } else {
            throw ParseException(language.getString("FOP_SYMBOL_ALREADY_IN_USE_AS_BOUND_VARIABLE", symbol))
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
            "TT" -> language.getString("FOP_OPERATOR_TT")
            "FF" -> language.getString("FOP_OPERATOR_FF")
            "OR" -> language.getString("FOP_OPERATOR_OR")
            "AND" -> language.getString("FOP_OPERATOR_AND")
            "NOT" -> language.getString("FOP_OPERATOR_NOT")
            "IMP" -> language.getString("FOP_OPERATOR_IMP")
            "BIIMP" -> language.getString("FOP_OPERATOR_BIIMP")
            "EX" -> language.getString("FOP_OPERATOR_EX")
            "FOR" -> language.getString("FOP_OPERATOR_FOR")
            "P" -> language.getString("FOP_OPERATOR_P", infos[1])
            "F" -> language.getString("FOP_OPERATOR_F", infos[1])
            "V" -> language.getString("FOP_OPERATOR_V")
            else -> language.getString("FOP_OPERATOR_ERROR")
        }
        throw ParseException(language.getString("FOP_SYMBOL_ALREADY_IN_USE_AS", symbol, info))
    }

    private fun containsSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): Boolean {
        return getForSymbol(curBoundedVars, symbol) !== null
    }

    private fun getForSymbol(curBoundedVars: HashSet<FOLBoundVariable>?, symbol: String): FOLBoundVariable? {
        return curBoundedVars?.firstOrNull { curBoundedVar -> curBoundedVar.name == symbol }
    }
}
