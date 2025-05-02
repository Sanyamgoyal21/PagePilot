package Frontend;

import Backend.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminPanel {
    public static void displayAdminPage() {
        // Create the main frame for the Admin Dashboard
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full-screen mode
        adminFrame.setLayout(new BorderLayout());

        // Left-side navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5 buttons with spacing
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

            // Left panel for adding a librarian
            JPanel addLibrarianPanel = new JPanel();
            addLibrarianPanel.setLayout(new GridLayout(5, 2, 10, 10)); // 5 rows, 2 columns, spacing of 10px
            addLibrarianPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel idLabel = new JLabel("ID:");
            JTextField idField = new JTextField(20);
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField(20);
            JButton addButton = new JButton("Add Librarian");

            addLibrarianPanel.add(nameLabel);
            addLibrarianPanel.add(nameField);
            addLibrarianPanel.add(idLabel);
            addLibrarianPanel.add(idField);
            addLibrarianPanel.add(passwordLabel);
            addLibrarianPanel.add(passwordField);
            addLibrarianPanel.add(new JLabel()); // Empty cell for spacing
            addLibrarianPanel.add(addButton);

            // Right panel for displaying current librarians
            JPanel viewLibrariansPanel = new JPanel(new BorderLayout());
            JTextArea librarianListArea = new JTextArea();
            librarianListArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(librarianListArea);
            viewLibrariansPanel.add(scrollPane, BorderLayout.CENTER);

            // Add panels to the split pane
            splitPane.setLeftComponent(addLibrarianPanel);
            splitPane.setRightComponent(viewLibrariansPanel);

            // Add the split pane to the librarian panel
            librarianPanel.setLayout(new BorderLayout());
            librarianPanel.add(splitPane, BorderLayout.CENTER);

            // Fetch and display current librarians
            Admin admin = new Admin();
            librarianListArea.setText("Current Librarians:\n");
            librarianListArea.append("ID\tName\tPassword\n");
            librarianListArea.append("---------------------------------\n");
            String sql = "SELECT * FROM librarian";
            try {
                try (Connection con = Admin.connect();
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        librarianListArea.append(rs.getInt("id") + "\t" +
                                rs.getString("name") + "\t" +
                                rs.getString("password") + "\n");
                    }
                }
            } catch (SQLException ex) {
                librarianListArea.append("Error fetching librarian data: " + ex.getMessage());
            }

            // Add functionality to the "Add Librarian" button
            addButton.addActionListener(addEvent -> {
                String name = nameField.getText().trim();
                String idStr = idField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (name.isEmpty() || idStr.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    admin.insertLibrarian(name, id, password);
                    JOptionPane.showMessageDialog(null, "Librarian added successfully!");

                    // Refresh the librarian list
                    librarianListArea.setText("Current Librarians:\n");
                    librarianListArea.append("ID\tName\tPassword\n");
                    librarianListArea.append("---------------------------------\n");
                    try (Connection con = Admin.connect();
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            librarianListArea.append(rs.getInt("id") + "\t" +
                                    rs.getString("name") + "\t" +
                                    rs.getString("password") + "\n");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error adding librarian: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            librarianPanel.revalidate();
            librarianPanel.repaint();
        });
        viewFineReportButton.addActionListener(e -> cardLayout.show(contentPanel, "FineReport"));
        generateReportButton.addActionListener(e -> cardLayout.show(contentPanel, "SystemReport"));
        manageStudentButton.addActionListener(e -> cardLayout.show(contentPanel, "Student"));
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