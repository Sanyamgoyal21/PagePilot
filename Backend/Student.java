package Backend;

import java.sql.*;
import java.util.Scanner;
import java.util.*;
import javax.swing.JOptionPane;
import java.time.LocalDate;

public class Student {
    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanki@2004";

    
    // Establish connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static int login(String username, String password) {
        String sql = "SELECT id FROM student WHERE name = ? AND password = ? AND active = TRUE";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Update login status
                String updateSql = "UPDATE student SET login_status = TRUE WHERE id = ?";
                try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                    updatePst.setInt(1, rs.getInt("id"));
                    updatePst.executeUpdate();
                }
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void logout(String username, String password) {
        try (Connection conn = connect()) {
            String sql = "UPDATE student SET login_status = FALSE WHERE name = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Logout successful!");
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookIssue(int bookId, int studentId) {
        String sql = "INSERT INTO issued_books (book_id, student_id, due_date) VALUES (?, ?, DATE_ADD(CURRENT_DATE(), INTERVAL 15 DAY))";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            int rows = pst.executeUpdate();
            System.out.println("Book issued successfully.");

            // Update available copies
            updateAvailableCopies(bookId, -1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateAvailableCopies(int bookId, int adjustment) {
        String sql = "UPDATE books SET available_copies = available_copies + ? WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, adjustment);
            pst.setInt(2, bookId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookReturn(int bookId, int studentId, int issue_id) {
        String sql = "UPDATE issued_books SET status = 'returned' WHERE book_id = ? AND student_id = ? AND issue_id = ?";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            pst.setInt(3, issue_id);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Book returned successfully.");

                // Check for fine
                String payFine = "SELECT fine FROM issued_books WHERE book_id = ? AND student_id = ? AND issue_id = ?";
                try (PreparedStatement payFineStmt = con.prepareStatement(payFine)) {
                    payFineStmt.setInt(1, bookId);
                    payFineStmt.setInt(2, studentId);
                    payFineStmt.setInt(3, issue_id);

                    try (ResultSet rs = payFineStmt.executeQuery()) {
                        if (rs.next()) {
                            int fine = rs.getInt("fine");
                            if (fine > 0) {
                                System.out.println("Fine to be paid: " + fine);
                                try (Scanner scanner = new Scanner(System.in)) {
                                    System.out.print("Enter amount to pay: ");
                                    int amount = scanner.nextInt();
                                    if (amount >= fine) {
                                        // Update fine
                                        String updateFine = "UPDATE issued_books SET fine = 0 WHERE book_id = ? AND student_id = ? AND issue_id = ?";
                                        try (PreparedStatement updateFineStmt = con.prepareStatement(updateFine)) {
                                            updateFineStmt.setInt(1, bookId);
                                            updateFineStmt.setInt(2, studentId);
                                            updateFineStmt.setInt(3, issue_id);
                                            updateFineStmt.executeUpdate();
                                            System.out.println("Fine paid successfully.");
                                        }
                                    } else {
                                        System.out.println("Insufficient amount. Fine not paid.");
                                    }
                                }
                            } else {
                                System.out.println("No fine to be paid.");
                            }
                        }
                    }
                }

                // Update available copies
                updateAvailableCopies(bookId, 1);

            } else {
                System.out.println("No matching record found for return.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> viewIssuedBooks(int studentId) {
        String sql = "SELECT issued_books.issue_id, issued_books.book_id, books.title, books.author, " +
                "issued_books.issue_date, issued_books.due_date, issued_books.fine, issued_books.status " +
                "FROM issued_books " +
                "JOIN books ON issued_books.book_id = books.id " +
                "WHERE issued_books.student_id = ?";

        List<Object[]> issuedBooks = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId); // Filter by student ID

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    issuedBooks.add(new Object[] {
                            rs.getInt("issue_id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDate("issue_date"),
                            rs.getDate("due_date"),
                            rs.getInt("fine"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issuedBooks;
    }

    public List<Object[]> searchIssuedBooks(int studentId, String searchBy, String value) {
        String sql = "SELECT issued_books.issue_id, issued_books.book_id, books.title, books.author, " +
                "issued_books.issue_date, issued_books.due_date, issued_books.fine, issued_books.status " +
                "FROM issued_books " +
                "JOIN books ON issued_books.book_id = books.id " +
                "WHERE issued_books.student_id = ?";

        if (searchBy != null && value != null) {
            switch (searchBy) {
                case "book_id":
                    sql += " AND issued_books.book_id = ?";
                    break;
                case "title":
                    sql += " AND LOWER(books.title) LIKE ?";
                    break;
                case "author":
                    sql += " AND LOWER(books.author) LIKE ?";
                    break;
            }
        }

        List<Object[]> issuedBooks = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            if (searchBy != null && value != null) {
                if (searchBy.equals("title") || searchBy.equals("author")) {
                    pst.setString(2, "%" + value.trim().toLowerCase() + "%"); // Use LIKE for title and author
                } else {
                    pst.setString(2, value.trim()); // Use exact match for other fields
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    issuedBooks.add(new Object[] {
                            rs.getInt("issue_id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDate("issue_date"),
                            rs.getDate("due_date"),
                            rs.getInt("fine"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issuedBooks;
    }

    public static void viewBooks() {
        String sql = "SELECT * FROM books";
        try (
                Connection con = connect();
                Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: "
                        + rs.getString("author") + ", Available Copies: " + rs.getInt("available_copies"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void requestNewBook(String bookTitle, String author, int studentId) {
        String sql = "INSERT INTO requests (student_id, type, book_title, author, reason, request_date, status) " +
                "VALUES (?, 'New Book', ?, ?, NULL, CURDATE(), 'Pending')";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            pst.setString(2, bookTitle);
            pst.setString(3, author);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Book request submitted successfully.");
            } else {
                System.out.println("Failed to submit book request.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void requestHoldBook(String bookTitle, String author, int id) {
        String sql = "INSERT INTO requests (student_id, notes) VALUES (?, ?)";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.setString(2, "Request for Hold book to renew. Book Name : " + bookTitle + " , Author: " + author);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Book request submitted successfully.");
            } else {
                System.out.println("Failed to submit book request.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean requestHoldBook(int studentId, int bookId, String bookName, String authorName, String reason) {
        String sql = "INSERT INTO hold_requests (student_id, book_id, book_name, author_name, reason, hold_date, expired_date, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";

        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            pst.setInt(2, bookId);
            pst.setString(3, bookName);
            pst.setString(4, authorName);
            pst.setString(5, reason);
            pst.setDate(6, java.sql.Date.valueOf(LocalDate.now())); // Hold date
            pst.setDate(7, java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // Expired date (7 days later)
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Return true if the request was successfully saved
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void renewBook(int bookId, int studentId) {
        String sql = "UPDATE issued_books SET due_date = DATE_ADD(due_date, INTERVAL 15 DAY) WHERE book_id = ? AND student_id = ? AND status = 'issued' OR status = 'overdue'";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Book renewed successfully.");
            } else {
                System.out.println("No matching record found for renewal.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewNotification(int studentId) {
        String sql = "SELECT * FROM notification WHERE student_id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (!rs.isBeforeFirst()) { // Check if the result set is empty
                System.out.println("No notifications found.");
                return;
            }
            System.out.println("Notifications for Student ID " + studentId + ":");
            while (rs.next()) {
                System.out.println("Notification: " + rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean borrowBook(int studentId, int bookId) {
        String checkAvailabilitySql = "SELECT available_copies FROM books WHERE id = ?";
        String borrowBookSql = "INSERT INTO issued_books (student_id, book_id, issue_date, due_date, status) " +
                "VALUES (?, ?, ?, ?, 'issued')";
        String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";

        try (Connection con = Database.connect();
                PreparedStatement checkPst = con.prepareStatement(checkAvailabilitySql);
                PreparedStatement borrowPst = con.prepareStatement(borrowBookSql);
                PreparedStatement updatePst = con.prepareStatement(updateBookSql)) {

            // Check if the book is available
            checkPst.setInt(1, bookId);
            ResultSet rs = checkPst.executeQuery();
            if (rs.next()) {
                int availableCopies = rs.getInt("available_copies");
                if (availableCopies <= 0) {
                    JOptionPane.showMessageDialog(null, "No copies of this book are available.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Borrow the book
            borrowPst.setInt(1, studentId);
            borrowPst.setInt(2, bookId);
            borrowPst.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Issue date
            borrowPst.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusWeeks(4))); // Due date (4 weeks from now)
            borrowPst.executeUpdate();

            // Update the available copies
            updatePst.setInt(1, bookId);
            updatePst.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean payFine(int issueId) {
        String sql = "UPDATE issued_books SET fine = 0 WHERE issue_id = ?";

        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, issueId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Return true if the fine was successfully paid
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean returnBook(int issueId, int bookId) {
        String updateIssuedBooksSql = "UPDATE issued_books SET status = 'returned' WHERE issue_id = ?";
        String updateBooksSql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (Connection con = Database.connect();
                PreparedStatement updateIssuedBooksPst = con.prepareStatement(updateIssuedBooksSql);
                PreparedStatement updateBooksPst = con.prepareStatement(updateBooksSql)) {

            // Update the status of the issued book to 'returned'
            updateIssuedBooksPst.setInt(1, issueId);
            int rowsAffected1 = updateIssuedBooksPst.executeUpdate();

            // Increment the available copies of the book
            updateBooksPst.setInt(1, bookId);
            int rowsAffected2 = updateBooksPst.executeUpdate();

            // Return true if both updates were successful
            return rowsAffected1 > 0 && rowsAffected2 > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Object[]> searchBooks(String searchBy, String value) {
        String sql = "SELECT id, title, author, total_copies, available_copies FROM books WHERE available_copies > 0";

        if (searchBy != null && value != null) {
            switch (searchBy) {
                case "id":
                    sql += " AND id = ?";
                    break;
                case "title":
                    sql += " AND LOWER(title) LIKE ?";
                    break;
                case "author":
                    sql += " AND LOWER(author) LIKE ?";
                    break;
            }
        }

        List<Object[]> books = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            if (searchBy != null && value != null) {
                if (searchBy.equals("title") || searchBy.equals("author")) {
                    pst.setString(1, "%" + value.trim().toLowerCase() + "%"); // Use LIKE for title and author
                } else {
                    pst.setString(1, value.trim()); // Use exact match for other fields
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    books.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("total_copies"),
                            rs.getInt("available_copies")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean requestNewBook(int studentId, String title, String author, String description) {
        String sql = "INSERT INTO requests (student_id, title, author, description, status) " +
                "VALUES (?, ?, ?, ?, 'pending')";

        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setString(4, description);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Object[]> viewBookRequests(int studentId) {
        String sql = "SELECT id, title, author, description, request_date, status FROM book_requests WHERE student_id = ?";

        List<Object[]> requests = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    requests.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getDate("request_date"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<Object[]> getAvailableBooks() {
        String sql = "SELECT id, title, author, available_copies FROM books WHERE available_copies > 0";

        List<Object[]> books = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                books.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("available_copies")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Object[]> getHoldRequests(int studentId) {
        String sql = "SELECT id, book_name, author_name, reason, hold_date, expired_date, status " +
                "FROM hold_requests WHERE student_id = ?";

        List<Object[]> holdRequests = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    holdRequests.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("book_name"),
                            rs.getString("author_name"),
                            rs.getString("reason"),
                            rs.getDate("hold_date"),
                            rs.getDate("expired_date"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holdRequests;
    }

    public boolean reissueBook(int issueId, int bookId) {
        String sql = "UPDATE issued_books SET due_date = DATE_ADD(due_date, INTERVAL 30 DAY) WHERE issue_id = ? AND book_id = ? AND status = 'issued'";

        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, issueId);
            pst.setInt(2, bookId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Return true if the due date was successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isActive(int studentId) {
        String sql = "SELECT active FROM student WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("active");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to inactive if not found or error occurs
    }

    public static boolean addStudent(String name, String email, String phone, String password) {
        String sql = "INSERT INTO student (name, email, phone, password, active, login_status) " +
                    "VALUES (?, ?, ?, ?, TRUE, FALSE)";
        
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean resetPassword(String username, String email, String newPassword) {
        String sql = "UPDATE student SET password = ? WHERE name = ? AND email = ? AND active = TRUE";
        try (Connection con = Database.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newPassword);
            pst.setString(2, username);
            pst.setString(3, email);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verifyEmail(String username, String email) {
        String sql = "SELECT id FROM student WHERE name = ? AND email = ? AND active = TRUE";
        try (Connection con = Database.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, email);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
}