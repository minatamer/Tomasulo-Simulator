
public class ReservationStations {
	
	String type; //either ADD or MUL
	int length;
	ReservationStationRow[] reservationStations;
	
	

	
	public ReservationStations(String type,int length) {
		this.type=type;
		this.length=length;
		this.reservationStations = new ReservationStationRow[length];
		
		for(int i=1;i<=length;i++) {
			reservationStations[i-1] = new ReservationStationRow(type, i);
		}
		
		
	}
	
    public void display() {
        for (ReservationStationRow row : reservationStations) {
            System.out.println(row);
        }
    }
    
    public boolean isNotBusy() {
        for (ReservationStationRow row : reservationStations) {
            if (row.busy) {
                return false; // If any row is busy, the array is busy
            }
        }
        return true; // All rows are not busy so its not busy
    }
	
}
