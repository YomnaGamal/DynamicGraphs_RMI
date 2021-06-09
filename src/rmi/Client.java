package rmi;

//import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.*;
import java.io.*;
import java.net.MalformedURLException;

public class Client extends UnicastRemoteObject implements IRemote {

	protected Client(int port) throws RemoteException {
		super(port);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		// Check for hostname argument
		if (args.length != 2) {
			System.out.println("Syntax - PowerServiceClient host");
			System.exit(1);
		}

//		// Assign security manager
//		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new SecurityManager());
//		}

		// Call registry for PowerService
		IRemote service = (IRemote) Naming.lookup("rmi://" + args[0]+":"+args[1] + "/shortestPath");
		boolean ready = false;
		while (!ready) {
			try {
				if (service.serverIsReady().equals("R")) {
					System.out.println("R");
					ready = true;
				} else {
					System.out.println("Server still doesn't running");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
		ArrayList<String> Batch = new ArrayList<String>();
		Batch.add("Q 11 1");
		Batch.add("A 11 3");
		Batch.add("Q 11 2");
		Batch.add("Q 11 7");
//		Batch.add("F");
//		ArrayList<String> Batch = generateBatchs(10,5);
		for (int i = 0; i < Batch.size(); i++) {
			System.out.println(Batch.get(i));
		}
		ArrayList<String> result = service.executeBatch(Batch);
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}

	}

	private static ArrayList<String> generateBatchs(int n, int k) {
		int i = 0;
		ArrayList<String> Batch = new ArrayList<String>();
		while (i < n) {
			StringBuilder salt = new StringBuilder();
			Random random = new Random();
			String s = "ADQ";
			int index = random.nextInt(s.length());
			salt.append(s.charAt(index));
			salt.append(" ");
			salt.append(Integer.toString(random.nextInt(k)));
			salt.append(" ");
			salt.append(Integer.toString(random.nextInt(k)));
			String saltStr = salt.toString();
			Batch.add(saltStr);
			i++;
		}
		Batch.add("F");
		return Batch;
	}

	@Override
	public String serverIsReady() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
