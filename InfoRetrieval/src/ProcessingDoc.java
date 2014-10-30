import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
 
public class ProcessingDoc {
 
	public static void main(String[] args) {
		
		//misses read all documents

		int total = 0;
		Scanner wordFile;
		String word;     // A word read from the file  
		Integer count;   // The number of occurrences of the word
		
        try
        {
            wordFile = new Scanner(new FileReader("src/Repository/pg12345.txt"));
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e);
            return;
        }

        while (wordFile.hasNext( ))
        {
            // Read the next word and get rid of the end-of-line marker if needed:  
            word = wordFile.next( );

            // This makes the Word lower case.  
            word = word.toLowerCase();

            word = word.replaceAll("[^a-zA-Z0-9\\s]", "");
            System.out.println(word);
            // Get the current count of this word, add one, and then store the new count:  
            //count = getCount(word, frequencyData) + 1;
            //frequencyData.put(word, count);
            //total = total + count;
            //counter++;

        }
 
	}
}