
import java.io.Serializable;

public class Account implements Serializable {

    private String pin;
    private String accountNo;
    private String id;
    private String balance;

    public Account(String pin, String accountNo, String id, String balance) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.id = id;
        this.balance = balance;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountN() {
        return accountNo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance() {
        return balance;
    }
    
   
    public String toString(){
        return "PIN:"+pin +"Account:"+accountNo+ "ID:"+id +"Balance:"+balance;
    }

}
