package dataheaven;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterPanel extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final String pwd = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$";
    private final String user = "^[\\w@$!%*?&]{6,18}$";
    public RegisterPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets(10, 0, 0, 10);
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener((ActionEvent e) -> {
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
                if(UserServices.checkUser(UserServices.generateUserSecret(usernameField.getText(), new String(passwordField.getPassword())))){
                    JOptionPane.showMessageDialog(this, "You are already registered.", "Registration error.", 0);
                } else {
                    boolean registered = UserServices.registerUser(UserServices.generateUserSecret(usernameField.getText(), new String(passwordField.getPassword())));
                    if(!registered){
                        JOptionPane.showMessageDialog(this, "Error happend during registration.", "Registration error.", 0);
                    } else {
                        DataHeaven.cardLayout.show(DataHeaven.cardPanel, "login");
                        JOptionPane.showMessageDialog(this, "You have finished registration.", "Registration.", 1);
                    }
                }
            }
        });
        JButton switchButton = new JButton("Back");
        switchButton.addActionListener((ActionEvent e) -> {
            DataHeaven.cardLayout.show(DataHeaven.cardPanel, "login");
        });
        cs.gridx=0;cs.gridy=0;add(usernameLabel, cs);
        cs.gridx=1;cs.gridy=0;add(usernameField, cs);
        cs.gridx=0;cs.gridy=1;add(passwordLabel, cs);
        cs.gridx=1;cs.gridy=1;add(passwordField, cs);
        cs.gridx=0;cs.gridy=2;cs.gridwidth=2;add(registerBtn, cs);
        cs.gridx=0;cs.gridy=3;cs.gridwidth=2;add(switchButton, cs);
    }
}