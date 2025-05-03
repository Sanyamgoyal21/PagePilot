package Backend;

import java.sql.*;

public class database {

    static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL_WITHOUT_DB = "jdbc:mysql://localhost:3306/";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "";

    // Connect without selecting a database (used for CREATE DATABASE)
    public static Connection connectWithoutDB() throws SQLException {
        return DriverManager.getConnection(DB_URL_WITHOUT_DB, USER, PASS);
    }

    // Connect to the specific database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Execute a query on the given connection
    public static void queryExecute(Connection conn, String query) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main method to create the database and tables
    public static void main(String[] args) {
        try {
            // Load JDBC driver
            Class.forName(DRIVER_CLASS_NAME);

            // Step 1: Create the database if it doesn't exist
            try (Connection conn = connectWithoutDB()) {
                String createDatabase = "CREATE DATABASE IF NOT EXISTS pagepilot;";
                queryExecute(conn, createDatabase);
            }

            // Step 2: Connect to the created database
            try (Connection conn = connect()) {

                String createTableAdmin = "CREATE TABLE IF NOT EXISTS admin (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100)," +
                        "password VARCHAR(50)," +
                        "login TINYINT(1) DEFAULT 0" +
                        ");";
                queryExecute(conn, createTableAdmin);

                String createTableLibrarian = "CREATE TABLE IF NOT EXISTS librarian (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100)," +
                        "password VARCHAR(50)," +
                        "login_status BOOLEAN DEFAULT FALSE," +
                        "active BOOLEAN DEFAULT TRUE" +
                        ");";
                queryExecute(conn, createTableLibrarian);

                String createTableStudent = "CREATE TABLE IF NOT EXISTS student (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100)," +
                        "password VARCHAR(50)," +
                        "login_status BOOLEAN DEFAULT FALSE," +
                        "active BOOLEAN DEFAULT TRUE" +
                        ");";
                queryExecute(conn, createTableStudent);

                String createTableBook = "CREATE TABLE IF NOT EXISTS books (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "title VARCHAR(100)," +
                        "author VARCHAR(100)," +
                        "total_copies INT DEFAULT 1," +
                        "available_copies INT DEFAULT 1," +
                        "added_on DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");";
                queryExecute(conn, createTableBook);

                String createTableIssueBooks = "CREATE TABLE IF NOT EXISTS issued_books (" +
                        "issue_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "book_id INT," +
                        "student_id INT," +
                        "due_date DATE," +
                        "issue_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "status ENUM('issued', 'returned', 'overdue') DEFAULT 'issued'," +
                        "fine INT DEFAULT 0" +
                        ");";
                queryExecute(conn, createTableIssueBooks);

                String createTableNotification = "CREATE TABLE IF NOT EXISTS notification (" +
                        "notification_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "student_id INT NOT NULL," +
                        "sender ENUM('librarian', 'admin') DEFAULT 'librarian'," +
                        "title VARCHAR(100)," +
                        "message TEXT NOT NULL," +
                        "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "is_read BOOLEAN DEFAULT FALSE" +
                        ");";
                queryExecute(conn, createTableNotification);

                String createTableRequests = "CREATE TABLE IF NOT EXISTS requests (" +
                        "request_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "student_id INT NOT NULL," +
                        "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending'," +
                        "notes TEXT" +
                        ");";
                queryExecute(conn, createTableRequests);

                System.out.println("✅ Database and all tables created successfully.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
