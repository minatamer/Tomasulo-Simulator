
public class RegisterFileRow {
	
	String name;
	String qi;
	float value;

	
	public RegisterFileRow(int index) {
		name = "F"+index;
		qi = "0";
		value = 0;
		
	}
	
    @Override
    public String toString() {
        return "RegisterFileRow{" +
                "name='" + name + '\'' +
                ", qi='" + qi + '\'' +
                ", value=" + value +
                '}';
    }
}


