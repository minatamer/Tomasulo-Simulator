
public class IntegerRegisterRow {
	
	String name;
	String qi;
	int value;

	
	public IntegerRegisterRow(int index) {
		name = "R"+index;
		qi = "0";
		value = 0;
		
	}
	
    @Override
    public String toString() {
        return "IntegerRegisterRow{" +
                "name='" + name + '\'' +
                ", qi='" + qi + '\'' +
                ", value=" + value +
                '}';
    }
}


