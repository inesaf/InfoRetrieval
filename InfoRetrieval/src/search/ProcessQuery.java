package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class ProcessQuery {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in); //perguntar se é necessário realizar o processamento dos dados
		System.out.println("\nEnter a query: ");
		String query = in.nextLine();
		String[] queryList = processingQuery(query);
		try {
			identifyDocs(queryList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Processing query introduced by user
	 * @return 
	 */
	public static String[] processingQuery(String query){
		String[] queryList;
		queryList =  query.split("\\W+");
		return queryList;
	}

	/**
	 * Gather documents which have at least one word of the query 
	 */
	public static void identifyDocs(String[] query) throws IOException {
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
