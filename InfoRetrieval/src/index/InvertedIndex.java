package index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import elements.Posting;

public class InvertedIndex {

	private final Map<String, List<Posting>> invertedIndex;

	public InvertedIndex() {
		invertedIndex = new TreeMap<String, List<Posting>>( ); //inverted index with key and postings
	}

	public Map<String, List<Posting>> getInvertedIndex() {
		return invertedIndex;
	}

	public int getNoDistinctTerms() {
		return invertedIndex.size();
	}

	public List<Posting> getKey(String term) {
		return invertedIndex.get(term);
	}

	//the key doesn't exist in the inverted index
	public void insertTerm(String term, int docID){
		ArrayList<Posting> postingList = new ArrayList<Posting>(); 
		invertedIndex.put(term, postingList);
		postingList.add(new Posting(docID, 1));
		return;
	}

	/**
	 * Return a list with the documents ID where the key/term appeared
	 */
	public List<Posting> getPostingList(String key){ 
		return invertedIndex.get(key);
	}
	
	/**
	 * The term already exists in the inverted index but the document may or may not be in the posting list 
	 */
	public void insertDocFreq(List<Posting> postingList, int docID){

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
	private Posting getPosting(List<Posting> postingList, int docID){
		for(Posting d : postingList)
			if(d.getDocID() == docID)
				return d;
		return null;
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
}
