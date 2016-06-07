

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TreePrinter {
	int counter = 1;
	int tempCounter = 2;
	int tempMemAddressStart;
	int branchCounter = 2;
	boolean typeErrorIndicator = false;
	HashMap<String, String> symbolTable;
	HashMap<String, Integer> registerTable;

	FileWriter parsewriter;
	FileWriter astWriter;
	FileWriter machineInsWriter;
	FileWriter cfgWriter;

	public TreePrinter(String fileName) throws IOException{
		File parseFile = new File(fileName.substring(0, fileName.length()-3)+"-parse.dot");
		parseFile.createNewFile();
		parsewriter = new FileWriter(parseFile);
		
		File astFile = new File(fileName.substring(0, fileName.length()-3)+"-ast.dot");
		astFile.createNewFile();
		astWriter = new FileWriter(astFile);

		File machineFile = new File(fileName.substring(0, fileName.length()-3)+".s");
		parseFile.createNewFile();
		machineInsWriter = new FileWriter(machineFile);
		
		File cfg = new File(fileName.substring(0, fileName.length()-3)+".3A.cfg.dot");
		cfg.createNewFile();
		cfgWriter = new FileWriter(cfg);

	}
	public void printParseTree(MainTreeNode mainTreeNode,int parentNode) throws IOException {		
			

		ArrayList<MainTreeNode> arr = mainTreeNode.getList();
		Iterator<MainTreeNode> i = arr.iterator();
		while(i.hasNext()){
			counter++;
			MainTreeNode temp = i.next();
			if(temp.getType().equals("nonTerminal")){				
				parsewriter.write("n"+(counter)+" [label=\""+temp.getName()+"\",fillcolor=\"/x11/lightgrey\",shape=box]\n");
				parsewriter.write("n"+parentNode+" -> "+"n"+(counter)+"\n");
				printParseTree(temp,counter);
			}else{
				parsewriter.write("n"+(counter)+" [label=\""+temp.getName()+":"+temp.getValue()+"\",fillcolor=\"/x11/lightgrey\",shape=box]\n");
				parsewriter.write("n"+parentNode+" -> "+"n"+(counter)+"\n");
			}
		}
	}
	public boolean printAST(ASTTree astTree, HashMap<String, String> symbol,HashMap<String, Integer> register) throws IOException {
			this.symbolTable = symbol;
			this.registerTable = register;
			ArrayList<StatementsNode> statementsList = astTree.getStatementsList();
			counter = 1;
			astWriter.write(
					"digraph tl12Ast {\n"+
					"ordering=out;\n"+
					"node [shape = box, style = filled, fillcolor=\"white\"]\n");

			astWriter.write("n0[label=\"program\"]\n"+
							"n1[label=\"Declearations\"]\n"+
							"n0 -> n1\n");		
			machineInsWriter.write("\t.data\n"+
					"newline:\t.asciiz \"\\n\"\n"+
					"\t.text\n"+
					"\t.globl main\n"+
					"main:\n"+
					"\tli $fp, 0x7ffffffc\n"+
					"\nB1:\n"+
					"\n"
				);
			cfgWriter.write(
					"digraph graphviz {\n"+
					"node [shape = none];\n"+
					"edge [tailport = s];\n"+
					"entry\n"+
					"subgraph cluster {\n"+
					"color=\"/x11/white\""+"\n"
			);
						
			cfgWriter.write("B1 [fillcolor=\"/x11/white\",shape=box, label = <B1<br/>"+"\n");
			printDecleartions(astTree.getDecleartionsList(),1);
//			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
			astWriter.write(
					"n2[label=\"Statements\"]\n"+
					"n0 -> n2\n");
			tempMemAddressStart = astTree.getTempMemStartAddress();
			tempMemAddressStart++;
			printStatements(statementsList,2);
			machineInsWriter.write("\t# exit\n"+
					"\tli $v0, 10\n"+
					"\tsyscall\n"+
					"\n"
				);
			cfgWriter.write("# exit <br/>"+"\n");

			cfgWriter.write(">]"+"\n");
			String prevBranch = "B"+(branchCounter-1);
			cfgWriter.write("entry -> B1"+"\n");
			cfgWriter.write(prevBranch+" -> exit"+"\n");

			cfgWriter.write("\n}\n}");

			astWriter.write("\n}\n");
			astWriter.close();
			if(!typeErrorIndicator){
				machineInsWriter.close();
				cfgWriter.close();		
			}
			return typeErrorIndicator;
	}
	

	private void printDecleartions(ArrayList<DeclerationsNode> arrayList,int parent) throws IOException {
			Iterator<DeclerationsNode> it = arrayList.iterator();
			while(it.hasNext()){
				DeclerationsNode declerationsNode = it.next();
				astWriter.write("n"+ ++tempCounter+"[label=\""+declerationsNode.getIdent()+":"+declerationsNode.getType()+"\"]\n");
				astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				machineInsWriter.write("\t# loadI 0 => r_"+declerationsNode.getIdent()+"\n");
				cfgWriter.write("# loadI 0 =&gt; r_"+declerationsNode.getIdent()+"<br/>"+"\n");
				machineInsWriter.write("\tli $t0, 0"+"\n");
				machineInsWriter.write("\tsw $t0, "+registerTable.get(declerationsNode.getIdent())*4+"($fp)\n"+"\n");
			}
	}
	
	private void printStatements(ArrayList<StatementsNode> statementsList,int parent) throws IOException{
		if(statementsList == null)
			return;
		Iterator<StatementsNode> it = statementsList.iterator();
		while(it.hasNext()){
			StatementsNode statementsNode = it.next();
			if("assignment".equals(statementsNode.getCategory())){
				Expression expr = statementsNode.getRightExpression();
				String leftType = symbolTable.get(statementsNode.getLeftVariable());
				String rightType = getType(expr);
				if("ERROR".equals(rightType))
					typeErrorIndicator = true;				
				boolean isError = false;
				if(leftType == null | rightType == null ){
					isError = true;
					typeErrorIndicator = true;
				}
				else if(!leftType.equals(rightType)){
					isError = true;					
					typeErrorIndicator = true;				
				}
				int temp;
				if(isError){
					astWriter.write("n"+ ++tempCounter+"[label=\":=\",fillcolor=\"red\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
					
					temp = tempCounter;
					String ltype = symbolTable.get(statementsNode.getLeftVariable());
					if(ltype == null){
						astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getLeftVariable()+"\",fillcolor=\"red\"]\n");
					}else{
						astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getLeftVariable()+":"+ltype+"\"]\n");						
					}
					astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
				}else{
					astWriter.write("n"+ ++tempCounter+"[label=\":=\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
					temp = tempCounter;
					astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getLeftVariable()+":"+leftType+"\"]\n");
					astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
				}
				
				if("READINT".equals(expr.getSymbol())){
						astWriter.write("n"+ ++tempCounter+"[label=\"READINT:INT\"]\n");
						astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
						machineInsWriter.write("\t# readInt  => r_"+statementsNode.getLeftVariable()+"\n");
						cfgWriter.write("# readInt  =&gt; r_"+statementsNode.getLeftVariable()+"<br/>"+"\n");
						machineInsWriter.write("\tli $v0, 5\n"+
							"\tsyscall\n"+
							"\tadd $t0, $v0, $zero\n"+
							"\tsw $t0, "+registerTable.get(statementsNode.getLeftVariable())*4+"($fp)\n"+"\n"
						);
				}else{
					String tempr2 = printExpression(expr,temp,2);
					int pointer2 = 2*-4+tempMemAddressStart*4;
					if(!"nUMORBOOL".equals(tempr2) && !tempr2.startsWith("r")){
						pointer2 = registerTable.get(tempr2)*4;
						tempr2 = "r_"+tempr2;
					}
					if("nUMORBOOL".equals(tempr2)){
					machineInsWriter.write(
							"\t# i2i r2 => r_"+statementsNode.getLeftVariable()+"\n"+
							"\tlw $t1, "+pointer2+"($fp)\n"+
							"\tadd $t0, $t1, $zero\n"+
							"\tsw $t0, "+registerTable.get(statementsNode.getLeftVariable())*4+"($fp)\n"+"\n"
							);
					cfgWriter.write("# i2i r2 =&gt; r_"+statementsNode.getLeftVariable()+"<br/>"+"\n");
					}else{
						machineInsWriter.write(
								"\t# i2i "+tempr2+" => r_"+statementsNode.getLeftVariable()+"\n"+
								"\tlw $t1, "+pointer2+"($fp)\n"+
								"\tadd $t0, $t1, $zero\n"+
								"\tsw $t0, "+registerTable.get(statementsNode.getLeftVariable())*4+"($fp)\n"+"\n"
								);
						cfgWriter.write("# i2i "+tempr2+" =&gt; r_"+statementsNode.getLeftVariable()+"<br/>"+"\n");
					}
				}
			}else if("if".equals(statementsNode.getCategory())){
				Expression expr = statementsNode.getRightExpression();
				String type = getType(expr);
				if("ERROR".equals(type))
					typeErrorIndicator = true;
				astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getCategory()+"\"]\n");
				astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				int temp = tempCounter;
				String prevBranch = "B"+(branchCounter-1);
				String condBranchName = "B"+branchCounter++;
				
				machineInsWriter.write("\t# jumpI =&gt; "+condBranchName+"\n");
				//TODO.. this is not needed ??
				
				cfgWriter.write("# jumpI =&gt; "+condBranchName+"<br/>"+"\n");
				cfgWriter.write(">]"+"\n");
				cfgWriter.write(prevBranch+"->"+condBranchName+"\n");
				cfgWriter.write(condBranchName+" [fillcolor=\"/x11/white\",shape=box, label = <"+condBranchName+"<br/>"+"\n");

				cfgWriter.write("# jumpI =&gt; "+condBranchName+"<br/>"+"\n");
				//
				machineInsWriter.write("\tj "+condBranchName+"\n"+"\n");
				machineInsWriter.write(condBranchName+":\n"+"\n");

				printExpression(expr,temp,1);
				String ifStmtBranchName = "B"+branchCounter++;
				String elseStmtsBranch = "B"+branchCounter++;
				String nextStmtsBranch = "B"+branchCounter++;
				cfgWriter.write("# cbr r1 -&gt; "+ifStmtBranchName+", "+elseStmtsBranch+"\n");

				cfgWriter.write(">]"+"\n");
				cfgWriter.write(condBranchName+"->"+ifStmtBranchName+"\n");
				cfgWriter.write(condBranchName+"->"+elseStmtsBranch+"\n");

				cfgWriter.write(ifStmtBranchName+"->"+nextStmtsBranch+"\n");
				cfgWriter.write(elseStmtsBranch+"->"+nextStmtsBranch+"\n");

				machineInsWriter.write("\t# cbr r1 -> "+ifStmtBranchName+", "+elseStmtsBranch+"\n");
				machineInsWriter.write("\tlw $t0, "+(1*-4+tempMemAddressStart*4)+"($fp)"+"\n");
				machineInsWriter.write("\tbeq $t0, $zero, "+elseStmtsBranch+"\n"+"\n");

				cfgWriter.write(ifStmtBranchName+" [fillcolor=\"/x11/white\",shape=box, label = <"+ifStmtBranchName+"<br/>"+"\n");

				if(statementsNode.getSt1() != null && statementsNode.getSt1().size() != 0 ){
					astWriter.write("n"+ ++tempCounter+"[label=\"stmt list:\"]\n");
					astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
					printInternalStatements(statementsNode.getSt1(),tempCounter);
					machineInsWriter.write("\t# jumpI => "+nextStmtsBranch+"\n");
					machineInsWriter.write("\tj "+nextStmtsBranch+"\n");
				}
				cfgWriter.write("# jumpI =&gt; "+nextStmtsBranch+"<br/>"+"\n");
				cfgWriter.write(">]"+"\n");
				cfgWriter.write(elseStmtsBranch+" [fillcolor=\"/x11/white\",shape=box, label = <"+elseStmtsBranch+"<br/>"+"\n");

				machineInsWriter.write(elseStmtsBranch+":\n"+"\n");
				if(statementsNode.getSt2() != null && statementsNode.getSt2().size() != 0 ){
					astWriter.write("n"+ ++tempCounter+"[label=\"else stmt list:\"]\n");
					astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
					printInternalStatements(statementsNode.getSt2(),tempCounter);
	
				}
				machineInsWriter.write(nextStmtsBranch+":\n"+"\n");
				cfgWriter.write("# jumpI =&gt; "+nextStmtsBranch+"<br/>"+"\n");
				cfgWriter.write(">]"+"\n");
				cfgWriter.write(nextStmtsBranch+" [fillcolor=\"/x11/white\",shape=box, label = <"+nextStmtsBranch+"<br/>"+"\n");

			}else if("writeInt".equals(statementsNode.getCategory())){
				Expression expr = statementsNode.getRightExpression();
				String type = getType(expr);
				if("ERROR".equals(type))
					typeErrorIndicator = true;
				if(!"INT".equals(type)){
					astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getCategory()+"\",fillcolor=\"red\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}else{
					astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getCategory()+"\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}
				String tempr1 = printExpression(expr,tempCounter,1);
				int pointer1 = 1*-4+tempMemAddressStart*4;
				if(!"nUMORBOOL".equals(tempr1) && !tempr1.startsWith("r")){
					pointer1 = registerTable.get(tempr1)*4;
					tempr1 = "r_"+tempr1;
				}
				
				if("nUMORBOOL".equals(tempr1)){
					cfgWriter.write("# writeInt r1"+"<br/>"+"\n");
					machineInsWriter.write("\t# writeInt r1"+"\n"+
							"\tli $v0, 1\n"+
							"\tlw $t1, "+pointer1+"($fp)\n"+
							"\tadd $a0, $t1, $zero\n"+
							"\tsyscall\n"+
							"\tli $v0, 4\n"+
							"\tla $a0, newline\n"+
							"\tsyscall\n"+"\n"
						);
				}else{
					cfgWriter.write("# writeInt "+tempr1+"<br/>"+"\n");
					machineInsWriter.write("\t# writeInt "+tempr1+"\n"+
							"\tli $v0, 1\n"+
							"\tlw $t1, "+pointer1+"($fp)\n"+
							"\tadd $a0, $t1, $zero\n"+
							"\tsyscall\n"+
							"\tli $v0, 4\n"+
							"\tla $a0, newline\n"+
							"\tsyscall\n"+"\n"
						);

				}
			}else if("while".equals(statementsNode.getCategory())){
				Expression expr = statementsNode.getRightExpression();
				String type = getType(expr);
				if("ERROR".equals(type))
					typeErrorIndicator = true;

				astWriter.write("n"+ ++tempCounter+"[label=\""+statementsNode.getCategory()+"\"]\n");
				astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				
				int temp = tempCounter;
				String prevBranch = "B"+(branchCounter-1);
				String condBranchName = "B"+branchCounter++;
				
				machineInsWriter.write("\t# jumpI => "+condBranchName+"\n");
				machineInsWriter.write("\tj "+condBranchName+"\n"+"\n");
				machineInsWriter.write(condBranchName+":\n"+"\n");
				
				cfgWriter.write("# jumpI =&gt; "+condBranchName+"<br/>"+"\n");
				cfgWriter.write(">]"+"\n");
				cfgWriter.write(prevBranch+"->"+condBranchName+"\n");
				cfgWriter.write(condBranchName+" [fillcolor=\"/x11/white\",shape=box, label = <"+condBranchName+"<br/>"+"\n");
				
				printExpression(expr,temp,1);
				
				String internalStBranchName = "B"+branchCounter++;
				String nextStmtsBranch = "B"+branchCounter++;
				machineInsWriter.write("\t# cbr r1 -> "+internalStBranchName+", "+nextStmtsBranch+"\n");

				cfgWriter.write("# cbr r1 -&gt; "+internalStBranchName+", "+nextStmtsBranch+"<br/>"+"\n");
				cfgWriter.write(">]"+"\n");
				cfgWriter.write(condBranchName+"->"+internalStBranchName+"\n");
				cfgWriter.write(internalStBranchName+"->"+condBranchName+"\n");

				cfgWriter.write(condBranchName+"->"+nextStmtsBranch+"\n");

				cfgWriter.write(internalStBranchName+" [fillcolor=\"/x11/white\",shape=box, label = <"+internalStBranchName+"<br/>"+"\n");
				machineInsWriter.write("\tlw $t0, "+(1*-4+tempMemAddressStart*4)+"($fp)"+"\n");
				machineInsWriter.write("\tbeq $t0, $zero, "+nextStmtsBranch+"\n"+"\n");
//				cfgWriter.write("\tj "+nextStmtsBranch+"\n");
				if(statementsNode.getSt1() != null && statementsNode.getSt1().size() != 0 ){
					astWriter.write("n"+ ++tempCounter+"[label=\"stmt list:\"]\n");
					astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
					printInternalStatements(statementsNode.getSt1(),tempCounter);
				}
				machineInsWriter.write("\t# jumpI => "+condBranchName+"\n");
				machineInsWriter.write("\tj "+condBranchName+"\n"+"\n");
				machineInsWriter.write(nextStmtsBranch+":\n"+"\n");
				cfgWriter.write("# jumpI =&gt; "+condBranchName+"<br/>"+"\n");

				cfgWriter.write(">]"+"\n");
				cfgWriter.write(nextStmtsBranch+" [fillcolor=\"/x11/white\",shape=box, label = <"+nextStmtsBranch+"<br/>"+"\n");

			}
		}
	}
	
	private void printInternalStatements(ArrayList<StatementsNode> st1,int parent) throws IOException {
		printStatements(st1,parent);
	}

	private String printExpression(Expression expr,int parent,int regIndex) throws IOException {
		if(expr != null){
			String type = getType(expr);
			if("ERROR".equals(type))
				typeErrorIndicator = true;
			if(expr.getSymbol() == null){
				return printExpression(expr.getLeftExpression(),parent,regIndex);
			}else if("IDENT".equals(expr.getSymbol()) | "NUM".equals(expr.getSymbol()) | "BOOL".equals(expr.getSymbol())){
				if("ERROR".equals(type)){
					 astWriter.write("n"+ ++tempCounter+"[label=\""+expr.getLeftValue()+"\",fillcolor=\"red\"]\n");
					 astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}else{
					astWriter.write("n"+ ++tempCounter+"[label=\""+expr.getLeftValue()+":"+type+"\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}
				
				if("NUM".equals(expr.getSymbol()) | "BOOL".equals(expr.getSymbol())){
					machineInsWriter.write("\t# loadI "+expr.getLeftValue()+" => r"+regIndex+"\n");
					cfgWriter.write("# loadI "+expr.getLeftValue()+" =&gt; r"+regIndex+"<br/>"+"\n");
					machineInsWriter.write("\tli $t0, "+expr.getLeftValue()+"\n");
					machineInsWriter.write("\tsw $t0, "+(regIndex*-4+tempMemAddressStart*4)+"($fp)\n"+"\n");
					return "nUMORBOOL";
				}else{
					return expr.getLeftValue();
				}
			}else if("PARENTHESIS".equals(expr.getSymbol())){
				int temp = parent;
				astWriter.write("n"+ ++tempCounter+"[label=\"LP\"]\n");
				astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
				
				String tempr1 = printExpression(expr.getLeftExpression(), temp,regIndex);
				
				astWriter.write("n"+ ++tempCounter+"[label=\"RP\"]\n");
				astWriter.write("n"+temp+" -> "+"n"+tempCounter+"\n");
				return tempr1;
			}
			else{
				if("ERROR".equals(type)){
					astWriter.write("n"+ ++tempCounter+"[label=\""+expr.getValue()+"\",fillcolor=\"red\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}else{
					astWriter.write("n"+ ++tempCounter+"[label=\""+expr.getValue()+":"+type+"\"]\n");
					astWriter.write("n"+parent+" -> "+"n"+tempCounter+"\n");
				}
				int temp = tempCounter;
				String tempr1 = printExpression(expr.getLeftExpression(),temp,1);
				String tempr2 = printExpression(expr.getRightExpresssion(),temp,2);
				
				if("nUMORBOOL".equals(tempr1)){
					if("nUMORBOOL".equals(tempr2)){
						// TODO check indexes 1 and 2 here..
						machineInsWriter.write("\t# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r1, r2 => r"+regIndex+"\n");
						cfgWriter.write("# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r1&#44; r2 =&gt; r"+regIndex+"<br/>"+"\n");

						machineInsWriter.write("\tlw $t1, "+(1*-4+tempMemAddressStart*4)+"($fp)"+"\n");
						machineInsWriter.write("\tlw $t2, "+(2*-4+tempMemAddressStart*4)+"($fp)"+"\n");
						machineInsWriter.write("\t"+getMipsInstruction(expr.getSymbol(),expr.getValue())+" $t"+regIndex+" , $t1, $t2"+"\n");
						machineInsWriter.write("\tsw $t"+regIndex+", "+(regIndex*4+tempMemAddressStart*4)+"($fp)\n"+"\n");
					}
					else{
						// TODO check indexes 1 and 2 here..
						machineInsWriter.write("\t# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r1, r_"+tempr2+" => r"+regIndex+"\n");
						cfgWriter.write("# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r1&#44; r_"+tempr2+" =&gt; r"+regIndex+"<br/>"+"\n");

						int pointer2 = 2*-4+tempMemAddressStart*4;						
						if(!tempr2.startsWith("r")){
							pointer2 = registerTable.get(tempr2)*4;
						}

						machineInsWriter.write("\tlw $t1, "+(1*-4+tempMemAddressStart*4)+"($fp)"+"\n");
						machineInsWriter.write("\tlw $t2, "+pointer2+"($fp)"+"\n");
						machineInsWriter.write("\t"+getMipsInstruction(expr.getSymbol(),expr.getValue())+" $t"+regIndex+" , $t1, $t2"+"\n");
						machineInsWriter.write("\tsw $t"+regIndex+", "+(regIndex*-4+tempMemAddressStart*4)+"($fp)\n"+"\n");
						//registerTable.get(tempr2)*4
					}
				}else{
					if("nUMORBOOL".equals(tempr2)){
//						machineInsWriter.write(expr.getSymbol()+registerTable.get(tempr1)+" "+registerTable.get(tempr2)+"=> r2");
						machineInsWriter.write("\t# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r_"+tempr1+", r2 => r"+regIndex+"\n");
						cfgWriter.write("# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" r_"+tempr1+"&#44; r2 =&gt; r"+regIndex+"<br/>"+"\n");
						int pointer1 = 1*-4+tempMemAddressStart*4;						
						if(!tempr1.startsWith("r")){
							pointer1 = registerTable.get(tempr1)*4;
						}
						
						machineInsWriter.write("\tlw $t1, "+pointer1+"($fp)"+"\n");
						machineInsWriter.write("\tlw $t2, "+(2*-4+tempMemAddressStart*4)+"($fp)"+"\n");
						machineInsWriter.write("\t"+getMipsInstruction(expr.getSymbol(),expr.getValue())+" $t"+regIndex+" , $t1, $t2"+"\n");
						machineInsWriter.write("\tsw $t"+regIndex+", "+(regIndex*-4+tempMemAddressStart*4)+"($fp)\n"+"\n");

					}else{
//						System.out.println(expr.getSymbol()+registerTable.get(tempr1)+" "+registerTable.get(tempr2)+"=> r2");						
						int pointer1 = 1*-4+tempMemAddressStart*4;
						int pointer2 = 2*-4+tempMemAddressStart*4;

						if(!tempr1.startsWith("r")){
							pointer1 = registerTable.get(tempr1)*4;
							tempr1 = "r_"+tempr1;
						}
							
						if(!tempr2.startsWith("r")){
							pointer2 = registerTable.get(tempr2)*4;
							tempr2 = "r_"+tempr2;
						}
						
						machineInsWriter.write("\t# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" "+tempr1+", "+tempr2+" => r"+regIndex+"\n");
						cfgWriter.write("# "+getMipsInstruction(expr.getSymbol(),expr.getValue())+" "+tempr1+"&#44; "+tempr2+" =&gt; r"+regIndex+"<br/>"+"\n");

						machineInsWriter.write("\tlw $t1, "+pointer1+"($fp)"+"\n");
						machineInsWriter.write("\tlw $t2, "+pointer2+"($fp)"+"\n");
						machineInsWriter.write("\t"+getMipsInstruction(expr.getSymbol(),expr.getValue())+" $t"+regIndex+" , $t1, $t2"+"\n");
						machineInsWriter.write("\tsw $t"+regIndex+", "+(regIndex*-4+tempMemAddressStart*4)+"($fp)\n"+"\n");
						
					}
				}
				return "r"+regIndex;
			}
		}
		return null;
	}
	
	private String getMipsInstruction(String symbol, String value) {
		// TODO Auto-generated method stub
		switch(value){
			//Multiplicative
			case "*":
				return "mul";
			case "div":
				return "div";
			case "mod":
				return "rem";
			//Additive	
			case "+":
				return "add";
			case "-":
				return "sub";
			//Compare
			case "=":
				return "seq";
			case "!=":
				return "sne";
			case "<":
				return "slt";
			case "<=":
				return "sle";
			case ">":
				return "sgt";
			case ">=":
				return "sge";
		}
		return symbol.substring(0, 3)+value;
	}
	private String getType(Expression expr) {
		if(expr != null){
			if(expr.getSymbol() == null){
				return getType(expr.getLeftExpression());				
			}else if("READINT".equals(expr.getSymbol())){
				return "INT";
			}else if("IDENT".equals(expr.getSymbol())){
				String type = symbolTable.get(expr.getLeftValue());
				if(type == null)
					return "ERROR";
				else
					return type;
			}else if("NUM".equals(expr.getSymbol())){
				return "INT";
			}else if("BOOL".equals(expr.getSymbol())){
				return "BOOL";
			}else if("PARENTHESIS".equals(expr.getSymbol())){
				return getType(expr.getLeftExpression());
			}
			else if("COMPARE".equals(expr.getSymbol())){
				String leftType = getType(expr.getLeftExpression());
				String rightType = getType(expr.getRightExpresssion());								
				if(!leftType.equals(rightType)){
					return "ERROR";
				}else if(!leftType.equals("INT") | !rightType.equals("INT")){
					return "ERROR";
				}else{
					return "BOOL";
				}
			}else if(!"ADDITIVE".equals(expr.getSymbol()) | !"MULTIPLICATIVE".equals(expr.getSymbol())){
				String leftType = getType(expr.getLeftExpression());
				String rightType = getType(expr.getRightExpresssion());
				if(leftType == null | rightType == null){
					return "ERROR";
				}	
				else if(!leftType.equals("INT") | !rightType.equals("INT")){
					return "ERROR";
				}else if(!leftType.equals(rightType)){
					return "ERROR";
				}else{
					return "INT";
				}
			}else{
				return "ERROR";
			}
		}
		return "ERROR";
	}
	public void printPTree(MainTreeNode mainTreeNode) throws IOException {
		parsewriter.write(
				"digraph tl12Ast {\n"+
				"ordering=out;\n"+
				"node [shape = box, style = filled, fillcolor=\"white\"]\n");
		parsewriter.write("n1 [label=\""+mainTreeNode.getName()+"\",fillcolor=\"/x11/lightgrey\",shape=box]\n");
		printParseTree(mainTreeNode,1);
		parsewriter.write("\n}\n");
		parsewriter.close();
	}
}
