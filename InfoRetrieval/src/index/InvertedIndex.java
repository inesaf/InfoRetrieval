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

	public int getNoDistinctTerms() {
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
	 * Method to print the inverted index in the console  
	 */
	@Override
	public String toString() {

		String indexSize = "\nTotal number distinct words: " + getNoDistinctTerms();
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

	
	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE - PARSE)
	 * Document tokenization where each token/word is added to the inverted index
	 */
	public void fillInvertedIndex(Repository docList) {
		Scanner doc;  

		//loop to read all documents (identified by their Project Gutenberg ID) 
		for (int i=0; i<docList.getSizeRepository(); i++){
			Doc d = docList.getDoc(i);
			System.out.println("Processing file: " + d.getID());
			try {  
				doc = new Scanner(new FileReader(d.getPath()));  
			}  
			catch (FileNotFoundException e) {  
				System.err.println(e);  
				return;
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
	}

	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE PARSE)
	 * Function used in parseDocs
	 */
	private static void parseLine(String line, int docID) {
		String term;
		List<Posting> postingList;


		for (String word : line.split("\\W+")) {
			term = word.toLowerCase();
			if(getKey(term) == null) {
				insertTerm(term, docID);
				//QUANDO TIVER TERM A FUNCIONAR, CRIAR UMA INSTÊNCIA COM ID, CF, WORD
			}
			else {
				postingList = getPostingList(term);
				insertDocFreq(postingList, docID);
			}
		}
		return;
	}

}
