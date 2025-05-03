package Backend;

import java.util.*;
import java.sql.*;

public class Admin {

    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanyam@123";

    // Establish connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // CREATE
    public void insertAdmin(String name, int id, String password) {
        String sql = "INSERT INTO admin (name, id , password) VALUES (?, ?, ?)";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setInt(2, id);
            pst.setString(3, password);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read
    public void readAdmin() {
        String sql = "SELECT * FROM admin";
        try (
                Connection con = connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: "
                        + rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String readLibrarian() {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT * FROM librarian";
        try (
                Connection con = connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.append(rs.getInt("id")).append("\t")
                        .append(rs.getString("name")).append("\t")
                        .append(rs.getString("password")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error fetching librarian data: ").append(e.getMessage());
        }
        return result.toString();
    }

    public void insertLibrarian(String name, String password) {
        String checkSql = "SELECT * FROM librarian WHERE password = ?";
        String insertSql = "INSERT INTO librarian (name, password) VALUES (?, ?)";
        try (
                Connection con = connect();
                PreparedStatement checkPst = con.prepareStatement(checkSql);
                PreparedStatement insertPst = con.prepareStatement(insertSql)) {
            // Check if the password is already in use
            checkPst.setString(1, password);
            ResultSet rs = checkPst.executeQuery();
            if (rs.next()) {
                throw new SQLException("Password is already in use. Please choose another password.");
            }

            // Insert the new librarian
            insertPst.setString(1, name);
            insertPst.setString(2, password);
            int rows = insertPst.executeUpdate();
            System.out.println("Librarian inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deleteLibrarian(int id) {
        String checkSql = "SELECT * FROM librarian WHERE id = ?";
        String deleteSql = "DELETE FROM librarian WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement checkPst = con.prepareStatement(checkSql);
                PreparedStatement deletePst = con.prepareStatement(deleteSql)) {
            // Check if the ID exists
            checkPst.setInt(1, id);
            ResultSet rs = checkPst.executeQuery();
            if (!rs.next()) {
                return false; // ID does not exist
            }

            // Proceed with deletion
            deletePst.setInt(1, id);
            int rows = deletePst.executeUpdate();
            return rows > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertStudent(String name, int id, String password) {
        String sql = "INSERT INTO student (name, id , password) VALUES (?, ?, ?)";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setInt(2, id);
            pst.setString(3, password);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStudent(String name, String email, String phone, String password) {
        String sql = "INSERT INTO student (name, email, phone, password) VALUES (?, ?, ?, ?)";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding student: " + e.getMessage());
        }
    }

    public String getStudentDetails(Integer id) {
        StringBuilder result = new StringBuilder();
        String sql = id == null
                ? "SELECT id, name, email, phone, active FROM student"
                : "SELECT id, name, email, phone, active FROM student WHERE id = ?";

        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            if (id != null) {
                pst.setInt(1, id);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Phone: ").append(rs.getString("phone"))
                        .append(", Active: ").append(rs.getBoolean("active") ? "Yes" : "No")
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error fetching student data: ").append(e.getMessage());
        }
        return result.toString();
    }

    public String getLibrarianDetails(Integer id) {
        StringBuilder result = new StringBuilder();
        String sql = id == null ? "SELECT * FROM librarian" : "SELECT * FROM librarian WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            if (id != null) {
                pst.setInt(1, id);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Active: ").append(rs.getBoolean("active") ? "Yes" : "No")
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error fetching librarian data: ").append(e.getMessage());
        }
        return result.toString();
    }

    public boolean updateAccountStatus(String table, int id, boolean active) {
        String sql = "UPDATE " + table + " SET active = ? WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setBoolean(1, active);
            pst.setInt(2, id);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void allFineList() {
        String sql = "SELECT fine, student_id, book_id, issue_id FROM issued_books WHERE fine > 0";
        try (
                Connection con = connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No fines found.");
                return;
            }
            System.out.println("Fines List:");
            System.out.println("-------------------------------------------------");
            while (rs.next()) {
                System.out.println(
                        "Issue ID: " + rs.getInt("issue_id") +
                                ", Student ID: " + rs.getInt("student_id") +
                                ", Book ID: " + rs.getInt("book_id") +
                                ", Amount: " + rs.getDouble("fine"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void manageAccountLibrarian(int id) {
        String sql = "SELECT * FROM librarian WHERE id = ? AND ACTIVE = 1";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: "
                        + rs.getString("password"));
            } else {
                System.out.println("No Librarian found with ID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void manageAccountStudent(int id) {
        String sql = "SELECT * FROM student WHERE id = ? AND ACTIVE = 1";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: "
                        + rs.getString("password"));
            } else {
                System.out.println("No Student found with ID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logout(int id) {
        String sql = "UPDATE admin SET login = 0 WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id); // Assuming you want to log out the librarian with ID 1
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Librarian logged out.");
            } else {
                System.out.println("No Admin found with ID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM admin WHERE name = ? AND password = ?";
        try (Connection con = connect();
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

    public static void login(int id) {
        String sql = "UPDATE admin SET login = 1 WHERE id = ?";
        try (
                Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id); // Assuming you want to log out the librarian with ID 1
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Librarian logged in.");
            } else {
                System.out.println("No Admin found with ID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void generateSystemReport() {
        try (Connection con = connect()) {
            // Books Statistics
            String booksSql = "SELECT COUNT(*) as total_books, " +
                    "SUM(total_copies) as total_copies, " +
                    "SUM(available_copies) as available_copies " +
                    "FROM books";

            // Users Statistics
            String usersSql = "SELECT " +
                    "(SELECT COUNT(*) FROM student) as total_students, " +
                    "(SELECT COUNT(*) FROM librarian) as total_librarians";

            // Issued Books Statistics
            String issuedSql = "SELECT " +
                    "COUNT(*) as total_issued, " +
                    "SUM(CASE WHEN status = 'overdue' THEN 1 ELSE 0 END) as overdue_books, " +
                    "SUM(fine) as total_fines " +
                    "FROM issued_books";

            try (Statement stmt = con.createStatement()) {
                // Print Books Statistics
                try (ResultSet booksRs = stmt.executeQuery(booksSql)) {
                    if (booksRs.next()) {
                        System.out.println("\n=== BOOKS STATISTICS ===");
                        System.out.println("Total Unique Books: " + booksRs.getInt("total_books"));
                        System.out.println("Total Copies: " + booksRs.getInt("total_copies"));
                        System.out.println("Available Copies: " + booksRs.getInt("available_copies"));
                    }
                }

                // Print Users Statistics
                try (ResultSet usersRs = stmt.executeQuery(usersSql)) {
                    if (usersRs.next()) {
                        System.out.println("\n=== USERS STATISTICS ===");
                        System.out.println("Total Students: " + usersRs.getInt("total_students"));
                        System.out.println("Total Librarians: " + usersRs.getInt("total_librarians"));
                    }
                }

                // Print Issued Books Statistics
                try (ResultSet issuedRs = stmt.executeQuery(issuedSql)) {
                    if (issuedRs.next()) {
                        System.out.println("\n=== CIRCULATION STATISTICS ===");
                        System.out.println("Total Books Issued: " + issuedRs.getInt("total_issued"));
                        System.out.println("Overdue Books: " + issuedRs.getInt("overdue_books"));
                        System.out.println("Total Fines Ammount: Rs." + issuedRs.getDouble("total_fines"));
                    }
                }

                System.out.println("\n=== REPORT GENERATED ON: " + new java.util.Date() + " ===");
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Double> viewMonthlyFines() {
        List<Double> monthlyFines = new ArrayList<>(Collections.nCopies(12, 0.0)); // Initialize with 12 months

        String sql = "SELECT MONTH(issue_date) AS month, SUM(fine) AS total_fine " +
                "FROM issued_books WHERE fine > 0 GROUP BY MONTH(issue_date)";
        try (
                Connection con = connect();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int month = rs.getInt("month");
                double totalFine = rs.getDouble("total_fine");
                monthlyFines.set(month - 1, totalFine); // Store fine in the corresponding month (0-indexed)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyFines;
    }

    // public static void main(String[] args)
    // {
    // // insertLibrarian("Prantik Sanki", 2, "678910");
    // // readAdmin();

    // // readLibrarian();
    // // deleteLibrarian(2) ;
    // // readLibrarian();

    // // insertStudent("Sanyam Goyel", 1, "12345");
    // // insertStudent("Prantik Sanki", 2, "678910");
    // // readStudent();
    // // allFineList() ;
    // // manageAccountStudent(1) ;
    // // login(1) ;
    // // logout(1) ;
    // generateSystemReport() ;

    // }

}