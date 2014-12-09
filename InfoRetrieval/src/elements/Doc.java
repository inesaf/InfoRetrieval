package elements;

import hierarchy.Hierarchy;
import hierarchy.SparseVector;

import java.util.ArrayList;

public class Doc{

	private int docID;
	private int gutenbergID;
	private String path;
	private int totalNoTerms;
	ArrayList<Integer> incidenceList;
	private String title; //not yet used
	private Hierarchy hierarchy;

	/**
	 * Constructor to be used by a query
	 */
	public Doc(int gutenberID) {
		this.gutenbergID = gutenberID;
		this.incidenceList = new ArrayList<Integer>();
		this.hierarchy = new Hierarchy();
	}

	public Doc(int ID, int gutenberID, String path) {
		this.docID = ID;
		this.gutenbergID = gutenberID;
		this.path = path;
		this.incidenceList = new ArrayList<Integer>();
		this.hierarchy = new Hierarchy();
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

	public ArrayList<Integer> getIncidenceList(){
		return incidenceList;
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

	public Hierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public int fillDocHierarchy(int window, int dicSize) {
		SparseVector vecOriginal = hierarchy.addOriginalLevel(0, incidenceList, dicSize); //nao é adicionar, é representar por 1s e 0s
		SparseVector vecAggre = vecOriginal.vecReduction1(incidenceList, window, dicSize);
		int i = 1;
		while(vecAggre.getVec().size()>2) {
			vecAggre = vecAggre.vecReduction(window);
			hierarchy.addLevel(i, vecAggre);
			i++;
		}
		return i;
	}

	public int calculateHammingDistance(ArrayList<Integer> vecQuery, int level) {
		int hammingDistance = 0;
		for(int i=0; i<vecQuery.size()-1; i++) //CUIDADO! não tenho em conta que os vectores podem ter tamanh
			if(vecQuery.get(i) !=  this.getHierarchy().getLevel(level).getVec().get(i))
				hammingDistance += 1;

		return hammingDistance;
	}

	/**
	 * Check the Hamming Distance among vectors only considering the query's non zero positions
	 */
	public int calculateHammingDistance2(ArrayList<Integer> vecQuery, int level) {
		int hammingDistance = 0;
		for(int i=0; i<vecQuery.size()-1; i++) //CUIDADO! não tenho em conta que os vectores podem ter tamanh
			if(vecQuery.get(i) == 1)
				if(vecQuery.get(i) != this.getHierarchy().getLevel(level).getVec().get(i))
					hammingDistance += 1;

		return hammingDistance;
	}

	@Override
	public String toString(){
		String result = "doc " + getID() + " | ";
		for(Integer termID : incidenceList)
			result += termID + " ";	
		return result;
	}

}
