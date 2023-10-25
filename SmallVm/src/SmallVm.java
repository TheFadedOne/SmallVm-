import java.io.*;

public class SmallVm {

	public static final int MAX_MEMORY_SIZE = 1000;
	public static Descriptor[] dataMemory;
	public static String[] instructionsMemory;
	public static boolean programIsRunning = false;
	
	public static void main(String[] args) {
		
        loadInstructionsToMemory();
        runProgram();
	}

	/*
	 * Loads the instructions into memory
	 */
	public static void loadInstructionsToMemory() {
		
		instructionsMemory = new String[MAX_MEMORY_SIZE];
		File file = new File("mySmallVm_Prog.txt");
		BufferedReader bufferedReader = null;
		 
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        String string;
        try {
        	//load each individual instruction to memory
        	int counter = 0;
			while ((string = bufferedReader.readLine()) != null) {
				instructionsMemory[counter] = string;
				System.out.println(instructionsMemory[counter]);
			    counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Starts the fetch, decode, execute cycle from the instruction at memory index 0
	 * Will end upon reading HALT instruction
	 */
	public static void runProgram() {
		
		dataMemory = new Descriptor[MAX_MEMORY_SIZE];
		int programCounter = 0;
		
		while(true) {
			
		}
	}
	
}
