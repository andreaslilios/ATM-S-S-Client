
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Operations extends Remote{
    
   public String validCard(String pin,String id) throws RemoteException;
    public String deposit(int money) throws RemoteException;
    public String analipsi(int money) throws RemoteException;
    public int info() throws RemoteException;
    
}