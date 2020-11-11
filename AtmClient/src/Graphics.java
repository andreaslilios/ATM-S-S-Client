
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Graphics extends JFrame {

    private String name;
    private boolean flag;
    private static Operations look_op;

    public Graphics() {
        this.name = "//localhost/BankServer";
        this.flag = false;
        try {

            //ΕΔΩ ΕΧΟΥΜΕ ΤΙΣ ΠΟΛΙΤΙΚΕΣ ΑΣΦΑΛΕΙΑΣ ΟΙ ΟΠΟΙΕΣ ΔΕΝ ΜΑΣ ΕΠΙΤΡΕΠΟΥΝ ΤΗΝ ΕΚΚΙΝΗΣΗ ΤΗΣ ΕΦΑΡΜΟΓΗΣ
//            System.setProperty("java.security.policy", "file:./security.policy");
//
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager());
//            }
            // Ο ΠΕΛΑΤΗΣ ΣΥΝΔΕΕΤΑΙ ΜΕΣΩ ΑΥΤΗΣ ΤΗΣ ΠΟΡΤΑΣ ΚΑΙ ΤΗΣ ΙΡ ΣΤΟΝ SERVER ΤΗΣ ΤΡΑΠΕΖΑΣ
            Registry regi = LocateRegistry.getRegistry("127.0.0.1", 1234);
            // ΑΝΤΙΚΕΙΜΕΝΟ ΔΙΕΠΑΦΗΣ ΓΙΑ ΤΗΝ ΕΠΙΚΟΙΝΩΝΙΑ
            look_op = (Operations) regi.lookup("//localhost/BankServer");
            System.out.println("OK CLIENT");

        } catch (NotBoundException ex) {
            Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
        }

        setTitle("ATM");
        JPanel panel1;
        JPanel panel2;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        GridLayout grid = new GridLayout(1, 2);

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel2 = new JPanel();
        //Τα κουμπια για τις 6 λειτουργιες
        JButton valid = new JButton("Επικύρωση Κάρτας");
        JButton katathesi = new JButton("Κατάθεση");
        JButton analipsi = new JButton("Ανάληψη");
        JButton info = new JButton("Ενημέρωση");
        JButton logout = new JButton("Έξοδος");
        ImageIcon image = new ImageIcon("atm-processing.png");
        JLabel fwto = new JLabel(image);
        JLabel sxolia = new JLabel("<HTML><B>Καλώς Ήρθατε! \n Επιλoγή ενέργειας.</B></HTML>");
        sxolia.setFont(new Font("serif", Font.PLAIN, 18));

        logout.setBounds(10, 370, 80, 25);
        sxolia.setBounds(110, 40, 400, 30);
        valid.setBounds(125, 100, 230, 30);
        katathesi.setBounds(125, 150, 230, 30);
        analipsi.setBounds(125, 200, 230, 30);
        info.setBounds(125, 250, 230, 30);

        panel1.add(sxolia);
        panel1.add(valid);
        panel1.add(katathesi);
        panel1.add(analipsi);
        panel1.add(info);
        panel1.add(logout);
        panel2.add(fwto);
        setLayout(grid);
        add(panel1);
        add(panel2);
        setSize(1000, 450);
        setLocationRelativeTo(null);

        valid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFrame frame2 = new JFrame("Validation");
                frame2.setVisible(true);
                setLayout(new FlowLayout());
                JPanel panel = new JPanel();
                panel.setLayout(null);
                JLabel pinLabel = new JLabel("Εισάγετε PIN");
                JTextField pinField = new JTextField();
                pinLabel.setBounds(200, 20, 200, 60);
                pinField.setBounds(170, 80, 150, 30);
                panel.add(pinLabel);
                panel.add(pinField);

                JLabel idLabel = new JLabel("Εισάγετε ID");
                JTextField idField = new JTextField();
                JButton codeButton = new JButton("OK");
                idLabel.setBounds(200, 100, 200, 60);
                idField.setBounds(170, 150, 150, 30);
                codeButton.setBounds(190, 200, 100, 30);
                panel.add(idLabel);
                panel.add(idField);
                panel.add(codeButton);
                frame2.add(panel);
                frame2.setSize(500, 300);
                frame2.setLocationRelativeTo(null);
                codeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame2.dispose();
                        //ΤΑ ΠΕΔΙΑ ΠΟΥ ΣΥΜΠΛΗΡΩΝΕΙ Ο ΧΡΗΣΤΗΣ ΡΙΝ,ΙD
                        String pin = pinField.getText();
                        String id = idField.getText();

                        try {
                            //ΕΠΙΚΟΙΝΩΝΙΑ ΜΕ ΤΡΑΠΕΖΑ ΜΕΣΩ ΔΙΕΠΑΦΗΣ Operations 
                            //καλώντας την λειτουργια validCard()(για επικυρωση)
                            String result = look_op.validCard(pin, id);
                            if (result.equalsIgnoreCase("1")) {
                                JOptionPane.showMessageDialog(null, "Λάθος εισαγωγή στοιχείων!", "Επικύρωση", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Αριθμός Λογαριασμού:\n" + result, "Επικύρωση", JOptionPane.INFORMATION_MESSAGE);
                                flag = true;
                            }
                        } catch (RemoteException ex) {
                            Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

            }
        });
        katathesi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (flag == false) {
                    JOptionPane.showMessageDialog(null, "Δεν έγινε επικύρωση κάρτας!", "Κατάθεση", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    flag = true;
                    JFrame frame2 = new JFrame("Κατάθεση");
                    frame2.setVisible(true);
                    setLayout(new FlowLayout());
                    JPanel panel = new JPanel();
                    panel.setLayout(null);
                    JLabel posoLabel = new JLabel("Εισάγετε ποσό κατάθεσης");
                    JTextField posoField = new JTextField();
                    posoLabel.setBounds(170, 50, 200, 60);
                    posoField.setBounds(170, 110, 150, 30);
                    panel.add(posoLabel);
                    panel.add(posoField);

                    JButton codeButton = new JButton("OK");
                    codeButton.setBounds(195, 200, 100, 30);
                    panel.add(codeButton);
                    frame2.add(panel);
                    frame2.setSize(500, 300);
                    frame2.setLocationRelativeTo(null);
                    codeButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            frame2.dispose();
                            // ποσο καταθεσης
                            int poso = Integer.parseInt(posoField.getText());

                            try {
                                //ΕΠΙΚΟΙΝΩΝΙΑ ΜΕ ΤΡΑΠΕΖΑ ΜΕΣΩ ΔΙΕΠΑΦΗΣ Operations 
                                //καλώντας την λειτουργια deposit()(για καταθεση)
                                String result = look_op.deposit(poso);
                                if (result.equalsIgnoreCase("ok")) {
                                    JOptionPane.showMessageDialog(null, "Κατάθεση Επιτυχής", "Κατάθεση", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Κατάθεση Aνεπιτυχής", "Κατάθεση", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (RemoteException ex) {
                                Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                }
            }
        });
        analipsi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (flag == false) {
                    JOptionPane.showMessageDialog(null, "Δεν έγινε επικύρωση κάρτας!", "Ανάληψη", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    flag = true;
                    JFrame frame2 = new JFrame("Ανάληψη");
                    frame2.setVisible(true);
                    setLayout(new FlowLayout());
                    JPanel panel = new JPanel();
                    panel.setLayout(null);
                    JLabel posoLabel = new JLabel("Εισάγετε ποσό ανάληψης");
                    JTextField posoField = new JTextField();
                    posoLabel.setBounds(170, 50, 200, 60);
                    posoField.setBounds(170, 110, 150, 30);
                    panel.add(posoLabel);
                    panel.add(posoField);

                    JButton codeButton = new JButton("OK");
                    codeButton.setBounds(190, 200, 100, 30);
                    panel.add(codeButton);
                    frame2.add(panel);
                    frame2.setSize(500, 300);
                    frame2.setLocationRelativeTo(null);
                    codeButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            frame2.dispose();
                            // ποσο για αναληψη
                            int poso = Integer.parseInt(posoField.getText());

                            try {
 //ΕΠΙΚΟΙΝΩΝΙΑ ΜΕ ΤΡΑΠΕΖΑ ΜΕΣΩ ΔΙΕΠΑΦΗΣ Operations 
                                //καλώντας την λειτουργια analipsi()(για αναληψη)
                                String result = look_op.analipsi(poso);
                                if (result.equalsIgnoreCase("ok")) {
                                    JOptionPane.showMessageDialog(null, "Ανάληψη Επιτυχής", "Ανάληψη", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Μη επαρκές υπόλοιπο", "Ανάληψη", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (RemoteException ex) {
                                Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            }
        });
        info.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (flag == false) {
                    JOptionPane.showMessageDialog(null, "Δεν έγινε επικύρωση κάρτας!", "Ενημέρωση", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    flag = true;
                    JFrame frame2 = new JFrame("Ενημέρωση");
                    frame2.setVisible(true);
                    setLayout(new FlowLayout());
                    JPanel panel = new JPanel();
                    panel.setLayout(null);
                    JLabel posoLabel = new JLabel("Πατήστε για ενημέρωση υπολοίπου");
                    //JTextField posoField = new JTextField();
                    posoLabel.setBounds(140, 50, 400, 100);
                    //posoField.setBounds(170, 110, 150, 30);
                    panel.add(posoLabel);
                    // panel.add(posoField);

                    JButton codeButton = new JButton("ΕΝΗΜΕΡΩΣΗ");
                    codeButton.setBounds(165, 170, 150, 30);
                    panel.add(codeButton);
                    frame2.add(panel);
                    frame2.setSize(500, 300);
                    frame2.setLocationRelativeTo(null);
                    codeButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            frame2.dispose();
                            //ΕΠΙΚΟΙΝΩΝΙΑ ΜΕ ΤΡΑΠΕΖΑ ΜΕΣΩ ΔΙΕΠΑΦΗΣ Operations 
                            //καλώντας την λειτουργια info()(για ενημερωση)

                            try {

                                int result = look_op.info();
                                if (result != 0) {
                                    JOptionPane.showMessageDialog(null, "Υπόλοιπο λογαριασμού: " + result, "Ενημέρωση", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Δεν υπάρχει υπόλοιπο", "Ενημέρωση", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (RemoteException ex) {
                                Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                }
            }
        });
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                //κλεισιμο συνδεσης ΑΤΜ
            }
        });

    }
}
