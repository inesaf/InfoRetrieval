package elements;

public class Term {
	
	private Term word;
	private int cf; //collection frequency
	
	public Term(Term word) {
		this.setWord(word);
	}

	public Term getWord() {
		return word;
	}

	public void setWord(Term word) {
		this.word = word;
	}

	public int getCf() {
		return cf;
	}

	public void setCf(int cf) {
		this.cf = cf;
	}
}
