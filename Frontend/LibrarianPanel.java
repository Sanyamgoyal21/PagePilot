package Frontend;

import Backend.Librarian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        // View Issued Books Panel
        JPanel viewIssuedBooksPanel = createViewIssuedBooksPanel();
        contentPanel.add(viewIssuedBooksPanel, "ViewIssuedBooks");

        // Return Books Panel
        JPanel returnBooksPanel = createReturnBooksPanel();

        // Manage Student Records Panel
        JPanel manageStudentRecordsPanel = createManageStudentRecordsPanel();
        contentPanel.add(manageStudentRecordsPanel, "ManageStudentRecords");

        // Request Approval Panel
        JPanel requestApprovalPanel = createRequestApprovalPanel();
        contentPanel.add(requestApprovalPanel, "RequestApproval");

        // Placeholder panels for other features
        JPanel viewOverdueBooksPanel = createViewOverdueBooksPanel();
        contentPanel.add(viewOverdueBooksPanel, "ViewOverdueBooks");

        // Add feature panels to the content panel
        contentPanel.add(placeholderPanel, "Placeholder");
        contentPanel.add(addViewDeleteBooksPanel, "AddViewDeleteBooks");
        contentPanel.add(returnBooksPanel, "ReturnBooks");

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
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion
        gbc.weightx = 1.0; // Give the search field extra horizontal space
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
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion
        gbc.weightx = 1.0; // Give the search field extra horizontal space
        formPanel.add(new JLabel("Search (Student ID/Book ID/Title/Author):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 25)); // Set preferred size (width: 300px, height: 25px)
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
            boolean success = librarian.issueBookToStudent(bookId, studentId);
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

    private static JPanel createViewIssuedBooksPanel() {
        JPanel viewIssuedBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying issued books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Student ID", "Issue Date", "Due Date" }, 0);
        JTable issuedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        viewIssuedBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Form panel for searching issued books
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Search Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion
        gbc.weightx = 1.0; // Give the search field extra horizontal space
        formPanel.add(new JLabel("Search (Student ID/Book ID/Title/Author):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 25)); // Set preferred size (width: 300px, height: 25px)
        formPanel.add(searchField, gbc);

        // Row 2: Search Button
        JButton searchButton = new JButton("Search Issued Books");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(searchButton, gbc);

        viewIssuedBooksPanel.add(formPanel, BorderLayout.SOUTH);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();

            // If the search field is empty, fetch and display all issued books
            if (searchValue.isEmpty()) {
                refreshIssuedBooksTable(tableModel, null, null);
                JOptionPane.showMessageDialog(null, "Showing all issued books.", "Search Cleared",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Librarian librarian = new Librarian();
            ResultSet rs;

            // If the search value is numeric, search by Student ID or Book ID
            if (searchValue.matches("\\d+")) {
                rs = librarian.viewIssuedBooks("student_id", searchValue);
                if (!hasResults(rs)) { // If no results by Student ID, search by Book ID
                    rs = librarian.viewIssuedBooks("book_id", searchValue);
                }
            } else {
                // Otherwise, search by Title or Author (case-insensitive)
                rs = librarian.viewIssuedBooks("title", searchValue.toLowerCase());
                if (!hasResults(rs)) { // If no results by Title, search by Author
                    rs = librarian.viewIssuedBooks("author", searchValue.toLowerCase());
                }
            }

            // Refresh the table with the search results
            if (!hasResults(rs)) {
                JOptionPane.showMessageDialog(null, "No issued books found matching the search criteria.", "No Results",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                refreshIssuedBooksTable(tableModel, rs, searchValue);
            }

            // Clear the search field after search
            searchField.setText("");
        });

        // Refresh issued books table initially
        refreshIssuedBooksTable(tableModel, null, null);

        return viewIssuedBooksPanel;
    }

    private static JPanel createReturnBooksPanel() {
        JPanel returnBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying issued books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Student ID", "Issue Date", "Due Date", "Fine",
                        "Status" },
                0);
        JTable issuedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        returnBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search (Student ID/Title/Author/Status): "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        returnBooksPanel.add(searchPanel, BorderLayout.NORTH);

        // Button to return the selected book
        JButton returnBookButton = new JButton("Return Book");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnBookButton);
        returnBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "student_id"; // Numeric input assumed to be Student ID
            } else if (searchValue.equalsIgnoreCase("issued") || searchValue.equalsIgnoreCase("overdue")
                    || searchValue.equalsIgnoreCase("returned")) {
                searchBy = "status"; // Status input
            } else {
                searchBy = "title"; // Default to title search
            }

            Librarian librarian = new Librarian();
            ResultSet rs = librarian.viewIssuedBooks(searchBy, searchValue);
            refreshIssuedBooksTable(tableModel, rs, searchValue);
        });

        // Add action listener for the Return Book button
        returnBookButton.addActionListener(e -> {
            int selectedRow = issuedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to return.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the selected book details
            int issueId = (int) tableModel.getValueAt(selectedRow, 0);
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);
            String dueDateStr = String.valueOf(tableModel.getValueAt(selectedRow, 6));
            String status = String.valueOf(tableModel.getValueAt(selectedRow, 8));

            // Check if dueDateStr is null or empty
            LocalDate dueDate = null;
            if (dueDateStr != null && !dueDateStr.equals("null")) {
                try {
                    dueDate = LocalDate.parse(dueDateStr);
                } catch (Exception ex) {
                    dueDate = null; // Handle invalid date format gracefully
                }
            }

            // Calculate the fine
            LocalDate today = LocalDate.now();
            int fine = 0;
            if (dueDate != null && today.isAfter(dueDate)) {
                long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                fine = (int) overdueDays * 10; // ₹10 per day fine
            }

            // Confirm the return and fine
            int confirm = JOptionPane.showConfirmDialog(null,
                    "The fine for this book is ₹" + fine + ". Do you want to proceed?", "Confirm Return",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Return the book
            Librarian librarian = new Librarian();
            boolean success = librarian.returnBook(issueId, bookId, fine);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book returned successfully! Fine: ₹" + fine, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshIssuedBooksTable(tableModel, null, null); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to return the book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Refresh issued books table initially
        refreshIssuedBooksTable(tableModel, null, null);

        return returnBooksPanel;
    }

    private static JPanel createManageStudentRecordsPanel() {
        JPanel manageStudentRecordsPanel = new JPanel(new BorderLayout());

        // Table for displaying student records
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Student ID", "Name", "Email", "Phone", "Active", "Total Fine" }, 0);
        JTable studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        manageStudentRecordsPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by Student ID: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        manageStudentRecordsPanel.add(searchPanel, BorderLayout.NORTH);

        // Buttons for managing students
        JButton toggleActiveButton = new JButton("Toggle Active Status");
        JButton addStudentButton = new JButton("Add Student");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(toggleActiveButton);
        buttonPanel.add(addStudentButton);
        manageStudentRecordsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();

            if (searchValue.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a Student ID to search.", "Missing Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Librarian librarian = new Librarian();
            List<Object[]> studentData = librarian.viewStudents("student_id", searchValue);
            refreshStudentTable(tableModel, studentData);
        });

        // Add action listener for the Toggle Active Status button
        toggleActiveButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a student to toggle active status.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = String.valueOf(tableModel.getValueAt(selectedRow, 4));
            boolean newStatus = currentStatus.equalsIgnoreCase("No");

            Librarian librarian = new Librarian();
            boolean success = librarian.updateStudentActiveStatus(studentId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(null, "Student active status updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshStudentTable(tableModel, null); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update student active status.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listener for the Add Student button
        addStudentButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField phoneField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Email:"));
            formPanel.add(emailField);
            formPanel.add(new JLabel("Phone:"));
            formPanel.add(phoneField);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Librarian librarian = new Librarian();
                boolean success = librarian.addStudent(name, email, phone, password);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Student added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshStudentTable(tableModel, null); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Refresh student table initially
        refreshStudentTable(tableModel, null);

        return manageStudentRecordsPanel;
    }

    private static JPanel createRequestApprovalPanel() {
        JPanel requestApprovalPanel = new JPanel(new BorderLayout());

        // Table for displaying requests
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Request ID", "Student ID", "Request Type", "Book Title", "Author", "Reason/Description",
                        "Request Date", "Status" },
                0);
        JTable requestsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        requestApprovalPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons for approving or rejecting requests
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        requestApprovalPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Refresh requests table initially
        refreshRequestsTable(tableModel);

        // Add action listener for the Approve button
        approveButton.addActionListener(e -> {
            int selectedRow = requestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a request to approve.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int requestId = (int) tableModel.getValueAt(selectedRow, 0);
            String requestType = (String) tableModel.getValueAt(selectedRow, 2);

            Librarian librarian = new Librarian();
            boolean success = librarian.approveRequest(requestId, requestType);
            if (success) {
                JOptionPane.showMessageDialog(null, "Request approved successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshRequestsTable(tableModel); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to approve the request. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listener for the Reject button
        rejectButton.addActionListener(e -> {
            int selectedRow = requestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a request to reject.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int requestId = (int) tableModel.getValueAt(selectedRow, 0);

            Librarian librarian = new Librarian();
            boolean success = librarian.rejectRequest(requestId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Request rejected successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshRequestsTable(tableModel); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to reject the request. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return requestApprovalPanel;
    }

    private static JPanel createViewOverdueBooksPanel() {
        JPanel overdueBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying overdue books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Student ID", "Issue Date", "Due Date",
                        "Fine" },
                0);
        JTable overdueBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(overdueBooksTable);
        overdueBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Title/Author: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        overdueBooksPanel.add(searchPanel, BorderLayout.NORTH);

        // Notify Students button
        JButton notifyButton = new JButton("Notify Students");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(notifyButton);
        overdueBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "book_id"; // Numeric input assumed to be Book ID
            } else if (!searchValue.isEmpty()) {
                searchBy = "title"; // Default to title search
            }

            Librarian librarian = new Librarian();
            List<Object[]> overdueBooks = librarian.viewOverdueBooks(searchBy, searchValue);
            refreshOverdueBooksTable(tableModel, overdueBooks);
        });

        // Add action listener for the Notify Students button
        notifyButton.addActionListener(e -> {
            int selectedRow = overdueBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to notify the student.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) tableModel.getValueAt(selectedRow, 4);
            JOptionPane.showMessageDialog(null, "Notification sent to Student ID: " + studentId, "Notification Sent",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Refresh overdue books table initially
        refreshOverdueBooksTable(tableModel, null);

        return overdueBooksPanel;
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

    private static void refreshIssuedBooksTable(DefaultTableModel tableModel, ResultSet rs, String searchValue) {
        tableModel.setRowCount(0); // Clear the table
        Librarian librarian = new Librarian();

        // If ResultSet is null, fetch all issued books
        if (rs == null) {
            rs = librarian.viewIssuedBooks(null, null);
        }

        try {
            while (rs.next()) {
                java.sql.Date dueDateSql = rs.getDate("due_date");
                LocalDate dueDate = null;
                if (dueDateSql != null) {
                    dueDate = dueDateSql.toLocalDate();
                }
                LocalDate today = LocalDate.now();
                String status = rs.getString("status");

                // Update status to overdue if the due date has passed
                if (status.equals("issued") && dueDate != null && today.isAfter(dueDate)) {
                    status = "overdue";
                    librarian.updateBookStatus(rs.getInt("issue_id"), "overdue");
                }

                tableModel.addRow(new Object[] {
                        rs.getInt("issue_id"),
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("student_id"),
                        rs.getDate("issue_date"),
                        dueDateSql, // Use the original SQL Date object
                        rs.getInt("fine"),
                        status
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void refreshOverdueBooksTable(DefaultTableModel tableModel, List<Object[]> overdueBooks) {
        tableModel.setRowCount(0); // Clear the table
        Librarian librarian = new Librarian();

        // If overdueBooks is null, fetch all overdue books
        if (overdueBooks == null) {
            overdueBooks = librarian.viewOverdueBooks(null, null);
        }

        for (Object[] row : overdueBooks) {
            tableModel.addRow(row);
        }

        if (overdueBooks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No overdue books found.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void refreshStudentTable(DefaultTableModel tableModel, List<Object[]> studentData) {
        tableModel.setRowCount(0); // Clear the table
        Librarian librarian = new Librarian();

        // If studentData is null, fetch all students
        if (studentData == null) {
            studentData = librarian.viewStudents(null, null);
        }

        for (Object[] row : studentData) {
            tableModel.addRow(row);
        }

        if (studentData.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No student data found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void refreshRequestsTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear the table
        Librarian librarian = new Librarian();
        List<Object[]> requests = librarian.viewRequests();

        for (Object[] row : requests) {
            tableModel.addRow(row);
        }

        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No requests found.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
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
