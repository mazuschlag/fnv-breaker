// Breaker.java
import java.io.*;
import java.math.*;
import java.lang.Thread;
import java.lang.Runtime;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;

public class Breaker {
	public class BreakerThread implements Runnable {
		private Queue<String> keys;
		private Hashtable<String,String> keyHashes;
		private final int TOTAL_THREADS;
		private int threadsRunning;
		private int i, j, k, l, m, n, o = 0;
		private static final int NULL_FLAG = -1;
		private static final int I_FLAG = 0;
		private static final int J_FLAG = 1;
		private static final int K_FLAG = 2;
		private static final int L_FLAG = 3;
		private static final int M_FLAG = 4;
		private static final int N_FLAG = 5;
		private static final int O_FLAG = 6;

		public BreakerThread(Queue<String> keyBuffer, Hashtable<String,String> hashTable, int threads) {
			keys = keyBuffer;
			keyHashes = hashTable;
			TOTAL_THREADS = threads;
			threadsRunning = threads;
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
				String current = getStringFromIncrement(I_FLAG, i);
				String result = fnv(current);
				checkResult(current, result);
			}

			waitForOthers(NULL_FLAG, I_FLAG);
			
			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					String current = getStringFromIncrement();
					StringBuilder current = new StringBuilder().append(ALPHABET[i]).append(ALPHABET[j]);
					increment(J_FLAG);
					String result = fnv(current.toString());
					checkResult(current.toString(), result);
				}
				waitForOthers(I_FLAG, J_FLAG);
			}
		}

		private synchronized void waitForOthers(int incrementFlag, int... resetFlags) {
			threadsRunning--;
			while (threadsRunning > 0) {	
				//System.out.println("Thread " + Thread.currentThread().getName() + " waiting...");
				try {
					wait();
					//System.out.println("Thread " + Thread.currentThread().getName() + " is awake!");
				} catch (InterruptedException e) {
					//System.out.println("Thread " + Thread.currentThread().getName() + " interrupted!");
				}
				break;
			}
			if (threadsRunning == 0) {
				//System.out.println("Thread " + Thread.currentThread().getName() + " will notify!");
				threadsRunning = TOTAL_THREADS;
				for (int flag : resetFlags) {
					resetIncrement(flag);
				}
				increment(incrementFlag);
				notifyAll();
			}
		}

		private synchronized String getStringFromIncrement(int flag, int index) {
			if (index < ALPHABET.length) {
				increment(I_FLAG);
				return Character.toString(ALPHABET[index]);
			}
			return "";
		}

		private void increment(int flag) {
			switch(flag) {
				case I_FLAG:
					i++;
					break;
				case J_FLAG:
					j++;
					break;
				case K_FLAG:
					k++;
					break;
				case L_FLAG:
					l++;
					break;
				case M_FLAG:
					m++;
					break;
				case N_FLAG:
					n++;
					break;
				case O_FLAG:
					o++;
					break;
				default:
					break;
			}
		}

		private void resetIncrement(int flag) {
			switch(flag) {
				case I_FLAG:
					i = 0;
					break;
				case J_FLAG:
					j = 0;
					break;
				case K_FLAG:
					k = 0;
					break;
				case L_FLAG:
					l = 0;
					break;
				case M_FLAG:
					m = 0;
					break;
				case N_FLAG:
					n = 0;
					break;
				case O_FLAG:
					o = 0;
					break;
				default:
					break;
			}
		}

		private synchronized String popFromQueue() {
			if (!keys.isEmpty()) {
				return keys.remove();	
			}
			return "";
		}


		private synchronized String fnv(String byteString) {
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

		private synchronized void checkResult(String current, String result) {
			if (keyHashes.get(result) != null) {
				keyHashes.put(result, current);
			}
		} 

		private void printResults() {
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

		private void checkMissing(Hashtable<String, String> input) {
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
		BreakerThread breakerThread = new BreakerThread(keyBuffer, hashTable, CORES);
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
