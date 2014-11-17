package index;
import elements.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The application class (main function).
 */
public class DocProcessing {

	public static void main(String[] args) {
		
		/**Create and store documents into repository*/ 
		String path = "/home/ines/Repository";
		Repository repository = new Repository(path);
		repository.insertDocsFromFolder();
		
		/**Parse text of each document within the repository and inverted index filling*/
		InvertedIndex invertedIndex = new InvertedIndex();
		fillInvertedIndex(repository, invertedIndex); //put into other class
		//System.out.println(invertedIndex.toString());
		
		/**The incidence list of each value is added*/
		repository.fillDocList(invertedIndex);
		System.out.println(repository.toString());
		
		//Map<String, Integer> dictionary = new HashMap<String, Integer>(); //dictionary where the string corresponds to a word in the text and the Integer to its collection frequency
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


	/**
	 * Processing documents gathered into Repository defined by the path
	 * @param dictionary 
	 * @param invertedIndex 
	 */
	/**public static void parse(ArrayList<Doc> docList, InvertedIndex invertedIndex, Map<String, Integer> dictionary) {
		
		readWordDoc(invertedIndex, dictionary, docList);  
		Scanner in = new Scanner(System.in);
		System.out.println("Do you want to remove the most common words of the inverted index? Y/N");
		String c = in.nextLine();
		boolean removeCommonWords = false;
		if(c.equalsIgnoreCase("y"))
			removeCommonWords = true;
		
		try {
			ArrayList<String> listCommonWords = printDicToFile(dictionary, removeCommonWords);
			System.out.println(listCommonWords.toString());
			//printInvertedIndexToFile(invertedIndex, listCommonWords);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return; 
	}*/


	
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


	

	/**
	 * HashMpa - Sort words in the dictionary by descending order of frequency
	 */
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
				new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Print dictionary of words and collection frequency into a file  
	 * @param removeCommonWords 
	 */
	private static ArrayList<String> printDicToFile(Map<String, Integer> dic, boolean removeCommonWords) throws FileNotFoundException {
		File fout = new File("dictionary.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		Map<String, Integer> sortedDic = sortByComparator(dic);
		
		double numCommonWords = 0;
		ArrayList<String> listCommonWords = new ArrayList<String>();
		if(removeCommonWords)
			numCommonWords = sortedDic.size() * 0.01;

		for (Map.Entry<String, Integer> entry : sortedDic.entrySet()) {
			if(numCommonWords > 0){
				listCommonWords.add(entry.getKey());
				--numCommonWords;
				continue;
			}
			try {
				bw.write(entry.getKey() + " : " + entry.getValue());
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listCommonWords;
	}

	/**
	 * Auxiliary method to print the inverted index in the console  
	 */
	private static void printMap(Map<String, Integer> map) {
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

}