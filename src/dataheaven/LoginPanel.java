package dataheaven;

import static dataheaven.DataHeaven.cardLayout;
import static dataheaven.DataHeaven.cardPanel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final String pwd = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$";
    private final String user = "^[\\w@$!%*?&]{6,18}$";
    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets(10, 0, 0, 10);
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener((ActionEvent e) -> {
            boolean cont = true;
            if(!usernameField.getText().matches(user)){
                usernameField.setBorder(new GlowBorder(Color.RED));
                JOptionPane.showMessageDialog(this, "Username must be between 6 and 18 characters.", "Username error.", 0);
                cont = false;
            }
            if(!new String(passwordField.getPassword()).matches(pwd)){
                passwordField.setBorder(new GlowBorder(Color.RED));
                JOptionPane.showMessageDialog(this, "Password must be between 8 and 24 characters.\nAt least one capital letter.\nAt least one number.\nAt least one special character.", "Password error.", 0);
                cont = false;
            }
            if(cont){
                passwordField.setBorder(new GlowBorder(Color.GRAY, 1, false));
                usernameField.setBorder(new GlowBorder(Color.GRAY, 1, false));
                if(UserServices.checkUser(generateUserSecret(usernameField.getText(), new String(passwordField.getPassword())))){
                    DataHeaven.usersecretpath = generateUserSecret(usernameField.getText(), new String(passwordField.getPassword()));
                    DataHeaven.usersecretkey = generateUserSecret(new String(passwordField.getPassword()), usernameField.getText());
                    FilePanel filePanel = new FilePanel();
                    cardPanel.add(filePanel, "files");
                    cardLayout.show(cardPanel, "files");
                } else {
                    JOptionPane.showMessageDialog(this, "There is no account with such username and password.", "Login error.", 0);
                }
            }
        });
        JLabel dactxt = new JLabel("Don't have an account? Click below.");
        JButton switchButton = new JButton("Register");
        switchButton.addActionListener((ActionEvent e) -> {
            DataHeaven.cardLayout.show(DataHeaven.cardPanel, "register");
        });
        cs.gridx=0;cs.gridy=0;add(usernameLabel, cs);
        cs.gridx=1;cs.gridy=0;add(usernameField, cs);
        cs.gridx=0;cs.gridy=1;add(passwordLabel, cs);
        cs.gridx=1;cs.gridy=1;add(passwordField, cs);
        cs.gridx=0;cs.gridy=2;cs.gridwidth=2;add(loginBtn, cs);
        cs.gridx=0;cs.gridy=3;add(dactxt, cs);
        cs.gridx=0;cs.gridy=4;cs.gridwidth=2;add(switchButton, cs);
    }
    private static String generateUserSecret(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        for (char ch1 : str1.toCharArray()) {
            for (char ch2 : str2.toCharArray()) {
                sb.append(ch1);
                sb.append(ch2);
            }
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(sb.toString().getBytes());
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hexStringBuilder.append(String.format("%02X", hashByte));
            }
            return hexStringBuilder.toString();
        } catch(Exception e){
            return null;
        }
    }
}