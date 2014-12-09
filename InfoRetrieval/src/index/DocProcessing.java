package index;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import search.ProcessQuery;
import elements.Doc;

/**
 * The application class (main function).
 */
public class DocProcessing {
	
	public static void main(String[] args) {
		
		/**Create and store documents into repository*/ 
		String repositoryPath = "/home/ines/Repository";
		String queryPath = "/home/ines/Query";
		
		Repository repository = new Repository(repositoryPath);
		repository.insertDocsFromFolder();
		System.out.println("Total number of documents to index = " + repository.getSizeRepository() + "\n");

		System.out.println("Creating Inverted Index ...");
		InvertedIndex invertedIndex = new InvertedIndex();
		invertedIndex.buildInvertedIndex(repository); 
		System.out.println("Number of distinct words = " + invertedIndex.getSize());
		System.out.println(invertedIndex.toString());

		System.out.println("Creating dictionary of words ...");
		Dictionary dictionary = new Dictionary();
		dictionary.buildDictionary(invertedIndex);
		Map<String, Integer> dictionarySortedByValue = dictionary.sortByValue();
		System.out.println("Higher colection frequency was \"" + dictionarySortedByValue.entrySet().iterator().next() + "\"");
		//System.out.println("\nDictionary sorted by value\n" + printMap(dictionarySortedByValue));
		
		int threshold = askThreshold();
		System.out.println("Removing stop words from dictionary and inverted index ...");
		List<String> stopWordList = dictionary.removeStopWords(threshold);
		invertedIndex.removeStopWords(stopWordList);
		//System.out.println(invertedIndex.toString());
		//Map<String, Integer> dictionarySortedByKey = dictionary.sortByKey();
		//System.out.println("\nDictionary sorted by key\n" + printMap(dictionarySortedByKey));
		
		System.out.println("Building incidence list of each Doc...");
		repository.buildIncidenceListDocList(invertedIndex);
		System.out.println(repository.toString());

		int window = askWindow();
		System.out.println("Building hierarchy of each Doc ...");
		repository.buildHierarchyDocList(window, dictionary.getDictionarySize());

		/**
		 * Ask the user to enter a query or choose a file:
		 */
		System.out.println("\nProcessing Query...");
		ProcessQuery query = new ProcessQuery();
		Doc queryDoc = query.queryProcessing(queryPath, repository.getSizeRepository(), invertedIndex);
		invertedIndex.createIncidenceList(queryDoc, query);
		System.out.println(queryDoc.toString()); //print a docIDList which are considered similar with the query 
		System.out.println(query.toString());
		queryDoc.fillDocHierarchy(window, dictionary.getDictionarySize());
		repository.printHierarchies(query, queryDoc);

		System.out.println("\nReturning ranked by similarity...");
		int level = repository.levelToBeginSearch(query);
		repository.rankDocs(level, query, queryDoc);
		
		
		/**long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		System.out.println("Time elapsed = " + (endTime - startTime)/1000000);*/
	}



	private static int askThreshold() {
		Scanner in = new Scanner(System.in);
		System.out.println("Choose a threshold to remove words that are above this: ");
		return in.nextInt();
	}



	private static int askWindow() {
		Scanner in = new Scanner(System.in);
		System.out.println("Choose a window size to perform the OR operator: ");
		return in.nextInt();
	}



	public static String printMap(Map<String, Integer> tree){
		String result = "";

		for(Entry<String, Integer> element : tree.entrySet()) 
			result += element.getKey() + " : " + element.getValue() + "\n";

		return result;
	}

}