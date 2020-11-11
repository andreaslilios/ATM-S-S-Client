
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Operations2 extends Remote {
    public String validCard2(String pin, String id) throws RemoteException;

    public String deposit2(int money, String id) throws RemoteException;

    public String analipsi2(int money, String id) throws RemoteException;

    public int info2(String id) throws RemoteException;
    
}
