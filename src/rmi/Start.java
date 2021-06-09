package rmi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class Start {
	final static String systemConfigFile = "system.properties";
	static String ServerAdd;
	static int ServerPort;
	static int numberOfNodes;
	static String[] nodesAddresses;
	static int registryPort;
	
	public static void main(String[] args) throws RemoteException, MalformedURLException {
		// TODO Auto-generated method stub
		
		setConfig(systemConfigFile); // read system configuration from file
		
		LocateRegistry.createRegistry(registryPort); // create rmi registry
		String URL="rmi://"+ServerAdd+":"+registryPort+"/shortestPath";
    	Naming.rebind(URL, new Server(true));
		System.out.println("Server is Running...");
	}
	private static ArrayList<String> readFromFile(String fileName) {
		ArrayList<String> config = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				config.add(line);
			}
			br.close();
			return config;
		} catch (Exception e) {
			System.err.format("Exception occurred while trying to read the input file");
			e.printStackTrace();
			return null;
		}
	}
	private static void setConfig(String fileName) {
		// TODO Auto-generated method stub
		ArrayList<String> config = readFromFile(fileName);
		
		ServerAdd = config.get(0).substring(11);
		System.out.println(ServerAdd);
		
		ServerPort = Integer.parseInt(config.get(1).substring(16));
		System.out.println(ServerPort);
		
		numberOfNodes = Integer.parseInt(config.get(2).substring(18));
		System.out.println(numberOfNodes);
		
		nodesAddresses=new String[numberOfNodes];
		
		for(int i=0;i<numberOfNodes;i++) {
			nodesAddresses[i]=config.get(3+i).substring(10);
			System.out.println(nodesAddresses[i]);
		}
		
		registryPort=Integer.parseInt(config.get(config.size()-1).substring(21));
		System.out.println(registryPort);
	}

}
