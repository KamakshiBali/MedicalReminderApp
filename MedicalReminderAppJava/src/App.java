public class App {
    public static void main(String[] args) {
        Medicene medicineDAO = new Medicene();
        
        // Insert a medicine
        boolean success = medicineDAO.insertMedicine("meowmeow");
        if (success) {
            System.out.println("Medicine inserted successfully!");
        } else {
            System.out.println("Failed to insert medicine!");
        }
        
        // Don't forget to close the connection when done
        // connectDB.closeConnection();
    }
}