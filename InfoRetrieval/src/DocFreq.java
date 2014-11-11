
public class DocFreq {
	private int docID;
	private int df; //document frequency

	public DocFreq(int ID, int frequency) {
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
}
