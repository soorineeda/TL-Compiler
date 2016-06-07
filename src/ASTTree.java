import java.util.ArrayList;
public class ASTTree {
	public int getTempMemStartAddress() {
		return tempMemStartAddress;
	}
	public void setTempMemStartAddress(int tempMemStartAddress) {
		this.tempMemStartAddress = tempMemStartAddress;
	}
	public ArrayList<DeclerationsNode> getDecleartionsList() {
		return decleartionsList;
	}
	public void setDecleartionsList(ArrayList<DeclerationsNode> decleartionsList) {
		this.decleartionsList = decleartionsList;
	}
	public ArrayList<StatementsNode> getStatementsList() {
		return statementsList;
	}
	public void setStatementsList(ArrayList<StatementsNode> statementsList) {
		this.statementsList = statementsList;
	}
	ArrayList<DeclerationsNode> decleartionsList;
	ArrayList<StatementsNode> statementsList;
	int	tempMemStartAddress;
}
