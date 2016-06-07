

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Queue;

public class Compiler{
	static HashMap<String, HashMap<String, String>> parseTable;
	public static void main(String args[]) throws IOException, LexicalErrorException, ParserErrorException{
		LexicalAnalyser lexicalAnalyser = new LexicalAnalyser();
		Queue<String> tokenQueue;
		MainTreeNode mainTreeNode;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("enter file name");
		String inputFileName = br.readLine();
		
//		String inputFileName = args[0];

		//get the Token Stream
//		String inputFileName = "primer.tl";
		try {
			tokenQueue = lexicalAnalyser.getTokenStream(inputFileName);
		} catch (IOException e) {
			System.out.println("IOException caught in Compiler");
			e.printStackTrace();
			throw e;
		} catch (LexicalErrorException lee) {
			System.out.println("LexicalErrorException caught in Compiler");
			lee.printStackTrace();
			throw lee;
		}
		
		//get the parseTable
		FindFirstSets findFirstSets = new FindFirstSets();
		parseTable = findFirstSets.getParseTable();
		
		//parseInput
		Parser parser = new Parser(parseTable);
		mainTreeNode = parser.parseTokenQueue(tokenQueue);
		
		//print the parse tree
		TreePrinter treePrinter = new TreePrinter(inputFileName);
		treePrinter.printPTree(mainTreeNode);
		
		//build the AST
		ASTBuilder astBuilder = new ASTBuilder();
		ASTTree astTree =  astBuilder.buildAST(mainTreeNode);
				
		//print the AST
		System.out.println("-------------------------------------");
		boolean isError = treePrinter.printAST(astTree,astBuilder.getSymbolTable(),astBuilder.getRegisterTable());		
		System.out.println("-------------------------------------");
		
		if(isError){
			System.out.println("Semantic error occured");
			System.out.println("check the output files "+inputFileName.substring(0, inputFileName.length()-3)+".tok, "+inputFileName.substring(0, inputFileName.length()-3)+"-parse.dot, "+inputFileName.substring(0, inputFileName.length()-3)+"-ast.dot in the workdirectory");
		}else{
			System.out.println("Machine code genrated successfully");
			System.out.println(
			"check the output files "+inputFileName.substring(0, inputFileName.length()-3)+".tok, "+
			inputFileName.substring(0, inputFileName.length()-3)+"-parse.dot, "+
			inputFileName.substring(0, inputFileName.length()-3)+"-ast.dot,"+
			inputFileName.substring(0, inputFileName.length()-3)+".3A.cfg.dot,"+
			inputFileName.substring(0, inputFileName.length()-3)+".s"+
			" in the workdirectory"			
					);
			
		}
	}
}