package Backend ;
import java.util.* ; 
import java.sql.*;

class Librarian
{

    static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/pagepilot";
    static final String USER = "root";
    static final String PASS = "Sanki@2004";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // CREATE
    // public static void insertLibrarian (String name, int id,  String password) 
    // {
    //     String sql = "INSERT INTO librarian (name, id , password) VALUES (?, ?, ?)";
    //     try (
    //      Connection con = connect();
    //      PreparedStatement pst = con.prepareStatement(sql)
    //      ) 
    //      {
    //         pst.setString(1, name);
    //         pst.setInt(2, id);
    //         pst.setString(3, password);
    //         int rows = pst.executeUpdate();
    //         System.out.println("Row inserted.");
    //     } 
    //     catch (SQLException e) 
    //     {
    //         e.printStackTrace();
    //     }
    // }

    // Read
    public void seeBooks() 
    {
        String sql = "SELECT * FROM books";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {

            // -> id INT PRIMARY KEY AUTO_INCREMENT,
            // -> title VARCHAR(100),
            // -> author VARCHAR(100),
            // -> total_copies INT DEFAULT 1,
            // -> available_copies INT DEFAULT 1,
            // -> added_on DATETIME DEFAULT CURRENT_TIMESTAMP


            while (rs.next()) 
            {
                System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: " + rs.getString("author")
                 + ", Total Copies: " + rs.getInt("total_copies") + ", Available Copies: " + rs.getInt("available_copies") + 
                 ", Added On: " + rs.getTimestamp("added_on"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public void addBooks(String title, String author, int total_copies, int available_copies) 
    {
        String sql = "INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, ?, ?, ?)";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, title);
            pst.setString(2, author);
            pst.setInt(3, total_copies);
            pst.setInt(4, available_copies);
            int rows = pst.executeUpdate();
            System.out.println("Row inserted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void deleteBooks(int id) 
    {
        String sql = "DELETE FROM books WHERE id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            System.out.println("Row deleted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    // CREATE TABLE issued_books
    // -> (
    // -> issue_id INT PRIMARY KEY AUTO_INCREMENT,
    // -> book_id INT,
    // -> student_id INT,
    // -> due_date DATE,
    // -> issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    // -> status ENUM('issued','returned','overdue') DEFAULT 'issued',
    // -> );




    public static void bookIssue(int bookId, int studentId) 
    {
        String sql = "INSERT INTO issued_books (book_id, student_id, due_date) VALUES (?, ?, DATE_ADD(CURRENT_DATE(), INTERVAL 15 DAY))";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            int rows = pst.executeUpdate();
            System.out.println("Book issued successfully.");

            // Update available copies
            updateAvailableCopies(bookId, -1);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private static void updateAvailableCopies(int bookId, int adjustment) {
        String sql = "UPDATE books SET available_copies = available_copies + ? WHERE id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) {
            pst.setInt(1, adjustment);
            pst.setInt(2, bookId);
            pst.executeUpdate();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void readIssuedBooks() 
    {
        String sql = "SELECT * FROM issued_books";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {
            while (rs.next()) 
            {
                System.out.println("Issue ID: " + rs.getInt("issue_id") + ", Book ID: " + rs.getInt("book_id") + ", Student ID: " + rs.getInt("student_id")
                 + ", Due Date: " + rs.getDate("due_date") + ", Issue Date: " + rs.getTimestamp("issue_date") + ", Status: " + rs.getString("status"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void returnBook(int issueId) 
    {
        String sql = "UPDATE issued_books SET status = 'returned' WHERE issue_id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, issueId);
            int rows = pst.executeUpdate();
            System.out.println("Book returned successfully.");

            // Update available copies
            updateAvailableCopies(issueId, 1);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public static void returnBook(int issueId, int book_id, int student_id) 
    {
        String sql = "UPDATE issued_books SET status = 'returned' WHERE issue_id = ? AND book_id = ? AND student_id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, issueId);
            pst.setInt(2, book_id);
            pst.setInt(3, student_id);
            int rows = pst.executeUpdate();
            System.out.println("Book returned successfully.");

            // Update available copies
            updateAvailableCopies(issueId, 1);
            calculateFineCharged(issueId, student_id);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public static void calculateFineCharged() {
        String sql = "SELECT issue_id, student_id, DATEDIFF(CURRENT_DATE(), due_date) AS days_overdue " +
                     "FROM issued_books WHERE status != 'returned'";
    
        try (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                int issueId = rs.getInt("issue_id");
                int studentId = rs.getInt("student_id");
                int daysOverdue = rs.getInt("days_overdue");
    
                if (daysOverdue > 0) {
                    int fineAmount = daysOverdue * 15;
                    System.out.println("Issuance #" + issueId + " | Fine charged: Rs. " + fineAmount);
    
                    String updateSql = "UPDATE issued_books SET fine = ? WHERE issue_id = ? AND student_id = ?";
                    try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                        updatePst.setInt(1, fineAmount);
                        updatePst.setInt(2, issueId);
                        updatePst.setInt(3, studentId);
                        updatePst.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static void overDueAdd()
    {
        String sql =  "UPDATE books " +
        "SET status = 'overdue' " +
        "WHERE status != 'returned' AND issue_date < (CURDATE() - INTERVAL 15 DAY)" ; 

        try
        (
            Connection con = connect();
            PreparedStatement pst = con.prepareStatement(sql)
        ) 
            {
                int rows = pst.executeUpdate();
                System.out.println("Overdue books updated successfully.");
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
    }

    

    public static void studentAdd(String name, String password) 
    {
        String sql = "INSERT INTO student (name, password) VALUES (? , ?)";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setString(2, password);
            int rows = pst.executeUpdate();
            System.out.println("Student Added.");
            studentDisplay(name, password);

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void studentUpdate(int id, String name, String password) 
    {
        String sql = "UPDATE student SET name = ?, password = ? WHERE id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setInt(3, id);
            int rows = pst.executeUpdate();
            System.out.println("Student Updated.");
            studentDisplay(name, password);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public static void studentDelete(int id) 
    {
        String sql = "DELETE FROM student WHERE id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            System.out.println("Student Deleted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void studentDisplay(String name , String password )
    {
        String sql = "SELECT * FROM student WHERE name = ? AND password = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setString(1, name);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
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



    // CREATE TABLE requests (
    // ->     request_id INT PRIMARY KEY AUTO_INCREMENT,
    // ->     issue_id INT NOT NULL,
    // ->     student_id INT NOT NULL,
    // ->     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    // ->     status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',

    // ->     notes TEXT
    // -> );



    public static void acceptRequest(int requestId) 
    {
        String sql = "UPDATE requests SET status = 'approved' WHERE request_id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, requestId);
            int rows = pst.executeUpdate();
            System.out.println("Request accepted.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void rejectRequest(int requestId) 
    {
        String sql = "UPDATE requests SET status = 'rejected' WHERE request_id = ?";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, requestId);
            int rows = pst.executeUpdate();
            System.out.println("Request rejected.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public static void allRequest()
    {
        String sql = "SELECT * FROM requests";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {
            while (rs.next()) 
            {
                System.out.println("Request ID: " + rs.getInt("request_id") + ", Issue ID: " + rs.getInt("issue_id") + ", Student ID: " + rs.getInt("student_id")
                 + ", Request Date: " + rs.getTimestamp("request_date") + ", Status: " + rs.getString("status") + ", Notes: " + rs.getString("notes"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }



    public static void seeOverdueBooks() 
    {
        String sql = "SELECT * FROM issued_books WHERE status = 'overdue'";
        try (
         Connection con = connect();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)
         ) 
         {
            while (rs.next()) 
            {
                System.out.println("Issue ID: " + rs.getInt("issue_id") + ", Book ID: " + rs.getInt("book_id") + ", Student ID: " + rs.getInt("student_id")
                 + ", Due Date: " + rs.getDate("due_date") + ", Issue Date: " + rs.getTimestamp("issue_date") + ", Status: " + rs.getString("status"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


    public static void notifyStudent(int student_id , int book_id)
    {
        String sql = "SELECT * FROM issued_books WHERE student_id = ? AND book_id = ? AND status = 'overdue'";
        try (
         Connection con = connect();
         PreparedStatement pst = con.prepareStatement(sql)
         ) 
         {
            pst.setInt(1, student_id);
            pst.setInt(2, book_id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) 
            {
                System.out.println("Issue ID: " + rs.getInt("issue_id") + ", Book ID: " + rs.getInt("book_id") + ", Student ID: " + rs.getInt("student_id")
                 + ", Due Date: " + rs.getDate("due_date") + ", Issue Date: " + rs.getTimestamp("issue_date") + ", Status: " + rs.getString("status"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }


//     | name         | id | password | login_status |
// +--------------+----+----------+--------------+
// | Sanyam Goyel |  1 | 12345    |            0 |


    public static void logout( int id)
    {
        String sql = "UPDATE librarian SET login_status = 0 WHERE id = ?";
        try (
        Connection con = connect();
        PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            System.out.println("Librarian logged out.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    
    public static void login( int id)
    {
        String sql = "UPDATE librarian SET login_status = 1 WHERE id = ?";
        try (
        Connection con = connect();
        PreparedStatement pst = con.prepareStatement(sql)
        ) 
        {
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            System.out.println("Librarian logged in.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
















    public static void main(String[] args) 
    {
        // insertLibrarian("Sanyam Goyel", 1, "12345");
        // readLibrarian();

        // addBooks("Coding with Cpp", "Narshimha k.", 5, 5);
        // addBooks("Let Us C", "Kanetkar", 3, 3);
        // addBooks("CSA", "Nayantara Kotoky", 4, 4);
        // seeBooks();
        // deleteBooks(1);
        // seeBooks();
        // bookIssue(1, 1);
        // readIssuedBooks();
        // bookIssue(2,2); 
        // returnBook(3,2,2) ;
        // studentAdd("Aniket Singh Bisht", "Xum94186");
        // studentDisplay("Aniket Singh Bisht", "Xum94186");
        // studentUpdate(3, "Aniket Singh Bisht", "123456789");
        // overDueAdd() ; 
        login(1);





    }
};