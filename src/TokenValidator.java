

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenValidator {
	Hashtable<String, String> symbol; 
	public TokenValidator(){
		symbol = new Hashtable<String, String>();
		symbol.put("(", "LP");
		symbol.put(")", "RP");
		symbol.put(":=", "ASGN");
		symbol.put(";", "SC");
		symbol.put("*", "MULTIPLICATIVE(*)");
		symbol.put("div", "MULTIPLICATIVE(div)");
		symbol.put("mod","MULTIPLICATIVE(mod)");
		symbol.put("+", "ADDITIVE(+)");
		symbol.put("-", "ADDITIVE(-)");
		symbol.put("=","COMPARE(=)");
		symbol.put("!=","COMPARE(!=)");
		symbol.put("<","COMPARE(<)");
		symbol.put("<=","COMPARE(<=)");
		symbol.put(">","COMPARE(>)");
		symbol.put(">=","COMPARE(>=)");
		
		symbol.put("false", "boollit(false)");
		symbol.put("true", "boollit(true)");
		
		symbol.put("if", "IF");
		symbol.put("then", "THEN");
		symbol.put("else", "ELSE");
		symbol.put("begin", "BEGIN");
		symbol.put("end", "END");
		symbol.put("while", "WHILE");
		symbol.put("do", "DO");
		symbol.put("program", "PROGRAM");
		symbol.put("var", "VAR");
		symbol.put("as", "AS");
		symbol.put("int", "INT");
		symbol.put("bool", "BOOL");
		
		symbol.put("writeint", "WRITEINT");
		symbol.put("readint", "READINT");
   }
	
	public String getToken(String string) throws LexicalErrorException{
		String token;
	    CharSequence number=string;
	    Pattern testPattern= Pattern.compile("[1-9][0-9]*|0");
		Matcher teststring= testPattern.matcher(number);
		Pattern testPattern2= Pattern.compile("[A-Z][A-Z0-9]*");
		Matcher teststring2 = testPattern2.matcher(number);
		int num = 0;
		if(teststring.matches())
		{
			try{
				num = Integer.parseInt(string);
			}catch(NumberFormatException nfe){
				System.out.println("Number out of range.."+num);
				throw new LexicalErrorException();
			}			
			token = "num("+string+")";
		}
		else if(teststring2.matches())
		{
			token = "ident("+string+")";
		}else if(symbol.containsKey(string)){
			token = symbol.get(string);
		}else {
			throw new LexicalErrorException();
		}
		return token;
	}
}
