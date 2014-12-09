package search;

import index.InvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import elements.Doc;


public class ProcessQuery {

	private static List<Integer> docIDList;

	public ProcessQuery(){
		setDocIDList(new ArrayList<Integer>());
	}

	public List<Integer> getDocIDList() {
		Collections.sort(docIDList);
		return docIDList;
	}

	public static void setDocIDList(List<Integer> docIDList) {
		Collections.sort(docIDList);
		ProcessQuery.docIDList = docIDList;
	}
	
	@Override
	public String toString(){
		String result = "List of docIDs that have one or more words of the query [ ";
		for(int similarDoc : docIDList)
			result += similarDoc + " ";
		
		return result + "]"; 
	}


	/**
	 * Gather documents which have at least one word of the query  -----> NOT USED I THINK
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

	public Doc queryProcessing(String queryPath, int queryID, InvertedIndex invertedIndex) {
		int option = askQueryType(queryPath);
		Doc queryDoc = null;
		if(option == 1){
			queryDoc = askForQuery(queryID, queryPath);
			invertedIndex.addQuery2InvertedIndex(queryDoc);
		}
		if(option == 2){
			queryDoc = processQueryFile(queryID, queryPath);
			invertedIndex.addQueryFile2InvertedIndex(queryDoc);
		}
		
		return queryDoc;

	}

	private int askQueryType(String queryPath) {
		Scanner in = new Scanner(System.in);
		System.out.println("\nTo do a search choose among the following options: ");
		System.out.println("1 - if you pretend to type a query");
		System.out.println("2 - if you pretend to use the document that is in " + queryPath);
		return in.nextInt();
	}

	public Doc processQueryFile(int queryID, String queryPath) {
		File docsRepository = new File(queryPath); //found files into the repository
		File[] filesList = docsRepository.listFiles();

		String nameFile = filesList[0].getName(); //get the first file (since its the only one within the directory 'queryPath')
		int gutenbergID = Integer.parseInt(nameFile.replaceAll("\\D+",""));

		return new Doc(queryID, gutenbergID, queryPath+"/"+nameFile);
	}
		

	public Doc askForQuery(int queryID, String queryPath) {
		return new Doc(queryID, 0, null);
	}


}
