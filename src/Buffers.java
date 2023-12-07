public class Buffers {
	String type; //LOAD or STORE
	int length;
	BufferRow[] bufferRows;
	
	public Buffers(String type, int length) {
		this.type=type;
		this.length=length;
		this.bufferRows = new BufferRow[length];
		
		for(int i=1;i<=length;i++) {
			bufferRows[i-1]= new BufferRow(type, i);
		}
	}
	
    public void display() {
        for (BufferRow row : bufferRows) {
            System.out.println(row);
        }
    }
    
    public boolean isNotBusy() {
        for (BufferRow row : bufferRows) {
            if (row.busy) {
                return false; // If any row is busy, the array is busy
            }
        }
        return true; // All rows are not busy so its not busy
    }
	

}
