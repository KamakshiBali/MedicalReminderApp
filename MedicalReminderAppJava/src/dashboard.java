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
        new WelcomePage();
    }
}

// Welcome Page
class WelcomePage extends JFrame {
    public WelcomePage() {
        setTitle("Medical Reminder App");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome to Medical Reminder App", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JButton signupButton = new JButton("Signup");
        JButton loginButton = new JButton("Login");

        panel.add(welcomeLabel);
        panel.add(signupButton);
        panel.add(loginButton);

        add(panel);

        // Signup button action
        signupButton.addActionListener(e -> {
            new SignupForm();
            dispose();
        });

        // Login button action
        loginButton.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        setVisible(true);
    }
}

// Signup Form
class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField dobField;
    private JTextField emailField;
    private JTextField mobileField;
    private JComboBox<String> genderComboBox;

    public SignupForm() {
        setTitle("Signup");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));

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
        JButton backButton = new JButton("Back");

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
        panel.add(backButton);

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

        // Back Button Action
        backButton.addActionListener(e -> {
            new WelcomePage();
            dispose();
        });

        setVisible(true);
    }
}

// Login Form
class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(backButton);

        add(panel);

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

        // Back Button Action
        backButton.addActionListener(e -> {
            new WelcomePage();
            dispose();
        });

        setVisible(true);
    }
}
