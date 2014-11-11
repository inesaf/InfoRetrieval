import elements.Doc;

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
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * The application class (main function).
 */
public class DocProcessing {
	
	//private static int totalNumWords = 0;

	public static void main(String[] args) {
		ArrayList<Doc> docList = new ArrayList<Doc>();
		Map<String, List<DocFreq>> invertedIndex = new TreeMap<String, List<DocFreq>>( ); //inverted index with key and postings
		Map<String, Integer> dictionary = new HashMap<String, Integer>(); //dictionary where the string corresponds to a word in the text and the Integer to its collection frequency
		
		String path = "/home/ines/Repository";
		
		listDocs(path, docList);
		parse(docList, invertedIndex, dictionary);
		hierarchicalSubpace(docList, invertedIndex, dictionary);
	}
	
	
	/**
	 * Store in a list Documents' Object with id and path of the corresponding file
	 * @param path, docList 
	 */
	private static void listDocs(String path, ArrayList<Doc> docList) {
		
		File docsRepository = new File(path); //files found into the repository
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);
		
		int totalNoDocs = filesList.length;

		System.out.println("Total number of documents to index " + totalNoDocs);

		//loop to identify all documents to be indexed (identified by their Project Gutenberg id) 
		for (int i = 0; i < totalNoDocs; i++){
			String docName = filesList[i].getName().replaceAll("\\D+","");
			int docID = Integer.parseInt(docName);
			docList.add(new Doc(docID, filesList[i].getPath()));
		}
		return;
	}
	
	private static void hierarchicalSubpace(ArrayList<Doc> docList, Map<String, List<DocFreq>> invertedIndex, Map<String, Integer> dictionary) {
		for (Doc d : docList){
			int i = 1;
			for (Entry<String, List<DocFreq>> term : invertedIndex.entrySet()){
				List<DocFreq> listDocsTerm = term.getValue();
				
				for (DocFreq posting : listDocsTerm)
					if (posting.getDocID() == d.getID())
						d.addIncidenceValue(i); //store incidences into the respective Doc
				i++;	
			}
			System.out.println(d.printDoc(d));
		}
	}
	
	/**
	 * Iterate over the array the Or operator with a specified window size
	 * @return 
	 * 
	 */
	private static int[] OR(int v[], int windowSize){
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
	}


	

	/**
	 * Processing documents gathered into Repository defined by the path
	 * @param dictionary 
	 * @param invertedIndex 
	 */
	public static void parse(ArrayList<Doc> docList, Map<String, List<DocFreq>> invertedIndex, Map<String, Integer> dictionary) {
		
		readWordDoc(invertedIndex, dictionary, docList);  
		//printInvertedIndex(invertedIndex); 
		//printMap(dictionary);
		Scanner in = new Scanner(System.in);
		System.out.println("Do you want to remove the most common words of the inverted index? Y/N");
		String c = in.nextLine();
		boolean removeCommonWords = false;
		if(c.equalsIgnoreCase("y"))
			removeCommonWords = true;
		
		try {
			ArrayList<String> listCommonWords = printDicToFile(dictionary, removeCommonWords);
			System.out.println(listCommonWords.toString());
			printInvertedIndexToFile(invertedIndex, listCommonWords);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return; 
	}


	/**
	 * Document tokenization where each token/word is added to the inverted index and dictionary 
	 */
	private static void readWordDoc(Map<String, List<DocFreq>> invertedIndex, Map<String, Integer> dictionary, ArrayList<Doc> docList) {
		Scanner doc;  

		//loop to read all documents (identified by their Project Gutenberg ID) 
		for (Doc d : docList){
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
					putIntoStructures(invertedIndex, dictionary, line, d.getID());
			}
		}
	}

	/**
	 * Line by line each word is added to the inverted index and the document is added to its list
	 * In parallel each word is also added to the dictionary where its frequency collection is defined
	 */
	public static void putIntoStructures(Map<String, List<DocFreq>> invertedIndex, Map<String, Integer> dictionary, String line, int docID) {

		for (String word : line.split("\\W+")) {
			String _word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			String key = _word.toLowerCase();
			totalNumWords++;
			List<DocFreq> postings = invertedIndex.get(key);

			if (postings == null){ //the key doesn't exist in the inverted index
				postings = new ArrayList<DocFreq>(); 
				invertedIndex.put(key, postings);
				postings.add(new DocFreq(docID, 1));
				dictionary.put(key, 1);
			}
			else {
				DocFreq d = searchDocID(postings, docID);
				int freq;
				int count = dictionary.get(key);
				dictionary.put(key, count+1);
				
				if (d == null) { //first time that key appears in docID and need to be added
					postings.add(new DocFreq(docID, 1));
				}
				else {
					freq = d.getDocFreq();
					d.setDocFreq(freq+1);
				}
			}
		}
	}


	private static DocFreq searchDocID(List<DocFreq> docList, int docID) {
		for(DocFreq d : docList)
			if(d.getDocID() == docID)
				return d;
		
		return null;
	}
	
	

	/**
	 * Print the inverted index into a file  
	 */
	private static void printInvertedIndexToFile(Map<String, List<DocFreq>> invertedIndex, ArrayList<String> listCommonWords) throws IOException {

		File fout = new File("inverted_index.txt");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for(String key : invertedIndex.keySet()){
			if(listCommonWords.contains(key))
				continue;
			bw.write(key + " -> ");
			List<DocFreq> postings = invertedIndex.get(key);
			for(DocFreq d : postings)
				bw.write(d.getDocID() + " : " + d.getDocFreq() + " ");
			bw.newLine();
		}
		int commonWords = listCommonWords.size();
		int distinctWords = invertedIndex.size()-commonWords;
		
		System.out.printf("\nTotal number of words: %10d\n", totalNumWords);
		System.out.printf("Total number of common words considered: %10d\n", commonWords);
		System.out.printf("Total number distinct words (already filtered): %10d	%3.2f%%\n", distinctWords, (distinctWords*100.0/totalNumWords));  

		bw.close();
	}


	/**
	 * Sort words in the dictionary by descending order of frequency
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
	private static void printInvertedIndex(Map<String, List<DocFreq>> invertedIndex) {
		System.out.println("-----------------------------------------------");  
		System.out.println("    Occurrences    Word");  

		for(String key : invertedIndex.keySet()){
			System.out.printf("\n%s", key);
			//for(Integer docID : invertedIndex.get(key))
			System.out.printf(" -> %5s", invertedIndex.get(key).toString());  
		}  
		
		System.out.printf("\nTotal number distinct words: %15d\n", invertedIndex.size());  

		System.out.println("-----------------------------------------------");  
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