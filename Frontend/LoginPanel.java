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
        JButton forgotPasswordButton = new JButton("Forgot Password?");
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
        forgotPasswordButton.setBounds(100, 110, 150, 25);
        successLabel.setBounds(10, 140, 300, 25);

        // Add components to panel
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(userLabel);
        panel.add(passwordLabel);
        panel.add(userText);
        panel.add(passwordText);
        panel.add(loginButton);
        panel.add(forgotPasswordButton);
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
                                loginSuccess = true;
                                successLabel.setText("Login successful as Admin!");
                                AdminPanel.displayAdminPage(); // Redirect to AdminPanel
                            }
                        break;
                }
                if (!loginSuccess && successLabel.getText().isEmpty()) {
                    successLabel.setText("Invalid username or password.");
                }
            }
        });

        // Add action listener for the Forgot Password button
        forgotPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                if (selectedRole.equals("Admin")) {
                    JOptionPane.showMessageDialog(null, 
                        "Please contact system administrator to reset admin password.",
                        "Password Reset",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JDialog resetDialog = new JDialog((Frame)null, "Reset Password", true);
                resetDialog.setLayout(new GridLayout(5, 2, 10, 10));
                resetDialog.setSize(300, 200);

                JTextField usernameField = new JTextField();
                JTextField emailField = new JTextField();
                JPasswordField newPasswordField = new JPasswordField();
                JPasswordField confirmPasswordField = new JPasswordField();
                JButton resetButton = new JButton("Reset Password");

                resetDialog.add(new JLabel("Username:"));
                resetDialog.add(usernameField);
                resetDialog.add(new JLabel("Email:"));
                resetDialog.add(emailField);
                resetDialog.add(new JLabel("New Password:"));
                resetDialog.add(newPasswordField);
                resetDialog.add(new JLabel("Confirm Password:"));
                resetDialog.add(confirmPasswordField);
                resetDialog.add(new JLabel(""));
                resetDialog.add(resetButton);

                resetButton.addActionListener(event -> {
                    String username = usernameField.getText().trim();
                    String email = emailField.getText().trim();
                    String newPassword = new String(newPasswordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());

                    if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(resetDialog, 
                            "All fields are required.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(resetDialog, 
                            "Passwords do not match.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean verified = false;
                    boolean reset = false;

                    if (selectedRole.equals("Student")) {
                        verified = Student.verifyEmail(username, email);
                        if (verified) {
                            reset = Student.resetPassword(username, email, newPassword);
                        }
                    } else if (selectedRole.equals("Librarian")) {
                        verified = Librarian.verifyEmail(username, email);
                        if (verified) {
                            reset = Librarian.resetPassword(username, email, newPassword);
                        }
                    }

                    if (!verified) {
                        JOptionPane.showMessageDialog(resetDialog, 
                            "Username and email do not match.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    } else if (reset) {
                        JOptionPane.showMessageDialog(resetDialog, 
                            "Password reset successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        resetDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(resetDialog, 
                            "Failed to reset password. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });

                resetDialog.setLocationRelativeTo(null);
                resetDialog.setVisible(true);
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