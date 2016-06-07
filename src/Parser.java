

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.StringTokenizer;

public class Parser {
	Iterator<String> tokenIterator ;
	String input;
	HashMap<String, HashMap<String, String>> parseTable;

	public Parser(HashMap<String, HashMap<String, String>> parseTable) {
		this.parseTable = parseTable;
	}

	public MainTreeNode parseTokenQueue(Queue<String> tokenQueue) throws ParserErrorException {
		MainTreeNode mainTreeNode;
		tokenIterator = tokenQueue.iterator();
		try {
			if(tokenIterator.hasNext())
				mainTreeNode = parseInput("<program>",tokenIterator.next());
			else
				throw new ParserErrorException("Input tokens doens't match with grammer");
		} catch (ParserErrorException pee) {
			System.out.println("Parser error..Parsing Input token stream failed");
			pee.printStackTrace();
			throw pee;
		}
		return mainTreeNode;
	}
	
	private MainTreeNode parseInput(String nonTerminal, String ninput) throws ParserErrorException {
		MainTreeNode mainTreeNode = new MainTreeNode();
		ArrayList<MainTreeNode> childList = new ArrayList<MainTreeNode>();
		mainTreeNode.setName(nonTerminal);
		mainTreeNode.setType("nonTerminal");
		
		input = ninput;
		HashMap<String, String> hm = parseTable.get(nonTerminal);
		String relatedProd = hm.get(getTrimmedInput(ninput));
		StringTokenizer st = new StringTokenizer(relatedProd);
		
		while(st.hasMoreTokens()){
			if(input == null){
				throw new ParserErrorException("Input tokens doens't match with grammer");
			}
			String nextRelatedWord = st.nextToken();
			if("e".equals(nextRelatedWord)){
				MainTreeNode newTreeNode = new MainTreeNode();
				newTreeNode.setName("e");
				newTreeNode.setType("terminal");
				childList.add(newTreeNode);
			}else if((int)nextRelatedWord.charAt(0) != 60)
			{
				MainTreeNode newTreeNode = new MainTreeNode();
				newTreeNode.setName(getTrimmedInput(input));
				newTreeNode.setType("terminal");
				newTreeNode.setValue(getValueTrimmed(input));
				childList.add(newTreeNode);
				if(!nextRelatedWord.equals(getTrimmedInput(input))){
					System.out.println("Error!!!");
					throw new ParserErrorException("Input tokens doens't match with grammer");
				}
				else if(tokenIterator.hasNext()){					
					input = tokenIterator.next();
				}else{
					input = null;
				}
			}else{
				MainTreeNode newTreeNode = parseInput(nextRelatedWord,input);
				childList.add(newTreeNode);
			}
		}
		mainTreeNode.setList(childList);
		return mainTreeNode;
	}

	private static String getValueTrimmed(String input) {
		String tempInput = input;
		if(tempInput.length() >= 5 && "ident".equals(tempInput.substring(0,5))){
			String temp = tempInput.substring(6,tempInput.length()-1);
			return temp;
		}else if(tempInput.length() >= 3 && "num".equals(tempInput.substring(0,3))){
			return tempInput.substring(4,input.length()-1);		
		}else if(tempInput.length() >= 7 && "boollit".equals(tempInput.substring(0,7))){
			return input.substring(8,input.length()-1);
		}else if(input.length() >= 14 && "MULTIPLICATIVE".equals(input.substring(0,14))){
			return input.substring(15,input.length()-1);
		}else if(input.length() >= 8 && "ADDITIVE".equals(input.substring(0,8))){
			return input.substring(9,input.length()-1);
		}else if(input.length() >= 7 && "COMPARE".equals(input.substring(0,7))){
			return input.substring(8,input.length()-1);
		}else{
			return null;
		}		
	}	
	private static String getTrimmedInput(String input) {
		if(input == null){
			return null;
		}
		else if(input.length() >= 5 && "ident".equals(input.substring(0,5))){
			return "ident";
		}else if(input.length() >= 3 && "num".equals(input.substring(0,3))){
			return "num";		
		}else if(input.length() >= 7 && "boollit".equals(input.substring(0,7))){
			return "boollit";
		}else if(input.length() >= 14 && "MULTIPLICATIVE".equals(input.substring(0,14))){
			return "MULTIPLICATIVE";
		}else if(input.length() >= 8 && "ADDITIVE".equals(input.substring(0,8))){
			return "ADDITIVE";
		}else if(input.length() >= 7 && "COMPARE".equals(input.substring(0,7))){
			return "COMPARE";
		}else{
			return input;
		}		
	}		
}
