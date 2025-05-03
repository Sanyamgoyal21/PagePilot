package Backend;

import java.sql.*;

public class Database {

    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanyam@123";

    // Establish connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void queryExecute(String query) {
        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String createDatabase = "CREATE DATABASE IF NOT EXISTS pagepilot;";
        queryExecute(createDatabase);

        String useDatabase = "USE pagepilot;";
        queryExecute(useDatabase);

        // Create admin table
        String createTableAdmin = "CREATE TABLE admin ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100), "
                + "password VARCHAR(50), "
                + "login TINYINT(1) DEFAULT 0"
                + ");";
        queryExecute(createTableAdmin);

        // Create librarian table
        String createTableLibrarian = "CREATE TABLE librarian ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100), "
                + "password VARCHAR(50), "
                + "login_status BOOLEAN DEFAULT FALSE, "
                + "active BOOLEAN DEFAULT TRUE"
                + ");";
        queryExecute(createTableLibrarian);

        // Create student table
        String createTableStudent = "CREATE TABLE student ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100), "
                + "password VARCHAR(50), "
                + "login_status BOOLEAN DEFAULT FALSE, "
                + "active BOOLEAN DEFAULT TRUE"
                + ");";
        queryExecute(createTableStudent);

        // Create books table
        String createTableBook = "CREATE TABLE books ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(100), "
                + "author VARCHAR(100), "
                + "total_copies INT DEFAULT 1, "
                + "available_copies INT DEFAULT 1, "
                + "added_on DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";
        queryExecute(createTableBook);

        // Create issued_books table
        String createTableIssueBooks = "CREATE TABLE issued_books ("
                + "issue_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "book_id INT, "
                + "student_id INT, "
                + "due_date DATE, "
                + "issue_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "status ENUM('issued', 'returned', 'overdue') DEFAULT 'issued', "
                + "fine INT DEFAULT 0"
                + ");";
        queryExecute(createTableIssueBooks);

        // Create notification table
        String createTablesNotification = "CREATE TABLE notification ("
                + "notification_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "student_id INT NOT NULL, "
                + "sender ENUM('librarian', 'admin') DEFAULT 'librarian', "
                + "title VARCHAR(100), "
                + "message TEXT NOT NULL, "
                + "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "is_read BOOLEAN DEFAULT FALSE"
                + ");";
        queryExecute(createTablesNotification);

        // Create requests table
        String createTableRequests = "CREATE TABLE requests ("
                + "request_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "student_id INT NOT NULL, "
                + "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending', "
                + "notes TEXT"
                + ");";
        queryExecute(createTableRequests);
    }
}