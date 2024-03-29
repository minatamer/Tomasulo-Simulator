import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;

public class Main {
	int cycle;
	ArrayList<Float> cache;
	ArrayList<InstructionRow> instructions;
	ReservationStations reservationStationsAdd;
	ReservationStations reservationStationsMul;
	Buffers buffersLoad;
	Buffers buffersStore;
	RegisterFile floatRegisterFile;
	RegisterFile integerRegisterFile;
	ArrayList<InstructionRow> executionQueue; 
	Hashtable<String, Float> bus = new Hashtable<>();
	int PC;
	int[] latencies; 
	boolean branchTaken;
	String branchTag;
	boolean stall;
	int addReservationStationSize;
	int mulReservationStationSize;
	int loadBufferSize;
	int storeBufferSize;
	

	
	public Main(int[] latencies , ArrayList<InstructionRow> instructions , int addReservationStationSize , int mulReservationStationSize , int loadBufferSize , int storeBufferSize) {
		this.cycle = 1;
		this.cache = new ArrayList<Float>(1024);
		for (int i = 0; i < 1024; i++) {
				cache.add(0f);
			}
		this.instructions = new ArrayList<InstructionRow>();
		this.reservationStationsAdd = new ReservationStations("ADD",addReservationStationSize);
		this.reservationStationsMul = new ReservationStations("MUL",mulReservationStationSize);
		this.buffersLoad = new Buffers("LOAD",loadBufferSize);
		this.buffersStore = new Buffers("STORE",storeBufferSize);
		this.floatRegisterFile = new RegisterFile(32 , "float"); 
		this.integerRegisterFile = new RegisterFile(32 , "integer"); 
		this.PC = 0;
		this.latencies = new int[10];
		this.executionQueue = new ArrayList<InstructionRow>();
		this.instructions = instructions;
		this.branchTaken = false;
		this.branchTag = "";
		this.stall = false;
	}
	
    public static void printHashtable(Hashtable<String, Float> hashtable) {
        Enumeration<String> keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            float value = hashtable.get(key);
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }
	
	public void display() {
		System.out.println("---------------------------------------------------------------");
		System.out.println("Current Cycle: " + cycle);
		System.out.println("PC: " + PC);
		
        System.out.println("\nInstruction list:");
        for (int i = 0; i<instructions.size() ; i++) {
        	System.out.println(instructions.get(i));
        }
	    // Display Reservation Stations for ADD
	    System.out.println("\nReservation Stations for ADD:");
	    reservationStationsAdd.display();

	    // Display Reservation Stations for MUL
	    System.out.println("\nReservation Stations for MUL:");
	    reservationStationsMul.display();

	    // Display Buffers for LOAD
	    System.out.println("\nBuffers for LOAD:");
	    buffersLoad.display();

	    // Display Buffers for STORE
	    System.out.println("\nBuffers for STORE:");
	    buffersStore.display();

	    // Display Float Register File
	    System.out.println("\nFloat Register File:");
	    floatRegisterFile.display("float");
	    
	    // Display Integer Register File
	    System.out.println("\nInteger Register File:");
	    integerRegisterFile.display("integer");

	    // Display Cache
	    System.out.println("\nCache:");
        for (Float value : cache) {
            System.out.print("[" + value + "] ");
        }

	    // Display Execution Queue
	    /*System.out.println("\nExecution Queue:");
	    for (InstructionRow instruction : executionQueue) {
	        System.out.println(instruction);
	    }*/
	    
	    // Display Bus
	    System.out.println("\nBus:");
	    printHashtable(bus);
	}
	
	public void issue(Main main) {
		if (PC >= instructions.size()) {
			return;
		}
		if(stall) {
			return;
		}
		InstructionRow currentInstruction = instructions.get(PC); 
		String[] words = currentInstruction.instructionString.split(" ");
		String label = currentInstruction.label;
		switch(words[0]) {
		case "L.D": 			
			for(int i=0;i<main.buffersLoad.length;i++) {
			if(main.buffersLoad.bufferRows[i].busy==false) {
				main.buffersLoad.bufferRows[i].busy=true;
				main.buffersLoad.bufferRows[i].address= Integer.parseInt(words[2]);
				for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.buffersLoad.bufferRows[i].tag;
						currentInstruction.tag = main.buffersLoad.bufferRows[i].tag;
					}
				}
				PC++;
				break;
			}
		} break;
		case "S.D":
			for(int i=0;i<main.buffersStore.length;i++) {
			if(main.buffersStore.bufferRows[i].busy==false) {
				main.buffersStore.bufferRows[i].busy=true;
				main.buffersStore.bufferRows[i].address= Integer.parseInt(words[2]);
				for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
					if(r.name.equals(words[1])) {
						if(r.qi.equals("0")) { //if equal to zero yeb2a value gahza
							main.buffersStore.bufferRows[i].v = r.value;
						}
						else {
							main.buffersStore.bufferRows[i].q = r.qi;
						}
						currentInstruction.tag = main.buffersStore.bufferRows[i].tag;
					}
				}
				PC++;
				break;
			}	

		} break;
		case "MUL.D": 	
			for(int i=0;i<main.reservationStationsMul.length;i++) {
			if(main.reservationStationsMul.reservationStations[i].busy==false) {
				main.reservationStationsMul.reservationStations[i].busy=true;
				main.reservationStationsMul.reservationStations[i].Op="MUL";
				for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
					if(r.name.equals(words[2])) {
						if(r.qi.equals("0")) { //handling first operand
							main.reservationStationsMul.reservationStations[i].vj=r.value;
						}
						else {
							main.reservationStationsMul.reservationStations[i].qj=r.qi;
						}	
					}
					if(r.name.equals(words[3])) { //handling second operand
						if(r.qi.equals("0")) {
							main.reservationStationsMul.reservationStations[i].vk=r.value;
						}
						else {
							main.reservationStationsMul.reservationStations[i].qk=r.qi;
						}	
					}
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.reservationStationsMul.reservationStations[i].tag;
						currentInstruction.tag = main.reservationStationsMul.reservationStations[i].tag;
					}
				}
				PC++;
				break;
			}
			
				
			
		} break;
		case "DIV.D":				
			for(int i=0;i<main.reservationStationsMul.length;i++) {
			if(main.reservationStationsMul.reservationStations[i].busy==false) {
				main.reservationStationsMul.reservationStations[i].busy=true;
				main.reservationStationsMul.reservationStations[i].Op="DIV";
				for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
					if(r.name.equals(words[2])) {
						if(r.qi.equals("0")) { //handling first operand
							main.reservationStationsMul.reservationStations[i].vj=r.value;
						}
						else {
							main.reservationStationsMul.reservationStations[i].qj=r.qi;
						}	
					}
					if(r.name.equals(words[3])) { //handling second operand
						if(r.qi.equals("0")) {
							main.reservationStationsMul.reservationStations[i].vk=r.value;
						}
						else {
							main.reservationStationsMul.reservationStations[i].qk=r.qi;
						}	
					}
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.reservationStationsMul.reservationStations[i].tag;
						currentInstruction.tag = main.reservationStationsMul.reservationStations[i].tag;
						
					}
				}
				PC++;
				break;
			}
				
			
		} break;
		case "ADD.D":
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
						if(r.name.equals(words[2])) {
							if(r.qi.equals("0")) { //handling first operand
								main.reservationStationsAdd.reservationStations[i].vj=r.value;
							}
							else {
								main.reservationStationsAdd.reservationStations[i].qj=r.qi;
							}	
						}
						if(r.name.equals(words[3])) { //handling second operand
							if(r.qi.equals("0")) {
								main.reservationStationsAdd.reservationStations[i].vk=r.value;
							}
							else {
								main.reservationStationsAdd.reservationStations[i].qk=r.qi;
							}	
						}
						if(r.name.equals(words[1])) { //handling tag
							r.qi = main.reservationStationsAdd.reservationStations[i].tag;
							currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
						}
					}
					PC++;
					break;
				}
					
				
			} break;
		case "DADD":
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
						if(r.name.equals(words[2])) {
							if(r.qi.equals("0")) { //handling first operand
								main.reservationStationsAdd.reservationStations[i].vj=r.value;
							}
							else {
								main.reservationStationsAdd.reservationStations[i].qj=r.qi;
							}	
						}
						if(r.name.equals(words[3])) { //handling second operand
							if(r.qi.equals("0")) {
								main.reservationStationsAdd.reservationStations[i].vk=r.value;
							}
							else {
								main.reservationStationsAdd.reservationStations[i].qk=r.qi;
							}	
						}
						if(r.name.equals(words[1])) { //handling tag
							r.qi = main.reservationStationsAdd.reservationStations[i].tag;
							currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
						}
					}
					PC++;
					break;
				}
					
				
			} break;
		case "SUB.D": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="SUB";
				for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
					if(r.name.equals(words[2])) {
						if(r.qi.equals("0")) { //handling first operand
							main.reservationStationsAdd.reservationStations[i].vj=r.value;
						}
						else {
							main.reservationStationsAdd.reservationStations[i].qj=r.qi;
						}	
					}
					if(r.name.equals(words[3])) { //handling second operand
						if(r.qi.equals("0")) {
							main.reservationStationsAdd.reservationStations[i].vk=r.value;
						}
						else {
							main.reservationStationsAdd.reservationStations[i].qk=r.qi;
						}	
					}
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.reservationStationsAdd.reservationStations[i].tag;
						currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
					}
				}
				PC++;
				break;
			}
				
			
		} break;
		case "SUBI": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="SUB";
				main.reservationStationsAdd.reservationStations[i].vk = Integer.parseInt(words[3]);
				for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
					if(r.name.equals(words[2])) {
						if(r.qi.equals("0")) { //handling first operand
							main.reservationStationsAdd.reservationStations[i].vj=r.value;
						}
						else {
							main.reservationStationsAdd.reservationStations[i].qj=r.qi;
						}	
					}
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.reservationStationsAdd.reservationStations[i].tag;
						currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
					}
				}
				PC++;
				break;
			}
				
			
		} break;
		case "ADDI": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="ADD";
				main.reservationStationsAdd.reservationStations[i].vk = Integer.parseInt(words[3]);
				for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
					if(r.name.equals(words[2])) {
						if(r.qi.equals("0")) { //handling first operand
							main.reservationStationsAdd.reservationStations[i].vj=r.value;
						}
						else {
							main.reservationStationsAdd.reservationStations[i].qj=r.qi;
						}	
					}
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.reservationStationsAdd.reservationStations[i].tag;
						currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
					}
				}
				PC++;
				break;
			}
				
			
		} break;
		case "BNEZ": 
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					main.reservationStationsAdd.reservationStations[i].vk = 0;
					//check if we are using float/integer register
					if (words[1].substring(0,1).equals("F")) {
						for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
							if(r.name.equals(words[1])) {
								if(r.qi.equals("0")) { //handling first operand
									main.reservationStationsAdd.reservationStations[i].vj=r.value;
								}
								else {
									main.reservationStationsAdd.reservationStations[i].qj=r.qi;
								}	
							}
						}
					}
					else {
						for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
							if(r.name.equals(words[1])) {
								if(r.qi.equals("0")) { //handling first operand
									main.reservationStationsAdd.reservationStations[i].vj=r.value;
								}
								else {
									main.reservationStationsAdd.reservationStations[i].qj=r.qi;
								}	
							}
						}
					}

					currentInstruction.tag = main.reservationStationsAdd.reservationStations[i].tag;
					PC++;
					stall = true;
					break;
				}
					
				
			} break;

		default: break;
		
		
		
		}
		
	}
//Execute Helper helps our main execute function to know which instructions are ready
//to execute by checking if the operands of the instructions are ready or not by 
//checking if the registers they are working with have their Q="0" or not
	public void executeHelper (Main main) { 
		for (int i=0 ; i<main.PC ; i++) {
			boolean firstRegister = false;
			boolean secondRegister = false;
			InstructionRow currentInstruction = main.instructions.get(i); 
			String[] words = currentInstruction.instructionString.split(" ");
			if (currentInstruction.executionStart == 0) {
				String tagLetter = currentInstruction.tag.substring(0, 1);
			    switch(tagLetter) {
			        case "A":
			        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
			        		if(r.tag.equals(currentInstruction.tag)) {
			        			if (r.qj.equals("0") && r.qk.equals("0")) {
			        				firstRegister=true;
			        				secondRegister=true;
			        			}
			        		}
			        	}
			        	break;
			        case "M":
			        	for(ReservationStationRow r : main.reservationStationsMul.reservationStations) {
			        		if(r.tag.equals(currentInstruction.tag)) {
				        		if(r.tag.equals(currentInstruction.tag)) {
				        			if (r.qj.equals("0") && r.qk.equals("0")) {
				        				firstRegister=true;
				        				secondRegister=true;
				        			}
				        		}
			        		}
			        	}
			        	break;
			        case "S":
			        	for(BufferRow r : main.buffersStore.bufferRows) {
			        		if (r.tag.equals(currentInstruction.tag)) {
			        			if(r.q.equals("")) {
			        				firstRegister=true;
			        				secondRegister=true;
			        			}
			        			
			        		}
			        	}
			        	break;
			        case "L":firstRegister=true;
    				secondRegister=true;
			        	break;
			        }
				if (firstRegister && secondRegister) {
					currentInstruction.executionStart = main.cycle;
					currentInstruction.executionEnd = currentInstruction.executionStart + currentInstruction.latency - 1;
					main.executionQueue.add(currentInstruction);
				}
			}

		}
	    // Display Execution Queue
	    System.out.println("\nExecution Queue:");
	    for (InstructionRow instruction : executionQueue) {
	        System.out.println(instruction);
	    }
		main.execute(main);
	}
	public String findTag (Main main, InstructionRow currentInstruction) {
		String[] words = currentInstruction.instructionString.split(" ");
		String result = "";
		for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
			if(words[1].equals(r.name)) {
				result= r.qi;
				break;
			}
				
			
		}
		return result;
	}
	 
	//execute does the actual arithmetic/branching/load/store operation
	public void execute(Main main) {
		for (int i = 0 ; i < main.executionQueue.size() ; i++) {
			InstructionRow instructionToBeExecuted = main.executionQueue.get(i);
			if (instructionToBeExecuted.executionEnd == main.cycle) {
				//DO THE EXECUTION LOGIC, AND PUT IT ON A BUS
				String[] words = instructionToBeExecuted.instructionString.split(" ");
				String label = instructionToBeExecuted.label;
				float value = 0;
				float op1 = 0;
				float op2 = 0;
				String tag = instructionToBeExecuted.tag;
				//System.out.println("ANA 3ANDY TAG: " + instructionToBeExecuted.tag);
				String tagLetter = tag.substring(0, 1);
			    switch(tagLetter) {
			        case "A":
			        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
			        		if(r.tag.equals(tag)) {
			        			if (r.Op.equals("ADD")) {
			        				op1 = r.vj;
			        				op2 = r.vk;
			        				value = op1+op2;
			        			}
			        			else {
			        				op1 = r.vj;
			        				op2 = r.vk;
			        				value = op1-op2;
			        			}
			        		}
			        	}
			        	break;
			        case "M":
			        	for(ReservationStationRow r : main.reservationStationsMul.reservationStations) {
			        		if(r.tag.equals(tag)) {
			        			if (r.Op.equals("MUL")) {
			        				op1 = r.vj;
			        				op2 = r.vk;
			        				value = op1*op2;
			        			}
			        			else {
			        				op1 = r.vj;
			        				op2 = r.vk;
			        				value = op1/op2;
			        			}
			        		}
			        	}
			        	break;
			        case "S":
						int address = Integer.parseInt(words[2]);
			        	for(BufferRow r : main.buffersStore.bufferRows) {
			        		if (r.tag.equals(tag)) {
			        			value=r.v;
			        		}
			        	}
						cache.set(address, value);
			        	for(BufferRow r : main.buffersStore.bufferRows) {
			        		if (r.tag.equals(tag)) {
			        			r.busy = false;
			        			r.address =-1;	
			        			r.q = "";
			        			r.v = 0;
			        		}
			        	}
			        	break;
			        case "L":
						int loadAddress = Integer.parseInt(words[2]);
						//System.out.println("I AM THE ADDRESS: " + loadAddress + "AND THIS IS WHAT IS IN MY ADDRESS: " + cache.get(loadAddress));
						value = cache.get(loadAddress);
			        	break;
			        }
						
				if (!words[0].equals("BNEZ") && !words[0].equals("S.D"))
					bus.put(instructionToBeExecuted.tag, value);
				if (words[0].equals("BNEZ")) {
					//assume you branch to the first line always -> according to TA
					if(value !=0) {
						branchTaken = true;
						branchTag= instructionToBeExecuted.tag;
						for (int j =0; j<main.instructions.size() ; j++) {
							main.instructions.get(j).executionStart=0;
							//main.instructions.get(j).executionEnd=0;
						}
					}
					else {
						branchTag= instructionToBeExecuted.tag;
					}
				}
				executionQueue.remove(i);
				i--;
				
			}
		}
		

	}
	
	public void writeResult(Main main) {
		if (branchTaken) {
			branchTaken = false;
			stall=false;
			main.PC = 0;
        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
        		if (r.tag.equals(branchTag)) {
        			r.busy = false;
        			r.Op = "";
        			r.vj = 0;
        			r.vk = 0;
        			r.qj = "0";
        			r.qk = "0";
        		}
        	}
        	branchTag="";
		}
		else {
			if (!branchTag.equals("")) {
	        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
	        		if (r.tag.equals(branchTag)) {
	        			r.busy = false;
	        			r.Op = "";
	        			r.vj = 0;
	        			r.vk = 0;
	        			r.qj = "0";
	        			r.qk = "0";
	        		}
	        	}
				stall=false;
	        	branchTag="";
			}
		}
        Iterator<Map.Entry<String, Float>> iterator = bus.entrySet().iterator();
        String tag = "";
        float value	= 0;	
        if (iterator.hasNext()) {
            Map.Entry<String, Float> firstEntry = iterator.next();
            tag = firstEntry.getKey();
            value = firstEntry.getValue();
            iterator.remove(); 
        } 
        else {
        	return;
        }
        for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
        	if (r.qi.equals(tag)) {
        		r.qi = "0";
        		r.value = value;
        	}
        }
        
        for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
        	if (r.qi.equals(tag)) {
        		r.qi = "0";
        		r.value = (int) value;
        	}
        }
        	// first check if any other reservationstationRow needs this tag
        for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
        		if(r.qj.equals(tag)) {
        			r.qj = "0";
        			r.vj = value;
        		}
        		if(r.qk.equals(tag)) {
        			r.qk = "0";
        			r.vk = value;
        		}
        	}
        	//then empty the corresponding reservation station to that row
        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
        		if (r.tag.equals(tag)) {
        			r.busy = false;
        			r.Op = "";
        			r.vj = 0;
        			r.vk = 0;
        			r.qj = "0";
        			r.qk = "0";
        		}
        	}
      
        	for(ReservationStationRow r : main.reservationStationsMul.reservationStations) {
        		if(r.qj.equals(tag)) {
        			r.qj = "0";
        			r.vj = value;
        		}
        		if(r.qk.equals(tag)) {
        			r.qk = "0";
        			r.vk = value;
        		}
        	}
        	for(ReservationStationRow r : main.reservationStationsMul.reservationStations) {
        		if (r.tag.equals(tag)) {
        			r.busy = false;
        			r.Op = "";
        			r.vj = 0;
        			r.vk = 0;
        			r.qj = "0";
        			r.qk = "0";
        		}
        	}
        	
        	
        	for(BufferRow r : main.buffersStore.bufferRows) {
        		if(r.q.equals(tag)) {
        			r.q = "";
        			r.v = value;
        		}
        	}
        	for(BufferRow r : main.buffersLoad.bufferRows) {
        		if (r.tag.equals(tag)) {
        			r.busy = false;
        			r.address =-1;	
        			r.q = "";
        			r.v = 0;
        		}
        	}
        	
        
        }
        
        
    public static boolean floatRegisterChecker(Main main) {
        for(FloatRegisterRow r : main.floatRegisterFile.floatRegisterRows) {
        	if (!(r.qi.equals("0"))) {
        		return false;
        	}
        }
        return true;
    }
    
    public static boolean integerRegisterChecker(Main main) {
        for(IntegerRegisterRow r : main.integerRegisterFile.integerRegisterRows) {
        	if (!(r.qi.equals("0"))) {
        		return false;
        	}
        }
        return true;
    }
    private static String getInstructionName(int index) {
        switch (index) {
            case 0:
                return "L.D";
            case 1:
                return "S.D";
            case 2:
                return "MUL.D";
            case 3:
                return "DIV.D";
            case 4:
                return "ADD.D";
            case 5:
                return "DADD";
            case 6:
                return "SUB.D";
            case 7:
                return "SUBI";
            case 8:
                return "ADDI";
            case 9:
                return "BNEZ";
            default:
                return "Unknown";
        }
    }
    
    private static boolean isValidInstruction(String instruction) {
        switch (instruction.toUpperCase()) {
            case "L.D":
            case "S.D":
            case "MUL.D":
            case "DIV.D":
            case "ADD.D":
            case "DADD":
            case "SUB.D":
            case "SUBI":
            case "ADDI":
            case "BNEZ":
                return true;
            default:
                return false;
        }
    }
    
    public static void simulate() {
    	Scanner scanner = new Scanner(System.in);
    	
   	 	//Get inputs of latencies
		/*index 0 is L.D
		  index 1 is S.D
		  index 2 is MUL.D
		  index 3 is DIV.D
		  index 4 is ADD.D
		  index 5 is DADD
		  index 6 is SUB.D
		  index 7 is SUBI
		  index 8 is ADDI
		  index 9 is BNEZ
		 */
    	System.out.println("Welcome to the (hopefully working) Tomasulo Algorithm Simulator!");
    	System.out.println("\nThe Tomasulo algorithm is a computer architecture algorithm used" +"\n" +"for dynamic scheduling of instructions in out-of-order execution processors. \n");
    	System.out.println("All we need from you to start this epic (and again hopefully working) Tomasulo \nAlgorithm Simulator"
    			+ " we will need you to input the following: ");
    	System.out.println("\n1. Latencies for each instruction");
    	System.out.println("2. The assembly instructions you want to execute");
    	System.out.println("3. The size of the reservation stations and buffers");
    	System.out.println("4. Preload anything in the cache or register file, however this needs \nto be done before execution in the Simulator method.");
    	System.out.println("Thats it! Enjoy. \n");
        int[] latencies = new int[10];
    	 for (int i = 0; i < latencies.length; i++) {
             if (i == 8 || i == 9) {
                 // Set default latency to 1 for ADDI and BNEZ
                 latencies[i] = 1;
             } else {
                 System.out.print("Enter an integer for latency for the " + getInstructionName(i) + " instruction: ");
                 latencies[i] = scanner.nextInt();
             }
         }
    	
        // Prompt user to enter instructions
        ArrayList<String> instructions = new ArrayList<>();
        System.out.println("\nEnter your instructions like the following format: MUL.D F1 F2 F3 , enter \"end\" if you are done entering instructions.");

        String instructionInput;
        while (true) {
            System.out.println("Enter instruction: ");
            instructionInput = scanner.nextLine().trim();
            
            if (instructionInput.equalsIgnoreCase("end")) {
                break;
            }

            if (instructionInput.equals(""))
            	System.out.println("You Entered empty instruction.");
            else
            	instructions.add(instructionInput);
        }

        // Initialize instructionRows
        ArrayList<InstructionRow> instructionRows = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
        	String [] words = instructions.get(i).split(" ");
        	if (!isValidInstruction(words[0])) { //HAS A LABEL, HANDLE IT SEPERATELY
        		String instruction = "";
        		for(int j=1 ; j<words.length ; j++) {
        			instruction+= words[j];
        			instruction+= " ";
        		}
        		 InstructionRow instructionRow = new InstructionRow(latencies, words[0], instruction );
        		 instructionRows.add(instructionRow);
        	}
        	else {
        		InstructionRow instructionRow = new InstructionRow(latencies, "", instructions.get(i));
                instructionRows.add(instructionRow);
        	}
            
        }
    	 // Prompt user to enter sizes of reservation stations and buffers
         System.out.print("\nEnter size of ADD reservation station: ");
         int addReservationStationSize = scanner.nextInt();

         System.out.print("Enter size of MUL reservation station: ");
         int mulReservationStationSize = scanner.nextInt();

         System.out.print("Enter size of LOAD buffer: ");
         int loadBufferSize = scanner.nextInt();

         System.out.print("Enter size of Store buffer: ");
         int storeBufferSize = scanner.nextInt();
         
         // Close the scanner
         scanner.close();
         
         Main main= new Main(latencies, instructionRows,addReservationStationSize , mulReservationStationSize , loadBufferSize , storeBufferSize);
         
         /////////////// ADD WHATEVER YOU WANT IN THE CACHE OR REGISTER FILE HERE ///////////////
         // use this for integer -> main.integerRegisterFile.integerRegisterRows[#].value= #;
         // use this for float -> main.floatRegisterFile.floatRegisterRows[#].value = #;
         // use this for cache -> main.cache.set(#address#, #float value eg: 5.0f# );
         main.floatRegisterFile.floatRegisterRows[1].value = 3;
         main.floatRegisterFile.floatRegisterRows[2].value = 2;
         main.floatRegisterFile.floatRegisterRows[5].value = 5;

         main.integerRegisterFile.integerRegisterRows[1].value = 3;
         
         main.cache.set(8, 5.0f);
         main.cache.set(7, 3.0f);
         
         while(true) {
        	//the 3-step pipeline, write->execute->issue
        	//done in reserve since in cycle 1 and 2 it will not 
        	
           	main.writeResult(main);
         	main.executeHelper(main);
         	main.issue(main);
            main.display();
            main.cycle++;

             if (main.cycle > 2 ) {
             	if(main.executionQueue.isEmpty() && main.bus.isEmpty() 
             		&& floatRegisterChecker(main) 
             		&& integerRegisterChecker(main)
             		&& main.reservationStationsAdd.isNotBusy() 
             		&& main.reservationStationsMul.isNotBusy() 
             		&& main.buffersLoad.isNotBusy() 
             		&& main.buffersStore.isNotBusy()) {
             		
             		break;
             	}
             }
         }
    }
	

	public static void main(String[] args) {
        simulate();		

	}

}
