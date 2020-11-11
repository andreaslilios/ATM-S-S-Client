
import java.beans.Statement;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.ArrayList;

public class DataBase extends UnicastRemoteObject implements Operations2 {

    private static String pin1;
    private static String acc;
    private static String id1;
    private static int bal;
    private static ResultSet records;
    private static int columnsNumber;
    private static ArrayList<String> pinList = new ArrayList<String>();
    private static ArrayList<String> acountNoList = new ArrayList<String>();
    private static ArrayList<String> IdList = new ArrayList<String>();
    private static ArrayList<Integer> BalanceList = new ArrayList<Integer>();

    public DataBase() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {

            String url1 = "//localhost/DataBase";
            //ΕΔΩ ΕΧΟΥΜΕ ΤΙΣ ΠΟΛΙΤΙΚΕΣ ΑΣΦΑΛΕΙΑΣ ΟΙ ΟΠΟΙΕΣ ΔΕΝ ΜΑΣ ΕΠΙΤΡΕΠΟΥΝ ΤΗΝ ΕΚΚΙΝΗΣΗ ΤΗΣ ΕΦΑΡΜΟΓΗΣ

//            System.setProperty("java.security.policy", "file:./security.policy");
//
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager());
//            }
            DataBase server = new DataBase();
            Registry r = LocateRegistry.createRegistry(12345);
            r.rebind(url1, server);
            System.out.println("DataBase is ok!");

            String url = "jdbc:sqlite:C:/sqlite/test.db";
            
            Class.forName("org.sqlite.JDBC");
//sundesi me tin vasi dedomenon kai dimiourgia vasis se periptosi pou den uparxei
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            java.sql.Statement stat = conn.createStatement();
            System.out.println("Database connection established");
            // diagrafi pinaka 'people' se periptosi pou iparxei
            stat.executeUpdate("DROP table if exists DB;");

//dimiougia pinaka me 4 stiles
            stat.executeUpdate("CREATE table DB (PIN String, AccountNo String, ID String primary key, Balance int);");
            stat.executeUpdate("INSERT INTO DB (PIN, AccountNo,ID,Balance) VALUES ('100','1000', '3212','10000')");
            stat.executeUpdate("INSERT INTO DB (PIN, AccountNo,ID,Balance) VALUES ('200','2000', '1122','5000')");
            stat.executeUpdate("INSERT INTO DB (PIN, AccountNo,ID,Balance) VALUES ('201','2000', '2451','5000')");
            stat.executeUpdate("INSERT INTO DB (PIN, AccountNo,ID,Balance) VALUES ('300','3000', '7891','8000')");
            stat.executeUpdate("INSERT INTO DB (PIN, AccountNo,ID,Balance) VALUES ('300','3000', '1966','8000')");

            records = stat.executeQuery("SELECT * from DB");
            ResultSetMetaData rsmd = records.getMetaData();
            columnsNumber = rsmd.getColumnCount();
            while (records.next()) {
//Print one row
                for (int i = 1; i <= columnsNumber; i++) {

                    System.out.print(records.getString(i) + " "); //Print one element of a row
                }
                //παιρνω μια-μια τις εγγραφες και τις εμφανιζω 
                pin1 = records.getString(1);
                acc = records.getString(2);
                id1 = records.getString(3);
                bal = records.getInt(4);
// φτιαχνω 4 λιστες για τα 4 πεδια του πινακα (μου χρησιμευουν σε παρακατω ελεγχους)
                pinList.add(pin1);
                acountNoList.add(acc);
                IdList.add(id1);
                BalanceList.add(bal);

                System.out.println();//Move to the next line to print the next row.
            }
            DataBase server2 = new DataBase();

            //conn.close();
        } catch (RemoteException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Τρέχουμε στην βάση, εαν υπάρχει ο πελάτης που μας έκανε αίτημα, του επιστρέφουμε τον αριθμό λογαριασμού του.
    public String validCard2(String pin, String id) throws RemoteException {
        String count = "1";
        for (int i = 0; i < IdList.size(); i++) {
            if (id.equalsIgnoreCase(IdList.get(i))) {
                for (int j = 0; j < pinList.size(); j++) {
                    if (pin.equalsIgnoreCase(pinList.get(i))) {
                        count = acountNoList.get(i);
                    }
                }

            }
        }

        return count;
    }
// Αφού γίνει η αναζήτηση του λογαριασμού του συγκεκριμένου πελάτη, 
    //προσθέτουμε στο υπόλοιπο το ποσό που κατέθεσε ο πελάτης.Επιστρέφουμε ανάλογο μήνυμα.
    public synchronized String deposit2(int money, String id) throws RemoteException {

        String minima = "no";
        Connection conn;

        for (int i = 0; i < IdList.size(); i++) {
            if (id.equalsIgnoreCase(IdList.get(i))) {

                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:test.db");
                    java.sql.Statement stat = conn.createStatement();
                    ResultSet ee1 = stat.executeQuery("SELECT Balance from DB  WHERE ID ='" + IdList.get(i) + "'");
                    int newBalance = ee1.getInt(1) + money;

                    stat.executeUpdate("UPDATE DB SET Balance ='" + newBalance + "' WHERE ID ='" + IdList.get(i) + "'");

                    minima = "ok";
                    ResultSet ee = stat.executeQuery("SELECT Balance from DB");
                    System.out.println(ee.getString(1));
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

        return minima;
    }
    
    //Αφού γίνει η αναζήτηση του λογαριασμού του συγκεκριμένου πελάτη, ελέγχουμε εαν μπορεί να γίνει ανάληψη ποσού, 
    //αν ναι αφαιρούμε απο το υπόλοιπο το ποσό.Και στις δύο περιπτώσεις επιστρέφουμε το ανάλογο μήνυμα.

    public String analipsi2(int money, String id) throws RemoteException {

        String minima = "no";
        Connection conn;

        for (int i = 0; i < IdList.size(); i++) {
            if (id.equalsIgnoreCase(IdList.get(i))) {

                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:test.db");
                    java.sql.Statement stat = conn.createStatement();
                    ResultSet ee1 = stat.executeQuery("SELECT Balance from DB where ID='" + IdList.get(i) + "'");
                    
                    int newBalance = ee1.getInt(1) - money;
                    

                    if (newBalance > 0) {
                        stat.executeUpdate("UPDATE DB SET Balance ='" + newBalance + "' WHERE ID ='" + IdList.get(i) + "'");
                        minima = "ok";
                    } else {
                        minima = "no";
                    }
                    ResultSet ee = stat.executeQuery("SELECT Balance from DB");
                    System.out.println(ee.getString(1));

                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

        return minima;

    }
    
    //Αναζητούμε το λογαριασμό του πελάτη και επιστρέφουμε μήνυμα με το υπόλοιπο του λογαριασμού του.

    public synchronized int info2(String id) throws RemoteException {

        Connection conn;
        int result = 0;

        for (int i = 0; i < IdList.size(); i++) {
            if (id.equalsIgnoreCase(IdList.get(i))) {

                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:test.db");
                    java.sql.Statement stat = conn.createStatement();

                    ResultSet ee = stat.executeQuery("SELECT Balance from DB");
                    result = ee.getInt(1);
                    System.out.println(result);

                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        return result;

    }

}
