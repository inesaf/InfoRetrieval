package elements;

import index.InvertedIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Repository {

	private String path;
	private static ArrayList<Doc> docList;


	public Repository(String path) {
		this.path = path;
		docList = new ArrayList<Doc>();
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

	/**
	 * Store in a list Documents' Object with id and path of the corresponding file
	 */
	public void insertDocsFromFolder() {
		File docsRepository = new File(path); //files found into the repository
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);

		int totalNoDocs = filesList.length;

		System.out.println("Total number of documents to index " + totalNoDocs);

		//loop to identify all documents to be indexed (identified by their Project Gutenberg id) 
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
	public void fillDocList(InvertedIndex invertedIndex) {
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
	
	/**
	 * Method to print the inverted index in the console  
	 */
	@Override
	public String toString(){
		String repositoryPath = "Repository of folders in " + path;
		String result = "\n";
		for (Doc d : docList){
			result += " " + d.printDoc() + "\n";
			
		}
		return repositoryPath + result;
	}
}
