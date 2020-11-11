
import java.awt.Graphics;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankServer extends UnicastRemoteObject implements Operations {

    private static Account acount = new Account(null, null, null, null);
    private static Operations look_op;
    private static Operations2 look_op2;
    private static ArrayList<String> records = new ArrayList<String>();

    public BankServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {

            //ΕΔΩ ΕΧΟΥΜΕ ΤΙΣ ΠΟΛΙΤΙΚΕΣ ΑΣΦΑΛΕΙΑΣ ΟΙ ΟΠΟΙΕΣ ΔΕΝ ΜΑΣ ΕΠΙΤΡΕΠΟΥΝ ΤΗΝ ΕΚΚΙΝΗΣΗ ΤΗΣ ΕΦΑΡΜΟΓΗΣ
//            System.setProperty("java.security.policy", "file:./security.policy");
//
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager());
//            }
            String url1 = "//localhost/BankServer";

            // Δημιουργια Server 
            BankServer server = new BankServer();
            Registry r = LocateRegistry.createRegistry(1234);
            r.rebind(url1, server);
            System.out.println("BankServer is ok!");

            try {
                //Δημιουργια Συνδεσης με DataBase
                Registry regi = LocateRegistry.getRegistry("127.0.0.1", 12345);
                look_op2 = (Operations2) regi.lookup("//localhost/DataBase");
                System.out.println("OK CLIENT");

            } catch (NotBoundException ex) {
                Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (RemoteException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String validCard(String pin, String id) throws RemoteException {
        // συνδεση μεσω αντικειμενου διεπαφης Operation2 για επικυρωση
        String result = look_op2.validCard2(pin, id);
        // κανουμε set τις 2 τιμες σε ενα αντικειμενο Account 
        //για να τις κρατησουμε για επαναχρησιμοποιηση
        acount.setId(id);
        acount.setPin(pin);

        return result;
    }

    public String deposit(int money) throws RemoteException {
        // συνδεση μεσω αντικειμενου διεπαφης Operation2 για καταθεση
        String result = look_op2.deposit2(money, acount.getId());
        System.out.println(acount.getId() + "\n" + money);

        return result;
    }

    public String analipsi(int money) throws RemoteException {
        // συνδεση μεσω αντικειμενου διεπαφης Operation2 για αναληψη
        String poso = look_op2.analipsi2(money, acount.getId());
        return poso;
    }

    public int info() throws RemoteException {
        // συνδεση μεσω αντικειμενου διεπαφης Operation2 για ενημερωση
        int poso = look_op2.info2(acount.getId());
        return poso;
    }
}
