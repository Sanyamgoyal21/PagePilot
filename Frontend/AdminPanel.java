package Frontend;

import javax.swing.*;
import java.awt.event.*;

public class AdminPanel {
    public static void displayAdminPage() {
        // Create the main frame for the Admin Dashboard
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setSize(400, 300);

        // Create a panel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, Admin!");
        welcomeLabel.setBounds(120, 20, 200, 25);
        panel.add(welcomeLabel);

        // Button to manage students
        JButton manageStudentsButton = new JButton("Manage Students");
        manageStudentsButton.setBounds(120, 60, 150, 25);
        panel.add(manageStudentsButton);

        // Button to view reports
        JButton viewReportsButton = new JButton("View Reports");
        viewReportsButton.setBounds(120, 100, 150, 25);
        panel.add(viewReportsButton);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(120, 140, 150, 25);
        panel.add(logoutButton);

        // Add action listeners for buttons
        manageStudentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(adminFrame, "Manage Students functionality coming soon!");
            }
        });

        viewReportsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(adminFrame, "View Reports functionality coming soon!");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adminFrame.dispose(); // Close the admin dashboard
            }
        });

        // Add the panel to the frame and make it visible
        adminFrame.add(panel);
        adminFrame.setVisible(true);
    }
}