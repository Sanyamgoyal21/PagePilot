package Backend ;
import java.util.* ; 
import java.sql.*;


class Admin
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


    public   void readLibrarian() 
    {
        String sql = "SELECT * FROM librarian";
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












    

    public static void main(String[] args) 
    {
        // insertLibrarian("Prantik Sanki", 2, "678910");
        // readAdmin();



        // readLibrarian(); 
        // deleteLibrarian(2) ; 
        // readLibrarian(); 


        // insertStudent("Sanyam Goyel", 1, "12345");
        // insertStudent("Prantik Sanki", 2, "678910");
        // readStudent();
        

    }


}