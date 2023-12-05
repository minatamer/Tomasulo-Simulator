
public class BufferRow {
	
	String tag;
	boolean busy;
	int address;
	float v;
	String q;
	
	
	public BufferRow(String type, int index) {
		if(type.equals("LOAD")) {
			tag = "L"+index;
			q = "";
			v = 0;
		}
		else {
			tag = "S"+index;
			q = "";
			v = 0;
		}
		busy = false;
		address =-1;	
	}
	
    @Override
    public String toString() {
        return "BufferRow{" +
                "tag='" + tag + '\'' +
                ", busy=" + busy +
                ", address=" + address +
                ", v=" + v +
                ", q='" + q + '\'' +
                '}';
    }

}
