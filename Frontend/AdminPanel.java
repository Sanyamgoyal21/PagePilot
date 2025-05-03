package Frontend;

import Backend.Admin;
import Backend.Database;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel {
    public static void displayAdminPage() {
        // Create the main frame for the Admin Dashboard
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full-screen mode
        adminFrame.setLayout(new BorderLayout());

        // Left-side navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(5, 1, 5, 5)); // 5 buttons with spacing
        navigationPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        navigationPanel.setPreferredSize(new Dimension(200, 0)); // Fixed width for navigation

        // Buttons for navigation
        JButton manageLibrarianButton = new JButton("Add/View/Delete Librarian");
        JButton viewFineReportButton = new JButton("View Fine Report");
        JButton generateReportButton = new JButton("Generate Overall System Report");
        JButton manageStudentButton = new JButton("Manage Student Account");
        JButton logoutButton = new JButton("Logout");

        // Add buttons to the navigation panel
        navigationPanel.add(manageLibrarianButton);
        navigationPanel.add(viewFineReportButton);
        navigationPanel.add(generateReportButton);
        navigationPanel.add(manageStudentButton);
        navigationPanel.add(logoutButton);

        // Main content area with CardLayout
        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Panels for each feature
        JPanel librarianPanel = createFeaturePanel("Manage Librarian Section");
        JPanel fineReportPanel = createFeaturePanel("View Fine Report Section");
        JPanel systemReportPanel = createFeaturePanel("Generate Overall System Report Section");
        JPanel studentPanel = createFeaturePanel("Manage Student Account Section");

        // Add feature panels to the content panel
        contentPanel.add(librarianPanel, "Librarian");
        contentPanel.add(fineReportPanel, "FineReport");
        contentPanel.add(systemReportPanel, "SystemReport");
        contentPanel.add(studentPanel, "Student");

        // Add action listeners to buttons
        manageLibrarianButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Librarian");
            librarianPanel.removeAll(); // Clear the panel before adding new components

            // Create a split pane to divide the screen
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(400); // Set initial divider position

            // Left panel with CardLayout for options
            JPanel leftPanel = new JPanel(new CardLayout());
            JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // 3 buttons with spacing
            JButton viewButton = new JButton("View Librarians");
            JButton addButton = new JButton("Add Librarian");
            JButton deleteButton = new JButton("Delete Librarian");
            optionsPanel.add(viewButton);
            optionsPanel.add(addButton);
            optionsPanel.add(deleteButton);

            // Add options panel to the left panel
            leftPanel.add(optionsPanel, "Options");

            // Add panel for adding a librarian
            JPanel addLibrarianPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            addLibrarianPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField(20);
            JButton submitAddButton = new JButton("Submit");
            addLibrarianPanel.add(nameLabel);
            addLibrarianPanel.add(nameField);
            addLibrarianPanel.add(passwordLabel);
            addLibrarianPanel.add(passwordField);
            addLibrarianPanel.add(new JLabel()); // Empty cell for spacing
            addLibrarianPanel.add(submitAddButton);
            leftPanel.add(addLibrarianPanel, "Add");

            // Add panel for deleting a librarian
            JPanel deleteLibrarianPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            deleteLibrarianPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel deleteIdLabel = new JLabel("ID:");
            JTextField deleteIdField = new JTextField(20);
            JButton submitDeleteButton = new JButton("Submit");
            deleteLibrarianPanel.add(deleteIdLabel);
            deleteLibrarianPanel.add(deleteIdField);
            deleteLibrarianPanel.add(new JLabel()); // Empty cell for spacing
            deleteLibrarianPanel.add(submitDeleteButton);
            leftPanel.add(deleteLibrarianPanel, "Delete");

            // Right panel for displaying current librarians
            JPanel viewLibrariansPanel = new JPanel(new BorderLayout());
            JTextArea librarianListArea = new JTextArea();
            librarianListArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(librarianListArea);
            viewLibrariansPanel.add(scrollPane, BorderLayout.CENTER);

            // Add panels to the split pane
            splitPane.setLeftComponent(leftPanel);
            splitPane.setRightComponent(viewLibrariansPanel);

            // Add the split pane to the librarian panel
            librarianPanel.setLayout(new BorderLayout());
            librarianPanel.add(splitPane, BorderLayout.CENTER);

            // CardLayout for switching between options
            CardLayout leftCardLayout = (CardLayout) leftPanel.getLayout();

            // Add functionality to the "View Librarians" button
            viewButton.addActionListener(viewEvent -> {
                leftCardLayout.show(leftPanel, "Options");
                librarianListArea.setText("Current Librarians:\n");
                librarianListArea.append("ID\tName\tPassword\n");
                librarianListArea.append("---------------------------------\n");

                Admin admin = new Admin();
                try {
                    String librarianData = admin.readLibrarian(); // Fetch librarian data
                    librarianListArea.append(librarianData); // Display the data in the text area
                } catch (Exception ex) {
                    librarianListArea.append("Error fetching librarian data: " + ex.getMessage());
                }
            });

            // Add functionality to the "Add Librarian" button
            addButton.addActionListener(addEvent -> {
                leftCardLayout.show(leftPanel, "Add"); // Show the Add Librarian form
                librarianListArea.setText("Current Librarians:\n");
                librarianListArea.append("ID\tName\tPassword\n");
                librarianListArea.append("---------------------------------\n");
                Admin admin = new Admin();
                try {
                    String librarianData = admin.readLibrarian(); // Fetch librarian data
                    librarianListArea.append(librarianData); // Display the data in the text area
                } catch (Exception ex) {
                    librarianListArea.append("Error fetching librarian data: " + ex.getMessage());
                }
            });

            submitAddButton.addActionListener(submitAddEvent -> {
                String name = nameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Admin admin = new Admin();
                    admin.insertLibrarian(name, password); // Add librarian to the database
                    JOptionPane.showMessageDialog(null, "Librarian added successfully!"); // Show success message

                    // Refresh the librarian list
                    librarianListArea.setText("Current Librarians:\n");
                    librarianListArea.append("ID\tName\tPassword\n");
                    librarianListArea.append("---------------------------------\n");
                    try {
                        String librarianData = admin.readLibrarian(); // Fetch librarian data
                        librarianListArea.append(librarianData); // Display the data in the text area
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error adding librarian: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RuntimeException ex) {
                    // Check if the exception message is about the password being in use
                    if (ex.getMessage().contains("Password is already in use")) {
                        JOptionPane.showMessageDialog(null,
                                "Password is already in use. Please choose another password.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error adding librarian: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add functionality to the "Delete Librarian" button
            deleteButton.addActionListener(deleteEvent -> {
                leftCardLayout.show(leftPanel, "Delete");
                librarianListArea.setText("Current Librarians:\n");
                librarianListArea.append("ID\tName\tPassword\n");
                librarianListArea.append("---------------------------------\n");
                Admin admin = new Admin();
                try {
                    String librarianData = admin.readLibrarian(); // Fetch librarian data
                    librarianListArea.append(librarianData); // Display the data in the text area
                } catch (Exception ex) {
                    librarianListArea.append("Error fetching librarian data: " + ex.getMessage());
                }
            });

            submitDeleteButton.addActionListener(submitDeleteEvent -> {
                String idStr = deleteIdField.getText().trim();
                if (idStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ID is required to delete a librarian!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    Admin admin = new Admin();
                    boolean isDeleted = admin.deleteLibrarian(id);

                    if (isDeleted) {
                        JOptionPane.showMessageDialog(null, "Librarian deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Librarian with ID " + id + " does not exist.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    // Refresh the librarian list
                    librarianListArea.setText("Current Librarians:\n");
                    librarianListArea.append("ID\tName\tPassword\n");
                    librarianListArea.append("---------------------------------\n");
                    try {
                        String librarianData = admin.readLibrarian(); // Fetch librarian data
                        librarianListArea.append(librarianData); // Display the data in the text area
                    } catch (Exception ex) {
                        librarianListArea.append("Error fetching librarian data: " + ex.getMessage());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            librarianPanel.revalidate();
            librarianPanel.repaint();
        });

        viewFineReportButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "FineReport"); // Show the Fine Report panel
            fineReportPanel.removeAll(); // Clear the panel before adding new components
            fineReportPanel.setLayout(new BorderLayout());

            JTextArea fineReportArea = new JTextArea();
            fineReportArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(fineReportArea);
            fineReportPanel.add(scrollPane, BorderLayout.CENTER);

            // Fetch and display fine details
            Admin admin = new Admin();
            fineReportArea.setText("=== Fine Report ===\n\n");

            try {
                // Fetch individual fine details
                String sql = "SELECT issue_id, student_id, book_id, fine, MONTH(issue_date) AS month " +
                        "FROM issued_books WHERE fine > 0";
                try (Connection con = Admin.connect();
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    fineReportArea.append("Individual Fines:\n");
                    fineReportArea.append("Issue ID\tStudent ID\tBook ID\tFine Amount\n");
                    fineReportArea.append("-------------------------------------------------\n");

                    while (rs.next()) {
                        int issueId = rs.getInt("issue_id");
                        int studentId = rs.getInt("student_id");
                        int bookId = rs.getInt("book_id");
                        double fine = rs.getDouble("fine");

                        fineReportArea.append(issueId + "\t" + studentId + "\t" + bookId + "\t" + fine + "\n");
                    }
                }

                // Fetch monthly fines
                List<Double> monthlyFines = admin.viewMonthlyFines();
                fineReportArea.append("\nMonthly Fines:\n");
                fineReportArea.append("Month\tTotal Fine Amount\n");
                fineReportArea.append("----------------------------\n");

                for (int i = 0; i < monthlyFines.size(); i++) {
                    fineReportArea.append((i + 1) + "\t" + monthlyFines.get(i) + "\n");
                }

                // Calculate overall total fine
                double totalFine = monthlyFines.stream().mapToDouble(Double::doubleValue).sum();
                fineReportArea.append("\nOverall Total Fine: Rs. " + totalFine);

            } catch (SQLException ex) {
                fineReportArea.append("Error fetching fine data: " + ex.getMessage());
            }

            fineReportPanel.revalidate();
            fineReportPanel.repaint();
        });

        generateReportButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "SystemReport"); // Show the System Report panel
            systemReportPanel.removeAll(); // Clear the panel before adding new components
            systemReportPanel.setLayout(new BorderLayout());

            JTextArea reportArea = new JTextArea();
            reportArea.setEditable(false); // Make the text area read-only
            JScrollPane scrollPane = new JScrollPane(reportArea);
            systemReportPanel.add(scrollPane, BorderLayout.CENTER);

            // Fetch and display the system report
            Admin admin = new Admin();
            reportArea.setText("=== SYSTEM REPORT ===\n\n");
            try {
                // Redirect the output of generateSystemReport() to the JTextArea
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream originalOut = System.out; // Save the original System.out
                System.setOut(ps); // Redirect System.out to the PrintStream

                // Call the generateSystemReport() method
                admin.generateSystemReport();

                // Restore the original System.out
                System.out.flush();
                System.setOut(originalOut);

                // Append the generated report to the JTextArea
                reportArea.append(baos.toString());
            } catch (Exception ex) {
                reportArea.append("Error generating system report: " + ex.getMessage());
            }

            systemReportPanel.revalidate();
            systemReportPanel.repaint();
        });

        manageStudentButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Student");
            studentPanel.removeAll(); // Clear the panel before adding new components
            studentPanel.setLayout(new BorderLayout());
        
            // Create buttons for Student and Librarian
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JButton studentButton = new JButton("Manage Students");
            JButton librarianButton = new JButton("Manage Librarians");
            buttonPanel.add(studentButton);
            buttonPanel.add(librarianButton);
        
            // Create a text area to display details
            JTextArea detailsArea = new JTextArea();
            detailsArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(detailsArea);
        
            // Add components to the student panel
            studentPanel.add(buttonPanel, BorderLayout.NORTH);
            studentPanel.add(scrollPane, BorderLayout.CENTER);
        
            // Add functionality to the "Student" button
            studentButton.addActionListener(studentEvent -> {
                String idStr = JOptionPane.showInputDialog("Enter Student ID (Leave blank to view all):");
                Integer id = null;
                if (idStr != null && !idStr.trim().isEmpty()) {
                    try {
                        id = Integer.parseInt(idStr.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
        
                Admin admin = new Admin();
                String studentDetails = admin.getStudentDetails(id);
                detailsArea.setText("=== Student Details ===\n\n" + studentDetails);
        
                // Option to toggle account status
                if (id != null) {
                    int choice = JOptionPane.showConfirmDialog(null, "Do you want to toggle the active status of this account?",
                            "Toggle Account Status", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        boolean currentStatus = studentDetails.contains("Active: Yes");
                        boolean success = admin.updateAccountStatus("student", id, !currentStatus);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Account status updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update account status.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        
            // Add functionality to the "Librarian" button
            librarianButton.addActionListener(librarianEvent -> {
                String idStr = JOptionPane.showInputDialog("Enter Librarian ID (Leave blank to view all):");
                Integer id = null;
                if (idStr != null && !idStr.trim().isEmpty()) {
                    try {
                        id = Integer.parseInt(idStr.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
        
                Admin admin = new Admin();
                String librarianDetails = admin.getLibrarianDetails(id);
                detailsArea.setText("=== Librarian Details ===\n\n" + librarianDetails);
        
                // Option to toggle account status
                if (id != null) {
                    int choice = JOptionPane.showConfirmDialog(null, "Do you want to toggle the active status of this account?",
                            "Toggle Account Status", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        boolean currentStatus = librarianDetails.contains("Active: Yes");
                        boolean success = admin.updateAccountStatus("librarian", id, !currentStatus);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Account status updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update account status.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        
            studentPanel.revalidate();
            studentPanel.repaint();
        });

        logoutButton.addActionListener(e -> adminFrame.dispose()); // Close the admin dashboard

        // Add navigation and content panels to the frame
        adminFrame.add(navigationPanel, BorderLayout.WEST);
        adminFrame.add(contentPanel, BorderLayout.CENTER);

        // Make the frame visible
        adminFrame.setVisible(true);
    }

    // Helper method to create a feature panel
    private static JPanel createFeaturePanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        // Test the Admin Panel
        SwingUtilities.invokeLater(() -> displayAdminPage());
    }
}