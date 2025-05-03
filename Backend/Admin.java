package Backend ;
import java.util.* ; 
import java.sql.*;


public class Admin
{
    
    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanki@2004";




    // Establish connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // CREATE
    public  void insertAdmin (String name, int id,  String password) 
    {
        String sql = "INSERT INTO admin (name, id , password) VALUES (?, ?, ?)";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setInt(2, id);
            pst.setString(3, password);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    // Read
    public   void readAdmin() 
    {
        String sql = "SELECT * FROM admin";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {
            while (rs.next()) 
            {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: " + rs.getString("password"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public String readLibrarian() {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT * FROM librarian";
        try (
            Connection con = connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
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
    // public   void readLibrarian() 
    // {
    //     String sql = "SELECT * FROM librarian";
    //     try (
    //      Connection con = connect();
    //      Statement stmt = con.createStatement();
    //      ResultSet rs = stmt.executeQuery(sql)
    //      ) 
    //      {
    //         while (rs.next()) 
    //         {
    //             System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: " + rs.getString("password"));
    //         }
    //     } 
    //     catch (SQLException e) 
    //     {
    //         e.printStackTrace();
    //     }
    // }

    public   void insertLibrarian (String name, int id,  String password) 
    {
        String sql = "INSERT INTO librarian (name, id , password) VALUES (?, ?, ?)";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setInt(2, id);
            pst.setString(3, password);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public   void deleteLibrarian(int id) {
        String sql = "DELETE FROM librarian WHERE id = ?";
        try (
            Connection con = connect(); 
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) 
            {
                System.out.println("Librarian deleted.");
            } 
            else 
            {
                System.out.println("No Librarian found with ID " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public   void insertStudent (String name, int id,  String password) 
    {
        String sql = "INSERT INTO student (name, id , password) VALUES (?, ?, ?)";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setInt(2, id);
            pst.setString(3, password);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public   void readStudent() 
    {
        String sql = "SELECT * FROM student";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {
            while (rs.next()) 
            {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: " + rs.getString("password"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public static void allFineList() 
    {
        String sql = "SELECT fine, student_id, book_id, issue_id FROM issued_books WHERE fine > 0";
        try (
            Connection con = connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) 
        {

            if(!rs.isBeforeFirst()) 
            {
                System.out.println("No fines found.");
                return;
            }
            System.out.println("Fines List:");
            System.out.println("-------------------------------------------------");
            while (rs.next()) 
            {
                System.out.println(
                    "Issue ID: " + rs.getInt("issue_id") + 
                    ", Student ID: " + rs.getInt("student_id") + 
                    ", Book ID: " + rs.getInt("book_id") + 
                    ", Amount: " + rs.getDouble("fine")
                );
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public static void manageAccountLibrarian(int id) 
    {
        String sql = "SELECT * FROM librarian WHERE id = ? AND ACTIVE = 1";
        try (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) 
            {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: " + rs.getString("password"));
            } 
            else 
            {
                System.out.println("No Librarian found with ID " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }




    public static void manageAccountStudent(int id) 
    {
        String sql = "SELECT * FROM student WHERE id = ? AND ACTIVE = 1";
        try (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) 
            {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Password: " + rs.getString("password"));
            } 
            else 
            {
                System.out.println("No Student found with ID " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void logout(int id) 
    {
        String sql = "UPDATE admin SET login = 0 WHERE id = ?";
        try (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id); // Assuming you want to log out the librarian with ID 1
            int rows = pst.executeUpdate();
            if (rows > 0) 
            {
                System.out.println("Librarian logged out.");
            } 
            else 
            {
                System.out.println("No Admin found with ID " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public static void login(int id) 
    {
        String sql = "UPDATE admin SET login = 1 WHERE id = ?";
        try (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id); // Assuming you want to log out the librarian with ID 1
            int rows = pst.executeUpdate();
            if (rows > 0) 
            {
                System.out.println("Librarian logged in.");
            } 
            else 
            {
                System.out.println("No Admin found with ID " + id);
            }
        } 
        catch (SQLException e) 
        {
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

    // public static void main(String[] args) 
    // {
    //     // insertLibrarian("Prantik Sanki", 2, "678910");
    //     // readAdmin();



    //     // readLibrarian(); 
    //     // deleteLibrarian(2) ; 
    //     // readLibrarian(); 


    //     // insertStudent("Sanyam Goyel", 1, "12345");
    //     // insertStudent("Prantik Sanki", 2, "678910");
    //     // readStudent();
    //     // allFineList() ;
    //     // manageAccountStudent(1) ;
    //     // login(1) ;
    //     // logout(1) ;
    //     generateSystemReport() ;

    // }


}