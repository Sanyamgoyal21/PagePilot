package Backend;

import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class Librarian {

    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanyam@123";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Add or Update Book
    public boolean addOrUpdateBook(String title, String author, int totalCopies, int availableCopies) {
        String checkSql = "SELECT * FROM books WHERE title = ? AND author = ?";
        String updateSql = "UPDATE books SET total_copies = total_copies + ?, available_copies = available_copies + ? WHERE title = ? AND author = ?";
        String insertSql = "INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, ?, ?, ?)";

        try (Connection con = connect();
                PreparedStatement checkPst = con.prepareStatement(checkSql);
                PreparedStatement updatePst = con.prepareStatement(updateSql);
                PreparedStatement insertPst = con.prepareStatement(insertSql)) {

            // Check if the book already exists
            checkPst.setString(1, title);
            checkPst.setString(2, author);
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                // Update existing book
                updatePst.setInt(1, totalCopies);
                updatePst.setInt(2, availableCopies);
                updatePst.setString(3, title);
                updatePst.setString(4, author);
                updatePst.executeUpdate();
                return true; // Book updated
            } else {
                // Insert new book
                insertPst.setString(1, title);
                insertPst.setString(2, author);
                insertPst.setInt(3, totalCopies);
                insertPst.setInt(4, availableCopies);
                insertPst.executeUpdate();
                return true; // Book added
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Operation failed
    }

    // View Books
    public ResultSet viewBooks(String searchBy, String value) {
        String sql = "SELECT * FROM books";
        if (searchBy != null) {
            switch (searchBy) {
                case "id":
                    sql += " WHERE id = ?";
                    break;
                case "title":
                    sql += " WHERE title LIKE ?";
                    break;
                case "author":
                    sql += " WHERE author LIKE ?";
                    break;
            }
        }

        try {
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql);
            if (searchBy != null) {
                pst.setString(1, searchBy.equals("id") ? value : "%" + value + "%");
            }
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Delete Book by ID
    public boolean deleteBookById(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void seeBooks() {
        String sql = "SELECT * FROM books";
        try (Connection con = connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: "
                        + rs.getString("author")
                        + ", Total Copies: " + rs.getInt("total_copies") + ", Available Copies: "
                        + rs.getInt("available_copies") +
                        ", Added On: " + rs.getTimestamp("added_on"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean issueBookToStudent(int bookId, int studentId, String dueDate) {
        String checkStudentSql = "SELECT active FROM student WHERE id = ?";
        String checkBookSql = "SELECT available_copies FROM books WHERE id = ?";
        String issueBookSql = "INSERT INTO issued_books (book_id, student_id, due_date) VALUES (?, ?, ?)";
        String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";

        try (Connection con = connect();
                PreparedStatement checkStudentPst = con.prepareStatement(checkStudentSql);
                PreparedStatement checkBookPst = con.prepareStatement(checkBookSql);
                PreparedStatement issueBookPst = con.prepareStatement(issueBookSql);
                PreparedStatement updateBookPst = con.prepareStatement(updateBookSql)) {

            // Check if the student exists and is active
            checkStudentPst.setInt(1, studentId);
            ResultSet studentRs = checkStudentPst.executeQuery();
            if (studentRs.next()) {
                boolean isActive = studentRs.getBoolean("active");
                if (!isActive) {
                    throw new IllegalStateException("Student account is deactivated.");
                }
            } else {
                throw new IllegalArgumentException("Student ID not found.");
            }

            // Check if the book is available
            checkBookPst.setInt(1, bookId);
            ResultSet bookRs = checkBookPst.executeQuery();
            if (bookRs.next()) {
                int availableCopies = bookRs.getInt("available_copies");
                if (availableCopies <= 0) {
                    return false; // Book not available
                }
            } else {
                return false; // Book not found
            }

            // Issue the book
            issueBookPst.setInt(1, bookId);
            issueBookPst.setInt(2, studentId);
            issueBookPst.setString(3, dueDate);
            issueBookPst.executeUpdate();

            // Update the available copies of the book
            updateBookPst.setInt(1, bookId);
            updateBookPst.executeUpdate();

            return true; // Book issued successfully
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Operation failed
    }

    public static void main(String[] args) {
        Librarian librarian = new Librarian();
        librarian.addOrUpdateBook("Java Programming", "John Doe", 5, 5);
        librarian.seeBooks();
        librarian.deleteBookById(1);
    }
}