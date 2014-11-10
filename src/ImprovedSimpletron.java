import java.util.Scanner;

public class ImprovedSimpletron extends Simpletron {
	public final int READ = 10;//
	protected float accumulator = 0;//
	protected final int WRITE = 11;//
	protected final int LOAD = 20;//
	protected final int STORE = 21;//
	protected final int ADD = 30;//
	protected final int SUBTRACT = 31;//
	protected final int DIVIDE = 32;//
	protected final int MULTIPLY = 33;//
	protected final int BRANCH = 40; // Desvia para uma posição específica na memória 
	protected final int BRANCHNEG = 41; // Se o acumulador for negativo desvia para uma posição específica na memória 
	protected final int BRANCHZERO = 42; // Se o acumulador for 0, desviar para uma posição específica na memória
	protected final int HALT = 43; //Parar o programa
	
	private final int READFLOAT = 12; //
	private final int READSTRING = 13;
	private final int WRITEFLOAT = 14; //
	private final int WRITESTRING = 15;
	
	private final int LOADFLOAT = 22;
	private final int STOREFLOAT = 23;
	
	private final int ADDFLOAT = 34;
	private final int SUBTRACTFLOAT = 35;
	private final int DIVIDEFLOAT = 36;
	private final int MULTIPLYFLOAT = 37;
	
	private final int RAD = 50;
	private final int EXP = 51;
	
	private final int limit = 8192;
	
	private Scanner s = new Scanner(System.in);
	private Scanner f = new Scanner(System.in);// melhor usar scanners separados para cada tipo
	private Scanner str = new Scanner(System.in);
	
	public ImprovedSimpletron() { // Construtor
		this.memory = new int[limit];
	}
	
	@Override
	public void closeScanner(){
		s.close();
		f.close();
		str.close();
	}
	
	
	@Override
	public void load() {
		int word = 0;
		while(word != -99999) {
			if(this.cnt >= this.limit) break;
			if (this.cnt < 10) System.out.print("0");
			
			System.out.print(this.cnt + " ? ");
			word = s.nextInt();
			if (word <= 999999 && word >= -9999) {
				this.addWord(word);
			}
		}
		
		System.out.println("\n*** Program loading completed ***");
		System.out.println("*** Program execution begins ***");
	}
	
	protected boolean operandIsValid(int operand) {
		return (operand >= 0 && operand < this.limit);
	}

	//Checagem do comando
	@Override
	protected boolean switchOperationCode(int command) {
		operationCode = this.isolateOperationCode(command);
		operand = this.isolateOperand(command);
		
		if (operand <= this.limit - 1) {
			switch(operationCode) {
				// ------- Código de operação --------
				case READ: 
					System.out.println("\n*** Enter an integer ***");
					int x = s.nextInt();
					this.memory[operand] = x; 
					
					break;
					
				case READSTRING:
					System.out.println("\n*** Enter a String ***");
					String word = str.nextLine();
					this.memory[operand] = word.length();
					for (int i = 0; i < word.length(); i++) {
						this.memory[operand + i + 1] = Character.getNumericValue(word.charAt(i));
					}
					
					break;
					
				case WRITESTRING:
					String wordtowrite = "";
					for (int i = 0; i <= this.memory[operand]; i++){
						wordtowrite += (char) this.memory[operand +i+1];
					}
					
					System.out.println(wordtowrite);
					
					break;
					
				case RAD:
					if ((float) Math.pow(this.accumulator,1/this.memory[operand]) <= 9999 && (float) Math.pow(this.accumulator,1/this.memory[operand]) >= -9999) {
						this.accumulator = (float) Math.pow(this.accumulator,1/this.memory[operand]);
						break;
					}else {
						this.overflowMsg();
						return false;
					}

					
				case EXP:
					if ((float) Math.pow(this.accumulator,this.memory[operand]) <= 9999 && (float) Math.pow(this.accumulator,this.memory[operand]) >= -9999) {
					this.accumulator = (float) Math.pow(this.accumulator,this.memory[operand]);
					break;
					}else{
						this.overflowMsg();
						return false;
					}
					
				case STOREFLOAT:
					this.memory[operand] = Float.floatToIntBits(this.accumulator);
					break;
					
				case LOADFLOAT:
					this.accumulator = Float.intBitsToFloat(this.memory[operand]);
					break;
					
				case ADDFLOAT:
					if (this.accumulator + Float.intBitsToFloat(this.memory[operand]) <= 9999 && this.accumulator + Float.intBitsToFloat(this.memory[operand]) >= -9999) {
						this.accumulator += Float.intBitsToFloat(this.memory[operand]);
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
				
				case SUBTRACTFLOAT:
					if (this.accumulator - Float.intBitsToFloat(this.memory[operand]) <= 9999 && this.accumulator - Float.intBitsToFloat(this.memory[operand]) >= -9999) {
						this.accumulator -= Float.intBitsToFloat(this.memory[operand]);
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
					
				case DIVIDEFLOAT:
					if (Float.intBitsToFloat(this.memory[operand]) == 0) {
						System.out.println("\n*** Attempt to divide by zero ***");
						System.out.println("*** Simpletron execution abnormally terminated ***");
						
						return false;
					}
					else {
						this.accumulator /= Float.intBitsToFloat(this.memory[operand]);
						break;
					}
					
				case MULTIPLYFLOAT:
					if (this.accumulator * Float.intBitsToFloat(this.memory[operand]) <= 9999 && this.accumulator * Float.intBitsToFloat(this.memory[operand]) >= -9999) {
						this.accumulator *= Float.intBitsToFloat(this.memory[operand]);
						break;
					}
					else {
						this.overflowMsg();
						return false;
					}
					
					
				case WRITE:
					int number = this.memory[operand];

					System.out.println();
					this.printNumber(number);
					System.out.println();
					
					break;
				
				case READFLOAT: 
					if(this.operandIsValid(operand)) {
						System.out.println("\n*** Enter a float number ***");
						float r = f.nextFloat();
						this.memory[operand] = Float.floatToIntBits(r);
					}
					
					break;
					
				case WRITEFLOAT:
					float f = Float.intBitsToFloat(this.memory[operand]);
					System.out.println(f);
					break;
				// ------ Operações de carregamento/ armazenamento ------
					
				case LOAD: //Traz o que está em certa posição na memória para o acumulador
					this.accumulator = this.memory[operand];
					break;
					
				case STORE: // Armazena o acumulador em certa posição da memória 
					this.memory[operand] = (int) this.accumulator;
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
	
	public static void main(String[] args) {
		ImprovedSimpletron simp = new ImprovedSimpletron();
		simp.load();
		simp.execute();
		simp.closeScanner();
	}
}