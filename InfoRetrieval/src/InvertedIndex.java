import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvertedIndex {
 
	Map<String, List<Doc>> index = new HashMap<String, List<Doc>>();
	List<String> files = new ArrayList<String>();
 
	
	/***/
	public void indexFile(File file) throws IOException {
		int fileno = files.indexOf(file.getPath());
		if (fileno == -1) {
			files.add(file.getPath());
			fileno = files.size() - 1;
		}
 
		int pos = 0;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			for (String _word : line.split("\\W+")) {
				String word = _word.toLowerCase();
				List<Doc> idx = index.get(word);
				if (idx == null) {
					idx = new LinkedList<Doc>();
					index.put(word, idx);
				}
				idx.add(new Doc(fileno));
			}
		}
		System.out.println("indexed " + file.getPath() + " " + pos + " words");
	}
	
	/***/
	public void search(List<String> words) {
		for (String _word : words) {
			Set<String> answer = new HashSet<String>();
			String word = _word.toLowerCase();
			List<Doc> idx = index.get(word);
			if (idx != null) {
				for (Doc doc : idx) {
					answer.add(files.get(doc.docID));
				}
			}
			System.out.print(word);
			for (String f : answer) {
				System.out.print(" " + f);
			}
			System.out.println("");
		}
	}
	/***/
	public static void main(String[] args) {
		try {
			InvertedIndex idx = new InvertedIndex();
			for (int i = 1; i < args.length; i++) {
				idx.indexFile(new File(args[i]));
			}
			idx.search(Arrays.asList(args[0].split(",")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	private class Doc {
		private int docID;
 
		public Doc(int docID) {
			this.docID = docID;
		}
	}
}