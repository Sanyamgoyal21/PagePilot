package Frontend;

import Backend.Librarian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        // Placeholder Panel (Default Panel)
        JPanel placeholderPanel = new JPanel();
        JLabel placeholderLabel = new JLabel("Welcome to the Librarian Dashboard", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 24));
        placeholderPanel.setLayout(new BorderLayout());
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);

        // Add/View/Delete Books Panel
        JPanel addViewDeleteBooksPanel = createAddViewDeleteBooksPanel();

        // Issue Books Panel
        JPanel issueBooksPanel = createIssueBooksPanel();
        contentPanel.add(issueBooksPanel, "IssueBooks");

        // Placeholder panels for other features
        JPanel viewIssuedBooksPanel = createFeaturePanel("View Issued Books");
        JPanel returnBooksPanel = createFeaturePanel("Return Books and Calculate Fines");
        JPanel manageStudentRecordsPanel = createFeaturePanel("Manage Student Records");
        JPanel requestApprovalPanel = createFeaturePanel("Request Approval");
        JPanel viewOverdueBooksPanel = createFeaturePanel("View Overdue Books and Notify Students");

        // Add feature panels to the content panel
        contentPanel.add(placeholderPanel, "Placeholder");
        contentPanel.add(addViewDeleteBooksPanel, "AddViewDeleteBooks");
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

        // Show the placeholder panel by default
        cardLayout.show(contentPanel, "Placeholder");

        // Make the frame visible
        librarianFrame.setVisible(true);
    }

    private static JPanel createAddViewDeleteBooksPanel() {
        JPanel addViewDeleteBooksPanel = new JPanel(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "ID", "Title", "Author", "Total Copies", "Available Copies" }, 0);
        JTable bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        addViewDeleteBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Book Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField titleField = new JTextField();
        formPanel.add(titleField, gbc);

        // Row 2: Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField authorField = new JTextField();
        formPanel.add(authorField, gbc);

        // Row 3: Total Copies
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Total Copies:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField totalCopiesField = new JTextField();
        formPanel.add(totalCopiesField, gbc);

        // Row 4: Available Copies
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Available Copies:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField availableCopiesField = new JTextField();
        formPanel.add(availableCopiesField, gbc);

        // Row 5: Search Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Search (ID/Title/Author):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField searchField = new JTextField();
        formPanel.add(searchField, gbc);

        // Row 6: Buttons (Add/Update, Delete, Search)
        JButton addBookButton = new JButton("Add/Update Book");
        JButton deleteBookButton = new JButton("Delete Selected Book");
        JButton searchBookButton = new JButton("Search Book");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.add(addBookButton);
        buttonPanel.add(deleteBookButton);
        buttonPanel.add(searchBookButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(buttonPanel, gbc);

        addViewDeleteBooksPanel.add(formPanel, BorderLayout.SOUTH);

        // Add action listeners for Add/Update, Delete, and Search buttons
        addBookButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String totalCopiesText = totalCopiesField.getText().trim();
            String availableCopiesText = availableCopiesField.getText().trim();

            // Validation: Check if any field is empty
            if (title.isEmpty() || author.isEmpty() || totalCopiesText.isEmpty() || availableCopiesText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled.", "Missing Field",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse numeric fields
            int totalCopies;
            int availableCopies;
            try {
                totalCopies = Integer.parseInt(totalCopiesText);
                availableCopies = Integer.parseInt(availableCopiesText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Total Copies and Available Copies must be numeric.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add or update the book
            Librarian librarian = new Librarian();
            boolean success = librarian.addOrUpdateBook(title, author, totalCopies, availableCopies);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book added/updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable(tableModel, null, null);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add/update the book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            Librarian librarian = new Librarian();
            boolean success = librarian.deleteBookById(bookId);

            if (success) {
                JOptionPane.showMessageDialog(null, "Book deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable(tableModel, null, null);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        searchBookButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();

            // If the search field is empty, fetch and display all books
            if (searchValue.isEmpty()) {
                refreshBookTable(tableModel, null, null);
                return;
            }

            Librarian librarian = new Librarian();
            ResultSet rs;

            // If the search value is numeric, search by ID
            if (searchValue.matches("\\d+")) {
                rs = librarian.viewBooks("id", searchValue);
            } else {
                // Otherwise, search by Title or Author
                rs = librarian.viewBooks("title", searchValue);
                if (!hasResults(rs)) { // If no results by Title, search by Author
                    rs = librarian.viewBooks("author", searchValue);
                }
            }

            // Refresh the table with the search results
            if (!hasResults(rs)) {
                JOptionPane.showMessageDialog(null, "No books found matching the search criteria.", "No Results",
                        JOptionPane.WARNING_MESSAGE);
            }
            refreshBookTable(tableModel, rs, searchValue);
        });

        // Refresh book table
        refreshBookTable(tableModel, null, null);

        return addViewDeleteBooksPanel;
    }

    private static JPanel createIssueBooksPanel() {
        JPanel issueBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "ID", "Title", "Author", "Total Copies", "Available Copies" }, 0);
        JTable bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        issueBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Form panel for issuing books
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Book ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField bookIdField = new JTextField();
        bookIdField.setEditable(true); // Allow editing
        formPanel.add(bookIdField, gbc);

        // Row 2: Title
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField titleField = new JTextField();
        titleField.setEditable(true); // Allow editing
        formPanel.add(titleField, gbc);

        // Row 3: Author
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField authorField = new JTextField();
        authorField.setEditable(true); // Allow editing
        formPanel.add(authorField, gbc);

        // Row 4: Student ID
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField studentIdField = new JTextField();
        formPanel.add(studentIdField, gbc);

        // Row 5: Search Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Search (ID/Title/Author):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField searchField = new JTextField();
        formPanel.add(searchField, gbc);

        // Row 6: Buttons (Issue Book, Search)
        JButton issueBookButton = new JButton("Issue Book");
        JButton searchBookButton = new JButton("Search Book");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(issueBookButton);
        buttonPanel.add(searchBookButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(buttonPanel, gbc);

        issueBooksPanel.add(formPanel, BorderLayout.SOUTH);

        // Add action listener for table row selection
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                bookIdField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                titleField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                authorField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
            }
        });

        // Add action listener for the Issue Book button
        issueBookButton.addActionListener(e -> {
            String bookIdText = bookIdField.getText().trim();
            String studentIdText = studentIdField.getText().trim();

            // Validation: Check if a book is selected and Student ID is entered
            if (bookIdText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a book to issue.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (studentIdText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the Student ID.", "Missing Field",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse numeric fields
            int bookId;
            int studentId;
            try {
                bookId = Integer.parseInt(bookIdText);
                studentId = Integer.parseInt(studentIdText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Book ID and Student ID must be numeric.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Issue the book
            Librarian librarian = new Librarian();
            boolean success = librarian.issueBookToStudent(bookId, studentId, null); // Null for due date for now
            if (success) {
                JOptionPane.showMessageDialog(null, "Book issued successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable(tableModel, null, null); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to issue the book. Either the book is not available or the details are incorrect.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listener for the Search Book button
        searchBookButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();

            // If the search field is empty, fetch and display all books
            if (searchValue.isEmpty()) {
                refreshBookTable(tableModel, null, null);
                return;
            }

            Librarian librarian = new Librarian();
            ResultSet rs;

            // If the search value is numeric, search by ID
            if (searchValue.matches("\\d+")) {
                rs = librarian.viewBooks("id", searchValue);
            } else {
                // Otherwise, search by Title or Author
                rs = librarian.viewBooks("title", searchValue);
                if (!hasResults(rs)) { // If no results by Title, search by Author
                    rs = librarian.viewBooks("author", searchValue);
                }
            }

            // Refresh the table with the search results
            if (!hasResults(rs)) {
                JOptionPane.showMessageDialog(null, "No books found matching the search criteria.", "No Results",
                        JOptionPane.WARNING_MESSAGE);
            }
            refreshBookTable(tableModel, rs, searchValue);
        });

        // Refresh book table initially
        refreshBookTable(tableModel, null, null);

        return issueBooksPanel;
    }

    private static JPanel createFeaturePanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private static void refreshBookTable(DefaultTableModel tableModel, ResultSet rs, String searchValue) {
        tableModel.setRowCount(0); // Clear the table
        Librarian librarian = new Librarian();

        // If ResultSet is null, fetch all books
        if (rs == null) {
            rs = librarian.viewBooks(null, null);
        }

        try {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasResults(ResultSet rs) {
        try {
            return rs != null && rs.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> displayLibrarianPage());
    }
}
