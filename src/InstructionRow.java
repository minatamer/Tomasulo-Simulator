
public class InstructionRow {
	String label;
	String instructionString;
	int latency; 
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
	int executionStart;
	int executionEnd;
	
	public InstructionRow(int[]latencies , String label , String instructionString) {
		this.label = label;
		this.instructionString = instructionString;
		this.executionStart = 0;
		this.executionEnd = 0;
		String[] words = instructionString.split(" ");
		switch(words[0]) {
		case "L.D": this.latency = latencies[0]; break;
		case "S.D": this.latency = latencies[1]; break;
		case "MUL.D": this.latency = latencies[2]; break;
		case "DIV.D": this.latency = latencies[3]; break;
		case "ADD.D": this.latency = latencies[4]; break;
		case "DADD": this.latency = latencies[5]; break;
		case "SUB.D": this.latency = latencies[6]; break;
		case "SUBI": this.latency = latencies[7]; break;
		case "ADDI": this.latency = latencies[8]; break;
		case "BNEZ": this.latency = latencies[9]; break;
		}
	}
	
    @Override
    public String toString() {
        return "InstructionRow{" +
                "label='" + label + '\'' +
                ", instructionString='" + instructionString + '\'' +
                ", latency=" + latency +
                ", executionStart=" + executionStart +
                ", executionEnd=" + executionEnd +
                '}';
    }
}
