import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

public class ProcessingDoc3 {
	static String path = "/home/ines/Repository";
	static File docsRepository = new File(path);
	static Map<String, Integer> dictionary = new HashMap<String, Integer>();

	public static void main(String[] args) {
		TreeMap<String, List<Integer>> invertedIndex = new TreeMap<String, List<Integer>>( ); //instead of integer put a list of documents
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);
		readWordDoc(invertedIndex, filesList);  
		//printAllCounts(invertedIndex); 
		Map<String, Integer> sortedDictionary = sortByComparator(dictionary);
		//printMap(sortedDictionary);
		try {
			printIndex(invertedIndex);
			printDicToFile(sortedDictionary);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dictionary.clear();
		invertedIndex.clear();
	}



	private static void readWordDoc(TreeMap<String, List<Integer>> invertedIndex, File[] docsList) {
		Scanner doc;  
		int totalNoDocs = docsList.length;
		
		System.out.println("Total number of documents to index " + totalNoDocs);
		
		//**FOR LOOP TO READ THE DOCUMENTS**  
		for (int i = 0; i < totalNoDocs; i++){
			String docName= docsList[i].getName().replaceAll("\\D+","");
			System.out.println(docName);
			int docID = Integer.parseInt(docName);
			try {  
				doc = new Scanner(new FileReader(docsList[i].getPath()));  
			}  
			catch (FileNotFoundException e) {  
				System.err.println(e);  
				return;
			}

			while (doc.hasNextLine()) {  
				String line = doc.nextLine();
				if(line.contains("START OF THIS PROJECT GUTENBERG"))
					continue;				
				else if (line.contains("END OF THIS PROJECT GUTENBERG"))
					break;

				else 
					putIntoInvertedIndex(invertedIndex, line, docID);
			}
		}
	}

	private static void putIntoInvertedIndex(TreeMap<String, List<Integer>> invertedIndex, String line, int docID) {

		for (String word : line.split("\\W+")) {
			String key = word.toLowerCase();
			List<Integer> postings = invertedIndex.get(key);
			if (postings == null){
				postings = new LinkedList<Integer>(); 
				invertedIndex.put(key, postings);
				postings.add(new Integer(docID));
				dictionary.put(key, 1);
			}
			else {
				if(!invertedIndex.get(key).contains(docID)){ //the doc could already be listed
					postings.add(new Integer(docID));
					int count = dictionary.get(key);
					dictionary.put(key, ++count);
				}
			}
		}

	}

	private static void printAllCounts(TreeMap<String, List<Integer>> invertedIndex) {
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
	
	private static void printIndex(TreeMap<String, List<Integer>> invertedIndex) throws IOException {

		File fout = new File("inverted_index.txt");
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for(String key : invertedIndex.keySet()){
			bw.write(key + " ");
			bw.write(invertedIndex.get(key).toString());
			bw.newLine();
		}  
		
		System.out.printf("\nTotal number distinct words: %15d\n", invertedIndex.size());  
	 
		bw.close();
	}
	
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
 
	public static void printMap(Map<String, Integer> map) {
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
	public static void printDicToFile(Map<String, Integer> dic) throws FileNotFoundException {
		File fout = new File("dictionary.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		for (Map.Entry<String, Integer> entry : dic.entrySet()) {
			try {
				bw.write(entry.getKey() + " : " + entry.getValue());
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}