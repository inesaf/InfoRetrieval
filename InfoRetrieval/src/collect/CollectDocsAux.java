package collect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class CollectDocsAux {

	static int[] choosenBooks = new int[]{1342, 1952, 11, 76, 5200, 1661, 1232, 2001, 23};
	int numChoosenBooks = choosenBooks.length;
	public static void main(String[] args){ 
		
		URL bookLink;
		for(int i=0; i<choosenBooks.length; i++)
		
		try {
			// get URL content
			bookLink = new URL("http://www.gutenberg.org/cache/epub/"+choosenBooks[i]+"/pg"+choosenBooks[i]+".txt");
			try {
			    Thread.sleep(6000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			// open the stream and put it into BufferedReader
			BufferedReader in = new BufferedReader(
					new InputStreamReader(bookLink.openStream()));
			
			String inputLine;
			
			//save to this filename
			String fileName = "/home/ines/Repository/"+i+".txt";
			
			File file = new File(fileName);

			//use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
 
			while ((inputLine = in.readLine()) != null) {
				bw.write(inputLine);
			}
 
			bw.close();
			in.close();
 
			System.out.println("Done");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}


