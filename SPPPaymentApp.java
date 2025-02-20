import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class SPPPaymentApp {
    private JFrame frame;
    private DefaultTableModel model;
    private JTextField idField, nameField, classField, amountField;
    private JComboBox<String> majorBox, paymentBox;
    private Connection connection;

    public SPPPaymentApp() {
        connectToDatabase(); // Hubungkan ke database
        createUI();
        loadDataFromDatabase(); // Muat data dari database ke tabel
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
            JOptionPane.showMessageDialog(frame, "Gagal terhubung ke database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JTable table; // Deklarasi variabel anggota

    private void createUI() {
        frame = new JFrame("SPPPaymentApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel titleLabel = new JLabel("Sistem Pembayaran SPP SMKN 12 Bandung", JLabel.CENTER);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        frame.add(titleLabel, gbc);

        // Form Input
        gbc.gridwidth = 1;
        gbc.gridy++;
        addFormField("ID Siswa :", idField = new JTextField(10), gbc, 0, 1);
        addFormField("Nama Siswa :", nameField = new JTextField(10), gbc, 2, 1);
        addFormField("Kelas :", classField = new JTextField(10), gbc, 0, 2);

        gbc.gridx = 2;
        frame.add(new JLabel("Jurusan :"), gbc);
        gbc.gridx = 3;
        majorBox = new JComboBox<>(new String[]{"Pilih", "TKJ", "RPL", "Elektro"});
        frame.add(majorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(new JLabel("Pembayaran :"), gbc);
        gbc.gridx = 1;
        paymentBox = new JComboBox<>(new String[]{"Pilih", "Tunai", "Transfer"});
        frame.add(paymentBox, gbc);

        addFormField("Jumlah :", amountField = new JTextField(10), gbc, 2, 3);

        // Tombol
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Simpan", e -> saveData());
        addButton(buttonPanel, "Hapus", e -> deleteData());
        addButton(buttonPanel, "Ubah", e -> updateData());
        addButton(buttonPanel, "Cetak PDF", e -> printData());
        frame.add(buttonPanel, gbc);

        // Tabel
        gbc.gridy++;
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID Siswa", "Nama Siswa", "Kelas", "Jurusan", "Pembayaran", "Jumlah"});
        table = new JTable(model); // Inisialisasi tabel
        frame.add(new JScrollPane(table) {{
            setPreferredSize(new Dimension(750, 200));
        }}, gbc);

        frame.setVisible(true);
    }

    private void addFormField(String label, JComponent field, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        frame.add(new JLabel(label), gbc);
        gbc.gridx = x + 1;
        frame.add(field, gbc);
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void saveData() {
        String idSiswa = idField.getText();
        String namaSiswa = nameField.getText();
        String kelas = classField.getText();
        String jurusan = majorBox.getSelectedItem().toString();
        String metodePembayaran = paymentBox.getSelectedItem().toString();
        String jumlah = amountField.getText();

        if (idSiswa.isEmpty() || namaSiswa.isEmpty() || kelas.isEmpty() || jumlah.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO pembayaran (id_siswa, nama_siswa, kelas, jurusan, metode_pembayaran, jumlah) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, idSiswa);
            ps.setString(2, namaSiswa);
            ps.setString(3, kelas);
            ps.setString(4, jurusan);
            ps.setString(5, metodePembayaran);
            ps.setString(6, jumlah);
            ps.executeUpdate();

            // Tambahkan data ke tabel
            model.addRow(new Object[]{idSiswa, namaSiswa, kelas, jurusan, metodePembayaran, jumlah});
            clearFields();
            JOptionPane.showMessageDialog(frame, "Data berhasil disimpan!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void deleteData() {
    int selectedRow = table.getSelectedRow(); // Ambil baris yang dipilih
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(frame, "Pilih baris yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String idSiswa = model.getValueAt(selectedRow, 0).toString(); // Ambil ID Siswa dari baris yang dipilih
    try {
        String query = "DELETE FROM pembayaran WHERE id_siswa = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, idSiswa);
        ps.executeUpdate();

        model.removeRow(selectedRow); // Hapus baris dari tabel
        JOptionPane.showMessageDialog(frame, "Data berhasil dihapus!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void updateData() {
    int selectedRow = table.getSelectedRow(); // Get the selected row
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(frame, "Pilih baris yang akan diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String idSiswa = idField.getText();
    String namaSiswa = nameField.getText();
    String kelas = classField.getText();
    String jurusan = majorBox.getSelectedItem().toString();
    String metodePembayaran = paymentBox.getSelectedItem().toString();
    String jumlah = amountField.getText();

    try {
        String query = "UPDATE pembayaran SET nama_siswa = ?, kelas = ?, jurusan = ?, metode_pembayaran = ?, jumlah = ? WHERE id_siswa = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, namaSiswa);
        ps.setString(2, kelas);
        ps.setString(3, jurusan);
        ps.setString(4, metodePembayaran);
        ps.setString(5, jumlah);
        ps.setString(6, idSiswa);

        // Debug statements
        System.out.println("Executing update query: " + query);
        System.out.println("ID Siswa: " + idSiswa);
        System.out.println("Nama Siswa: " + namaSiswa);
        System.out.println("Kelas: " + kelas);
        System.out.println("Jurusan: " + jurusan);
        System.out.println("Metode Pembayaran: " + metodePembayaran);
        System.out.println("Jumlah: " + jumlah);

        int rowsAffected = ps.executeUpdate();
        System.out.println("Rows affected: " + rowsAffected);

        // Update data di tabel
        model.setValueAt(idSiswa, selectedRow, 0);
        model.setValueAt(namaSiswa, selectedRow, 1);
        model.setValueAt(kelas, selectedRow, 2);
        model.setValueAt(jurusan, selectedRow, 3);
        model.setValueAt(metodePembayaran, selectedRow, 4);
        model.setValueAt(jumlah, selectedRow, 5);
        clearFields();
        JOptionPane.showMessageDialog(frame, "Data berhasil diubah!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // Detail error
    }
}


    private void loadDataFromDatabase() {
        try {
            String query = "SELECT * FROM pembayaran";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id_siswa"),
                        rs.getString("nama_siswa"),
                        rs.getString("kelas"),
                        rs.getString("jurusan"),
                        rs.getString("metode_pembayaran"),
                        rs.getString("jumlah")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printData() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "Tidak ada data untuk dicetak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan sebagai PDF");
        if (fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Judul Laporan
            document.add(new Paragraph("Laporan Pembayaran SPP SMKN 1 Bandung\n\n"));

            // Tabel PDF
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Header Tabel
            for (int i = 0; i < model.getColumnCount(); i++) {
                pdfTable.addCell(model.getColumnName(i));
            }

            // Data Tabel
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pdfTable.addCell(model.getValueAt(i, j).toString());
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(frame, "PDF berhasil disimpan!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        classField.setText("");
        amountField.setText("");
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}

class LoginForm extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("admin".equals(usernameField.getText()) && "admin".equals(new String(passwordField.getPassword()))) {
            new SPPPaymentApp();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
