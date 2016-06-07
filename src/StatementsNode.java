

import java.util.ArrayList;

public class StatementsNode {
	String category;
	String leftVariable;
	Expression rightExpression;
	ArrayList<StatementsNode> st1;
	ArrayList<StatementsNode> st2;
	boolean isTypeError;
	public boolean isTypeError() {
		return isTypeError;
	}
	public void setTypeError(boolean isTypeError) {
		this.isTypeError = isTypeError;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLeftVariable() {
		return leftVariable;
	}
	public void setLeftVariable(String leftVariable) {
		this.leftVariable = leftVariable;
	}
	public Expression getRightExpression() {
		return rightExpression;
	}
	public void setRightExpression(Expression rightVariable) {
		this.rightExpression = rightVariable;
	}
	public ArrayList<StatementsNode> getSt1() {
		return st1;
	}
	public void setSt1(ArrayList<StatementsNode> st1) {
		this.st1 = st1;
	}
	public ArrayList<StatementsNode> getSt2() {
		return st2;
	}
	public void setSt2(ArrayList<StatementsNode> st2) {
		this.st2 = st2;
	}
	
}
