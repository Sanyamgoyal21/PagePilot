package Frontend;

import Backend.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel {
    public static void displayStudentPage(int studentId) {
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
        // JButton borrowBooksButton = new JButton("Borrow Books");
        // JButton returnBooksButton = new JButton("Return Books and Pay Fines");
        // JButton viewBorrowingStatusButton = new JButton("View Borrowing Status");
        // JButton requestNewBooksButton = new JButton("Request New Books");
        // JButton requestHoldBooksButton = new JButton("Request Hold Books (1 Week)");
        // JButton reissueBooksButton = new JButton("Reissue Borrowed Books");
        // JButton viewNotificationsButton = new JButton("View Notifications (Due Dates, Fines, Approvals)");
        // JButton logoutButton = new JButton("Logout");



        JButton borrowBooksButton = new JButton("Issue Books");
        JButton returnBooksButton = new JButton("Return Books & Pay Fines");
        JButton viewBorrowingStatusButton = new JButton("Borrowing Status");
        JButton requestNewBooksButton = new JButton("Request New Books");
        JButton requestHoldBooksButton = new JButton("Place Hold (1 Week)");
        JButton reissueBooksButton = new JButton("Reissue Books");
        JButton viewNotificationsButton = new JButton("View Notifications");
        JButton logoutButton = new JButton("Log Out");



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
        JPanel borrowBooksPanel = createBorrowBooksPanel(studentId); // Pass the logged-in student's ID
        contentPanel.add(borrowBooksPanel, "BorrowBooks");
        JPanel returnBooksPanel = createReturnBooksPanel(studentId); // Pass the logged-in student's ID
        contentPanel.add(returnBooksPanel, "ReturnBooks");
        JPanel viewBorrowingStatusPanel = createViewBorrowingStatusPanel(studentId); // Pass the logged-in student's ID
        contentPanel.add(viewBorrowingStatusPanel, "ViewBorrowingStatus");
        JPanel requestNewBooksPanel = createRequestNewBooksPanel(studentId); // Pass the logged-in student's ID
        contentPanel.add(requestNewBooksPanel, "RequestNewBooks");
        JPanel requestHoldBooksPanel = createRequestHoldBooksPanel(studentId); // Pass the logged-in student's ID
        JPanel reissueBooksPanel = createReissueBooksPanel(studentId); // Pass the logged-in student's ID
        JPanel viewNotificationsPanel = createFeaturePanel("View Notifications (Due Dates, Fines, Approvals)");

        // Add feature panels to the content panel
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

    // Helper method to create the Borrow Book panel
    private static JPanel createBorrowBooksPanel(int studentId) {
        JPanel borrowBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Book ID", "Title", "Author", "Total Copies", "Available Copies" }, 0);
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        borrowBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Title/Author: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        borrowBooksPanel.add(searchPanel, BorderLayout.NORTH);

        // Borrow Book button
        JButton borrowBookButton = new JButton("Borrow Book");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(borrowBookButton);
        borrowBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "id"; // Numeric input assumed to be Book ID
            } else if (!searchValue.isEmpty()) {
                searchBy = "title"; // Default to title search
            }

            Student student = new Student();
            List<Object[]> books = student.searchBooks(searchBy, searchValue);
            refreshBooksTable(tableModel, books);
        });

        // Add action listener for the Borrow Book button
        borrowBookButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to borrow.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            int availableCopies = (int) tableModel.getValueAt(selectedRow, 4);

            if (availableCopies <= 0) {
                JOptionPane.showMessageDialog(null, "No copies of this book are available.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student();
            boolean success = student.borrowBook(studentId, bookId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshBooksTable(tableModel, null); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to borrow the book. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Refresh books table initially
        refreshBooksTable(tableModel, null);

        return borrowBooksPanel;
    }

    // Helper method to create the Return Books panel
    private static JPanel createReturnBooksPanel(int studentId) {
        JPanel returnBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying issued books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Issue Date", "Due Date", "Fine", "Status" },
                0);
        JTable issuedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        returnBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Title/Author: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        returnBooksPanel.add(searchPanel, BorderLayout.NORTH);

        // Buttons for returning books and paying fines
        JButton returnBookButton = new JButton("Return Book");
        JButton payFineButton = new JButton("Pay Fine");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnBookButton);
        buttonPanel.add(payFineButton);
        returnBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Refresh issued books table initially
        refreshIssuedBooksTable(tableModel, studentId);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "book_id"; // Numeric input assumed to be Book ID
            } else if (!searchValue.isEmpty()) {
                searchBy = "title"; // Default to title search
            }

            Student student = new Student();
            List<Object[]> issuedBooks = student.searchIssuedBooks(studentId, searchBy, searchValue);
            refreshIssuedBooksTable(tableModel, issuedBooks);
        });

        // Add action listener for the Return Book button
        returnBookButton.addActionListener(e -> {
            int selectedRow = issuedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to return.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int issueId = (int) tableModel.getValueAt(selectedRow, 0);
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);

            Student student = new Student();
            boolean success = student.returnBook(issueId, bookId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book returned successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshIssuedBooksTable(tableModel, studentId); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to return the book. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listener for the Pay Fine button
        payFineButton.addActionListener(e -> {
            int selectedRow = issuedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to pay the fine.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int issueId = (int) tableModel.getValueAt(selectedRow, 0);

            Student student = new Student();
            boolean success = student.payFine(issueId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Fine paid successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshIssuedBooksTable(tableModel, studentId); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to pay the fine. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return returnBooksPanel;
    }

    // Helper method to create the View Borrowing Status panel
    private static JPanel createViewBorrowingStatusPanel(int studentId) {
        JPanel borrowingStatusPanel = new JPanel(new BorderLayout());

        // Table for displaying issued books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Issue Date", "Due Date", "Fine", "Status" },
                0);
        JTable issuedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        borrowingStatusPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Title/Author: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        borrowingStatusPanel.add(searchPanel, BorderLayout.NORTH);

        // Refresh issued books table initially
        refreshIssuedBooksTable(tableModel, studentId);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "book_id"; // Numeric input assumed to be Book ID
            } else if (!searchValue.isEmpty()) {
                searchBy = "title"; // Default to title search
            }

            Student student = new Student();
            List<Object[]> issuedBooks = student.searchIssuedBooks(studentId, searchBy, searchValue);
            refreshIssuedBooksTable(tableModel, issuedBooks);
        });

        return borrowingStatusPanel;
    }

    // Helper method to create the Request New Books panel
    private static JPanel createRequestNewBooksPanel(int studentId) {
        JPanel requestNewBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying book requests
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Request ID", "Book Title", "Author", "Description", "Request Date", "Status" }, 0);
        JTable requestsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        requestNewBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Form for submitting new book requests
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Book Title:"), gbc);

        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Row 2: Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        JTextField authorField = new JTextField(20);
        formPanel.add(authorField, gbc);

        // Row 3: Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Description (Optional):"), gbc);

        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);

        // Row 4: Submit Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Submit Request");
        formPanel.add(submitButton, gbc);

        requestNewBooksPanel.add(formPanel, BorderLayout.SOUTH);

        // Refresh requests table initially
        refreshRequestsTable(tableModel, studentId);

        // Add action listener for the Submit button
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Title and Author are required fields!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student();
            boolean success = student.requestNewBook(studentId, title, author, description);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book request submitted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                titleField.setText("");
                authorField.setText("");
                descriptionArea.setText("");
                refreshRequestsTable(tableModel, studentId); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to submit the book request. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return requestNewBooksPanel;
    }

    // Helper method to create the Request Hold Books panel
    private static JPanel createRequestHoldBooksPanel(int studentId) {
        JPanel requestHoldBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying available books
        DefaultTableModel booksTableModel = new DefaultTableModel(
                new String[] { "Book ID", "Book Name", "Author Name", "Available Copies" }, 0);
        JTable booksTable = new JTable(booksTableModel);
        JScrollPane booksScrollPane = new JScrollPane(booksTable);
        requestHoldBooksPanel.add(booksScrollPane, BorderLayout.CENTER);

        // Table for displaying hold requests
        DefaultTableModel holdRequestsTableModel = new DefaultTableModel(
                new String[] { "Hold ID", "Book Name", "Author Name", "Reason", "Hold Date", "Expired Date", "Status" },
                0);
        JTable holdRequestsTable = new JTable(holdRequestsTableModel);
        JScrollPane holdRequestsScrollPane = new JScrollPane(holdRequestsTable);

        // Add hold requests table below the books table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(holdRequestsScrollPane, BorderLayout.CENTER);

        // Form for requesting hold
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Reason
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Reason for Hold:"), gbc);

        gbc.gridx = 1;
        JTextField reasonField = new JTextField(20);
        formPanel.add(reasonField, gbc);

        // Row 2: Submit Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton holdBookButton = new JButton("Hold Book");
        formPanel.add(holdBookButton, gbc);

        bottomPanel.add(formPanel, BorderLayout.SOUTH);
        requestHoldBooksPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Refresh books table initially
        refreshAvailableBooksTable(booksTableModel);

        // Refresh hold requests table initially
        refreshHoldRequestsTable(holdRequestsTableModel, studentId);

        // Add action listener for the Hold Book button
        holdBookButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to hold.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
            String bookName = (String) booksTableModel.getValueAt(selectedRow, 1);
            String authorName = (String) booksTableModel.getValueAt(selectedRow, 2);
            String reason = reasonField.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please provide a reason for holding the book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student();
            boolean success = student.requestHoldBook(studentId, bookId, bookName, authorName, reason);
            if (success) {
                JOptionPane.showMessageDialog(null, "Book hold request submitted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                reasonField.setText("");
                refreshAvailableBooksTable(booksTableModel); // Refresh the books table
                refreshHoldRequestsTable(holdRequestsTableModel, studentId); // Refresh the hold requests table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to submit the hold request. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return requestHoldBooksPanel;
    }

    // Helper method to create the Reissue Books panel
    private static JPanel createReissueBooksPanel(int studentId) {
        JPanel reissueBooksPanel = new JPanel(new BorderLayout());

        // Table for displaying borrowed books
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "Issue ID", "Book ID", "Title", "Author", "Issue Date", "Due Date", "Fine", "Status" },
                0);
        JTable borrowedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        reissueBooksPanel.add(scrollPane, BorderLayout.CENTER);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by ID/Title/Author: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        reissueBooksPanel.add(searchPanel, BorderLayout.NORTH);

        // Reissue Book button
        JButton reissueBookButton = new JButton("Reissue Book");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reissueBookButton);
        reissueBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Refresh borrowed books table initially
        refreshBorrowedBooksTable(tableModel, studentId);

        // Add action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchValue = searchField.getText().trim();
            String searchBy = null;

            if (searchValue.matches("\\d+")) {
                searchBy = "book_id"; // Numeric input assumed to be Book ID
            } else if (!searchValue.isEmpty()) {
                searchBy = "title"; // Default to title search
            }

            Student student = new Student();
            List<Object[]> borrowedBooks = student.searchIssuedBooks(studentId, searchBy, searchValue);
            refreshBorrowedBooksTable(tableModel, borrowedBooks);
        });

        // Add action listener for the Reissue Book button
        reissueBookButton.addActionListener(e -> {
            int selectedRow = borrowedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to reissue.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int issueId = (int) tableModel.getValueAt(selectedRow, 0);
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);
            String status = (String) tableModel.getValueAt(selectedRow, 7);

            if ("returned".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "Already returned. Cannot reissue.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("overdue".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "Book has been overdue. First return it and then issue it again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("issued".equalsIgnoreCase(status)) {
                Student student = new Student();
                boolean success = student.reissueBook(issueId, bookId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Book reissued successfully! Due date extended by 30 days.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBorrowedBooksTable(tableModel, studentId); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to reissue the book. Please try again.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Auto-refresh the table every 30 seconds
        Timer timer = new Timer(30000, e -> refreshBorrowedBooksTable(tableModel, studentId));
        timer.start();

        return reissueBooksPanel;
    }

    // Helper method to refresh the books table
    private static void refreshBooksTable(DefaultTableModel tableModel, List<Object[]> books) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();

        // If books is null, fetch all books
        if (books == null) {
            books = student.searchBooks(null, null);
        }

        for (Object[] row : books) {
            tableModel.addRow(row);
        }

        // if (books.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No books found.", "Information", JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Helper method to refresh the issued books table by student ID
    private static void refreshIssuedBooksTable(DefaultTableModel tableModel, int studentId) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();
        List<Object[]> issuedBooks = student.viewIssuedBooks(studentId);

        for (Object[] row : issuedBooks) {
            tableModel.addRow(row);
        }

        // if (issuedBooks.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No issued books found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Overloaded helper method to refresh the issued books table with a list of
    // books
    private static void refreshIssuedBooksTable(DefaultTableModel tableModel, List<Object[]> issuedBooks) {
        tableModel.setRowCount(0); // Clear the table

        for (Object[] row : issuedBooks) {
            tableModel.addRow(row);
        }

        // if (issuedBooks.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No issued books found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Helper method to refresh the requests table
    private static void refreshRequestsTable(DefaultTableModel tableModel, int studentId) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();
        List<Object[]> requests = student.viewBookRequests(studentId);

        for (Object[] row : requests) {
            tableModel.addRow(row);
        }

        // if (requests.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No book requests found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Helper method to refresh the available books table
    private static void refreshAvailableBooksTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();
        List<Object[]> books = student.getAvailableBooks();

        for (Object[] row : books) {
            tableModel.addRow(row);
        }

        // if (books.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No available books found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Helper method to refresh the hold requests table
    private static void refreshHoldRequestsTable(DefaultTableModel tableModel, int studentId) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();
        List<Object[]> holdRequests = student.getHoldRequests(studentId);

        for (Object[] row : holdRequests) {
            tableModel.addRow(row);
        }

        // if (holdRequests.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No hold requests found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Helper method to refresh the borrowed books table by student ID
    private static void refreshBorrowedBooksTable(DefaultTableModel tableModel, int studentId) {
        tableModel.setRowCount(0); // Clear the table
        Student student = new Student();
        List<Object[]> borrowedBooks = student.viewIssuedBooks(studentId);

        for (Object[] row : borrowedBooks) {
            tableModel.addRow(row);
        }

        // if (borrowedBooks.isEmpty()) {
        //     JOptionPane.showMessageDialog(null, "No borrowed books found.", "Information",
        //             JOptionPane.INFORMATION_MESSAGE);
        // }
    }

    // Overloaded helper method to refresh the borrowed books table with a list of books
    private static void refreshBorrowedBooksTable(DefaultTableModel tableModel, List<Object[]> borrowedBooks) {
        tableModel.setRowCount(0); // Clear the table

        for (Object[] row : borrowedBooks) {
            tableModel.addRow(row);
        }


        
        if (borrowedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No borrowed books found.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Launch the Student Panel
        SwingUtilities.invokeLater(() -> displayStudentPage(1)); // Example student ID
    }
}