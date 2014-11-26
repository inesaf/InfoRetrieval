package index;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import elements.Doc;
import elements.Posting;

/**
 * The application class (main function).
 */
public class DocProcessing {

	private static int window = 2;

	public static void main(String[] args) {
		
		/**Create and store documents into repository*/ 
		String path = "/home/ines/Repository";
		Repository repository = new Repository(path);
		repository.insertDocsFromFolder();
		
		/**Parse text of each document within the repository and inverted index filling*/
		InvertedIndex invertedIndex = new InvertedIndex();
		fillInvertedIndex(repository, invertedIndex); //put into other class
		System.out.println(invertedIndex.toString());
		
		/**The incidence list of each value is added*/
		repository.fillDocList(invertedIndex);
		System.out.println(repository.toString());
		
		/**The hierarchy of each document is built*/
		repository.fillDocsHierarchy(window, invertedIndex.getInvertedIndex().size());
		
		/** dictionary where the string corresponds to a word in the text and the Integer to its collection frequency*/
		Dictionary dictionary = new Dictionary();
		dictionary.fillTermList(invertedIndex);
		
		/**Map<String, Integer> dictionarySortedByKey = dictionary.sortByKey();
		System.out.println("\nDictionary sorted by key\n" + printMap(dictionarySortedByKey));
		
		Map<String, Integer> dictionarySortedByValue = dictionary.sortByValue();
		System.out.println("\nDictionary sorted by value\n" + printMap(dictionarySortedByValue));*/
		
		//hierarchicalSubpace(docList, invertedIndex, dictionary);
		
		
		/**long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		System.out.println("Time elapsed = " + (endTime - startTime)/1000000);*/
	}
	
	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE - PARSE)
	 * Document tokenization where each token/word is added to the inverted index
	 */
	public static void fillInvertedIndex(Repository docList, InvertedIndex invertedIndex) {
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
					parseLine(invertedIndex, line, d.getID());
			}
		}
	}
	
	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE PARSE)
	 * Function used in parseDocs
	 */
	private static void parseLine(InvertedIndex invertedIndex, String line, int docID) {
		String _word;
		String term;
		List<Posting> postingList;

		
		for (String word : line.split("\\W+")) {
			_word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			term = _word.toLowerCase();
			if(invertedIndex.getKey(term) == null) {
				invertedIndex.insertTerm(term, docID);
				//QUANDO TIVER TERM A FUNCIONAR, CRIAR UMA INSTÊNCIA COM ID, CF, WORD
			}
			else {
				postingList = invertedIndex.getPostingList(term);
				invertedIndex.insertDocFreq(postingList, docID);
			}
		}
		return;
	}
	
	public static String printMap(Map<String, Integer> tree){
		String result = "";
		
		for(Entry<String, Integer> element : tree.entrySet()) 
			result += element.getKey() + " : " + element.getValue() + "\n";
		
		return result;
	}

	
	/**private static void hierarchicalSubpace(ArrayList<Doc> docList, Map<String, List<Posting>> invertedIndex, Map<String, Integer> dictionary) {
		for (Doc d : docList){
			int i = 1;
			for (Entry<String, List<Posting>> term : invertedIndex.entrySet()){
				List<Posting> listDocsTerm = term.getValue();
				
				for (Posting posting : listDocsTerm)
					if (posting.getDocID() == d.getID())
						d.addIncidenceValue(i); //store incidences into the respective Doc
				i++;	
			}
			System.out.println(d.printDoc(d));
		}
	}*/
	
	/**
	 * Iterate over the array the Or operator with a specified window size
	 * @return 
	 * 
	 */
	/**private static int[] OR(int v[], int windowSize){
		int result = 0;
		int index = 0;
		int reducedV[] =  new int[v.length/windowSize];
		
		for(int i=0; i<v.length; i+=windowSize){
			for(int j=i; j<windowSize; j++){
				//System.out.println(result + " " +  v[j]);
				result = result | v[j];
				//System.out.println("result = " + result);
			}
			reducedV[index] = result;
			//System.out.println("rv tem " + reducedV[index] + " indice " + index);
			result = 0;
			index++;
		}
		System.out.println(reducedV[0]+ " " + reducedV[1]+ " " + reducedV[2]);
		return reducedV;
	}*/

}