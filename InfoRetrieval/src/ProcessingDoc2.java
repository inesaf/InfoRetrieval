import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.TreeMap;

public class ProcessingDoc2 {

	public static void main(String[] args) {
		TreeMap<String, Integer> invertedIndex = new TreeMap<String, Integer>( );  
		readWordDoc(invertedIndex);  
		printAllCounts(invertedIndex);  
	}


	private static void readWordDoc(TreeMap<String, Integer> invertedIndex) {
		Scanner doc;  
		String word;     // A word read from the file  
		Integer count;   // The number of occurrences of the word

		
		//**FOR LOOP TO READ THE DOCUMENTS**  
		//for (int x=0; x<Docs.length; x++) 
		try  
		{  
			doc = new Scanner(new FileReader("/home/ines/Repository/pg11.txt"));  
		}  
		catch (FileNotFoundException e)  
		{  
			System.err.println(e);  
			return;
		}

		while (doc.hasNext()) {
			// Read the next word and get rid of the end-of-line marker if needed:  
			word = doc.next();
			
			// This makes the Word lower case.  
			word = word.toLowerCase();
			
			word = word.replaceAll("[^a-zA-Z0-9\\s]", "");

			// Get the current count of this word, add one, and then store the new count:  
			count = getCount(word, invertedIndex) + 1;  
			invertedIndex.put(word, count);  
		}
	}

	private static int getCount(String word, TreeMap<String, Integer> frequencyData){  
		if (frequencyData.containsKey(word)){  // The word has occurred before, so get its count from the map  
			return frequencyData.get(word);  
		}  
		else {  // No occurrences of this word  
			return 0;  
		}  
	}

	private static void printAllCounts(TreeMap<String, Integer> invertedIndex) {
		System.out.println("-----------------------------------------------");  
		System.out.println("    Occurrences    Word");  

		for(String word : invertedIndex.keySet( ))  
		{  
			System.out.printf("%15d    %s\n", invertedIndex.get(word), word);  
		}  

		System.out.println("-----------------------------------------------");  
	}
}