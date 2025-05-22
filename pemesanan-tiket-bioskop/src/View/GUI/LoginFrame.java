package View.GUI;

import Controller.UserController;
import Model.User;

import javax.swing.*;
import java.awt.*;

 import View.GUI.AdminDashboard;
import View.GUI.Staff.StaffDashboard;

public class LoginFrame extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnSubmit;
    private UserController userController;




    public LoginFrame() {
        userController = new UserController();
        setTitle("Login - Delis Cinema");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Color merahDelis = Color.decode("#EB1C24");

        // Panel kiri (merah)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(merahDelis);
        leftPanel.setPreferredSize(new Dimension(300, 450));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/Logo.png"));
        Image img = rawIcon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
        ImageIcon logoImg = new ImageIcon(img);
        JLabel logo = new JLabel(logoImg);

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);



        JLabel welcomeText = new JLabel("SELAMAT DATANG KEMBALI!", SwingConstants.CENTER);
        welcomeText.setFont(new Font("Poppins", Font.BOLD, 14));
        welcomeText.setForeground(Color.WHITE);
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subText = new JLabel("<html><center>Silahkan login terlebih dahulu<br/>untuk verifikasi diri anda.</center></html>", SwingConstants.CENTER);
        subText.setFont(new Font("Poppins", Font.PLAIN, 12));
        subText.setForeground(Color.WHITE);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(welcomeText);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(subText);
        leftPanel.add(Box.createVerticalGlue());

        // Panel kanan (form login)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Poppins", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        rightPanel.add(lblLogin, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Username
        gbc.gridy = 1;
        rightPanel.add(new JLabel("Username"), gbc);
        gbc.gridy = 2;
        tfUsername = new JTextField();
        tfUsername.setPreferredSize(new Dimension(450, 30));
        tfUsername.setBackground(new Color(230, 230, 230));
        rightPanel.add(tfUsername, gbc);

        // Password
        gbc.gridy = 3;
        rightPanel.add(new JLabel("Password"), gbc);
        gbc.gridy = 4;
        pfPassword = new JPasswordField();
        pfPassword.setPreferredSize(new Dimension(450, 30));
        pfPassword.setBackground(new Color(230, 230, 230));
        rightPanel.add(pfPassword, gbc);

        // Submit Button
        gbc.gridy = 5;
        btnSubmit = new JButton("SUBMIT");
        btnSubmit.setBackground(merahDelis);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(450, 35));
        btnSubmit.setFocusPainted(false);
        rightPanel.add(btnSubmit, gbc);

        // Aksi Login
        btnSubmit.addActionListener(e -> {
            String username = tfUsername.getText().trim();
            String password = new String(pfPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Username dan password tidak boleh kosong.");
                return;
            }

            String passwordHashed = hashPassword(password); // Ganti dengan hash kalau ada

            User user = userController.login(username, passwordHashed);
            if (user != null) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Login berhasil sebagai " + user.getRole());
                dispose();

                if (user.getRole().equals("admin")) {
                     new AdminDashboard(user).setVisible(true);
                } else if (user.getRole().equals("staff")) {
                     new StaffDashboard(user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Login gagal. Periksa username/password.");
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

// komentar
