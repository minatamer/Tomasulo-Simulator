
public class FloatRegisterRow {
	
	String name;
	String qi;
	float value;

	
	public FloatRegisterRow(int index) {
		name = "F"+index;
		qi = "0";
		value = 0;
		
	}
	
    @Override
    public String toString() {
        return "FloatRegisterRow{" +
                "name='" + name + '\'' +
                ", qi='" + qi + '\'' +
                ", value=" + value +
                '}';
    }
}


