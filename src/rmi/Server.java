package rmi;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.concurrent.ThreadLocalRandom;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Server extends UnicastRemoteObject implements IRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9129303223363938592L;
	private final static String graphFileName = "g1";
	private final static String graphEnd = "S";
	private final static String batchEnd = "F";
	private ArrayList<ArrayList<Integer>> graph;
	private ConcurrentHashMap<Integer, Integer> nodesMap; // map node number to its index in the graph
	private ReadWriteLock lock;
	private HashMap<Long, Long> clientStartTime; // map client id to its starting time
	static Logger log = Logger.getLogger(Server.class.getName());
	
	protected Server(Boolean fromFile) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		nodesMap = new ConcurrentHashMap<>();
		lock = new ReentrantReadWriteLock();
		graph = new ArrayList<>();
		clientStartTime = new HashMap<>();
		if (fromFile) {
			readFromFile(graphFileName);
		} else {
			readFromStandardInput();
		}
		System.setProperty("serverlog", "serverlog.out");
        PropertyConfigurator.configure("log/log4j.properties");
        log.info("Server is running");

	}

	@Override
	public String serverIsReady() throws RemoteException {
		// TODO Auto-generated method stub
		if (graph != null) {
			return "R";
		}
		return null;
	}

	@Override
	public ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException {
		// TODO Auto-generated method stub
		ArrayList<String> outputs = new ArrayList<String>();
		long startTime = System.currentTimeMillis();
		clientStartTime.put(Thread.currentThread().getId(), startTime);

		for (int i = 0; i < batch.size(); i++) {
			if (batch.get(i).equalsIgnoreCase(batchEnd)) {
				break;
			}
			String[] request = batch.get(i).split(" ");
			int src = Integer.parseInt(request[1]);
			int dest = Integer.parseInt(request[2]);
			// synchronized(this) {
			if (request[0].equals("Q")) {

				// outputs.add(query(src,dest));
				lock.readLock().lock();
				try {
					outputs.add(query(src, dest));
				} finally {
					lock.readLock().unlock();
				}
			} else if (request[0].equals("A")) {
				// add(src,dest);

				lock.writeLock().lock();
				try {
					add(src, dest);
				} finally {
					lock.writeLock().unlock();
				}
			} else if (request[0].equals("D")) {
				// delete(src,dest);
				lock.writeLock().lock();
				try {
					delete(src, dest);
				} finally {
					lock.writeLock().unlock();
				}
			}

			// }
			try {
				// operation takes a random amount of time (0 to 10000ms).
				// nextInt is normally exclusive of the top value,
				// so add 1 to make it inclusive
				int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
				Thread.sleep(randomNum);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		long stopTime = System.currentTimeMillis();
		if (clientStartTime.containsKey(Thread.currentThread().getId())) {
			System.out.println("the Thread ID=" + Thread.currentThread().getId());
			System.out.println("the execution Time:"
					+ (stopTime - (long) clientStartTime.get(Thread.currentThread().getId())) + " ms");
			log.info("the Thread ID=" + Thread.currentThread().getId());
			log.info("the execution Time:"
					+ (stopTime - (long) clientStartTime.get(Thread.currentThread().getId())) + " ms");
		}

		return outputs;
	}

	private void readFromFile(String filename) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equalsIgnoreCase(graphEnd)) {
					String[] s = line.split(" ");
					int from = Integer.parseInt(s[0]);
					int to = Integer.parseInt(s[1]);

					add(from, to);
				}
			}
			br.close();
		} catch (Exception e) {
			System.err.format("Exception occurred while trying to read the input file");
			e.printStackTrace();
		}
	}

	private void readFromStandardInput() {
		Scanner sc = new Scanner(System.in);
		String line;
		while (!(line = sc.nextLine()).equalsIgnoreCase(graphEnd)) {
			Scanner scLine = new Scanner(line);
			int from = scLine.nextInt();
			int to = scLine.nextInt();
			add(from, to);
			scLine.close();
		}
		sc.close();
	}

	private String query(int from, int to) {
		if (!nodesMap.containsKey(from) || !nodesMap.containsKey(to)) {
			return "-1";
		}
		return BFS(from, to);
	}

	private String BFS(int from, int to) {
		// TODO Auto-generated method stub
		int distance = 0;
		Queue<Integer> q = new LinkedList<>();
		q.add(from);
		HashSet<Integer> visited = new HashSet<>();
		visited.add(from);
		while (!q.isEmpty()) {
			int size = q.size();
			for (int i = 0; i < size; i++) {
				int x = q.poll();
				if (x == to) {
					return Integer.toString(distance);
				}
				Iterator<Integer> itr = graph.get(getIndex(x)).iterator();
				while (itr.hasNext()) {
					int new_node = itr.next();
					if (!visited.contains(new_node)) {
						q.add(new_node);
						visited.add(new_node);
					}
				}
			}
			distance++;
		}
		return "-1";
	}

	private void delete(int from, int to) {
		if (nodesMap.containsKey(from) && nodesMap.containsKey(to)) {
			int idx = nodesMap.get(from);
			if (graph.get(idx).contains(to)) {
				graph.get(idx).remove(new Integer(to));
			}

		}
	}

	private void add(int from, int to) {
		int idx_from = getIndex(from);
		getIndex(to);
		graph.get(idx_from).add(to);
	}

	private int getIndex(int node) {
		if (nodesMap.containsKey(node)) {// return index if node exists
			return nodesMap.get(node);
		} else {// create new node and return its index
			int index = graph.size();
			graph.add(new ArrayList<>());
			nodesMap.put(node, index);
			return index;
		}

	}

}
