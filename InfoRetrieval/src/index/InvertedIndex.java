package index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import search.ProcessQuery;
import elements.Doc;
import elements.Posting;

public class InvertedIndex {

	private static Map<String, List<Posting>> invertedIndex;

	public InvertedIndex() {
		invertedIndex = new TreeMap<String, List<Posting>>( ); //inverted index with key and postings
	}

	public Map<String, List<Posting>> getInvertedIndex() {
		return invertedIndex;
	}

	public int getSize() {
		return invertedIndex.size();
	}

	public static List<Posting> getKey(String term) {
		return invertedIndex.get(term);
	}

	//the key doesn't exist in the inverted index
	public static void insertTerm(String term, int docID){
		ArrayList<Posting> postingList = new ArrayList<Posting>(); 
		invertedIndex.put(term, postingList);
		postingList.add(new Posting(docID, 1));
		return;
	}

	/**
	 * Return a list with the documents ID where the key/term appeared
	 */
	public static List<Posting> getPostingList(String key){ 
		return invertedIndex.get(key);
	}

	/**
	 * The term already exists in the inverted index but the document may or may not be in the posting list 
	 */
	public static void insertDocFreq(List<Posting> postingList, int docID){

		Posting d = getPosting(postingList, docID);
		int freq;

		if (d == null) { //first time that key appears in docID and need to be added
			postingList.add(new Posting(docID, 1));
		}
		else {
			freq = d.getDocFreq();
			d.setDocFreq(freq+1);
		}
		return;
	}

	/**
	 * Get the document within the postingList 
	 */
	private static Posting getPosting(List<Posting> postingList, int docID){
		for(Posting d : postingList)
			if(d.getDocID() == docID)
				return d;
		return null;
	}

	public void insertPostingList(String term, List<Posting> postingList) {
		invertedIndex.put(term, postingList);
		return;
	}

	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE - PARSE)
	 * Document tokenization where each token/word is added to the inverted index
	 */
	public void buildInvertedIndex(Repository docList) {

		//loop to read all documents (identified by their Project Gutenberg ID) 
		for (int i=0; i<docList.getSizeRepository(); i++){
			Doc d = docList.getDoc(i);
			addDoc2InvertedIndex(d);
		}
	}

	public void addDoc2InvertedIndex(Doc d){
		System.out.println("Processing file: " + d.getID());
		Scanner doc = null;
		try {
			doc = new Scanner(new FileReader(d.getPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  

		// the header and footer are discarded
		while (doc.hasNextLine()) {  
			String line = doc.nextLine();
			if(line.contains("START OF THIS PROJECT GUTENBERG"))
				continue;				
			else if (line.contains("END OF THIS PROJECT GUTENBERG"))
				break;
			else 
				parseLine(line, d.getID());
		}
	}

	/**
	 * Add queryDoc to Index while it add  
	 */
	public void addQuery2InvertedIndex(Doc d){
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter a query: ");
		parseQueryLine(in.next(), d.getID());
	}

	/**
	 * Add queryDoc to Index while it add  
	 */
	public void addQueryFile2InvertedIndex(Doc d){
		// codigo repetido no metodo fillInvertedIndex
		Scanner doc = null;
		try {
			doc = new Scanner(new FileReader(d.getPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  

		// the header and footer are discarded
		while (doc.hasNextLine()) {  
			String line = doc.nextLine();
			if(line.contains("START OF THIS PROJECT GUTENBERG"))
				continue;				
			else if (line.contains("END OF THIS PROJECT GUTENBERG"))
				break;
			else 
				parseQueryLine(line, d.getID());
		}
	}

	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE PARSE)
	 * Function used in parseDocs
	 */
	private static void parseLine(String line, int docID) {
		String term;
		String _word;
		List<Posting> postingList;

		for (String word : line.split("\\W+")) { //"\W+" splits the source at word boundaries removing spaces and punctuation.
			_word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			term = _word.toLowerCase();
			if(getKey(term) == null) {
				insertTerm(term, docID);
			}
			else {
				postingList = getPostingList(term);
				insertDocFreq(postingList, docID);
			}
		}
		return;
	}



	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE PARSE)
	 * Function used in parseQueryDocs
	 */
	private static void parseQueryLine(String line, int docID) {
		String term;
		String _word;
		List<Posting> postingList;

		for (String word : line.split("\\W+")) {
			_word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			term = _word.toLowerCase();
			if(getKey(term) != null){  //apenas introduzo o id da query quando o termo existe no indice (senão teria de alterar a lista de incidencas
				postingList = getPostingList(term);
				insertDocFreq(postingList, docID);
			}
		}
		return;
	}

	public void removeStopWords(List<String> stopWordList) {
		for(String word : stopWordList){
			invertedIndex.remove(word);
		}
		return;
	}

	/**
	 * By traversing the Inverted Index the incidence list is created 
	 * @param query 
	 */
	public void createIncidenceList(Doc queryDoc, ProcessQuery query) {
		int queryID = queryDoc.getID();
		int termIndex = 0;
		for(Entry<String, List<Posting>>  entry : invertedIndex.entrySet()){
			for(Posting posting : entry.getValue()){
				int auxID = posting.getDocID();
				if(queryID == auxID){
					queryDoc.addIncidenceValue(termIndex);
					getSimilarDocs(entry.getValue(), query, queryID);//guardar os outros values da linha 
				}
			}
			termIndex++;
		}
	}

	private void getSimilarDocs(List<Posting> docList, ProcessQuery query, int queryID) {
		List<Integer> list = query.getDocIDList();
		for(Posting posting : docList){
			int docID = posting.getDocID();
			if(!list.contains(docID) && docID != queryID)
				list.add(docID);
		}
	}


	/**
	 * Method to print the inverted index in the console  
	 */
	@Override
	public String toString() {

		String indexSize = "\nTotal number distinct words: " + getSize();
		String result = "";

		for(String key : invertedIndex.keySet()){
			result += key + " "+ invertedIndex.get(key).toString() + "\n";
		}

		return result+indexSize;
	}

	/**
	 * Print the inverted index into a file  
	 */

	//???????????????????????????????????????????????????????????????????????

	public void printInvertedIndexToFile(ArrayList<String> listCommonWords){

		File fout = new File("inverted_index.txt");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));	
			for(String key : invertedIndex.keySet()){
				if(listCommonWords.contains(key))
					continue;
				bw.write(key + " -> ");
				List<Posting> postings = invertedIndex.get(key);
				for(Posting d : postings)
					bw.write(d.getDocID() + " : " + d.getDocFreq() + " ");
				bw.newLine();
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int commonWords = listCommonWords.size();
		int distinctWords = invertedIndex.size()-commonWords;

		System.out.printf("\nTotal number of words: %10d\n", invertedIndex.size());
		System.out.printf("Total number of common words considered: %10d\n", commonWords);
		System.out.printf("Total number distinct words (already filtered): %10d	%3.2f%%\n", distinctWords, (distinctWords*100.0/invertedIndex.size()));  

	}

}
