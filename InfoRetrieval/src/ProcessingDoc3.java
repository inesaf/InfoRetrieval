import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.TreeMap;

public class ProcessingDoc3 {

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

		while (doc.hasNextLine()) {  
			String line = doc.nextLine();
			if(line.contains("*** START OF THIS PROJECT GUTENBERG"))
				continue;				
			else if (line.contains("*** END OF THIS PROJECT GUTENBERG"))
				break;

			else 
				putIntoInvertedIndex(invertedIndex, line);
		}
	}

	private static void putIntoInvertedIndex(TreeMap<String, Integer> invertedIndex, String line) {
		Integer count;   // The number of occurrences of the word
		
		for (String _word : line.split("\\W+")) {
			String word = _word.toLowerCase();
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