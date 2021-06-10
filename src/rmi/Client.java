package rmi;

//import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.*;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Client extends UnicastRemoteObject implements IRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4501269218926743441L;
	static Logger log = Logger.getLogger(Client.class.getName());
	protected Client() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
	}



	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		// Check for hostname argument
		/**
		 * if (args.length != 2) { System.out.println("Syntax - PowerServiceClient
		 * host"); System.exit(1); }
		 */
		System.setProperty("clientlog", "clientlog.out");
        PropertyConfigurator.configure("src/log/log4j.properties");

		// Call registry for PowerService
		IRemote service = (IRemote) Naming.lookup("rmi://" + "127.0.1.1" + ":" + "1099" + "/shortestPath");
		boolean ready = false;
		while (!ready) {
			try {
				if (service.serverIsReady().equals("R")) {
					System.out.println("R");
					ready = true;
					log.info("Server is running");
				} else {
					System.out.println("Server still doesn't running");
					log.info("Server still doesn't running");
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
//		ArrayList<String> Batch = new ArrayList<String>();
//		Batch.add("Q 11 0");
//		Batch.add("A 11 3");
//		Batch.add("Q 11 10");
//		Batch.add("Q 3 6");
//		Batch.add("D 11 5");
//		Batch.add("F");
		int nofB = 1;
		for (int n = 0; n < nofB; n++) {

			ArrayList<String> Batch = generateBatchs(10, 10);
			log.info("Batch contains:");
			System.out.println("Batch contains:");
			for (int i = 0; i < Batch.size(); i++) {
				System.out.println(Batch.get(i));
				log.info(Batch.get(i));
			}
//		log.debug("Batch generated successfully");
			long startTime = System.currentTimeMillis();
			ArrayList<String> result = service.executeBatch(Batch);
			long endTime = System.currentTimeMillis();
			log.info("Results calculated successfully");
			log.info("Time of execution = " + (-startTime + endTime));
			log.info("Start printing Result:");
//		System.out.println("Results calculated successfully, Start printing it:");

			for (int i = 0; i < result.size(); i++) {
				System.out.println(result.get(i));
				log.info(result.get(i));
			}
			log.info("End Batch");
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
			salt.append(Integer.toString(random.nextInt(k) + 1));
			salt.append(" ");
			salt.append(Integer.toString(random.nextInt(k) + 1));
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
