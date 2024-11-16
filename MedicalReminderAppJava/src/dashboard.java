import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class dashboard {
    static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; // Replace with your database URL
    static final String DB_USERNAME = "root"; // Replace with your MySQL username
    static final String DB_PASSWORD = "root123@123"; // Replace with your MySQL password

    public static void main(String[] args) {
        new AuthFrame();
    }
}

class AuthFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField dobField;
    private JTextField emailField;
    private JTextField mobileField;
    private JComboBox<String> genderComboBox;

    public AuthFrame() {
        setTitle("User Authentication");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create Signup Form
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel dobLabel = new JLabel("DOB (YYYY-MM-DD):");
        dobField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        JLabel mobileLabel = new JLabel("Mobile No:");
        mobileField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        genderComboBox = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});

        JButton signupButton = new JButton("Signup");
        JButton loginButton = new JButton("Login");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(mobileLabel);
        panel.add(mobileField);
        panel.add(genderLabel);
        panel.add(genderComboBox);
        panel.add(signupButton);
        panel.add(loginButton);

        add(panel);

        // Signup Button Action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String dob = dobField.getText();
                String email = emailField.getText();
                String mobile = mobileField.getText();
                String gender = (String) genderComboBox.getSelectedItem();

                if (username.isEmpty() || password.isEmpty() || dob.isEmpty() || email.isEmpty() || mobile.isEmpty() || "Select".equals(gender)) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection(dashboard.DB_URL, dashboard.DB_USERNAME, dashboard.DB_PASSWORD)) {
                    String query = "INSERT INTO user (username, password, dob, email, mobile, gender) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setDate(3, Date.valueOf(dob)); // Convert to SQL Date
                    ps.setString(4, email);
                    ps.setString(5, mobile);
                    ps.setString(6, gender);

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLIntegrityConstraintViolationException ex) {
                    JOptionPane.showMessageDialog(null, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection(dashboard.DB_URL, dashboard.DB_USERNAME, dashboard.DB_PASSWORD)) {
                    String query = "SELECT * FROM user WHERE username = ? AND password = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, username);
                    ps.setString(2, password);

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "User not found or incorrect password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
