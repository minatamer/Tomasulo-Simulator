import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
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
	RegisterFile registerFile;
	ArrayList<InstructionRow> executionQueue; 
	Hashtable<String, Float> bus = new Hashtable<>();
	int PC;
	int[] latencies; 
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
	
	
	public Main(int [] latencies , ArrayList<InstructionRow> instructions) {
		this.cycle = 0;
		this.cache = new ArrayList<Float>();
		this.instructions = new ArrayList<InstructionRow>();
		this.reservationStationsAdd = new ReservationStations("ADD",3);
		this.reservationStationsMul = new ReservationStations("MUL",2);
		this.buffersLoad = new Buffers("LOAD",5);
		this.buffersStore = new Buffers("STORE",5);
		this.registerFile = new RegisterFile(64);
		this.PC = 0;
		this.latencies = latencies;
		this.executionQueue = new ArrayList<InstructionRow>();
		this.instructions = instructions;
	}
	
	public void display() {
		System.out.println("Current Cycle: " + cycle);
	    // Display Reservation Stations for ADD
	    System.out.println("Reservation Stations for ADD:");
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

	    // Display Register File
	    System.out.println("\nRegister File:");
	    registerFile.display();

	    // Display Cache
	    System.out.println("\nCache:");
        for (Float value : cache) {
            System.out.println(value);
        }

	    // Display Execution Queue
	    System.out.println("\nExecution Queue:");
	    for (InstructionRow instruction : executionQueue) {
	        System.out.println(instruction);
	    }
	}
	
	public void issue(Main main) {
		if (PC >= instructions.size()) {
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
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
					if(r.name.equals(words[1])) { //handling tag
						r.qi = main.buffersLoad.bufferRows[i].tag;
					}
				}
			}
			break;
		} break;
		case "S.D":
			for(int i=0;i<main.buffersStore.length;i++) {
			if(main.buffersStore.bufferRows[i].busy==false) {
				main.buffersStore.bufferRows[i].busy=true;
				main.buffersStore.bufferRows[i].address= Integer.parseInt(words[2]);
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
					if(r.name.equals(words[1])) { //handling tag
						if(r.qi.equals("0")) { //if equal to zero yeb2a value gahza
							main.buffersStore.bufferRows[i].v = r.value;
						}
						else {
							main.buffersStore.bufferRows[i].q = r.qi;
						}
					}
				}
				break;
			}	

		} break;
		case "MUL.D": 	
			for(int i=0;i<main.reservationStationsMul.length;i++) {
			if(main.reservationStationsMul.reservationStations[i].busy==false) {
				main.reservationStationsMul.reservationStations[i].busy=true;
				main.reservationStationsMul.reservationStations[i].Op="MUL";
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
					}
				}
				break;
			}
			
				
			
		} break;
		case "DIV.D":				
			for(int i=0;i<main.reservationStationsMul.length;i++) {
			if(main.reservationStationsMul.reservationStations[i].busy==false) {
				main.reservationStationsMul.reservationStations[i].busy=true;
				main.reservationStationsMul.reservationStations[i].Op="DIV";
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
					}
				}
				break;
			}
				
			
		} break;
		case "ADD.D":
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
						}
					}
					break;
				}
					
				
			} break;
		case "DADD":
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
						}
					}
					break;
				}
					
				
			} break;
		case "SUB.D": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="SUB";
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
					}
				}
				break;
			}
				
			
		} break;
		case "SUBI": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="SUB";
				main.reservationStationsAdd.reservationStations[i].vk = Float.parseFloat(words[3]);
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
					}
				}
				break;
			}
				
			
		} break;
		case "ADDI": 			
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
			if(main.reservationStationsAdd.reservationStations[i].busy==false) {
				main.reservationStationsAdd.reservationStations[i].busy=true;
				main.reservationStationsAdd.reservationStations[i].Op="ADD";
				main.reservationStationsAdd.reservationStations[i].vk = Float.parseFloat(words[3]);
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
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
					}
				}
				break;
			}
				
			
		} break;
		case "BNEZ": 
			for(int i=0;i<main.reservationStationsAdd.length;i++) {
				if(main.reservationStationsAdd.reservationStations[i].busy==false) {
					main.reservationStationsAdd.reservationStations[i].busy=true;
					main.reservationStationsAdd.reservationStations[i].Op="ADD";
					main.reservationStationsAdd.reservationStations[i].vk = 0;
					for(RegisterFileRow r : main.registerFile.registerFileRows) {
						if(r.name.equals(words[1])) {
							if(r.qi.equals("0")) { //handling first operand
								main.reservationStationsAdd.reservationStations[i].vj=r.value;
							}
							else {
								main.reservationStationsAdd.reservationStations[i].qj=r.qi;
							}	
						}
					}
					break;
				}
					
				
			} break;

		default: break;
		
		
		
		}
		PC++;
		cycle++;
	}
	public void executeHelper (Main main) {
		for (int i=0 ; i<main.PC ; i++) {
			boolean firstRegister = false;
			boolean secondRegister = false;
			InstructionRow currentInstruction = main.instructions.get(i); 
			String[] words = currentInstruction.instructionString.split(" ");
			if (currentInstruction.executionStart == 0) {
				for(RegisterFileRow r : main.registerFile.registerFileRows) {
					if ((words[0].equals("S.D") || words[0].equals("L.D") || words[0].equals("BNEZ")) &&  r.name.equals(words[1])) {
						if(r.qi.equals("0")) {
							firstRegister=true;
						}
					}
					else {
						if(r.name.equals(words[2])) {
							if(r.qi.equals("0")) {
								firstRegister=true;
							}
						}
						if(r.name.equals(words[3])) {
							if(r.qi.equals("0")) {
								secondRegister=true;
							}
						}
					}

				}
				if (words[0].equals("ADDI") || words[0].equals("SUBI") || words[0].equals("L.D") || words[0].equals("S.D") || words[0].equals("BNEZ")) {
					secondRegister = true;
				}
				if (firstRegister && secondRegister) {
					currentInstruction.executionStart = main.cycle;
					currentInstruction.executionEnd = currentInstruction.executionStart + currentInstruction.latency;
					main.executionQueue.add(currentInstruction);
				}
			}

		}
		main.execute(main);
	}
	public String findTag (Main main, InstructionRow currentInstruction) {
		String[] words = currentInstruction.instructionString.split(" ");
		String result = "";
		for(RegisterFileRow r : main.registerFile.registerFileRows) {
			if(words[1].equals(r.name)) {
				result= r.qi;
				break;
			}
				
			
		}
		return result;
	}
	
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
				switch(words[0]) {
					case "DADD": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
							if(words[3].equals(r.name))
								op2 = r.value;
						}
						value = op1 + op2;
						break;
					case "ADD.D": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
							if(words[3].equals(r.name))
								op2 = r.value;
						}
						value = op1 + op2;
						break;
					case "SUB.D": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
							if(words[3].equals(r.name))
								op2 = r.value;
						}
						value = op1 - op2;
						break;
					case "MUL.D": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
							if(words[3].equals(r.name))
								op2 = r.value;
						}
						value = op1 * op2;
						break;
					case "DIV.D": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
							if(words[3].equals(r.name))
								op2 = r.value;
						}
						value = op1 / op2;
						break;
					case "ADDI": 
						op2 = Float.parseFloat(words[3]);
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
						}
						value = op1 + op2;
						break;
					case "SUBI": 
						op2 = Float.parseFloat(words[3]);
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[2].equals(r.name))
								op1 = r.value;
						}
						value = op1 - op2;
						break;
					case "BNEZ": 
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[1].equals(r.name))
								op1 = r.value;
						}
						op2 = 0;
						value = op1 + op2;
						if (value == 0) {
							for (int j=0 ; j<main.instructions.size() ; j++) {
								if(main.instructions.get(i).label == instructionToBeExecuted.label) {
									PC = j;
									break;
								}
									
							}
						}
						break;
					case "L.D": 
						int loadAddress = Integer.parseInt(words[2]);
						value = cache.get(loadAddress);
						break;
					case "S.D": //just execute here
						int address = Integer.parseInt(words[2]);
						for(RegisterFileRow r : main.registerFile.registerFileRows) {
							if(words[1].equals(r.name))
								value = r.value;
							break;
						}
						cache.set(address, value);
						break;
						
				}
				if (!words[0].equals("BNEZ") && !words[0].equals("S.D"))
					bus.put(findTag (main, instructionToBeExecuted), value);
				executionQueue.remove(i);
				
			}
		}
		cycle++;

	}
	
	public void writeResult(Main main) {
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
        for(RegisterFileRow r : main.registerFile.registerFileRows) {
        	if (r.qi.equals(tag)) {
        		r.qi = "0";
        		r.value = value;
        	}
        }
        String tagLetter = tag.substring(0, 1);
        switch(tagLetter) {
        case "A":
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
        	for(ReservationStationRow r : main.reservationStationsAdd.reservationStations) {
        		if (r.tag.equals(tag)) {
        			r.busy = false;
        			r.Op = "";
        			r.vj = 0;
        			r.vk = 0;
        			r.qj = "";
        			r.qk = "";
        		}
        	}
        	break;
        case "M":
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
        			r.qj = "";
        			r.qk = "";
        		}
        	}
        	break;
        case "S":
        	for(BufferRow r : main.buffersLoad.bufferRows) {
        		if (r.tag.equals(tag)) {
        			r.busy = false;
        			r.address =-1;	
        			r.q = "";
        			r.v = 0;
        		}
        	}
        	break;
        case "L":
        	for(BufferRow r : main.buffersLoad.bufferRows) {
        		if(r.q.equals(tag)) {
        			r.q = "0";
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
        	break;
        }
        
        
		cycle++;
        
	}

	public static void main(String[] args) {
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
		int[] latencies = {1,1,1,40,4,1,2,1,1,1};
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("MUL.D F3 F1 F2");
        /*instructions.add("ADD F5 F3 F4");
        instructions.add("ADD F7 F2 F6");
        instructions.add("ADD F10 F8 F9");
        instructions.add("MUL F11 F7 F10");
        instructions.add("ADD F5 F5 F11");*/
        
        ArrayList<InstructionRow> instructionRows = new ArrayList<InstructionRow>();
        for (int i=0 ; i<instructions.size() ; i++) {
        	InstructionRow instructionRow = new InstructionRow(latencies, "", instructions.get(i));
        	instructionRows.add(instructionRow);
        }
        
        
        Main main = new Main(latencies, instructionRows);
        main.registerFile.registerFileRows[1].value = 3;
        main.registerFile.registerFileRows[2].value = 2;
        main.issue(main);
        main.display();
        //main.issue(main);
        main.execute(main);
        main.display();
        main.writeResult(main);
        main.display();
        /*while(main.PC < main.instructions.size()) {
        	main.issue(main);
        	main.execute(main);
        	main.writeResult(main);
        	main.display();
            main.PC++;
            main.cycle++;
        }*/
        

		
		

	}

}
