package Backend;

import java.sql.*;

public class Database {

    static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL_WITHOUT_DB = "jdbc:mysql://localhost:3306/";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanyam@123"; // Replace with your actual password

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

    public ResultSet viewStudents(String searchBy, String value) {
        String sql = "SELECT s.id AS student_id, s.name, s.email, s.phone, s.active, " +
                "IFNULL(SUM(ib.fine), 0) AS total_fine " +
                "FROM student s " +
                "LEFT JOIN issued_books ib ON s.id = ib.student_id";

        if (searchBy != null && value != null) {
            sql += " WHERE s." + searchBy + " = ?";
        }

        sql += " GROUP BY s.id";

        try {
            Connection con = Database.connect();
            PreparedStatement pst = con.prepareStatement(sql);

            if (searchBy != null && value != null) {
                pst.setString(1, value);
            }

            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                        "name VARCHAR(100) NOT NULL," +
                        "email VARCHAR(100) NOT NULL," +
                        "phone VARCHAR(15) NOT NULL," +
                        "password VARCHAR(50) NOT NULL," +
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
                        "fine INT DEFAULT 0.0" +
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
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "student_id INT NOT NULL," +
                        "type ENUM('New Book', 'Hold Book') NOT NULL," +
                        "book_title VARCHAR(255)," +
                        "author VARCHAR(255)," +
                        "reason TEXT," +
                        "request_date DATE NOT NULL," +
                        "status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending'," +
                        "description TEXT," +
                        "notes VARCHAR(255)," +
                        "FOREIGN KEY (student_id) REFERENCES student(id)" +
                        ");";
                queryExecute(conn, createTableRequests);

                String createTableBookRequests = "CREATE TABLE IF NOT EXISTS book_requests (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "student_id INT NOT NULL," +
                        "title VARCHAR(255) NOT NULL," +
                        "author VARCHAR(255) NOT NULL," +
                        "description TEXT," +
                        "request_date DATE NOT NULL," +
                        "status VARCHAR(50) DEFAULT 'Pending'," +
                        "FOREIGN KEY (student_id) REFERENCES student(id)" +
                        ");";
                queryExecute(conn, createTableBookRequests);

                String createTableHoldRequests = "CREATE TABLE IF NOT EXISTS hold_requests (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "student_id INT NOT NULL," +
                        "book_id INT NOT NULL," +
                        "book_name VARCHAR(255) NOT NULL," +
                        "author_name VARCHAR(255) NOT NULL," +
                        "reason TEXT," +
                        "hold_date DATE NOT NULL," +
                        "expired_date DATE NOT NULL," +
                        "status VARCHAR(50) DEFAULT 'Pending'," +
                        "FOREIGN KEY (student_id) REFERENCES student(id)," +
                        "FOREIGN KEY (book_id) REFERENCES books(id)" +
                        ");";
                queryExecute(conn, createTableHoldRequests);

                System.out.println("Database and all tables created successfully.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
