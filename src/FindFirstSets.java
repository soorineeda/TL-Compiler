

import java.util.*;
public class FindFirstSets {
	MultiMap firstSets = new MultiMap();
	MultiMap intermediateFirstSets = new MultiMap();
	MultiMap followSets = new MultiMap();
	MultiMap followMaps = new MultiMap();
	HashMap<String,HashMap<String,String>> parseTable = new HashMap<String,HashMap<String,String>>(); 
	MultiMap tempSet;
	static String str = 
		"<program>::=PROGRAM <declarations> BEGIN <statementSequence> END\n"+
		"<declarations>::=VAR ident AS <type> SC <declarations>|e\n"+
		"<type>::=INT|BOOL\n"+
		"<statementSequence>::=<statement> SC <statementSequence>|e\n"+
		"<statement>::=<assignment>|<ifStatement>|<whileStatement>|<writeInt>\n"+
		"<assignment>::=ident ASGN <assignment2>\n"+
		"<assignment2>::=<expression>|READINT\n"+
		"<ifStatement>::=IF <expression> THEN <statementSequence> <elseClause> END\n"+
		"<elseClause>::=ELSE <statementSequence>|e\n"+
		"<whileStatement>::=WHILE <expression> DO <statementSequence> END\n"+
		"<writeInt>::=WRITEINT <expression>\n"+
		"<expression>::=<simpleExpression> <expression2>\n"+
		"<expression2>::=e|COMPARE <expression>\n"+
		"<simpleExpression>::=<term> <simpleExpression2>\n"+
		"<simpleExpression2>::=ADDITIVE <simpleExpression>|e\n"+
		"<term>::=<factor> <term2>\n"+
		"<term2>::= MULTIPLICATIVE <term>|e\n"+
		"<factor>::=ident|num|boollit|LP <expression> RP\n";
	
	public HashMap<String, HashMap<String, String>> getParseTable(){
		FindFirstSets findFirstSets = new FindFirstSets();
		findFirstSets.findFirstSets();
		findFirstSets.findFollowSets();
		findFirstSets.createParseTable();	
		return findFirstSets.parseTable;
	}
	
	private void createParseTable() {
		StringTokenizer st = new StringTokenizer(str,"\n");
		while(st.hasMoreTokens())
		{
			String newLine = st.nextToken();
			StringTokenizer productions = new StringTokenizer(newLine,"::=");
			String leftProd = productions.nextToken();
			String rightLine = productions.nextToken();
			StringTokenizer rightProds = new StringTokenizer(rightLine,"|");
			HashMap<String,String> hm = new HashMap<String,String>();
			while(rightProds.hasMoreTokens()){
				String rightProd = rightProds.nextToken();
				if("e".equals(rightProd)){
					ArrayList<String> arr  = followSets.get(leftProd);
					Iterator<String> it = arr.iterator();
					while(it.hasNext()){
						String nextNonTerminal = it.next();
						hm.put(nextNonTerminal,"e");
					}											
//						continue;
				}else{
					boolean indicator = false;
					ArrayList<String> arr = intermediateFirstSets.get(rightProd);
					Iterator<String> it = arr.iterator();
					while(it.hasNext()){
						String nextNonTerminal = it.next();					
						if(!"e".equals(nextNonTerminal)){
							hm.put(nextNonTerminal,rightProd);
						}else{
							indicator = true;
						}						
					}
					if(indicator){
						arr = followSets.get(leftProd);
						it = arr.iterator();
						while(it.hasNext()){
							String nextNonTerminal = it.next();
							hm.put(nextNonTerminal,"e");
						}					
					}
				}
			}
			parseTable.put(leftProd, hm);
		}
		printParseTable();
	}

	private void printParseTable() {
		Set set = parseTable.entrySet();
	    // Get an iterator
	    Iterator i = set.iterator();
	    // Display elements
	    while(i.hasNext()) {
	    	Map.Entry me = (Map.Entry)i.next();
	    }
	}

	public void findFollowSets() {

		followMaps = new MultiMap();
		followSets.put("<program>", "$");
		StringTokenizer st = new StringTokenizer(str,"\n");
		while(st.hasMoreTokens())
		{
			String newLine = st.nextToken();
			StringTokenizer productions = new StringTokenizer(newLine,"::=");
			String leftProd = productions.nextToken();
			String rightLine = productions.nextToken();
			StringTokenizer rightProds = new StringTokenizer(rightLine,"|");
			while(rightProds.hasMoreTokens()){
				String rightProd = rightProds.nextToken();
				StringTokenizer rightElements = new StringTokenizer(rightProd);
				ArrayList<String> arr = new ArrayList<String>();
				while(rightElements.hasMoreTokens()){
					arr.add(rightElements.nextToken());
				}
				for(int i = 0;i<arr.size();i++)
				{
					int j = i+1;
					String currentSymbol = arr.get(i);
					int c = (int) currentSymbol.charAt(0);
					if(c == 101){ //101 --> e
						//null production do nothing.
					}else if(c == 60){ // 60 --> <
						boolean repeat = true;
						while(repeat)
						{
							repeat = false;
							String nextSymbol;
							if(j < arr.size()){
								nextSymbol = arr.get(j);
							}else{
								nextSymbol = null;
							}
							if(nextSymbol == null){
								followSets.put(currentSymbol,followSets.get(leftProd));
								ArrayList<String> mapsList = followMaps.get(currentSymbol);
								if(mapsList != null){
									for(int k = 0;k<mapsList.size();k++){
										followSets.put(mapsList.get(k), followSets.get(currentSymbol));
									}
								}
								if(!leftProd.equals(currentSymbol))
									followMaps.put(leftProd, currentSymbol);
//								i = arr.size();//breaking for loop..
							}else if((int)nextSymbol.charAt(0) != 60){
								followSets.put(currentSymbol, nextSymbol);
								tempSet = new MultiMap();
								updateDependentFollowSets(currentSymbol);
								tempSet = null;
							}else{
								followSets.put(currentSymbol,firstSets.get(nextSymbol));
								if(firstSets.exists(nextSymbol, "e")){
									repeat = true;
									j++;
								}
								ArrayList<String> mapsList = followMaps.get(currentSymbol);
								if(mapsList != null){
									for(int k = 0;k<mapsList.size();k++){
										followSets.put(mapsList.get(k), followSets.get(currentSymbol));
									}
								}
	//							i = arr.size();//breaking for loop..							
							}
						}
					}
					else{
						//terminal do nothing..
					}
				}
			}
			
		}
		
		st = new StringTokenizer(str,"\n");
		while(st.hasMoreTokens())
		{
			String newLine = st.nextToken();
			StringTokenizer productions = new StringTokenizer(newLine,"::=");				
			String leftProd = productions.nextToken();	
			followSets.removeNullProds(leftProd);
			ArrayList<String> arr = followSets.get(leftProd);
		}
	}
	
	private void updateDependentFollowSets(String currentSymbol) {
		tempSet.put("temp", currentSymbol);
		ArrayList<String> mapsList = followMaps.get(currentSymbol);
		if(mapsList != null){
			for(int k = 0;k<mapsList.size();k++){
				followSets.put(mapsList.get(k), followSets.get(currentSymbol));
				if(!tempSet.exists("temp", mapsList.get(k)))
					updateDependentFollowSets(mapsList.get(k));
			}
		}

	}

	public void findFirstSets(){

			StringTokenizer st = new StringTokenizer(str,"\n");
			while(st.hasMoreTokens())
			{
				String newLine = st.nextToken();
				StringTokenizer productions = new StringTokenizer(newLine,"::=");
				String leftProd = productions.nextToken();	
				findTheDependentFirstSet(leftProd);
			}
			
			st = new StringTokenizer(str,"\n");
			while(st.hasMoreTokens())
			{
				String newLine = st.nextToken();
				StringTokenizer productions = new StringTokenizer(newLine,"::=");				
				String leftProd = productions.nextToken();	
				ArrayList<String> arr = firstSets.get(leftProd);
			}
	}
	
	public void findTheDependentFirstSet(String nonTerminal){
		StringTokenizer st = new StringTokenizer(str,"\n");
		while(st.hasMoreTokens()){
			String newLine = st.nextToken();
			StringTokenizer productions = new StringTokenizer(newLine,"::=");
			String leftProd = productions.nextToken();	
			if(leftProd.equals(nonTerminal)){				
				while(productions.hasMoreTokens()){
					String rightLine = productions.nextToken();
					StringTokenizer rightProds = new StringTokenizer(rightLine,"|");
					while(rightProds.hasMoreTokens()){
						String rightProd = rightProds.nextToken();
						StringTokenizer rightElements = new StringTokenizer(rightProd);
						int c = (int)rightProd.charAt(0);
						if(c == 101){ //101 --> e
							firstSets.put(leftProd, "e");
							intermediateFirstSets.put(rightProd,"e");
						}else if(c == 60){ // 60 --> <
							boolean repeat = true;
							while(repeat && rightElements.hasMoreElements()){
								String nextNonTerminal = rightElements.nextToken();
								if(firstSets.get(nextNonTerminal) == null){						
									findTheDependentFirstSet(nextNonTerminal);																
								}
								{
									ArrayList<String> values = firstSets.get(nextNonTerminal);
									if(values != null){
										Iterator<String> it = values.iterator();
//										synchronized (values) 
										{
											firstSets.put(leftProd, values);	
											intermediateFirstSets.put(rightProd,values);
										}

									}
									if(firstSets.exists(nextNonTerminal, "e")){
										repeat = true;
									}else{
										repeat = false;
									}
								}
							}
						}else{
							String nextTerminal = rightElements.nextToken();
							firstSets.put(leftProd, nextTerminal);	
							intermediateFirstSets.put(rightProd,nextTerminal);
						}
					}
				}
			}
		}	
		
	}	
	}
