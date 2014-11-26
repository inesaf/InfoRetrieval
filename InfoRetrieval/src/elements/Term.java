package elements;

public class Term {
	
	private String word;
	private int cf; //collection frequency
	
	public Term(String word) {
		this.word = word;
		this.cf = 1;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCf() {
		return cf;
	}

	public void setCf(int cf) {
		this.cf = cf;
	}
	
	@Override
	public String toString(){
		return getWord() + ":" + getCf() + "\n";
	}
}
