import java.io.*;
import java.util.Scanner;

public class SmallVm {

	public static final int MAX_MEMORY_SIZE = 1000;
	public static Descriptor[] dataMemory;
	public static int dataSlotsInUse = 0;
	public static String[] instructionsMemory;
	public static boolean programIsRunning = false;
	
	public static BufferedWriter bufferedWriter;
	
	public static void main(String[] args) {
		
		// BufferedWriter used for printing output to text file.
		try {
			bufferedWriter = new BufferedWriter(new FileWriter("mySmallVm_Output.txt"));
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		writeToOutputFile("Andrew Blackwell, CSCI 4200, Fall 2023");
		writeToOutputFile("****************************************");
        loadInstructionsToMemory();
        writeToOutputFile("****************************************");
        
        
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
				writeToOutputFile(instructionsMemory[counter]);
			    counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 
	 */
	public static void writeToOutputFile(String string) {
		try {
			bufferedWriter.write(string + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(string);
	}
	
	
	/*
	 * Starts the fetch, decode, execute cycle from the instruction at memory index 0
	 * Will end upon reading HALT instruction
	 */
	public static void runProgram() {
		
		dataMemory = new Descriptor[MAX_MEMORY_SIZE];
		int programCounter = 0;
		programIsRunning = true;
		
		while(programIsRunning) {
			
			// split statement into individual lexemes
			String[] statementLexemes = instructionsMemory[programCounter].split(" ");
			String operation = statementLexemes[0];
			String[] operands = removeOperatorLexeme(statementLexemes);
			
			switch (operation) {
			case "ADD":
				add(operands[0], operands[1], operands[2]);
				break;
			case "SUB":
				sub(operands[0], operands[1], operands[2]);
				break;
			case "MUL":
				mul(operands[0], operands[1], operands[2]);
				break;
			case "DIV":
				div(operands[0], operands[1], operands[2]);
				break;
			case "IN":
				in(operands[0]);
				break;
			case "OUT":
				// Join strings together since OUT can only have 1 operand.
				// Do this if output is supposed to be a text string and got split into separate parts.
				String output = joinStrings(operands);
				out(output);
				break;
			case "STO":
				sto(operands[0], operands[1]);
				break;
			case "HALT":
				halt();
				break;
			case ";":
				//do nothing
				break;
			}
			programCounter++;
		}
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * ADD, SUB, MUL, DIV Instructions. Takes in two source values, performs the math operation, and then stores
	 * the result in the specified destination.
	 */
	public static void add(String destination, String source1, String source2) {
		int sum = getValueFromString(source1) + getValueFromString(source2);
		sto(destination, String.valueOf(sum));
	}
	
	public static void sub(String destination, String source1, String source2) {
		int difference = getValueFromString(source1) - getValueFromString(source2);
		sto(destination, String.valueOf(difference));
	}
	
	public static void mul(String destination, String source1, String source2) {
		int product = getValueFromString(source1) * getValueFromString(source2);
		sto(destination, String.valueOf(product));
	}
	
	public static void div(String destination, String source1, String source2) {
		int quotient = getValueFromString(source1) / getValueFromString(source2);
		sto(destination, String.valueOf(quotient));
	}
	
	
	/*
	 * IN Instruction. Waits for the user to type in an Integer value then stores it in specified destination.
	 */
	public static void in(String destination) {
		
		Scanner scanner = new Scanner(System.in);
		int source = scanner.nextInt();
		sto(destination, String.valueOf(source));
		writeToOutputFile(String.valueOf(source));
	}
	
	
	/*
	 * OUT Instruction. Prints out value.
	 */
	public static void out(String output) {
		
		String actualOutput = "";
		
		// Text string
		if (output.charAt(0) == '"' && output.charAt(output.length() - 1) == '"')
		{
			actualOutput = output.substring(1, output.length() - 1);
		} 
		// Variable value
		else {
			actualOutput = String.valueOf(dataMemory[getVariableIndex(output)].value);
		}
		writeToOutputFile(actualOutput);
	}
	
	
	/*
	 * STO Instruction. Stores specified source value into a destination variable.
	 */
	public static void sto(String destination, String source) {
		
		// Source is an Integer value
		if (isNumeric(source))
		{
			if (getVariableIndex(destination) == -1) {
				dataMemory[dataSlotsInUse] = new Descriptor(destination, Integer.parseInt(source));
				dataSlotsInUse++;
			} else {
				dataMemory[getVariableIndex(destination)].value = Integer.parseInt(source);
			}
		} 
		// Source is a name of another variable
		else {
			if (getVariableIndex(destination) == -1) {
				dataMemory[dataSlotsInUse] = new Descriptor(destination, dataMemory[getVariableIndex(source)].value);
				dataSlotsInUse++;
			} else {
				dataMemory[getVariableIndex(destination)].value = dataMemory[getVariableIndex(source)].value;
			}
		}
	}
	
	
	/*
	 * HALT Instruction. Ends the program.
	 */
	public static void halt() {
		programIsRunning = false;
	}
	
	
	/*
	 * This is a helper method. Used for joining an array of strings into one string.
	 */
	public static String joinStrings(String[] strings) {
		String joinedString = "";
		for (int i = 0; i < strings.length; ++i) {
			joinedString = joinedString.concat(strings[i] + " ");
		}
		return joinedString.trim();
	}
	
	
	/*
	 * This is a helper method. Simply removes the first lexeme in a string array of lexemes and shifts over the operands.
	 * Assuming that the first string in the array is the operator.
	 */
	public static String[] removeOperatorLexeme(String[] lexemes) {
		String[] operands = new String[lexemes.length - 1];
		for (int i = 0; i < lexemes.length - 1; ++i) {
			operands[i] = new String(lexemes[i + 1]);
		}
		return operands;
	}
	
	
	/*
	 * This is a helper method. Used for searching memory and returning memory index value of a specified variable.
	 * Return -1 if the name of the variable is not found.
	 */
	public static int getVariableIndex(String varName) {
		
		for (int index = 0; index < dataSlotsInUse; ++index) {
			if (dataMemory[index].name.equals(varName)) {
				return index;
			}
		}
		return -1;
	}
	
	
	/*
	 * This is a helper method. Take a string and if it is a numerical value it will return that value.
	 * If it is a variable name, then it will return the value of that variable.
	 */
	public static int getValueFromString(String string) {
		if (isNumeric(string)) {
			return Integer.parseInt(string);
		} else {
			return dataMemory[getVariableIndex(string)].value;
		}
	}
	
	
	/*
	 * This is a helper method. Used for checking if a string is an Integer value.
	 */
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int i = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
}
