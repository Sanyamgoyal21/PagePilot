package Frontend;
import Backend.Student;
import javax.swing.*;
import java.awt.*;

public class StudentPanel {
    public static void displayStudentPage() {
        // Create the main frame for the Student Dashboard
        JFrame studentFrame = new JFrame("Student Dashboard");
        studentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        studentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full-screen mode
        studentFrame.setLayout(new BorderLayout());

        // Left-side navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(8, 1, 5, 5)); // 8 buttons with spacing
        navigationPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        navigationPanel.setPreferredSize(new Dimension(200, 0)); // Fixed width for navigation

        // Buttons for navigation
        JButton borrowBooksButton = new JButton("Borrow Books");
        JButton returnBooksButton = new JButton("Return Books and Pay Fines");
        JButton viewBorrowingStatusButton = new JButton("View Borrowing Status");
        JButton requestNewBooksButton = new JButton("Request New Books");
        JButton requestHoldBooksButton = new JButton("Request Hold Books (1 Week)");
        JButton reissueBooksButton = new JButton("Reissue Borrowed Books");
        JButton viewNotificationsButton = new JButton("View Notifications (Due Dates, Fines, Approvals)");
        JButton logoutButton = new JButton("Logout");

        // Add buttons to the navigation panel
        navigationPanel.add(borrowBooksButton);
        navigationPanel.add(returnBooksButton);
        navigationPanel.add(viewBorrowingStatusButton);
        navigationPanel.add(requestNewBooksButton);
        navigationPanel.add(requestHoldBooksButton);
        navigationPanel.add(reissueBooksButton);
        navigationPanel.add(viewNotificationsButton);
        navigationPanel.add(logoutButton);

        // Main content area with CardLayout
        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Panels for each feature
        JPanel borrowBooksPanel = createFeaturePanel("Borrow Books");
        JPanel returnBooksPanel = createFeaturePanel("Return Books and Pay Fines");
        JPanel viewBorrowingStatusPanel = createFeaturePanel("View Borrowing Status");
        JPanel requestNewBooksPanel = createFeaturePanel("Request New Books");
        JPanel requestHoldBooksPanel = createFeaturePanel("Request Hold Books (1 Week)");
        JPanel reissueBooksPanel = createFeaturePanel("Reissue Borrowed Books");
        JPanel viewNotificationsPanel = createFeaturePanel("View Notifications (Due Dates, Fines, Approvals)");

        // Add feature panels to the content panel
        contentPanel.add(borrowBooksPanel, "BorrowBooks");
        contentPanel.add(returnBooksPanel, "ReturnBooks");
        contentPanel.add(viewBorrowingStatusPanel, "ViewBorrowingStatus");
        contentPanel.add(requestNewBooksPanel, "RequestNewBooks");
        contentPanel.add(requestHoldBooksPanel, "RequestHoldBooks");
        contentPanel.add(reissueBooksPanel, "ReissueBooks");
        contentPanel.add(viewNotificationsPanel, "ViewNotifications");

        // Add action listeners to buttons
        borrowBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "BorrowBooks"));
        returnBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "ReturnBooks"));
        viewBorrowingStatusButton.addActionListener(e -> cardLayout.show(contentPanel, "ViewBorrowingStatus"));
        requestNewBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "RequestNewBooks"));
        requestHoldBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "RequestHoldBooks"));
        reissueBooksButton.addActionListener(e -> cardLayout.show(contentPanel, "ReissueBooks"));
        viewNotificationsButton.addActionListener(e -> cardLayout.show(contentPanel, "ViewNotifications"));
        logoutButton.addActionListener(e -> studentFrame.dispose()); // Close the student dashboard

        // Add navigation and content panels to the frame
        studentFrame.add(navigationPanel, BorderLayout.WEST);
        studentFrame.add(contentPanel, BorderLayout.CENTER);

        // Make the frame visible
        studentFrame.setVisible(true);
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
        // Launch the Student Panel
        SwingUtilities.invokeLater(() -> displayStudentPage());
    }
}
