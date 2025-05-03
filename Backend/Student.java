package Backend;

import java.sql.*;
import java.util.Scanner;
import java.util.*;

public class Student {
    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "";

    // Establish connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM student WHERE name = ? AND password = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next(); // Return true if a matching record is found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public static void viewIssuedBooks(int studentId) {
        String sql = "SELECT * FROM issued_books WHERE student_id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("book_id") + ", Issue ID: " + rs.getInt("issue_id")
                        + ", Due Date: " + rs.getDate("due_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public static void requestNewBook(String bookTitle, String author, int id) {
        String sql = "INSERT INTO requests (student_id, notes) VALUES (?, ?)";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.setString(2, "Request for new book: " + bookTitle + " by " + author);
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

    // public static void main(String[] args) {
    // // Example usage
    // // login("Sanyam Goyel", "12345");
    // // bookIssue(2, 1);
    // // bookReturn(2, 1, 7);
    // // bookReturn(1, 1, 1);
    // // viewIssuedBooks(1) ;
    // // requestHoldBook("New Book Title", "New Author", 1);
    // // renewBook(2,1) ;
    // viewNotification(1) ;

    // }
}