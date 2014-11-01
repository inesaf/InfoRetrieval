import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class ProcessingDoc3 {
	static String path = "/home/ines/Repository";
	static File docsRepository = new File(path);

	public static void main(String[] args) {
		TreeMap<String, List<Integer>> invertedIndex = new TreeMap<String, List<Integer>>( ); //instead of integer put a list of documents
		File[] filesList = docsRepository.listFiles();
		Arrays.sort(filesList);
		readWordDoc(invertedIndex, filesList);  
		printAllCounts(invertedIndex); 
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
			}
			else {
				if(!invertedIndex.get(key).contains(docID)) //the doc could already be listed
					postings.add(new Integer(docID));
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
	
	
}