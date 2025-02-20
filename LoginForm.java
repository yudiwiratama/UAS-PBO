import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;

    public LoginForm() {
        setTitle("Login Admin");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Komponen
        addComponent(new JLabel("Username:"), gbc, 0, 0);
        usernameField = new JTextField(15);
        addComponent(usernameField, gbc, 1, 0);

        addComponent(new JLabel("Password:"), gbc, 0, 1);
        passwordField = new JPasswordField(15);
        addComponent(passwordField, gbc, 1, 1);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        addComponent(loginButton, gbc, 0, 2, 2);

        setVisible(true);

        // Koneksi ke database
        connectToDatabase();
    }

    private void addComponent(JComponent comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    private void addComponent(JComponent comp, GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridwidth = width;
        addComponent(comp, gbc, x, y);
        gbc.gridwidth = 1;
    }

    private void connectToDatabase() {
        try {
            // Ganti dengan detail database 
            String url = "jdbc:mysql://172.17.0.2:3306/spp_payment";
            String username = "root"; // Ganti dengan username MySQL 
            String password = "P@ssw0rd"; // Ganti dengan password MySQL 

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Terhubung ke database!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkCredentials(String username, String password) {
        try {
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // Jika ada hasil, berarti kredensial valid
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (checkCredentials(username, password)) {
            new SPPPaymentApp();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginForm(); // Jalankan form login
    }
}
