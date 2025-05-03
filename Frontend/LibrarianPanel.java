package Frontend;
import Backend.Librarian;
import javax.swing.*;
import java.awt.*;

public class LibrarianPanel {
    public static void displayLibrarianPage() {
        // Create the main frame for the Librarian Dashboard
        JFrame librarianFrame = new JFrame("Librarian Dashboard");
        librarianFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        librarianFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full-screen mode
        librarianFrame.setLayout(new BorderLayout());

        // Left-side navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(8, 1, 5, 5)); // 8 buttons with spacing
        navigationPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        navigationPanel.setPreferredSize(new Dimension(200, 0)); // Fixed width for navigation

        // Buttons for navigation
        JButton addViewDeleteBooksButton = new JButton("Add/View/Delete Books");
        JButton issueBooksButton = new JButton("Issue Books to Students");
        JButton viewIssuedBooksButton = new JButton("View Issued Books");
        JButton returnBooksButton = new JButton("Return Books and Calculate Fines");
        JButton manageStudentRecordsButton = new JButton("Manage Student Records");
        JButton requestApprovalButton = new JButton("Request Approval");
        JButton viewOverdueBooksButton = new JButton("View Overdue Books and Notify Students");
        JButton logoutButton = new JButton("Logout");

        // Add buttons to the navigation panel
        navigationPanel.add(addViewDeleteBooksButton);
        navigationPanel.add(issueBooksButton);
        navigationPanel.add(viewIssuedBooksButton);
        navigationPanel.add(returnBooksButton);
        navigationPanel.add(manageStudentRecordsButton);
        navigationPanel.add(requestApprovalButton);
        navigationPanel.add(viewOverdueBooksButton);
        navigationPanel.add(logoutButton);

        // Main content area with CardLayout
        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Panels for each feature
        JPanel addViewDeleteBooksPanel = createFeaturePanel("Add/View/Delete Books");
        JPanel issueBooksPanel = createFeaturePanel("Issue Books to Students");
        JPanel viewIssuedBooksPanel = createFeaturePanel("View Issued Books");
        JPanel returnBooksPanel = createFeaturePanel("Return Books and Calculate Fines");
        JPanel manageStudentRecordsPanel = createFeaturePanel("Manage Student Records");
        JPanel requestApprovalPanel = createFeaturePanel("Request Approval");
        JPanel viewOverdueBooksPanel = createFeaturePanel("View Overdue Books and Notify Students");

        // Add feature panels to the content panel
        contentPanel.add(addViewDeleteBooksPanel, "AddViewDeleteBooks");
        contentPanel.add(issueBooksPanel, "IssueBooks");
        contentPanel.add(viewIssuedBooksPanel, "ViewIssuedBooks");
        contentPanel.add(returnBooksPanel, "ReturnBooks");
        contentPanel.add(manageStudentRecordsPanel, "ManageStudentRecords");
        contentPanel.add(requestApprovalPanel, "RequestApproval");
        contentPanel.add(viewOverdueBooksPanel, "ViewOverdueBooks");

        // Add action listeners to buttons
        addViewDeleteBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "AddViewDeleteBooks"));
        issueBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "IssueBooks"));
        viewIssuedBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "ViewIssuedBooks"));
        returnBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "ReturnBooks"));
        manageStudentRecordsButton.addActionListener(e -> cardLayout.show(contentPanel, "ManageStudentRecords"));
        requestApprovalButton.addActionListener(e -> cardLayout.show(contentPanel, "RequestApproval"));
        viewOverdueBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "ViewOverdueBooks"));
        logoutButton.addActionListener(e -> librarianFrame.dispose()); // Close the librarian dashboard

        // Add navigation and content panels to the frame
        librarianFrame.add(navigationPanel, BorderLayout.WEST);
        librarianFrame.add(contentPanel, BorderLayout.CENTER);

        // Make the frame visible
        librarianFrame.setVisible(true);
    }

    // Helper method to create a feature panel
    private static JPanel createFeaturePanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        // Launch the Librarian Panel
        SwingUtilities.invokeLater(() -> displayLibrarianPage());
    }
}
