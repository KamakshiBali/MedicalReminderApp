// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;

// public class connectDB {
//     // Database URL, username, and password
//     static String databaseName = "";
//     static String url = "jdbc:mysql://localhost:3306/" + databaseName; // Replace with your DB URL and name
//     static String username = "root"; // Replace with your MySQL username
//     static String password = "root123@123"; // Replace with your MySQL password
    
//     // Connection object
//     static Connection connection = null;
//     public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
//         try {
//             // Load the MySQL JDBC driver using the new approach
//             Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            
//             connection = DriverManager.getConnection(url, username, password);
//             System.out.println("Connection successful!");
//             PreparedStatement ps = connection.prepareStatement("INSERT INTO `medicenes`.`meds` (`medName`) VALUES ('kammeows12');");
//             int status = ps.executeUpdate();
//             if (status!=0){
//                 System.out.println("Database connected, record inserted");
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }


import java.sql.Connection;
import java.sql.DriverManager;
// import java.sql.PreparedStatement;
import java.sql.SQLException;

public class connectDB {
    // Database URL, username, and password
    private static final String DATABASE_NAME = "medicenes";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123@123";
    
    private static Connection connection = null;
    
    // Get connection instance
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connection successful!");
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException("Failed to connect to database");
            }
        }
        return connection;
    }
}