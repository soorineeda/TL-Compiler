

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ASTBuilder {
	ArrayList<DeclerationsNode> declearationsList = new ArrayList<DeclerationsNode>();
	ArrayList<StatementsNode> statementsList = new ArrayList<StatementsNode>();
	ArrayList<StatementsNode> tempStatementsList = new ArrayList<StatementsNode>();
	HashMap<String, String> symbolTable = new HashMap<String, String>();
	HashMap<String, Integer> registerTable = new HashMap<String, Integer>();

	public HashMap<String, String> getSymbolTable() {
		return symbolTable;
	}
	
	public HashMap<String, Integer> getRegisterTable() {
		return registerTable;
	}


	public ASTTree buildAST(MainTreeNode mainTreeNode) throws ParserErrorException {
		ASTTree astTree = new ASTTree();
		ArrayList<MainTreeNode> rootChildren = mainTreeNode.getList();
		
		int tempMemStartAddress = processDeclearationsList(rootChildren.get(1));
		processSatementSeq(rootChildren.get(3));
		astTree.setDecleartionsList(declearationsList);
		astTree.setStatementsList(statementsList);
		astTree.setTempMemStartAddress(tempMemStartAddress);
		return astTree;
	}
	
	private int processDeclearationsList(MainTreeNode decNode) throws ParserErrorException {
		ArrayList<MainTreeNode> decNodeChildren = decNode.getList();
		// TODO rewrite while condition..
		int i = 0;
		while(decNodeChildren != null && !"e".equals(decNodeChildren.get(0).getName())){
			String ident = decNodeChildren.get(1).getValue();
			String type = decNodeChildren.get(3).getList().get(0).getName();
			DeclerationsNode dNode = new DeclerationsNode();
			dNode.setIdent(ident);
			dNode.setType(type);			
			declearationsList.add(dNode);
			if(!symbolTable.containsKey(ident)){
				symbolTable.put(ident,type);
				registerTable.put(ident, i--);
			}
			else
				throw new ParserErrorException("Duplicate decleartions:"+ident+"-->"+type);
			decNodeChildren = decNodeChildren.get(5).getList();
		}
		int tempMemStartAddress = i;
		return tempMemStartAddress;
	}
	
	private void processSatementSeq(MainTreeNode stSeqNode) {
		if(stSeqNode != null){
			ArrayList<MainTreeNode> stSeqChildren = stSeqNode.getList();
			if(!"e".equals(stSeqChildren.get(0).getName())){
				StatementsNode statementsNode = processStatement(stSeqChildren.get(0));
				statementsList.add(statementsNode);
				processSatementSeq(stSeqChildren.get(2));				
			}
		}
	}
	
	private StatementsNode processStatement(MainTreeNode stNode) {
		ArrayList<MainTreeNode> stChildren = stNode.getList();
		StatementsNode statementsNode = new StatementsNode();
		String statement = stChildren.get(0).getName();
 		if("<ifStatement>".equals(statement)){
 			statementsNode = processIf(stChildren.get(0));
 		}else if("<assignment>".equals(statement)){
 			statementsNode = processAssign(stChildren.get(0));
		}else if("<whileStatement>".equals(statement)){
			statementsNode = processWhile(stChildren.get(0));
		}else if("<writeInt>".equals(statement)){
			statementsNode = processWrite(stChildren.get(0));
		}				
 		return statementsNode;
	}
	
	private static StatementsNode processIf(MainTreeNode ifNode) {
		ArrayList<MainTreeNode> ifChildren = ifNode.getList();
		StatementsNode statementsNode = new StatementsNode();
		statementsNode.setCategory("if");
		statementsNode.setRightExpression(getExpression(ifChildren.get(1)));
		ASTBuilder astBuilder = new ASTBuilder();
		astBuilder.getInternalStatementsList(ifChildren.get(3));
		statementsNode.setSt1(astBuilder.getTempStatementsList());
		//process else statements...
		MainTreeNode elseNode = ifChildren.get(4);
		ArrayList<MainTreeNode> elseChildren = elseNode.getList();
		if(elseChildren != null && !"e".equals(elseChildren.get(0).getName())){
			ASTBuilder astBuilder2 = new ASTBuilder();
			astBuilder2.getInternalStatementsList(elseChildren.get(1));
			statementsNode.setSt2(astBuilder2.getTempStatementsList());
		}
		return statementsNode;
	}

	private static StatementsNode processWrite(MainTreeNode writeNode) {
		ArrayList<MainTreeNode> writeChildren = writeNode.getList();
		StatementsNode statementsNode = new StatementsNode();
		statementsNode.setCategory("writeInt");
		statementsNode.setRightExpression(getExpression(writeChildren.get(1)));
		return statementsNode;
	}

	private static StatementsNode processWhile(MainTreeNode whileNode) {
		ArrayList<MainTreeNode> whileChildren = whileNode.getList();
		StatementsNode statementsNode = new StatementsNode();
		statementsNode.setCategory("while");
		statementsNode.setRightExpression(getExpression(whileChildren.get(1)));
		ASTBuilder astBuilder3 = new ASTBuilder();
		astBuilder3.getInternalStatementsList(whileChildren.get(3));
		statementsNode.setSt1(astBuilder3.getTempStatementsList());
		return statementsNode;
	}

	private static StatementsNode processAssign(MainTreeNode asgnNode) {
		ArrayList<MainTreeNode> asgnChildren = asgnNode.getList();
		StatementsNode statementsNode = new StatementsNode();
		statementsNode.setCategory("assignment");
		statementsNode.setLeftVariable(asgnChildren.get(0).getValue());
		statementsNode.setRightExpression(processAssign2(asgnChildren.get(2)));
		return statementsNode;
	}
	
	private static Expression processAssign2(MainTreeNode asgn2) {
		ArrayList<MainTreeNode> asgn2Children = asgn2.getList();				
		if(asgn2Children.get(0).getName().equals("READINT")){
			Expression e = new Expression();
			e.setSymbol("READINT");
			return e;
		}else{
			return getExpression(asgn2Children.get(0));
		}
	}

	private static Expression getExpression(MainTreeNode exprNode) {
		Expression expr = new Expression();
		ArrayList<MainTreeNode> exprChildren = exprNode.getList();		
		Expression tempexp1 = processSimpleExpr(exprChildren.get(0));		
		if(!exprChildren.get(1).getList().get(0).getName().equals("e")){
			Expression tempexp2 = processExpr2(exprChildren.get(1));
			expr.setLeftExpression(tempexp1);
			expr.setRightExpresssion(tempexp2);
			ArrayList<MainTreeNode> subChildren = exprChildren.get(1).getList();			
			expr.setSymbol(subChildren.get(0).getName());
			expr.setValue(subChildren.get(0).getValue());
		}else{
			expr.setLeftExpression(tempexp1);
		}
		return expr;
	}	
	
	private static Expression processSimpleExpr(MainTreeNode simplExprNode) {
		Expression expr = new Expression();
		ArrayList<MainTreeNode> simplExprChildren = simplExprNode.getList();
		Expression tempexp1 = processTerm(simplExprChildren.get(0));
		if(!simplExprChildren.get(1).getList().get(0).getName().equals("e")){
			Expression tempexp2 = processSimpleExpr2(simplExprChildren.get(1));
			expr.setLeftExpression(tempexp1);
			expr.setRightExpresssion(tempexp2);
			ArrayList<MainTreeNode> subChildren = simplExprChildren.get(1).getList();
			expr.setSymbol(subChildren.get(0).getName());
			expr.setValue(subChildren.get(0).getValue());
			
		}else{
			expr.setLeftExpression(tempexp1);
		}
		return expr;
	}
	
	private static Expression processSimpleExpr2(MainTreeNode simplExp2Node) {
		ArrayList<MainTreeNode> simplExp2Children = simplExp2Node.getList();
		return processSimpleExpr(simplExp2Children.get(1));
	}

	private static Expression processTerm(MainTreeNode termNode) {
		Expression expr = new Expression();
		ArrayList<MainTreeNode> termChildren = termNode.getList();
		Expression tempexp1 = processFactor(termChildren.get(0));
		if(!termChildren.get(1).getList().get(0).getName().equals("e")){
			Expression tempexp2 = processTerm2(termChildren.get(1));
			expr.setLeftExpression(tempexp1);
			expr.setRightExpresssion(tempexp2);
			ArrayList<MainTreeNode> subChildren = termChildren.get(1).getList();
			expr.setSymbol(subChildren.get(0).getName());
			expr.setValue(subChildren.get(0).getValue());			
		}else{
			expr.setLeftExpression(tempexp1);
		}
		return expr;
	}

	private static Expression processFactor(MainTreeNode factorNode) {
		Expression expr = new Expression();
		ArrayList<MainTreeNode> factorChildren = factorNode.getList();
		MainTreeNode factorchild = factorChildren.get(0);
		if("ident".equals(factorchild.getName())){
			expr.setSymbol("IDENT");
			expr.setLeftValue(factorchild.getValue());
		}else if("num".equals(factorchild.getName())){
			expr.setSymbol("NUM");
			expr.setLeftValue(factorchild.getValue());
		}else if("boollit".equals(factorchild.getName())){
			expr.setSymbol("BOOL");
			if("true".equals(factorchild.getValue())){
				expr.setLeftValue("1");				
			}else{
				expr.setLeftValue("0");								
			}
//			expr.setLeftValue(factorchild.getValue());
		}else if("LP".equals(factorchild.getName())){
			expr.setSymbol("PARENTHESIS");
			expr.setLeftExpression(getExpression(factorChildren.get(1)));
		}
		return expr;
	}

	private static Expression processTerm2(MainTreeNode term2Node) {
		ArrayList<MainTreeNode> term2Children = term2Node.getList();
		return processTerm(term2Children.get(1));
	}

	private static Expression processExpr2(MainTreeNode exp2Node) {
		ArrayList<MainTreeNode> exp2Children = exp2Node.getList();
		return getExpression(exp2Children.get(1));
	}

	
	private void getInternalStatementsList(MainTreeNode stSeqNode) {
		if(stSeqNode != null){
			ArrayList<MainTreeNode> stSeqChildren = stSeqNode.getList();
			if(!"e".equals(stSeqChildren.get(0).getName())){
				StatementsNode statementsNode = processStatement(stSeqChildren.get(0));
				tempStatementsList.add(statementsNode);
				getInternalStatementsList(stSeqChildren.get(2));
			}
		}
	}

	public ArrayList<StatementsNode> getTempStatementsList(){
		return tempStatementsList;
	}
}
