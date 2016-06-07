

import java.io.*;
import java.util.*;

public class LexicalAnalyser {
	Hashtable<String,String> symbol = new Hashtable<String, String>();
	Queue<String> tokenQueue = new LinkedList<String>();
	File file;
	FileWriter writer;
	static TokenValidator tokenValidator;
	public  Queue<String> getTokenStream(String fileName) throws IOException, LexicalErrorException {
//	   String fileName = null ;
	   String nextLine, nextTLToken;
//	   BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	   FileOperations fileOperations ;
	   tokenValidator = new TokenValidator();
	   //Read the file name
//	   System.out.println("Enter the source file name");
//	   fileName = br.readLine();
//	   fileName = args[0];
	   //Create Read and write streams
	   fileOperations = new FileOperations(fileName);
	   try {
		   fileOperations.readInputFile();
		   fileOperations.createFileWriter();
	   } catch (FileNotFoundException e) {
		   System.out.println("Error!! File not found..Please enter correct file name.");
		   e.printStackTrace();
	   }
	   
	   //Read newLine and process the line
	   nextLine = fileOperations.getNextLine();
	   while(nextLine != null){
		   try {
				StringTokenizer st = new StringTokenizer(nextLine);
				while(st.hasMoreTokens()){
					String nextLexeme = st.nextToken();
					if(!isComment(nextLexeme)){
						nextTLToken = tokenValidator.getToken(nextLexeme);
						fileOperations.writeToFile(nextTLToken+"\n");
						tokenQueue.add(nextTLToken);
					}else{
						break;
					}
				}
				nextLine = fileOperations.getNextLine();
		   } catch (LexicalErrorException lee) {
			   System.out.println("Lexical Error!!");
			   fileOperations.writeToFile("Lexical Error!!");
			   throw new LexicalErrorException();
		   }
	   }
	   
	   //close the files streams
	   fileOperations.closeFiles();
	   return tokenQueue;
	}
	
	private static boolean isComment(String str) {
		if(str.charAt(0) == '%'){
			return true;
		}		
		return false;
	}   		
}
