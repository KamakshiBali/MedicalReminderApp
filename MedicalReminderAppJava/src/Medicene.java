import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Medicene {
    public boolean insertMedicine(String medName) {
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO meds (medName) VALUES (?)")) {
            
            ps.setString(1, medName);
            int status = ps.executeUpdate();
            return status != 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}