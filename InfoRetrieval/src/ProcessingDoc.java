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
import java.util.TreeMap;

public class ProcessingDoc {
	
	private static int totalNumWords = 0;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in); //perguntar se é necessário realizar o processamento dos dados

		System.out.println("Do you wish to process documents to create a new inverted index? Y/N");
		String a = in.nextLine();
		if(a.equalsIgnoreCase("y")){
			System.out.println("The default path is '/home/ines/Repository'. Do you wish to change it? Y/N");
			String b = in.nextLine();
			if(b.equalsIgnoreCase("y")){
				System.out.println("Specify the new path: ");
				String path = in.nextLine();
				processingDocs(path);
			}
			if(b.equalsIgnoreCase("n"))
				processingDocs("/home/ines/Repository");
		}
	}


	/**
	 * Processing documents gathered into Repository defined by the path
	 */
	public static void processingDocs(String path) {

		Map<String, List<Doc>> invertedIndex = new TreeMap<String, List<Doc>>( ); //inverted index with key and postings
		Map<String, Integer> dictionary = new HashMap<String, Integer>(); //dictionary where the string corresponds to a word in the text and the Integer to its collection frequency
		File docsRepository = new File(path); //files found into the repository
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);
		readWordDoc(invertedIndex, dictionary, filesList);  
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dictionary.clear();
		invertedIndex.clear();

	}


	/**
	 * Document tokenization where each token/word is added to the inverted index and dictionary 
	 */
	private static void readWordDoc(Map<String, List<Doc>> invertedIndex, Map<String, Integer> dictionary, File[] docsList) {
		Scanner doc;  
		int totalNoDocs = docsList.length;

		System.out.println("Total number of documents to index " + totalNoDocs);

		//loop to read all documents (identified by their Project Gutenberg id) 
		for (int i = 0; i < totalNoDocs; i++){
			String docName= docsList[i].getName().replaceAll("\\D+","");
			System.out.println("Processing file: " + docName);
			int docID = Integer.parseInt(docName);
			try {  
				doc = new Scanner(new FileReader(docsList[i].getPath()));  
			}  
			catch (FileNotFoundException e) {  
				System.err.println(e);  
				return;
			}

			// the header and footer is discarded
			while (doc.hasNextLine()) {  
				String line = doc.nextLine();
				if(line.contains("START OF THIS PROJECT GUTENBERG"))
					continue;				
				else if (line.contains("END OF THIS PROJECT GUTENBERG"))
					break;
				else 
					putIntoStructures(invertedIndex, dictionary, line, docID);
			}
		}
	}

	/**
	 * Line by line each word is added to the inverted index and the document is added to its list
	 * In parallel each word is also added to the dictionary where its frequency collection is defined
	 */
	public static void putIntoStructures(Map<String, List<Doc>> invertedIndex, Map<String, Integer> dictionary, String line, int docID) {

		for (String word : line.split("\\W+")) {
			String _word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			String key = _word.toLowerCase();
			totalNumWords++;
			List<Doc> postings = invertedIndex.get(key);

			if (postings == null){ //the key doesn't exist in the inverted index
				postings = new ArrayList<Doc>(); 
				invertedIndex.put(key, postings);
				postings.add(new Doc(docID, 1));
				dictionary.put(key, 1);
			}
			else {
				Doc d = searchDocID(postings, docID);
				int freq;
				int count = dictionary.get(key);
				dictionary.put(key, count+1);
				
				if (d == null) { //first time that key appears in docID and need to be added
					postings.add(new Doc(docID, 1));
				}
				else {
					freq = d.getFrequency();
					d.setFrequency(freq+1);
				}
			}
		}
	}


	private static Doc searchDocID(List<Doc> docList, int docID) {
		for(Doc d : docList)
			if(d.getID() == docID)
				return d;
		
		return null;
	}
	
	

	/**
	 * Print the inverted index into a file  
	 */
	private static void printInvertedIndexToFile(Map<String, List<Doc>> invertedIndex, ArrayList<String> listCommonWords) throws IOException {

		File fout = new File("inverted_index.txt");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for(String key : invertedIndex.keySet()){
			if(listCommonWords.contains(key))
				continue;
			bw.write(key + " -> ");
			List<Doc> postings = invertedIndex.get(key);
			for(Doc d : postings)
				bw.write(d.getID() + " : " + d.getFrequency() + " ");
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
	private static void printInvertedIndex(Map<String, List<Doc>> invertedIndex) {
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