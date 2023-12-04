
public class RegisterFile {
	
	int limit;
	RegisterFileRow[] registerFileRows;
	
	public RegisterFile(int limit) {
		this.limit=limit;
		this.registerFileRows = new RegisterFileRow[limit];
		
		for(int i=0;i<limit;i++) {
			registerFileRows[i]= new RegisterFileRow(i);
		}
		
	}
	
    public void display() {
        for (RegisterFileRow row : registerFileRows) {
            System.out.println(row);
        }
    }
	

}
