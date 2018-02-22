// Breaker.java
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class Breaker {
	private String fileName;
	private Hashtable<String, String> hashTable;

	public Breaker(String arg) {
		fileName = arg;
		hashTable	= new Hashtable<String,String>();
	}

	public void parse() {
		try {	
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				hashTable.put(line, "");
			}
			
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void output() {
		String key;
		StringBuffer keys = new StringBuffer();
		Enumeration hashes = hashTable.keys();
		while(hashes.hasMoreElements()) {	
			key = (String) hashes.nextElement();
			keys.append(key);
			keys.append("\n");
		}
		System.out.println(keys.toString());
	}

	public static void main(String[] args) {
		Breaker breaker = new Breaker(args[0]);
		breaker.parse();
		breaker.output();
	}
}
