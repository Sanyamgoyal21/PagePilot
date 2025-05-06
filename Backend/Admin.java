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

    // Method to get the active status of a librarian by ID
    public boolean getLibrarianStatus(int librarianId) throws SQLException {
        String sql = "SELECT active FROM librarian WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, librarianId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("active");
                } else {
                    throw new SQLException("Librarian with ID " + librarianId + " not found.");
                }
            }
        }
    }

    // Method to get the active status of a student by ID
    public boolean getStudentStatus(int studentId) throws SQLException {
        String sql = "SELECT active FROM student WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("active");
                } else {
                    throw new SQLException("Student with ID " + studentId + " not found.");
                }
            }
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

    public boolean insertLibrarian(String name, String password) throws SQLException {
        String sql = "INSERT INTO librarian (name, password, active) VALUES (?, ?, ?)";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setBoolean(3, true); // Set the librarian as active by default
            return pst.executeUpdate() > 0; // Returns true if the insertion was successful
        }
    }

    public boolean deleteLibrarian(int id) {
        String sql = "DELETE FROM librarian WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting librarian: " + e.getMessage());
        }
    }

    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            return pst.executeUpdate() > 0; // Returns true if a row was deleted
        }
    }

    public boolean insertStudent(String name, String email, String phone, String password) throws SQLException {
        String sql = "INSERT INTO student (name, email, phone, password, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);
            pst.setBoolean(5, true); // Set the student as active by default
            return pst.executeUpdate() > 0; // Returns true if the insertion was successful
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

    public boolean updateAccountStatus(String userType, int userId, boolean newStatus) throws SQLException {
        String tableName = userType.equalsIgnoreCase("student") ? "student" : "librarian";
        String sql = "UPDATE " + tableName + " SET active = ? WHERE id = ?";
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setBoolean(1, newStatus);
            pst.setInt(2, userId);
            return pst.executeUpdate() > 0; // Returns true if the update was successful
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
        String sql = "SELECT * FROM admin WHERE id = ? AND password = ?";
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

    public static boolean isActive(String username) {
        String sql = "SELECT active FROM admin WHERE name = ?";
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

    public void generateSystemReport() {
        try (Connection con = Database.connect()) {
            System.out.println("=== OVERALL SYSTEM REPORT ===");

            // Total number of students
            String studentCountQuery = "SELECT COUNT(*) AS total_students FROM student";
            try (PreparedStatement pst = con.prepareStatement(studentCountQuery);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total Students: " + rs.getInt("total_students"));
                }
            }

            // Total number of librarians
            String librarianCountQuery = "SELECT COUNT(*) AS total_librarians FROM librarian";
            try (PreparedStatement pst = con.prepareStatement(librarianCountQuery);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total Librarians: " + rs.getInt("total_librarians"));
                }
            }

            // Total number of books
            String bookCountQuery = "SELECT COUNT(*) AS total_books FROM books";
            try (PreparedStatement pst = con.prepareStatement(bookCountQuery);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total Books: " + rs.getInt("total_books"));
                }
            }

            // Total issued books
            String issuedBooksQuery = "SELECT COUNT(*) AS total_issued_books FROM issued_books WHERE status = 'issued'";
            try (PreparedStatement pst = con.prepareStatement(issuedBooksQuery);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total Issued Books: " + rs.getInt("total_issued_books"));
                }
            }

            // Total fines collected
            String totalFinesQuery = "SELECT SUM(fine) AS total_fines FROM issued_books";
            try (PreparedStatement pst = con.prepareStatement(totalFinesQuery);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total Fines Collected: " + rs.getDouble("total_fines"));
                }
            }

        } catch (SQLException e) {
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

    public List<Double> getMonthlyFineReport() {
        List<Double> monthlyFines = new ArrayList<>(Collections.nCopies(12, 0.0)); // Initialize with 12 months
        String sql = "SELECT MONTH(issue_date) AS month, SUM(fine) AS total_fine " +
                "FROM issued_books WHERE fine > 0 GROUP BY MONTH(issue_date)";
        try (Connection con = connect();
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

    public List<Object[]> getAllLibrarians() {
        String sql = "SELECT id, name, password, active FROM librarian";
        List<Object[]> librarians = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                librarians.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getBoolean("active") ? "Yes" : "No" // Convert boolean to "Yes"/"No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return librarians;
    }

    public List<Object[]> searchLibrarians(String searchValue) {
        String sql = "SELECT id, name, password FROM librarian WHERE id = ? OR LOWER(name) LIKE ?";
        List<Object[]> librarians = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            try {
                pst.setInt(1, Integer.parseInt(searchValue)); // Try to parse as ID
            } catch (NumberFormatException e) {
                pst.setInt(1, -1); // If not a number, set an invalid ID
            }
            pst.setString(2, "%" + searchValue.toLowerCase() + "%"); // Search by name
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    librarians.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return librarians;
    }

    public List<Object[]> searchStudents(String query) {
        String sql = "SELECT id, name, email, phone, active FROM student WHERE name LIKE ? OR email LIKE ?";
        List<Object[]> students = new ArrayList<>();
        try (Connection con = Database.connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, "%" + query + "%");
            pst.setString(2, "%" + query + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    students.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getBoolean("active") ? "Yes" : "No"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Object[]> getIndividualFineReport(int studentId) {
        String sql = "SELECT issue_id, book_id, fine, issue_date, due_date, status " +
                "FROM issued_books WHERE student_id = ? AND fine > 0";
        List<Object[]> fineReport = new ArrayList<>();
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    fineReport.add(new Object[] {
                            rs.getInt("issue_id"),
                            rs.getInt("book_id"),
                            rs.getDouble("fine"),
                            rs.getDate("issue_date"),
                            rs.getDate("due_date"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fineReport;
    }

    public void displayIndividualFineReport(int studentId) {
        Admin admin = new Admin();
        List<Object[]> fineReport = admin.getIndividualFineReport(studentId);

        if (fineReport.isEmpty()) {
            System.out.println("No fines found for Student ID: " + studentId);
            return;
        }

        System.out.println("Fine Report for Student ID: " + studentId);
        System.out.println("-------------------------------------------------");
        System.out.printf("%-10s %-10s %-10s %-15s %-15s %-10s\n",
                "Issue ID", "Book ID", "Fine", "Issue Date", "Due Date", "Status");
        for (Object[] row : fineReport) {
            System.out.printf("%-10d %-10d %-10.2f %-15s %-15s %-10s\n",
                    row[0], row[1], row[2], row[3], row[4], row[5]);
        }
    }

    public void displayMonthlyFineReport() {
        Admin admin = new Admin();
        List<Double> monthlyFines = admin.getMonthlyFineReport();

        System.out.println("Monthly Fine Report");
        System.out.println("-------------------------------------------------");
        System.out.printf("%-10s %-10s\n", "Month", "Total Fine");
        for (int i = 0; i < monthlyFines.size(); i++) {
            System.out.printf("%-10s %-10.2f\n", getMonthName(i + 1), monthlyFines.get(i));
        }
    }

    // Helper method to get month name
    private String getMonthName(int month) {
        return new java.text.DateFormatSymbols().getMonths()[month - 1];
    }

    public List<Object[]> getAllBooks() {
        String sql = "SELECT id, title, author, total_copies, available_copies FROM books";
        List<Object[]> books = new ArrayList<>();
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                books.add(new Object[] {
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
        return books;
    }

    public List<Object[]> getAllStudents() {
        String sql = "SELECT id, name, email, phone, active FROM student";
        List<Object[]> students = new ArrayList<>();
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                students.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getBoolean("active") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) AS total_students FROM student";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_students");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalLibrarians() {
        String sql = "SELECT COUNT(*) AS total_librarians FROM librarian";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_librarians");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) AS total_books FROM books";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_books");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalIssuedBooks() {
        String sql = "SELECT COUNT(*) AS total_issued_books FROM issued_books WHERE status = 'issued'";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_issued_books");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalFinesCollected() {
        String sql = "SELECT SUM(fine) AS total_fines FROM issued_books";
        try (Connection con = connect();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_fines");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static void main(String[] args) {
        Admin admin = new Admin();
        admin.displayIndividualFineReport(1); // Replace 1 with the desired student ID
        admin.displayMonthlyFineReport();
    }
}