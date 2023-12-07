
public class RegisterFile {
	
	int limit;
	FloatRegisterRow[] floatRegisterRows;
	IntegerRegisterRow[] integerRegisterRows;
	
	public RegisterFile(int limit , String type) {
		this.limit=limit;
		if (type.equals("float")) {
			this.floatRegisterRows = new FloatRegisterRow[limit];
			for(int i=0;i<limit;i++) {
				floatRegisterRows[i]= new FloatRegisterRow(i);
			}
		}
		else {
			this.integerRegisterRows = new IntegerRegisterRow[limit];
			for(int i=0;i<limit;i++) {
				integerRegisterRows[i]= new IntegerRegisterRow(i);
			}
		}
		
	}
	
    public void display(String type) {
    	if (type.equals("float")) {
            for (FloatRegisterRow row : floatRegisterRows) {
                System.out.println(row);
            }
    	}
    	else {
            for (IntegerRegisterRow row : integerRegisterRows) {
                System.out.println(row);
            }
    	}

    }
	

}
