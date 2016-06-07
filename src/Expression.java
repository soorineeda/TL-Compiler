

public class Expression {
	String symbol;
	Expression leftExpression;
	Expression rightExpresssion;
	String leftValue;
	String value;
	boolean isError;
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Expression getLeftExpression() {
		return leftExpression;
	}
	public void setLeftExpression(Expression leftExpression) {
		this.leftExpression = leftExpression;
	}
	public Expression getRightExpresssion() {
		return rightExpresssion;
	}
	public void setRightExpresssion(Expression rightExpresssion) {
		this.rightExpresssion = rightExpresssion;
	}
	public String getLeftValue() {
		return leftValue;
	}
	public void setLeftValue(String leftValue) {
		this.leftValue = leftValue;
	}	
}
