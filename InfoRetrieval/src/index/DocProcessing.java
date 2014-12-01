package index;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import search.ProcessQuery;
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
		invertedIndex.fillInvertedIndex(repository); //put into other class
		System.out.println(invertedIndex.toString());
		
		/**The incidence list of each value is added*/
		repository.fillDocList(invertedIndex);
		System.out.println(repository.toString());
		
		/**The hierarchy of each document is built*/
		repository.fillDocsHierarchy(window, invertedIndex.getInvertedIndex().size());
		
		/** dictionary where the string corresponds to a word in the text and the Integer to its collection frequency*/
		Dictionary dictionary = new Dictionary();
		dictionary.fillTermList(invertedIndex);
		
		ProcessQuery query = new ProcessQuery();
		
		List docIDList= query.identifyDocs(invertedIndex); //nao e preciso retornar
		
		
		
		/**Map<String, Integer> dictionarySortedByKey = dictionary.sortByKey();
		System.out.println("\nDictionary sorted by key\n" + printMap(dictionarySortedByKey));
		
		Map<String, Integer> dictionarySortedByValue = dictionary.sortByValue();
		System.out.println("\nDictionary sorted by value\n" + printMap(dictionarySortedByValue));*/
		
		//hierarchicalSubpace(docList, invertedIndex, dictionary);
		
		
		/**long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		System.out.println("Time elapsed = " + (endTime - startTime)/1000000);*/
	}
	

	
	public static String printMap(Map<String, Integer> tree){
		String result = "";
		
		for(Entry<String, Integer> element : tree.entrySet()) 
			result += element.getKey() + " : " + element.getValue() + "\n";
		
		return result;
	}

}