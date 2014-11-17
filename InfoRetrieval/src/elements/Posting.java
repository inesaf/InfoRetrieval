package elements;

import java.lang.String;

public class Posting {
	private int docID;
	private int df; //document frequency

	public Posting(int ID, int frequency) {
		this.docID = ID;
		this.setDocFreq(frequency);
	}
	
	public int getDocID() {
		return docID;
	}

	public int getDocFreq() {
		return df;
	}

	public void setDocID(int ID) {
		this.docID = ID;
	}
	
	public void setDocFreq(int frequency) {
		this.df = frequency;
	}
	
	@Override
	public String toString(){
		String docIDString = String.valueOf(getDocID());
		String dfString = String.valueOf(getDocFreq());
		return docIDString + ":" + dfString;
	}
}
