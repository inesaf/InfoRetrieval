
public class Doc {
	private int ID;
	private int frequency;

	public Doc(int ID, int frequency) {
		this.ID = ID;
		this.setFrequency(frequency);
	}
	
	public int getID() {
		return ID;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
