package index;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import search.ProcessQuery;
import elements.Doc;
import elements.Posting;

public class Repository {

	private String path;
	private int hierarchySize;
	private static ArrayList<Doc> docList;


	public Repository(String path) {
		this.path = path;
		docList = new ArrayList<Doc>();
		hierarchySize = 0;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSizeRepository(){
		return docList.size();
	}

	public int getHierarchySize(){
		return hierarchySize;

	}

	public void setHierarchySize(int newHierarchy) {
		this.hierarchySize = newHierarchy;
	}

	/**
	 * Store in a list Documents' Object with id and path of the corresponding file
	 */
	public void insertDocsFromFolder() {
		File docsRepository = new File(path); //found files into the repository
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);

		int totalNoDocs = filesList.length;

		//loop to identify all documents to be indexed
		for (int i = 0; i < totalNoDocs; i++){
			String nameFile = filesList[i].getName();
			int gutenbergID = Integer.parseInt(nameFile.replaceAll("\\D+",""));
			docList.add(new Doc(i, gutenbergID, path+"/"+nameFile));
		}
		return;
	}

	public Doc getDoc(int docID){
		for(Doc d : docList)
			if(d.getID() == docID)
				return d;
		return null;
	}

	/**
	 * Traverses the inverted index and fill the documents with the respective words index
	 */
	public void buildIncidenceListDocList(InvertedIndex invertedIndex) {
		Map<String, List<Posting>> index = invertedIndex.getInvertedIndex();
		int docID;
		int termIndex = 0;
		for(Entry<String, List<Posting>> entry : index.entrySet()){
			for(Posting posting : entry.getValue()){
				docID = posting.getDocID();
				Doc d = getDoc(docID);
				d.addIncidenceValue(termIndex);
			}
			termIndex++;
		}
	}

	public void buildHierarchyDocList(int window, int dicSize) {
		for(Doc d : docList)
			hierarchySize = d.fillDocHierarchy(window, dicSize);
		setHierarchySize(hierarchySize);
		return;
	}

	/**
	 * Method to print the inverted index in the console  
	 */
	@Override
	public String toString(){
		String repositoryPath = "Repository of folders in " + path;
		String result = "\n";
		for (Doc d : docList){
			result += " " + d.toString() + "\n";

		}
		return repositoryPath + result;
	}


	public void printHierarchies(ProcessQuery query, Doc queryDoc) {
		for(int i=getHierarchySize()-1; i>=0; i--){
			int sizeQ = queryDoc.getHierarchy().getLevel(i).getVec().size();
			int num1sQuery = queryDoc.getHierarchy().getLevel(i).getNum1s();
			System.out.println("\nque " + queryDoc.getID() + " level " + i + " num1sQue " + num1sQuery + " size "+ sizeQ + " " + queryDoc.getHierarchy().getLevel(i));

			for(int docID : query.getDocIDList()){
				Doc d = getDoc(docID); //obter o doc através do ID
				int num1sDoc = d.getHierarchy().getLevel(i).getNum1s();
				int size = d.getHierarchy().getLevel(i).getVec().size();
				System.out.println("doc " + d.getID() + " level " + i +" num1sDoc "+ num1sDoc + " size "+ size + " " + d.getHierarchy().getLevel(i));
			}
		}

	}

	public int levelToBeginSearch(ProcessQuery query) {
		for(int i=getHierarchySize()-1; i>=0; i--){
			for(int docID : query.getDocIDList()){
				Doc d = getDoc(docID); //obter o doc através do ID
				int num1sDoc = d.getHierarchy().getLevel(i).getNum1s();
				int sizeVec = d.getHierarchy().getLevel(i).getVec().size();
				//check from what level the incidence list begins to have differences
				if(num1sDoc != sizeVec)
					return i;
			}
		}
		return 0;
	}

	public void rankDocs(int level, ProcessQuery query, Doc queryDoc) {
		// begin to calculate the hamming distance from the given level
		for (int i=level; i>=0; i--){
			System.out.println("\nlevel " + i);
			ArrayList<Integer> vecQuery = queryDoc.getHierarchy().getLevel(i).getVec();
			for(int docID : query.getDocIDList()){
				Doc d = getDoc(docID);
				int result = d.calculateHammingDistance2(vecQuery, i); 
				System.out.println("d" + d.getGutenberID() + " " + result);
			}
		}
	}
}
