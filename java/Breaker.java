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
import java.util.Arrays;

public class Breaker {
	public class BreakerThread implements Runnable {
		private Queue<String> keys; // Queue of common passwords
		private Hashtable<String,String> keyHashes; // Hashmap with hashed passwords to be guessed
		private int threadsRunning; // Number of threads currently running. Used to wait/notify other threads
		private final int TOTAL_THREADS; // Number of threads the CPU should support
		private int i, j, k, l, m, n, o = 0; // Iterators
		private final int TOTAL_PASSWORDS; // Number of passwords to check
		private int foundPasswords; // Number of passwords found
		private boolean finished; // Are we done yet?
		private final String resultsPath;

		// Flags used to tell functions which iterator needs to be incremented/reset
		private static final int NULL_FLAG = -1; // When no iterators need to be incremented
		private static final int I_FLAG = 0;
		private static final int J_FLAG = 1;
		private static final int K_FLAG = 2;
		private static final int L_FLAG = 3;
		private static final int M_FLAG = 4;
		private static final int N_FLAG = 5;
		private static final int O_FLAG = 6;

		public BreakerThread(Queue<String> keyBuffer, Hashtable<String,String> hashTable, int threads, String resultsFile) {
			keys = keyBuffer; 
			keyHashes = hashTable; 
			TOTAL_THREADS = threads; 
			threadsRunning = threads;
			TOTAL_PASSWORDS = hashTable.size();
			foundPasswords = 0;
			finished = false; 
			resultsPath = resultsFile;
		}

		public void run() {

			// Check words from password file
			while (!keys.isEmpty()) {
				checkFromFile();
				if (finished) return;
			}

			//System.out.println(Thread.currentThread().getName() + " finished file");
			if (finished) return;

			/* The next few blocks repeat the same basic idea: iterate through all possible
			   combinations of a specific word length, checking each one to see if it's in the
			   key of the hashmap. When one iteration finishes, the thread waits for all others
			   to finish their work, and then the iterator that just finished is reset, the next 
			   iterator is incremented, and the process begins again. If one thread realizes all
			   passwords have been found, it sets the 'finished' flag to true and all threads break
			   out of the loops and terminate */
			waitForOthers(NULL_FLAG, NULL_FLAG);

			while (i < ALPHABET.length) {
				checkFromIterationOne();	
				if (finished) return;
			}
			 
			//System.out.println(Thread.currentThread().getName() + " finished one");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG);

			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					checkFromIterationTwo();
					if (finished) return;
				}
				if (finished) return;
				waitForOthers(I_FLAG, J_FLAG);
			}

			//System.out.println(Thread.currentThread().getName() + " finished two");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG);

			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					while (k < ALPHABET.length) {
						checkFromIterationThree();
						if (finished) return;
					}
					if (finished) return;
					waitForOthers(J_FLAG, K_FLAG);
				}
				if (finished) return;
				waitForOthers(I_FLAG, J_FLAG, K_FLAG);
			}
			
			//System.out.println(Thread.currentThread().getName() + " finished three");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG, K_FLAG);

			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					while (k < ALPHABET.length) {
						while (l < ALPHABET.length) {
							checkFromIterationFour();	
							if (finished) return;
						}
						if (finished) break;
						waitForOthers(K_FLAG, L_FLAG);
					}
					if (finished) return;
					waitForOthers(J_FLAG, K_FLAG, L_FLAG);
				}
				if (finished) return;
				waitForOthers(I_FLAG, J_FLAG, K_FLAG, L_FLAG);
			}

			//System.out.println(Thread.currentThread().getName() + " finished four");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG, K_FLAG, L_FLAG);

			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					while (k < ALPHABET.length) {
						while (l < ALPHABET.length) {
							while (m < ALPHABET.length) {
								checkFromIterationFive();	
								if (finished) return;
							}
							if (finished) return;
							waitForOthers(L_FLAG, M_FLAG);
						}
						if (finished) return;
						waitForOthers(K_FLAG, L_FLAG, M_FLAG);
					}
					if (finished) return;
					waitForOthers(J_FLAG, K_FLAG, L_FLAG, M_FLAG);
				}
				if (finished) return;
				waitForOthers(I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG);
			}

			//System.out.println(Thread.currentThread().getName() + " finished five");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG);		
		
			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					while (k < ALPHABET.length) {
						while (l < ALPHABET.length) {
							while (m < ALPHABET.length) {
								while (n < ALPHABET.length) {
									checkFromIterationSix();
									if (finished) return;	
								}
								if (finished) return;
								waitForOthers(M_FLAG, N_FLAG);
							}
							if (finished) return;
							waitForOthers(L_FLAG, M_FLAG, N_FLAG);
						}
						if (finished) return;
						waitForOthers(K_FLAG, L_FLAG, M_FLAG, N_FLAG);
					}
					if (finished) return;
					waitForOthers(J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG);
				}
				if (finished) return;
				waitForOthers(I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG);
			}

			//System.out.println(Thread.currentThread().getName() + " finished six");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG);

			while (i < ALPHABET.length) {
				while (j < ALPHABET.length) {
					while (k < ALPHABET.length) {
						while (l < ALPHABET.length) {
							while (m < ALPHABET.length) {
								while (n < ALPHABET.length) {
									while(o < ALPHABET.length) {
										checkFromIterationSeven();
										if (finished) return;	
									}
									if (finished) return;
									waitForOthers(N_FLAG, O_FLAG);
								}
								if (finished) return;
								waitForOthers(M_FLAG, N_FLAG, O_FLAG);
							}
							if (finished) return;
							waitForOthers(L_FLAG, M_FLAG, N_FLAG, O_FLAG);
						}
						if (finished) return;
						waitForOthers(K_FLAG, L_FLAG, M_FLAG, N_FLAG, O_FLAG);
					}
					if (finished) return;
					waitForOthers(J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG, O_FLAG);
				}
				if (finished) return;
				System.out.println(Thread.currentThread().getName() + " i: " + i);
				waitForOthers(I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG, O_FLAG);
			}

			//System.out.println(Thread.currentThread().getName() + " finished seven");
			if (finished) return;

			waitForOthers(NULL_FLAG, I_FLAG, J_FLAG, K_FLAG, L_FLAG, M_FLAG, N_FLAG, O_FLAG);
		}

		// Check for most common passwords from file
		private synchronized void checkFromFile() {
			String current = popFromQueue();
			String result = fnv(current);		
			checkResult(current, result);
		}

		// Checking all words one character long
		private synchronized void checkFromIterationOne() {
			String current = getStringFromIncrement(I_FLAG, i);
			String result = fnv(current);
			checkResult(current, result);	
		}

		// Checking all words two characters long
		private synchronized void checkFromIterationTwo() {
			String current = getStringFromIncrement(J_FLAG, i, j);
			String result = fnv(current);
			checkResult(current, result);
		}

		// Checking all words three characters long
		private synchronized void checkFromIterationThree() {
			String current = getStringFromIncrement(K_FLAG, i, j, k);
			String result = fnv(current);
			checkResult(current, result);
		}

		// Checking all words four characters long
		private synchronized void checkFromIterationFour() {
			String current = getStringFromIncrement(L_FLAG, i, j, k, l);
			String result = fnv(current);
			checkResult(current, result);
		}

		// Checking all words five characters long
		private synchronized void checkFromIterationFive() {
			String current = getStringFromIncrement(M_FLAG, i, j, k, l, m);
			String result = fnv(current);
			checkResult(current, result);
		}

		// Checking all words six characters long
		private synchronized void checkFromIterationSix() {
			String current = getStringFromIncrement(N_FLAG, i, j, k, l, m, n);
			String result = fnv(current);
			checkResult(current, result);
		}

		// Checking all words seven characters long
		private synchronized void checkFromIterationSeven() {
			String current = getStringFromIncrement(O_FLAG, i, j, k, l, m, n, o);
			String result = fnv(current);
			checkResult(current, result);	
		}

		// Pop the next password from the most common password queue
		private String popFromQueue() {
			if (!keys.isEmpty()) {
				return keys.remove();	
			}
			return "";
		}

		// Creates a string based on the current iteration
		private String getStringFromIncrement(int flag, int... indexes) {
			// Double check that the current iteration is in bounds
			// If not, return empty string (which will propagate down the next function calls)
			for (int index : indexes) {
				if (index >= ALPHABET.length) {
					return "";
				}
			}
			increment(flag);
			StringBuilder current = new StringBuilder();
			for (int index : indexes) {
				current.append(ALPHABET[index]);
			}
			return current.toString();
		}

		// Calculate the hash of the plaintext password guess.
		// If there was no guess (empty buffer/index out of range), return empty string.
		private String fnv(String byteString) {
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

		// Check if the result of the password hash matches any of the keys in the hashmap
		// If so, add it to the hashman as that key's value
		private void checkResult(String current, String result) {
			if (keyHashes.get(result) != null) {
				keyHashes.put(result, current);
				foundPasswords++;
				if (foundPasswords == TOTAL_PASSWORDS) {
					finished = true;
				}
			}
		} 

		// Wait for all other threads to finish their tasks before continuing to the next iteration
		private synchronized void waitForOthers(int incrementFlag, int... resetFlags) {
			threadsRunning--;
			// Not the last thread so wait
			while (threadsRunning > 0 && !finished) {	
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Thread " + Thread.currentThread().getName() + " interrupted!");
				}
				break;
			}
			// Last thread, wake everyone up!
			if (threadsRunning == 0) {
				// Reset thread count, everyone is awake
				threadsRunning = TOTAL_THREADS;
				// Reset any iterators that need to be reset
				for (int flag : resetFlags) {
					resetIncrement(flag);
				}
				// Increment the proper iterator by one
				increment(incrementFlag);
				notifyAll();
			}
		}

		// Add to an iterator shared between the threads by one
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

		// Reset an iterator shared between the threads to zero
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

		// When all threads are finished, write the results to a file
		private void printResults() {
			StringBuffer stringBuffer = new StringBuffer();
			Set<String> keys = keyHashes.keySet();
			for (String key : keys) {
				stringBuffer.append(keyHashes.get(key));
				stringBuffer.append(" : ");
				stringBuffer.append(key);
				stringBuffer.append("\n");
			}
			try {
				PrintWriter writer = new PrintWriter(resultsPath, "UTF-8");
				writer.print(stringBuffer.toString());	
				writer.close();
			} catch (FileNotFoundException e) {
				System.out.println("No such file");
			} catch (UnsupportedEncodingException e) {
				System.out.println("No such encoding");
			}
		}
	}

	private String inputFile; // inputFile is from the user
	private String passwordFile; // passwordFile contains fifty thousand most common passwords
	private String resultsFile; // resultsFile will hold the path where the results will be written
	private Queue<String> keyBuffer; // Buffers for storing the common passwords
	private Hashtable<String, String> hashTable; // Hashtable for storing given hashes as keys. The solutions will be the value
	// These numbers should be immutable as they will be used for the hash
	private static final BigInteger FNV_PRIME = new BigInteger("309485009821345068724781371");
	private static final BigInteger FNV_OFFSET = new BigInteger("144066263297769815596495629667062367629");
	private static final BigInteger MODULO = new BigInteger("2").pow(128);
	private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	// Get the number of CPUs on the machine
	private static final int CORES = Runtime.getRuntime().availableProcessors();
	
	public Breaker(String arg1, String arg2, String arg3) {
		inputFile = arg1;
		passwordFile = arg2;
		resultsFile = arg3;
		keyBuffer = new LinkedList<String>();
		hashTable	= new Hashtable<String,String>();
	}

	// Open the file given by the user and the common password file
	public void parseFiles() {
		// Open the user's file
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
		// Open the password file
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
		// Create object that the threads will share
		BreakerThread breakerThread = new BreakerThread(keyBuffer, hashTable, CORES, resultsFile);
		
		// Number of threads is based on the number of CPUs (CORES)
		Thread[] threadList = new Thread[CORES];
		for (int i = 0; i < threadList.length; i++) {
			threadList[i] = new Thread(breakerThread, Integer.toString(i));
			threadList[i].start();
		}

		// Wait for all threads to finish
		for (Thread t : threadList) {
			try {
				t.join();	
			} catch (InterruptedException e) {
				System.out.println("Main thread interrupted");
			}
		}

		// Threads are finished, output results 
		breakerThread.printResults();
		System.out.println("Passwords successfully found, shutting down");
	}

	public static void main(String[] args) {
		Breaker breaker = new Breaker(args[0], args[1], args[2]);
		breaker.parseFiles();
		breaker.runThreads();
	}
}
