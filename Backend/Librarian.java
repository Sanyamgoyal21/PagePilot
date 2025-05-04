package Backend;

import java.util.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    // Add Student
    public boolean addStudent(String name, String email, String phone, String password) {
        String sql = "INSERT INTO student (name, email, phone, password, active) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    // View Issued Books
    public ResultSet viewIssuedBooks(String searchBy, String value) {
        String sql = "SELECT issued_books.issue_id, issued_books.book_id, books.title, books.author, " +
                "issued_books.student_id, issued_books.issue_date, issued_books.due_date, " +
                "issued_books.fine, issued_books.status " +
                "FROM issued_books " +
                "JOIN books ON issued_books.book_id = books.id";

        if (searchBy != null && value != null) {
            switch (searchBy) {
                case "student_id":
                    sql += " WHERE issued_books.student_id = ?";
                    break;
                case "book_id":
                    sql += " WHERE issued_books.book_id = ?";
                    break;
                case "title":
                    sql += " WHERE books.title LIKE ?";
                    break;
                case "author":
                    sql += " WHERE books.author LIKE ?";
                    break;
                case "status":
                    sql += " WHERE issued_books.status = ?";
                    break;
            }
        }

        try {
            Connection con = Database.connect();
            PreparedStatement pst = con.prepareStatement(sql);

            // Set the parameter if searchBy and value are provided
            if (searchBy != null && value != null) {
                if (searchBy.equals("title") || searchBy.equals("author")) {
                    pst.setString(1, "%" + value + "%"); // Use LIKE for title and author
                } else {
                    pst.setString(1, value); // Use exact match for other fields
                }
            }

            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // View Overdue Books
    public List<Object[]> viewOverdueBooks(String searchBy, String value) {
        String sql = "SELECT issued_books.issue_id, issued_books.book_id, books.title, books.author, " +
                "issued_books.student_id, issued_books.issue_date, issued_books.due_date, issued_books.fine " +
                "FROM issued_books " +
                "JOIN books ON issued_books.book_id = books.id " +
                "WHERE issued_books.status = 'overdue'";

        if (searchBy != null && value != null) {
            switch (searchBy) {
                case "book_id":
                    sql += " AND issued_books.book_id = ?";
                    break;
                case "title":
                    sql += " AND books.title LIKE ?";
                    break;
                case "author":
                    sql += " AND books.author LIKE ?";
                    break;
            }
        }

        List<Object[]> overdueBooks = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            if (searchBy != null && value != null) {
                if (searchBy.equals("title") || searchBy.equals("author")) {
                    pst.setString(1, "%" + value + "%"); // Use LIKE for title and author
                } else {
                    pst.setString(1, value); // Use exact match for other fields
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    overdueBooks.add(new Object[] {
                            rs.getInt("issue_id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("student_id"),
                            rs.getDate("issue_date"),
                            rs.getDate("due_date"),
                            rs.getInt("fine")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overdueBooks;
    }

    // View Students
    public List<Object[]> viewStudents(String searchBy, String value) {
        String sql = "SELECT s.id AS student_id, s.name, s.email, s.phone, s.active, " +
                "IFNULL(SUM(ib.fine), 0) AS total_fine " +
                "FROM student s " +
                "LEFT JOIN issued_books ib ON s.id = ib.student_id";

        if (searchBy != null && value != null) {
            sql += " WHERE s." + searchBy + " = ?";
        }

        sql += " GROUP BY s.id";

        List<Object[]> studentData = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            if (searchBy != null && value != null) {
                pst.setString(1, value);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    studentData.add(new Object[] {
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getBoolean("active") ? "Yes" : "No",
                            rs.getDouble("total_fine")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentData;
    }

    // View Requests
    public List<Object[]> viewRequests() {
        String sql = "SELECT id AS request_id, student_id, type AS request_type, " +
                "book_title, author, reason, request_date, status " +
                "FROM requests";

        List<Object[]> requests = new ArrayList<>();
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                requests.add(new Object[] {
                        rs.getInt("request_id"),
                        rs.getInt("student_id"),
                        rs.getString("request_type"),
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getString("reason"),
                        rs.getDate("request_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
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

    public boolean issueBookToStudent(int bookId, int studentId) {
        String checkStudentSql = "SELECT active FROM student WHERE id = ?";
        String checkBookSql = "SELECT available_copies FROM books WHERE id = ?";
        String issueBookSql = "INSERT INTO issued_books (book_id, student_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
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

            // Calculate the issue date and due date
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(30); // Add 30 days to the issue date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Issue the book
            issueBookPst.setInt(1, bookId);
            issueBookPst.setInt(2, studentId);
            issueBookPst.setString(3, issueDate.format(formatter)); // Set the issue date
            issueBookPst.setString(4, dueDate.format(formatter)); // Set the due date
            issueBookPst.setString(5, "issued"); // Set the status to issued
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

    public boolean returnBook(int issueId, int bookId, int fine) {
        String updateStatusSql = "UPDATE issued_books SET status = 'returned', fine = ? WHERE issue_id = ?";
        String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (Connection con = connect();
                PreparedStatement updateStatusPst = con.prepareStatement(updateStatusSql);
                PreparedStatement updateBookPst = con.prepareStatement(updateBookSql)) {

            // Update the status and fine for the issued book
            updateStatusPst.setInt(1, fine);
            updateStatusPst.setInt(2, issueId);
            updateStatusPst.executeUpdate();

            // Increment the available copies of the book
            updateBookPst.setInt(1, bookId);
            updateBookPst.executeUpdate();

            return true; // Book returned successfully
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Operation failed
    }

    public boolean updateBookStatus(int issueId, String status) {
        String sql = "UPDATE issued_books SET status = ? WHERE issue_id = ?";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, issueId);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStudentActiveStatus(int studentId, boolean isActive) {
        String sql = "UPDATE student SET active = ? WHERE id = ?";

        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setBoolean(1, isActive);
            pst.setInt(2, studentId);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM librarian WHERE name = ? AND password = ?";
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

    public static boolean isActive(String username) {
        String sql = "SELECT active FROM librarian WHERE name = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
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

    public boolean approveRequest(int requestId, String requestType) {
        String updateRequestSql = "UPDATE requests SET status = 'Approved' WHERE id = ?";
        String addBookSql = "INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, ?, 5, 5)";

        try (Connection con = connect();
                PreparedStatement updateRequestPst = con.prepareStatement(updateRequestSql);
                PreparedStatement addBookPst = con.prepareStatement(addBookSql)) {

            // Approve the request
            updateRequestPst.setInt(1, requestId);
            updateRequestPst.executeUpdate();

            // If the request is for a new book, add the book to the library
            if ("New Book".equalsIgnoreCase(requestType)) {
                // Fetch book details from the request
                String fetchRequestSql = "SELECT book_title, author FROM requests WHERE id = ?";
                try (PreparedStatement fetchRequestPst = con.prepareStatement(fetchRequestSql)) {
                    fetchRequestPst.setInt(1, requestId);
                    try (ResultSet rs = fetchRequestPst.executeQuery()) {
                        if (rs.next()) {
                            String bookTitle = rs.getString("book_title");
                            String author = rs.getString("author");

                            // Add the book to the library
                            addBookPst.setString(1, bookTitle);
                            addBookPst.setString(2, author);
                            addBookPst.executeUpdate();
                        }
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectRequest(int requestId) {
        String sql = "UPDATE requests SET status = 'Rejected' WHERE id = ?";

        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, requestId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Librarian librarian = new Librarian();
        librarian.addOrUpdateBook("Java Programming", "John Doe", 5, 5);
        librarian.seeBooks();
        librarian.deleteBookById(1);
    }
}