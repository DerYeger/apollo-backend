package eu.yeger.gramofo.fol.parser

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.Settings
import eu.yeger.gramofo.model.domain.fol.*
import eu.yeger.gramofo.model.domain.fol.Function

typealias ParserResult = Result<FormulaHead, String>

fun parseFormula(formula: String, language: Language = English): ParserResult {
    return FOLParser(language).parseFormula(formula)
}

/**
 * This class provides a singleton object, which can parse input strings into
 * data structures. It works like a recursive descent parser.
 */
private class FOLParser(private val language: Language) {
    private val symbolTable: HashMap<String, String> = HashMap()
    private var curBoundedVars: HashSet<BoundVariable>? = null

    private fun addOperatorsToSymbolTable() {
        for (symbol in Settings[Settings.TRUE]) {
            symbolTable[symbol] = "TT"
        }
        for (symbol in Settings[Settings.FALSE]) {
            symbolTable[symbol] = "FF"
        }
        for (symbol in Settings[Settings.OR]) {
            symbolTable[symbol] = "OR"
        }
        for (symbol in Settings[Settings.AND]) {
            symbolTable[symbol] = "AND"
        }
        for (symbol in Settings[Settings.NOT]) {
            symbolTable[symbol] = "NOT"
        }
        for (symbol in Settings[Settings.IMPLICATION]) {
            symbolTable[symbol] = "IMP"
        }
        for (symbol in Settings[Settings.BI_IMPLICATION]) {
            symbolTable[symbol] = "BIIMP"
        }
        for (symbol in Settings[Settings.EXISTS]) {
            symbolTable[symbol] = "EX"
        }
        for (infixFunc in Settings[Settings.FOR_ALL]) {
            symbolTable[infixFunc] = "FOR"
        }
        for (infixPred in Settings[Settings.INFIX_PRED]) {
            symbolTable[infixPred] = "P-2"
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
    fun parseFormula(sFormula: String): Result<FormulaHead, String> {
        symbolTable.clear()
        curBoundedVars = HashSet()
        return try {
            addOperatorsToSymbolTable()
            val scanner = FOLScanner(sFormula, language)
            val formula = parseFormula(scanner)
            if (scanner.curType() != FOLToken.END_OF_SOURCE) {
                throw ParseException(language.getString("FOP_END_OF_INPUT", scanner.curValue(), scanner.restOfText))
            }
            Ok(FormulaHead(formula, symbolTable))
        } catch (e: ParseException) {
            Err(e.message)
        }
    }

    // Formula ::= Biimpl
    @Throws(ParseException::class)
    private fun parseFormula(scanner: FOLScanner): Formula {
        return parseBiImplication(scanner)
    }

    // Biimpl ::= Impl ['<->' Impl]* 
    @Throws(ParseException::class)
    private fun parseBiImplication(scanner: FOLScanner): Formula {
        var biimpl = parseImplication(scanner)
        while (scanner.curType() == FOLToken.BI_IMPLICATION) {
            scanner.nextToken()
            val impl = parseImplication(scanner)
            biimpl = Operator.Binary.BiImplication(biimpl, impl)
        }
        return biimpl
    }

    // Impl ::= Or ['->' Or]* 
    @Throws(ParseException::class)
    private fun parseImplication(scanner: FOLScanner): Formula {
        var impl = parseOr(scanner)
        while (scanner.curType() == FOLToken.IMPLICATION) {
            scanner.nextToken()
            val or = parseOr(scanner)
            impl = Operator.Binary.Implication(impl, or)
        }
        return impl
    }

    // Or ::= And ['||' And]* 
    @Throws(ParseException::class)
    private fun parseOr(scanner: FOLScanner): Formula {
        var or = parseAnd(scanner)
        while (scanner.curType() == FOLToken.OR) {
            scanner.nextToken()
            val and = parseAnd(scanner)
            or = Operator.Binary.Or(or, and)
        }
        return or
    }

    // And ::= UnaryOperator ['&&' UnaryOperator]* 
    @Throws(ParseException::class)
    private fun parseAnd(scanner: FOLScanner): Formula {
        var and = parseUnaryOperator(scanner)
        while (scanner.curType() == FOLToken.AND) {
            scanner.nextToken()
            val unaryOperator = parseUnaryOperator(scanner)
            and = Operator.Binary.And(and, unaryOperator)
        }
        return and
    }

    // UnaryOperator ::= Operand | ['-' | 'forall' VarSymbol | 'exists' VarSymbol] UnaryOperator
    @Throws(ParseException::class)
    private fun parseUnaryOperator(scanner: FOLScanner): Formula {
        val unaryOperator: Formula
        val variable: BoundVariable
        return when (scanner.curType()) {
            FOLToken.NOT -> {
                scanner.nextToken()
                unaryOperator = parseUnaryOperator(scanner)
                Operator.Unary.Not(unaryOperator)
            }
            FOLToken.FOR_ALL -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                Quantifier.Universal(variable, unaryOperator)
            }
            FOLToken.EXISTS -> {
                scanner.nextToken()
                variable = parseVarSymbol(scanner)
                curBoundedVars!!.add(variable)
                unaryOperator = parseUnaryOperator(scanner)
                curBoundedVars!!.remove(variable)
                Quantifier.Existential(variable, unaryOperator)
            }
            else -> parseOperand(scanner)
        }
    }

    // VarSymbol: Symbol with first lower case letter
    @Throws(ParseException::class)
    private fun parseVarSymbol(scanner: FOLScanner): BoundVariable {
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
        return BoundVariable(symbol)
    }

    // Operand ::= Predicate | Constant | '(' Formula ')' | '.' Formula
    // Constant ::= True | False
    @Throws(ParseException::class)
    private fun parseOperand(scanner: FOLScanner): Formula {
        val formula: Formula
        return when (scanner.curType()) {
            FOLToken.TRUE -> {
                scanner.nextToken()
                Constant.True()
            }
            FOLToken.FALSE -> {
                scanner.nextToken()
                Constant.False()
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
    private fun parsePredicate(scanner: FOLScanner): Formula {
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
    private fun parseNormalPredicate(scanner: FOLScanner): Formula {
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
                termChildren.add(parseNormalTerm(scanner))
                while (scanner.curType() == FOLToken.COMMA) {
                    scanner.nextToken()
                    termChildren.add(parseNormalTerm(scanner))
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
            1 -> Relation.Unary(name = symbol, term = children[0])
            2 -> Relation.Binary(name = symbol, firstTerm = children[0], secondTerm = children[1], isInfix = false)
            else -> throw ParseException(language.getString("FOP_RELATION_INVALID_CHILDREN", symbol, children.size))
        }
    }

    // InfixPredicate ::= Term InfixPred Term
    @Throws(ParseException::class)
    private fun parseInfixPredicate(scanner: FOLScanner): Formula {
        val leftOperand = parseNormalTerm(scanner)
        if (!(scanner.curType() == FOLToken.INFIX_PRED || scanner.curType() == FOLToken.EQUAL_SIGN)) {
            throw ParseException(language.getString("FOP_INFIX_EXPECTED", leftOperand.name, scanner.curValue()))
        } // else
        val symbol = scanner.curValue()
        val symbolType = "P-" + 2
        checkSymbolInfo(symbol, symbolType)
        scanner.nextToken()
        val rightOperand = parseNormalTerm(scanner)
        return Relation.Binary(
            name = symbol,
            firstTerm = leftOperand,
            secondTerm = rightOperand,
            isInfix = true
        )
    }

    // NormalTerm ::='(' infixTerm ')' | Variable | FuncSymbol '(' ((InfixTerm) [',' InfixTerm]* )? ')'
    @Throws(ParseException::class)
    private fun parseNormalTerm(scanner: FOLScanner): Term {
        if (scanner.curType() == FOLToken.BRACKET && scanner.curValue() == "(") {
            scanner.nextToken()
            val termNode = parseNormalTerm(scanner)
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
                termChildren.add(parseNormalTerm(scanner))
                while (scanner.curType() == FOLToken.COMMA) {
                    scanner.nextToken()
                    termChildren.add(parseNormalTerm(scanner))
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
                0 -> Function.Constant(symbol)
                1 -> Function.Unary(symbol, termChildren.first())
                else -> throw ParseException(language.getString("FOP_FUNCTION_TOO_MANY_CHILDREN", symbol, termChildren.size))
            }
        } else if (termChildren.size == 0) {
            BoundVariable(symbol)
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

    private fun containsSymbol(curBoundedVars: HashSet<BoundVariable>?, symbol: String): Boolean {
        return getForSymbol(curBoundedVars, symbol) !== null
    }

    private fun getForSymbol(curBoundedVars: HashSet<BoundVariable>?, symbol: String): BoundVariable? {
        return curBoundedVars?.firstOrNull { curBoundedVar -> curBoundedVar.name == symbol }
    }
}
