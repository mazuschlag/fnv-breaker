// Breaker.java
import java.io.*;
import java.lang.Thread;
import java.lang.Runtime;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.math.BigInteger;
import java.util.Set;

public class Breaker {
	public class BreakerThread implements Runnable {
		Queue<String> keys;
		Hashtable<String,String> keyHashes;
		ArrayList<String> foundPasswords;
		boolean finished = false;
		int i = 0;

		public BreakerThread(Queue<String> keyBuffer, Hashtable<String,String> hashTable) {
			keys = keyBuffer;
			keyHashes = hashTable;
			foundPasswords = new ArrayList<String>();
		}

		public void run() {
			while (!keys.isEmpty()) {
				//System.out.println("Current Thread: " + Thread.currentThread().getName());
				String current = popFromQueue();
				String result = fnv(current);
				checkResult(current, result);
			}
			//System.out.println("Checking alphabet");
			while (i < ALPHABET.length) {
				String current = Character.toString(ALPHABET[i]);
				String result = fnv(current);
				checkResult(current, result);
				i++;
			}
		}

		public synchronized String popFromQueue() {
			if (!keys.isEmpty()) {
				return keys.remove();	
			}
			return "";
		}

		public synchronized String fnv(String byteString) {
			if (byteString != "") {
				byte[] bytes = byteString.getBytes();
				BigInteger hash = Breaker.FNV_OFFSET;
				for (byte b : bytes) {
					hash = hash.multiply(Breaker.FNV_PRIME).mod(Breaker.MODULO);
					hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
				}
				String result = hash.toString(16);
				return result;
			} 
			return "";
		}

		public synchronized void checkResult(String current, String result) {
			if (keyHashes.get(result) != null) {
				keyHashes.put(result, current);
			}
		} 

		public void printResults() {
			//System.out.println("Total output: " + keyHashes.size());
			StringBuffer stringBuffer = new StringBuffer();
			Set<String> keys = keyHashes.keySet();
			for (String key : keys) {
				stringBuffer.append(keyHashes.get(key));
				stringBuffer.append(" : ");
				stringBuffer.append(key);
				stringBuffer.append("\n");
			}
			System.out.print(stringBuffer);
		}

		public void checkMissing(Hashtable<String, String> input) {
		 	Set<String> inKeys = input.keySet();
		 	for (String inKey : inKeys) {
		 		if (keyHashes.get(inKey) == null) {
		 			System.out.println("Missing key: " + inKey);
		 		}
		 	}
		}
	}

	private String inputFile;
	private String passwordFile;
	private Queue<String> keyBuffer;
	private Hashtable<String, String> hashTable;
	private static final BigInteger FNV_PRIME = new BigInteger("309485009821345068724781371");
	private static final BigInteger FNV_OFFSET = new BigInteger("144066263297769815596495629667062367629");
	private static final BigInteger MODULO = new BigInteger("2").pow(128);
	private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private static final int CORES = Runtime.getRuntime().availableProcessors();
	
	public Breaker(String arg1, String arg2) {
		inputFile = arg1;
		passwordFile = arg2;
		keyBuffer = new LinkedList<String>();
		hashTable	= new Hashtable<String,String>();
	}

	public void parseFiles() {
		try {	
			File file = new File(inputFile);
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

		try {
			File file = new File(passwordFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				keyBuffer.add(line);
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runThreads() {
		int bufferSize = hashTable.size();
		BreakerThread breakerThread = new BreakerThread(keyBuffer, hashTable);
		Thread[] threadList = new Thread[CORES];
		for (int i = 0; i < threadList.length; i++) {
			threadList[i] = new Thread(breakerThread, Integer.toString(i));
			threadList[i].start();
		}
		for (Thread t : threadList) {
			try {
				t.join();	
			} catch (InterruptedException e) {
				//System.out.println(t.getName() + " has finished. Back to main thread");
			}
		}
		//System.out.println("All threads have finished");
		//System.out.println("Total input: " + bufferSize);
		breakerThread.printResults();
		//breakerThread.checkMissing(hashTable);
	}
	/*
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
	}*/

	public static void main(String[] args) {
		Breaker breaker = new Breaker(args[0], args[1]);
		breaker.parseFiles();
		breaker.runThreads();
	}
}
