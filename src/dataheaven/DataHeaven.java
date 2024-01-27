package dataheaven;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DataHeaven extends JFrame {
    public static String usersecretpath; //Putanja do fajla (sam tzv username).
    public static String usersecretkey; //Ključ za šifrovanje i dešifrovanje.
    public static CardLayout cardLayout;
    public static JPanel cardPanel;
    public static JFrame frame;
    public DataHeaven() {
        frame = this;
        frame.setTitle("DataHeaven");
        frame.setSize(400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        LoginPanel loginPanel = new LoginPanel();
        cardPanel.add(loginPanel, "login");
        RegisterPanel registerPanel = new RegisterPanel();
        cardPanel.add(registerPanel, "register");
        cardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(cardPanel);
        cardLayout.show(cardPanel, "login");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataHeaven mainApp = new DataHeaven();
            mainApp.setVisible(true);
        });
    }
}
