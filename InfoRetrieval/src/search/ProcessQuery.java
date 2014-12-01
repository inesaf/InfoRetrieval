package search;

import index.InvertedIndex;
import index.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import elements.Doc;
import elements.Posting;


public class ProcessQuery {

	private static InvertedIndex invertedIndexQuery;
	private static List<Integer> docIDList;
	String path = "/home/ines/Query/pg11.txt";

	public ProcessQuery(){
		invertedIndexQuery = new InvertedIndex();
		docIDList = new ArrayList<Integer>();
	}



	/**Identify and get query document*/


	/**
	 * PROVISÓRIO (PÔR NUMA CLASSE - PARSE)
	 * Document tokenization where each token/word is added to the inverted index
	 * @return 
	 */
	public List<Integer> identifyDocs(InvertedIndex invertedIndex) {
		Scanner doc;  
		try {  
			doc = new Scanner(new FileReader(path));  
		}  
		catch (FileNotFoundException e) {  
			System.err.println(e);  
			return null;
		}

		// the header and footer are discarded
		while (doc.hasNextLine()) {  
			String line = doc.nextLine();
			if(line.contains("START OF THIS PROJECT GUTENBERG"))
				continue;				
			else if (line.contains("END OF THIS PROJECT GUTENBERG"))
				break;
			else 
				parseLine2(invertedIndex, line);
		}

		return docIDList;
	}

	private static void parseLine(InvertedIndex invertedIndex, String line) {
		String _word;
		String term;
		List<Posting> postingList;

		//vendo palavra a palavra guardar numa estrutura os documentos que têm essa palavra
		for (String word : line.split("\\W+")) {
			_word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			term = _word.toLowerCase();
			if(invertedIndexQuery.getKey(term) == null) { //tem de se colocar os documentos onde essa palavra aparece
				postingList = invertedIndex.getKey(term);
				if(postingList != null)
					invertedIndexQuery.insertPostingList(term, postingList);
			}
		}

		return;
	}

	private static void parseLine2(InvertedIndex invertedIndex, String line) {
		String _word;
		String term;
		List<Posting> postingList;

		//nº documentos presentes na coleccao

		//vendo palavra a palavra guardar numa estrutura os documentos que têm essa palavra
		for (String word : line.split("\\W+")) {
			_word = word.replaceAll("_", ""); //includes the case of _word_ e.g.
			term = _word.toLowerCase();
			if(invertedIndexQuery.getKey(term) == null) { //tem de se colocar os documentos onde essa palavra aparece
				postingList = invertedIndex.getKey(term);
				if(postingList != null){
					invertedIndexQuery.insertPostingList(term, postingList);
					for(Posting posting : postingList){ //pôr numa função
						int docID = posting.getDocID();
						if (!docIDList.contains(docID))
							docIDList.add(docID);
					}
				}
			}
		}

		return;
	}
	
	//transformar a query como um doc (ie. ter as hierarquias)
	private Doc setQuery2Doc(){
		int gutenbergID = Integer.parseInt(path.replaceAll("\\D+",""));
		Doc query = new Doc(gutenbergID);
		return query;
	}
	
	//comecar o processo de procura pelas hierarquias
	//getDocs da docIDList indicada
	//comecar pelo nivel mais abaixo
	//em cada nivel contar o nº1
	//guardar os q são iguais à query

	/**
	 * Gather documents which have at least one word of the query 
	 */
	private static void identifyDocs(String[] query) throws IOException {
		File fin = new File("inverted_index.txt"); 
		FileInputStream fis = new FileInputStream(fin);

		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		while ((line = br.readLine()) != null) {
			String arr[] = line.split("\\W+", 2); //ESTOU A ASSUMIR QUE VAI RECEBER UMA QUERY
			String key = arr[0];
			String docs = arr[1];
			for (String queryWord : query)
				if(queryWord.equalsIgnoreCase(key))
					System.out.println(key + " -> "+ docs);
		}

		br.close();
	}

}
