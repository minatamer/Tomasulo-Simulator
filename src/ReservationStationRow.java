
public class ReservationStationRow {
	
	String tag;
	boolean busy;
	String Op;
	float vj;
	float vk;
	String qj;
	String qk;
	
	public ReservationStationRow(String type, int index) {
		if(type.equals("ADD"))
			tag = "A"+index;
		else
			tag = "M"+index;
		busy = false;
		Op = "";
		vj = 0;
		vk = 0;
		qj = "0";
		qk = "0";	
	}
	
    public String toString() {
        return "ReservationStationRow{" +
                "tag='" + tag + '\'' +
                ", busy=" + busy +
                ", Op='" + Op + '\'' +
                ", vj=" + vj +
                ", vk=" + vk +
                ", qj='" + qj + '\'' +
                ", qk='" + qk + '\'' +
                '}';
    }

}
