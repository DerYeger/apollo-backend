package eu.yeger.gramofo.fol;

import eu.yeger.gramofo.fol.formula.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * This class provides a singleton object, which can parse input strings into
 * data structures. It works like a recursive descent parser.
 */
public class FOLParser {

	private Settings settings;
	private HashMap<String, String> symbolTable;
	private HashSet<FOLBoundVariable> curBoundedVars;
	private boolean errorMessageHTMLMode;

	/**
	 * use singleton() instead of constructor
	 */
	public FOLParser() {
		settings = new Settings("settings/operators.config");
		symbolTable = new HashMap<String, String>();
	}

	private void addOperatorsToSymboltable() {
		
		for(String infixFunc: settings.getSetting(Settings.TRUE)){
			symbolTable.put(infixFunc, "TT");
		}
		for(String infixFunc: settings.getSetting(Settings.FALSE)){
			symbolTable.put(infixFunc, "FF");
		}
		for(String infixFunc: settings.getSetting(Settings.OR)){
			symbolTable.put(infixFunc, "OR");
		}
		for(String infixFunc: settings.getSetting(Settings.AND)){
			symbolTable.put(infixFunc, "AND");
		}
		for(String infixFunc: settings.getSetting(Settings.NOT)){
			symbolTable.put(infixFunc, "NOT");
		}
		for(String infixFunc: settings.getSetting(Settings.IMPLICATION)){
			symbolTable.put(infixFunc, "IMP");
		}
		for(String infixFunc: settings.getSetting(Settings.BIIMPLICATION)){
			symbolTable.put(infixFunc, "BIIMP");
		}
		for(String infixFunc: settings.getSetting(Settings.EXISTS)){
			symbolTable.put(infixFunc, "EX");
		}
		for(String infixFunc: settings.getSetting(Settings.FORALL)){
			symbolTable.put(infixFunc, "FOR");
		}
		for(String infixPred: settings.getSetting(Settings.INFIX_PRED)){
			symbolTable.put(infixPred, "P-2");
		}
		for(String infixFunc: settings.getSetting(Settings.INFIX_FUNC)){
			symbolTable.put(infixFunc, "F-2");
		}
		for(String infixFunc: settings.getSetting(Settings.EQUAL_SIGN)){
			symbolTable.put(infixFunc, "P-2");
		}
	}
	
	
	/**
	 * Parse a term given as string
	 * @param sTerm string to be parsed
	 * @param symbolList list with already assigned symbols. If a symbol is already in use with a different meaning,
	 *                   the parser will stop with an error message.
	 * @return a ParseResult containing either the result or an error message
	 */
	public ParseResult<FOLFormula> parseTerm(String sTerm, HashMap<String, String> symbolList){
		symbolTable.clear();
		curBoundedVars = new HashSet<>();
		String errorMessage = null;
		errorMessageHTMLMode = false;
		
		try {
			checkSettings();
			addOperatorsToSymboltable();
			FOLScanner scanner = new FOLScanner(sTerm, settings);
			FOLFormula termNode = parseInfixTerm(scanner);
			if(scanner.curType() != FOLToken.END_OF_SOURCE){
				throw createParseException(
						Lang.getString("FOP_INPUT_ERROR_1", scanner.curValue(), scanner.getRestOfText()),
						scanner);
			}
			
			return new ParseResult<>(termNode);
		} catch (ParseException e) {
			return new ParseResult<>(e.getMessage());
		}
	}

	/**
	 * Parses one formula given as string to FormulaSet
	 * @param sFormula string to be parsed
	 * @param errorAsHTML format error messages as html
	 * @return a ParseResult containing either the result or an error message
	 */
	public ParseResult<FOLFormulaHead> parseFormula(String sFormula, boolean errorAsHTML){
		symbolTable.clear();
		curBoundedVars = new HashSet<>();
		String errorMessage = null;
		errorMessageHTMLMode = errorAsHTML;

		try {
			checkSettings();
			addOperatorsToSymboltable();
			FOLScanner scanner = new FOLScanner(sFormula, settings);
			FOLFormula formula = parseFormula(scanner);
			if(scanner.curType() != FOLToken.END_OF_SOURCE){
				throw createParseException(
						Lang.getString("FOP_INPUT_ERROR_1", scanner.curValue(), scanner.getRestOfText()),
						scanner);
			}

			return new ParseResult<FOLFormulaHead>(new FOLFormulaHead(formula, symbolTable));
		} catch (ParseException e) {
			errorMessage = errorAsHTML ? surroundWithHTMLHeader(e.getMessage()) : e.getMessage();
			return new ParseResult<FOLFormulaHead>(errorMessage);
		}
	}


	/**
	 * Parses a sequence with antecedent and succedent as string. The several
	 * sub-formulas are expected to be separated by ;'s.
	 * @param antecedentSource antecedent as String
	 * @param succedentSource succedent as String
	 * @return a ParseResult containing either the result or an error message
	 */
	public ParseResult<FOLSequence> parseSequence(String antecedentSource, String succedentSource) {
		
		symbolTable.clear();
		curBoundedVars = new HashSet<>();
		String errorMessage = null;
		errorMessageHTMLMode = true;
		FOLSequence sequence = new FOLSequence();

		try {
			checkSettings();
			addOperatorsToSymboltable();
			sequence.setAntecedent(parseFormulaSet(antecedentSource));         //parse!!
			sequence.setSuccedent(parseFormulaSet(succedentSource));           //parse!!
		} catch (ParseException e) {
			// setup nice error message
			errorMessage = surroundWithHTMLHeader(e.getMessage());
		}
		
		symbolTable.clear();
		if(errorMessage == null) {
			return new ParseResult<FOLSequence>(sequence);
		} else {
			return new ParseResult<FOLSequence>(errorMessage);
		}
	}

	public static String surroundWithHTMLHeader(String content) {
		String errorMessage;
		StringBuilder sb = new StringBuilder();

		sb.append("<html>"                           ).append("\n");
		sb.append("  <head>"                         ).append("\n");
		sb.append("    <style type=\"text/css\">"    ).append("\n");
		sb.append("      html {"                     ).append("\n");
		sb.append("        font-family: Noto Sans;"  ).append("\n");
		sb.append("        font-size: 15px;"         ).append("\n");
		sb.append("        font-weight: bold;"       ).append("\n");
		sb.append("      }"                          ).append("\n");
		sb.append("      .error {"                   ).append("\n");
		sb.append("        color: red;"              ).append("\n");
		sb.append("        font-weight: bold;"       ).append("\n");
		sb.append("      }"                          ).append("\n");
		sb.append("    </style>"                     ).append("\n");
		sb.append("  </head>"                        ).append("\n");
		sb.append("  <body>"                         ).append("\n");
		sb.append(     content                       ).append("\n");
		sb.append("  </body>"                        ).append("\n");
		sb.append("</html>"                          ).append("\n");

		errorMessage = sb.toString();
		return errorMessage;
	}

	private void checkSettings() throws ParseException {
		if(settings == null) {
            throw new ParseException(Lang.getString("MISSING_SETTINGS"));
        } else if (settings.getErrorMessage() != null){
            throw new ParseException(settings.getErrorMessage());
        }
	}


	private LinkedHashSet<FOLFormulaHead> parseFormulaSet(String source) throws ParseException{
		LinkedHashSet<FOLFormulaHead> formulaSet = new LinkedHashSet<>();

		FOLScanner scanner = new FOLScanner(source, settings);
		curBoundedVars = new HashSet<>();


		if(scanner.curType() == FOLToken.END_OF_SOURCE){
			return formulaSet;
		}

		if(scanner.curType() == FOLToken.COMMA){
			throw createParseException(Lang.getString("FOP_INPUT_ERROR_2"),	scanner);
		}


		while(scanner.curType() != FOLToken.END_OF_SOURCE){
			formulaSet.add(new FOLFormulaHead(parseFormula(scanner), symbolTable));

			if(scanner.curType() == FOLToken.COMMA){

				if(scanner.lookAHeadType() == FOLToken.END_OF_SOURCE){
					throw createParseException(Lang.getString("FOP_INPUT_ERROR_3"),	scanner);
				} else {
					scanner.nextToken();
				}

			} else if(scanner.curType() == FOLToken.END_OF_SOURCE){
				break;
			} else {
				if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals(")"))){
					throw createParseException(Lang.getString("FOP_INPUT_ERROR_4"),	scanner);
				}//else
				throw createParseException(Lang.getString("FOP_INPUT_ERROR_5"),	scanner);
			}
		}

		return formulaSet;
	}



	//Formula ::= Biimpl
	private FOLFormula parseFormula(FOLScanner scanner) throws ParseException {
		return parseBiImplication(scanner);
	}

	//Biimpl ::= Impl ['<->' Impl]* 
	private FOLFormula parseBiImplication(FOLScanner scanner) throws ParseException {
		FOLFormula biimpl = parseImplication(scanner);

		while(scanner.curType() == FOLToken.BIIMPLICATION){
			scanner.nextToken();
			FOLFormula impl = parseImplication(scanner);
			biimpl = FOLFactory.createOperatorBiimplication(biimpl, impl, false, false);
		}
		return biimpl;
	}

	//Impl ::= Or ['->' Or]* 
	private FOLFormula parseImplication(FOLScanner scanner) throws ParseException {
		FOLFormula impl = parseOr(scanner);

		while(scanner.curType() == FOLToken.IMPLICATION){
			scanner.nextToken();
			FOLFormula or = parseOr(scanner);
			impl = FOLFactory.createOperatorImplication(impl, or, false, false);
		}
		return impl;
	}

	//Or ::= And ['||' And]* 
	private FOLFormula parseOr(FOLScanner scanner) throws ParseException {
		FOLFormula or = parseAnd(scanner);

		while(scanner.curType() == FOLToken.OR){
			scanner.nextToken();
			FOLFormula and = parseAnd(scanner);
			or = FOLFactory.createOperatorOr(or, and, false, false);
		}
		return or;
	}

	//And ::= UnaryOperator ['&&' UnaryOperator]* 
	private FOLFormula parseAnd(FOLScanner scanner) throws ParseException {
		FOLFormula and = parseUnaryOperator(scanner);

		while(scanner.curType() == FOLToken.AND){
			scanner.nextToken();
			FOLFormula unaryOperator = parseUnaryOperator(scanner);
			and = FOLFactory.createOperatorAnd(and, unaryOperator, false, false);
		}
		return and;
	}

	//UnaryOperator ::= Operand | ['-' | 'forall' VarSymbol | 'exists' VarSymbol] UnaryOperator
	private FOLFormula parseUnaryOperator(FOLScanner scanner) throws ParseException {
		FOLFormula unaryOperator;
		FOLBoundVariable variable;

		switch(scanner.curType()){
		case FOLToken.NOT:
			scanner.nextToken();
			unaryOperator = parseUnaryOperator(scanner);
			return FOLFactory.createOperatorNot(unaryOperator, false, false);

		case FOLToken.FORALL:
			scanner.nextToken();
			variable = parseVarSymbol(scanner); 
			curBoundedVars.add(variable);
			unaryOperator = parseUnaryOperator(scanner);
			curBoundedVars.remove(variable);
			return FOLFactory.createQuantifierForall(variable, unaryOperator, false, false);

		case FOLToken.EXISTS:
			scanner.nextToken();
			variable = parseVarSymbol(scanner); 
			curBoundedVars.add(variable);
			unaryOperator = parseUnaryOperator(scanner);
			curBoundedVars.remove(variable);
			return FOLFactory.createQuantifierExists(variable, unaryOperator, false, false);

		default:
			return parseOperand(scanner);
		}


	}

	//VarSymbol: Symbol with first lower case letter
	private FOLBoundVariable parseVarSymbol(FOLScanner scanner) throws ParseException {
		if(!(scanner.curType() == FOLToken.SYMBOL) ){
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_6", scanner.curValue()), scanner);
		} //else

		String symbol = scanner.curValue();

		if( !(symbol.charAt(0) >= 'a' && symbol.charAt(0) <= 'z') ){
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_7"), scanner);
		}
		
		checkSymbolInfo(symbol, "V", scanner);
		
		if(containsSymbol(curBoundedVars, symbol)){
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_8", symbol), scanner);
		}
		
		scanner.nextToken();		
		return FOLFactory.createBoundVariable(symbol);
	}

	//Operand ::= Predicate | Constant | '(' Formula ')' | '.' Formula
	//Constant ::= True | False
	private FOLFormula parseOperand(FOLScanner scanner) throws ParseException {
		FOLFormula formula;

		switch(scanner.curType()){
		case FOLToken.TRUE:
			scanner.nextToken();
			return FOLFactory.createTrueConstant();

		case FOLToken.FALSE:
			scanner.nextToken();
			return FOLFactory.createFalseConstant();

		case FOLToken.BRACKET:
			if(scanner.curValue().equals(")")){
				throw createParseException(Lang.getString("FOP_INPUT_ERROR_4"),	scanner);
			}//else
			scanner.nextToken();
			formula = parseFormula(scanner);
			if(!(scanner.curType() == FOLToken.BRACKET) || !(scanner.curValue().equals(")"))){
				throw createParseException(Lang.getString("FOP_INPUT_ERROR_9"),	scanner);
			}//else
			formula.setHasBrackets(true);
			scanner.nextToken();
			return formula;

		case FOLToken.DOT:
			scanner.nextToken();
			formula = parseFormula(scanner);
			formula.setHasDot(true);
			return formula;
			
		default:
			return parsePredicate(scanner);
		}
	}
	
	
	

	//Predicate ::= NormalPredicate | InfixPredicate
	private FOLFormula parsePredicate(FOLScanner scanner) throws ParseException {

		if(scanner.curType() != FOLToken.SYMBOL){
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_10", scanner.curValue()), scanner);
		}

		String symbol = scanner.curValue(); 
		//its not yet read, because scanner.nextToken isn't called

		// if it starts with a big letter it is a Predicate
		//else it is a term and must be followed by an infix-Predicate
		if( (symbol.charAt(0) >= 'A' && symbol.charAt(0) <= 'Z') ){
			return parseNormalPredicate(scanner);
		} else {
			return parseInfixPredicate(scanner);
		}		

	}

	
	
	

	//NormalPredicate ::= (  PredSymbol '(' ((Term) [',' Term]* )?  ')'  ) 
	private FOLFormula parseNormalPredicate(FOLScanner scanner) throws ParseException {

		String symbol = scanner.curValue();
		scanner.nextToken();

		LinkedHashSet<FOLFormula> termChildren = new LinkedHashSet<>();
		if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals("("))){
			scanner.nextToken();

			if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals(")"))){
				//OK predicate end
				scanner.nextToken();
			} else {
				//parse terms
				termChildren.add( parseInfixTerm(scanner) );
				while(scanner.curType() == FOLToken.COMMA){
					scanner.nextToken();
					termChildren.add( parseInfixTerm(scanner) );
				}

				if(!(scanner.curType() == FOLToken.BRACKET) || !(scanner.curValue().equals(")"))){
					throw createParseException(	Lang.getString("FOP_INPUT_ERROR_10", symbol), scanner);
				}
				scanner.nextToken();
			}

		}//else no opening bracket -> no term		


		String symbolType = "P-" + termChildren.size();
		checkSymbolInfo(symbol, symbolType, scanner);
	
		return FOLFactory.createPredicate(termChildren, false, false, symbol);
	}


	

	//InfixPredicate ::= Term InfixPred Term
	private FOLFormula parseInfixPredicate(FOLScanner scanner) throws ParseException {

		FOLFormula leftOperand = parseInfixTerm(scanner);

		if( !(scanner.curType() == FOLToken.INFIX_PRED || scanner.curType() == FOLToken.EQUAL_SIGN) ){
			
			throw createParseException(
					Lang.getString("FOP_INPUT_ERROR_12", leftOperand.getName(), scanner.curValue()),
					scanner);
		}//else

		String symbol = scanner.curValue();
		String symbolType = "P-" + 2;		
		checkSymbolInfo(symbol, symbolType, scanner);		
		scanner.nextToken();

		FOLFormula rightOperand = parseInfixTerm(scanner);

		return FOLFactory.createInfixPredicate(leftOperand, rightOperand, false, false, symbol);
	}


	
	
	
	
	
	//InfixTerm ::= NormalTerm [infixFunc NormalTerm]* 
	private FOLFormula parseInfixTerm(FOLScanner scanner) throws ParseException{
		FOLFormula infixTerm = parseNormalTerm(scanner);

		while(scanner.curType() == FOLToken.INFIX_FUNC){
			String symbol = scanner.curValue();
			checkSymbolInfo(symbol, "F-2", scanner);
			scanner.nextToken();
			FOLFormula normalTerm = parseNormalTerm(scanner);
			infixTerm = FOLFactory.createInfixFunction(infixTerm, normalTerm, false, false, symbol);
		}
		return infixTerm;	
	}


	//NormalTerm ::='(' infixTerm ')' | Variable | FuncSymbol '(' ((InfixTerm) [',' InfixTerm]* )? ')'
	private FOLFormula parseNormalTerm(FOLScanner scanner) throws ParseException{
		
		if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals("("))){
			scanner.nextToken();
			FOLFormula termNode = parseInfixTerm(scanner);
			if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals(")"))){
				scanner.nextToken();
				termNode.setHasBrackets(true);
				return termNode;
			} else {
				throw createParseException(	Lang.getString("FOP_INPUT_ERROR_13", scanner.curValue()), scanner);
			}
		}//else
		
		if(!(scanner.curType() == FOLToken.SYMBOL)){
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_14", scanner.curValue()), scanner);
		}//else
		
		String symbol = scanner.curValue();
		if( !(symbol.charAt(0) >= 'a' && symbol.charAt(0) <= 'z') ){
			throw createParseException(Lang.getString("FOP_INPUT_ERROR_15"),	scanner);
		}
		scanner.nextToken();
		
		LinkedHashSet<FOLFormula> termChildren = new LinkedHashSet<>();
		if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals("("))){
			scanner.nextToken();

			if((scanner.curType() == FOLToken.BRACKET) && (scanner.curValue().equals(")"))){
				//OK Function end
				scanner.nextToken();
			} else {
				//parse terms
				termChildren.add( parseInfixTerm(scanner) );
				while(scanner.curType() == FOLToken.COMMA){
					scanner.nextToken();
					termChildren.add( parseInfixTerm(scanner) );
				}

				if(!(scanner.curType() == FOLToken.BRACKET) || !(scanner.curValue().equals(")"))){
					throw createParseException(	Lang.getString("FOP_INPUT_ERROR_11", symbol), scanner);
				}
				scanner.nextToken();
			}

		}//else not opening bracket -> no term		

						
		if(!containsSymbol(curBoundedVars, symbol)){
			checkSymbolInfo(symbol, "F-" + termChildren.size(), scanner);
			return FOLFactory.createFunction(termChildren, false, false, symbol);
			
		} else if(termChildren.size() == 0){
			FOLFormula forSymbol = getForSymbol(curBoundedVars, symbol);
			if (forSymbol != null) {
				return FOLFactory.createBoundVariable(symbol, forSymbol);
			}
			return FOLFactory.createBoundVariable(symbol);
			
		} else {
			throw createParseException(	Lang.getString("FOP_INPUT_ERROR_16", symbol), scanner);
			
		}
	}









	/**
	 * throws a ParseException, if the symbol is already in use
	 * 
	 * (adds a a info-string for printing)
	 * @param symbol the symbol to be checked
	 */
	private void checkSymbolInfo(String symbol, String symbolType, FOLScanner scanner) throws ParseException{
		if(!symbolTable.containsKey(symbol) || symbolTable.get(symbol).equals(symbolType)){
			symbolTable.put(symbol, symbolType);
			return; //everything all right
		} //else
		
		//error handling:
		String[] infos = symbolTable.get(symbol).split("-");
		String info;
		switch(infos[0]){
		case "TT":
			info = Lang.getString("FOP_OPERATOR_TT");
			break;
		case "FF":
			info = Lang.getString("FOP_OPERATOR_FF");
			break;
		case "OR":
			info = Lang.getString("FOP_OPERATOR_OR");
			break;
		case "AND":
			info = Lang.getString("FOP_OPERATOR_AND");
			break;
		case "NOT":
			info = Lang.getString("FOP_OPERATOR_NOT");
			break;
		case "IMP":
			info = Lang.getString("FOP_OPERATOR_IMP");
			break;
		case "BIIMP":
			info = Lang.getString("FOP_OPERATOR_BIIMP");
			break;
		case "EX":
			info = Lang.getString("FOP_OPERATOR_EX");
			break;
		case "FOR":
			info = Lang.getString("FOP_OPERATOR_FOR");
			break;
		case "P":
			info = Lang.getString("FOP_OPERATOR_P", infos[1]); 
			break;
		case "F":
			info = Lang.getString("FOP_OPERATOR_F", infos[1]); 
			break;
		case "V":
			info =  Lang.getString("FOP_OPERATOR_V");
			break;
		default: 
			info =  Lang.getString("FOP_OPERATOR_ERROR");
			break;
		}

		throw createParseException( Lang.getString("FOP_INPUT_ERROR_17", symbol, info), scanner);

	}

	private boolean containsSymbol(HashSet<FOLBoundVariable> curBoundedVars, String symbol) {
		if (getForSymbol(curBoundedVars, symbol) != null) {
			return true;
		}
		return false;
	}

	private FOLFormula getForSymbol(HashSet<FOLBoundVariable> curBoundedVars, String symbol) {
		for (FOLFormula curBoundedVar : curBoundedVars) {
			if (curBoundedVar.getName().equals(symbol)) {
				return curBoundedVar;
			}
		}
		return null;
	}

	/**
	 * @param scanner
	 * @return
	 */
	private ParseException createParseException(String message, FOLScanner scanner) {
		StringBuilder sb = new StringBuilder();
		sb.append(message);
		if(errorMessageHTMLMode){
			sb.append("\n<br>\n");
			sb.append("<font class=\"error\">");
			sb.append(	scanner.curValue());
			sb.append("</font>");
			sb.append(scanner.getRestOfText());
		}

		return new ParseException(sb.toString());
	}



	public Settings getSettings() {
		return settings;
	}
}
