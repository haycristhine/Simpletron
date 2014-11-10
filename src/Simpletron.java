
import java.util.Scanner;

public class Simpletron {
	protected int[] memory;
	
	protected int cnt = 0;
	protected int accumulator = 0;
	protected int instructionCounter = 0;
	protected int instructionRegister = 0;
	protected int operationCode = 0;
	protected int operand = 0;
	
	public final int READ = 10;
	protected final int WRITE = 11;
	protected final int LOAD = 20;
	protected final int STORE = 21;
	protected final int ADD = 30;
	protected final int SUBTRACT = 31;
	protected final int DIVIDE = 32;
	protected final int MULTIPLY = 33;
	protected final int BRANCH = 40; // Desvia para uma posição específica na memória 
	protected final int BRANCHNEG = 41; // Se o acumulador for negativo desvia para uma posição específica na memória 
	protected final int BRANCHZERO = 42; // Se o acumulador for 0, desviar para uma posição específica na memória
	protected final int HALT = 43; //Parar o programa
	protected final int limit = 100;
	
	private Scanner s = new Scanner(System.in);
	
	protected void startMessage() {
		System.out.println("***  Welcome to Simpletron!                      ***");
		System.out.println("***  Please enter your program one instruction   ***");
		System.out.println("***  (or data word) at a time. I will display    ***");
		System.out.println("***  the location number and a question mark (?) ***");
		System.out.println("***  You then type the word for that location.   ***");
		System.out.println("***  Type -99999 to stop entering your program.   ***");
	}
	
	public Simpletron() { // Construtor
		memory = new int[limit];
		this.startMessage();
	}
	
	public void addWord(int word) {
		if (cnt <= this.limit - 1) {
			memory[cnt] = word;
			cnt++;
		}
	}

	protected int numberOrder(int n) {
		int counter;
		for (counter = 1; n % Math.pow(10, counter) != n; counter++);
		return counter;
	}
	
	int isolateOperationCode(int command) {
		return command / (int) Math.pow(10, numberOrder(command) - 2);
	}
	
	int isolateOperationCode(int command, int size) {
		return command / (int) Math.pow(10, size - 2);
	}
	
	int isolateOperand(int command /*224*/) {
		int size = numberOrder(command);
		return (command - isolateOperationCode(command, size) * (int)Math.pow(10, size - 2));
	}
	
	protected void printNumber(int number) {
		if (number < 0) {
			System.out.print("-");
			number = Math.abs(number);
		}
		else System.out.print("+");
		
		int size = this.numberOrder(number);
		
		for(int i = 4; i > size; i--) {
			System.out.print("0");
		}
		
		System.out.print(number);
	}
	
	protected void dump() {
		System.out.println("\nRegisters:\n");
		
		System.out.print("accumulator:\t\t");
		this.printNumber(this.accumulator);
		
		System.out.print("\ninstructionCounter:\t");
		this.printNumber(this.instructionCounter);
		
		System.out.print("\ninstructionRegister:\t");
		this.printNumber(this.instructionRegister);
		
		System.out.print("\ninstructionRegister:\t");
		this.printNumber(this.instructionRegister);
		
		System.out.print("\noperationCode:\t\t");
		this.printNumber(this.operationCode);
		
		System.out.print("\noperand:\t\t");
		this.printNumber(this.operand);
		
		System.out.println("\n\nMemory: \n");
		
		for(int i = 0; i < 10; i++) {
			for(int j = i * 10; j < (i + 1) * 10; j++) {
				this.printNumber(this.memory[j]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	public void load() {
		int word = 0;
		while(word != -99999) {
			if(this.cnt >= this.limit) break;
			if (this.cnt < 10) System.out.print("0");
			
			System.out.print(this.cnt + " ? ");
			word = s.nextInt();
			
			if (word <= 9999 && word >= -9999) {
				this.addWord(word);
			}
		}
		
		System.out.println("\n*** Program loading completed ***");
		System.out.println("*** Program execution begins ***");
	}
	
	// Erros
	protected void overflowMsg() {
		System.out.println("\n*** Accumulator overflow! ***");
		System.out.println("*** Simpletron execution abnormally terminated ***");
		System.out.println();
	}
	
	public void invalidOperationCodeMsg() {
		System.out.println("*** Invalid operationCode. Memory operand is not valid ***");
	}
	
	protected void invalidMemoryOperand() {
		System.out.println("*** Memory operand is not valid ***");
	}
	
	protected boolean operandIsValid(int operand) {
		return (operand >= 0 && operand < this.limit);
	}
	
	protected boolean switchOperationCode(int command) {
		operationCode = isolateOperationCode(command);
		operand = isolateOperand(command);
		
		if (operand <= this.limit - 1) {
			switch(operationCode) {
				// ------- Código de operação --------
				case READ: 
					if(this.operandIsValid(operand)) {
						System.out.println("\n*** Enter an integer ***");
						int x = s.nextInt();
						this.memory[operand] = x; 
					}
					else {
						this.invalidMemoryOperand();
					}
					break;
					
				case WRITE:
					int number = this.memory[operand];

					System.out.println();
					this.printNumber(number);
					System.out.println();
					
					break;
					
				// ------ Operações de carregamento/ armazenamento ------
					
				case LOAD: //Traz o que está em certa posição na memória para o acumulador
					this.accumulator = this.memory[operand];
					break;
					
				case STORE: // Armazena o acumulador em certa posição da memória 
					this.memory[operand] = this.accumulator;
					break;
					
				// ------ Operações aritméticas ------
					
				case ADD: // Adição
					if (this.accumulator + this.memory[operand] <= 9999 && this.accumulator + this.memory[operand] >= -9999) {
						this.accumulator += this.memory[operand];
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
					
				case SUBTRACT: // Subtração
					if (this.accumulator - this.memory[operand] <= 9999 && this.accumulator - this.memory[operand] >= -9999) {
						this.accumulator -= this.memory[operand];
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
					
				case DIVIDE: // Divisão
					if (this.memory[operand] == 0) {
						System.out.println("\n*** Attempt to divide by zero ***");
						System.out.println("*** Simpletron execution abnormally terminated ***");
						
						return false;
					}
					else {
						this.accumulator /= this.memory[operand];
						break;
					}
				
				case MULTIPLY: // Multiplicação
					if (this.accumulator * this.memory[operand] <= 9999 && this.accumulator * this.memory[operand] >= -9999) {
						this.accumulator *= this.memory[operand];
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
					
				// ----- Operações de transferência de controle -----
				case BRANCH: // Desvia para uma posição específica na memória
					if (this.operandIsValid(operand)) {
						this.instructionCounter = operand - 1;
					}
					else {
						this.invalidOperationCodeMsg();
					}
					
					break;
					
				case BRANCHNEG: // Desvia se o acumulador for negativo
					if (this.operandIsValid(operand)) {
						if (this.accumulator < 0) {
							this.instructionCounter = operand - 1;
						}
					}
					else {
						this.invalidOperationCodeMsg();
					}
					
					break;
					
				case BRANCHZERO: // Desvia se o acumulador for 0
					if (this.operandIsValid(operand)) {
						if (this.accumulator == 0) {
							this.instructionCounter = operand - 1;
						}
					}
					else {
						this.invalidOperationCodeMsg();
					}
					
					break;
					
				case HALT:
					System.out.println("\n*** Simpletron execution terminated ***");
					return false;
					
				// Mensagem padrão
				default: System.out.println("\n*** Comando inválido. ***");
			}
			
			return true;
		}
		
		return false;
	}
	
	public void execute() {
		for(this.instructionCounter = 0; this.instructionCounter < cnt; this.instructionCounter++) { 
			// O indicador da posição é usado no loop, devido às operações de BRANCH
			// Caso haja algum Branch no método switchoperationCode, esse cuidará de mudar o counter
			this.instructionRegister = this.memory[this.instructionCounter];
 			if(!this.switchOperationCode(this.instructionRegister)) {
				break;
			}
		}
		
		dump();
	}
	
	public void closeScanner() {
		s.close();
	}
		
	public static void main(String[] args) {
		Simpletron simp = new Simpletron();
		simp.load();
		simp.execute();
		simp.closeScanner();
	}
}
