

import java.io.*;

public class FileOperations {
	File file;
	FileWriter writer;
	String fileName;
	String outputFileName;
	FileReader fileReader; 
    BufferedReader bufferedReader; 

	public FileOperations(String fileName) {
		this.fileName = fileName;
		outputFileName = fileName.substring(0, fileName.length()-3)+".tok";
	}

	public void readInputFile() throws FileNotFoundException {
	    try {
	    	fileReader = new FileReader(fileName);
	        bufferedReader = new BufferedReader(fileReader);
       }catch(FileNotFoundException ex) {
    	   throw ex;
       }
	}

	public void createFileWriter() {
		file = new File(outputFileName);
		try {
			file.createNewFile();
			writer = new FileWriter(file); 
		} catch (IOException e) {
			System.out.println("Error!! Unknown IOException occured while trying to create write stream");
			e.printStackTrace();
      	}
	}
	   
   public void closeFiles() {
	   try {
		   writer.flush();
		   writer.close();
		   fileReader.close();
		   bufferedReader.close();
	   } catch (IOException e) {
		   System.out.println("Error!! Couldn't close the files");
		   e.printStackTrace();
	   }
   }

	public String getNextLine() {
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println("Error!! Unknown IOException occured while trying to read next line");
			e.printStackTrace();
		}
		return null;
	}

	public void writeToFile(String string) {
		try {
			writer.write(string);
		} catch (IOException e) {
			System.out.println("Error!! Unknown IOException occured while trying to write to the file");
			e.printStackTrace();
		}
	}
}
