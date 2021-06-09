package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemote extends Remote {
	public String serverIsReady() throws RemoteException;

	public ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException;
}
