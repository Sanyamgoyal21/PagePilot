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
        JTextField userText = new JTextField(20);
        JPasswordField passwordText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel successLabel = new JLabel("");

        // Create the dropdown/combobox
        String[] userTypes = { "Student", "Librarian", "Admin" };
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

        // Add action listener for the Login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                String selectedRole = (String) roleComboBox.getSelectedItem();

                boolean loginSuccess = false;

                switch (selectedRole) {
                    case "Student":
                        int studentId = Student.login(username, password); // Return student ID on successful login
                        if (studentId != -1) {
                            if (Student.isActive(studentId)) { // Check if the student is active
                                loginSuccess = true;
                                successLabel.setText("Login successful as Student!");
                                StudentPanel.displayStudentPage(studentId); // Pass the student ID to StudentPanel
                            } else {
                                successLabel.setText("Access denied. Student account is inactive.");
                            }
                        }
                        break;

                    case "Librarian":
                        if (Librarian.login(username, password)) {
                            if (Librarian.isActive(username)) { // Check if the librarian is active
                                loginSuccess = true;
                                successLabel.setText("Login successful as Librarian!");
                                LibrarianPanel.displayLibrarianPage(); // Redirect to LibrarianPanel
                            } else {
                                successLabel.setText("Access denied. Librarian account is inactive.");
                            }
                        }
                        break;

                    case "Admin":
                        if (Admin.login(username, password)) {
                            if (Admin.isActive(username)) { // Check if the admin is active
                                loginSuccess = true;
                                successLabel.setText("Login successful as Admin!");
                                AdminPanel.displayAdminPage(); // Redirect to AdminPanel
                            } else {
                                successLabel.setText("Access denied. Admin account is inactive.");
                            }
                        }
                        break;
                }

                if (!loginSuccess && successLabel.getText().isEmpty()) {
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