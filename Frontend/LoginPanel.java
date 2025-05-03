package Frontend;
import Backend.Student;
import Backend.Librarian;
import Backend.Admin;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class LoginPanel {
    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        JLabel userLabel = new JLabel("User");
        JLabel passwordLabel = new JLabel("Password");
        JLabel roleLabel = new JLabel("Role");
        JTextField userText = new JTextField(20);wq
        JPasswordField passwordText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel successLabel = new JLabel(""); 
        
        // Create the dropdown/combobox
        String[] userTypes = {"Student", "Librarian", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(userTypes);

        // Set bounds for all components
        roleLabel.setBounds(10, 20, 80, 25);
        roleComboBox.setBounds(100, 20, 165, 25);
        userLabel.setBounds(10, 50, 80, 25);
        passwordLabel.setBounds(10, 80, 80, 25);
        userText.setBounds(100, 50, 165, 25);
        passwordText.setBounds(100, 80, 165, 25);
        loginButton.setBounds(10, 110, 80, 25);
        successLabel.setBounds(10, 140, 300, 25);

        // Add components to panel
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(userLabel);
        panel.add(passwordLabel);
        panel.add(userText);
        panel.add(passwordText);
        panel.add(loginButton);
        panel.add(successLabel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                String selectedRole = (String) roleComboBox.getSelectedItem();
                
                boolean loginSuccess = false;
                switch(selectedRole) {
                    case "Student":
                        loginSuccess = Student.login(username, password);
                        break;
                    case "Librarian":
                        loginSuccess = Librarian.login(username, password);
                        break;
                    case "Admin":
                        // Add Admin login logic here
                        break;
                }
                
                if(loginSuccess) {
                    successLabel.setText("Login successful as " + selectedRole + "!");
                    // Proceed to the next screen or functionality based on role
                    // For example, you can open a new JFrame or redirect to another panel

                } else {
                    successLabel.setText("Invalid username or password.");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250); // Increased height to accommodate new component

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }
}