package Frontend;

import Backend.Student;
import Backend.Librarian;
import Backend.Admin;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class LoginPanel {
    private static void placeComponents(JPanel panel) {
        panel.setLayout(new GridBagLayout()); // Use layout manager
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        JLabel userLabel = new JLabel("ID");
        JLabel passwordLabel = new JLabel("Password");
        JLabel roleLabel = new JLabel("Role");
        JTextField userText = new JTextField(20);
        JPasswordField passwordText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        JLabel successLabel = new JLabel("");
    
        String[] userTypes = { "Student", "Librarian", "Admin" };
        JComboBox<String> roleComboBox = new JComboBox<>(userTypes);
    
        // Add components in proper order
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(roleLabel, gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);
    
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userText, gbc);
    
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordText, gbc);
    
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
    
        gbc.gridy = 4;
        panel.add(forgotPasswordButton, gbc);
    
        gbc.gridy = 5;
        panel.add(successLabel, gbc);
        

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
                        System.out.println("Attempting librarian login...");
                        if (Librarian.login(username, password)) {
                            System.out.println("Librarian login successful for username: " + username);
                            if (Librarian.isActive(username)) { // Check if the librarian is active
                                System.out.println("Librarian account is active.");
                                loginSuccess = true;
                                successLabel.setText("Login successful as Librarian!");
                                LibrarianPanel.displayLibrarianPage(); // Redirect to LibrarianPanel
                            } else {
                                System.out.println("Librarian account is inactive.");
                                successLabel.setText("Access denied. Librarian account is inactive.");
                            }
                        } else {
                            System.out.println("Librarian login failed for username: " + username);
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

                JDialog resetDialog = new JDialog((Frame) null, "Reset Password", true);
                resetDialog.setLayout(new GridLayout(5, 2, 10, 10));
                resetDialog.setSize(300, 200);

                JTextField usernameField = new JTextField();
                JTextField emailField = new JTextField();
                JPasswordField newPasswordField = new JPasswordField();
                JPasswordField confirmPasswordField = new JPasswordField();
                JButton resetButton = new JButton("Reset Password");

                resetDialog.add(new JLabel("name:"));
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
        JFrame frame = new JFrame("Library Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setLayout(new GridBagLayout());
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
    
        // Left image panel
        JLabel imageLabel = new JLabel();
        ImageIcon rawIcon = new ImageIcon("C:\\Users\\sanya\\OneDrive - UPES\\Desktop\\ScreenShots\\Screenshot 2025-05-07 000540.png");
        Image scaledImg = rawIcon.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImg));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        frame.add(imageLabel, gbc);
    
        // Right login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        placeComponents(loginPanel);
    
        gbc.gridx = 1;
        frame.add(loginPanel, gbc);
    
        frame.setVisible(true);
    }
}