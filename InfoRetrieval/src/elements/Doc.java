package elements;

import java.util.ArrayList;
import java.lang.String;


public class Doc{

	private int docID;
	private int gutenbergID;
	private String path;
	private int totalNoTerms;
	ArrayList<Integer> incidenceList = new ArrayList<Integer>();
	private String title; //not yet used
	

	public Doc(int ID, int gutenberID, String path) {
		this.docID = ID;
		this.gutenbergID = gutenberID;
		this.path = path;
	}

	public int getID() {
		return docID;
	}

	public void setID(int ID) {
		this.docID = ID;
	}
	
	public int getGutenberID() {
		return gutenbergID;
	}

	public void setGutenberID(int gutenberID) {
		this.gutenbergID = gutenberID;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addIncidenceValue(int value){
		incidenceList.add(value);
	}

	public int getTotalNoTerms() {
		return totalNoTerms;
	}

	public void setTotalNoTerms(int totalNoTerms) {
		this.totalNoTerms = totalNoTerms;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String printDoc(){
		String result = "doc " + getID() + " -> ";
		for(Integer termID : incidenceList)
			result += termID + " ";	
		return result;
	}
}