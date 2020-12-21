package eu.yeger.gramofo.fol;

public class FOLToken {
	
	public static final int END_OF_SOURCE = 0;
	public static final int SYMBOL = 1;
	public static final int CHAR = 2;
	public static final int TRUE =  3;
	public static final int FALSE =  4;
	public static final int OR =  5;
	public static final int AND =  6;
	public static final int NOT =  7;
	public static final int IMPLICATION =  8;
	public static final int BIIMPLICATION =  9;
	public static final int EXISTS =  10; 
	public static final int FORALL =  11;
	public static final int INFIX_PRED =  12;
	public static final int EQUAL_SIGN =  13;
	public static final int INFIX_FUNC =  14;
	public static final int BRACKET = 15;
	public static final int DOT = 16;
	public static final int COMMA = 17;

	
	private int type;
	private String value;
	
	public FOLToken(int type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}

	public void setTypeAndValue(int type, String value) {
		this.type = type;
		this.value = value;
	}
}
