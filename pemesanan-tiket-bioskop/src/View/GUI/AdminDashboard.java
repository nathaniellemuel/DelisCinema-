package View.GUI;

import Controller.UserController;
import Model.User;
import Controller.FilmController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboard extends JFrame {

    private User user;
    private UserController userController;
    private FilmController filmController;
    private JLabel lblDateTime;
    private Timer timer;

    public AdminDashboard(User user) {
        this.user = user;
        this.userController = new UserController();
        this.filmController = new FilmController();

        if (this.user == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Dashboard Admin - Delis Cinema");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.decode("#EB1C24"));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/Logo.png"));
        Image scaledImage = rawIcon.getImage().getScaledInstance(120, 50, Image.SCALE_SMOOTH); // ukuran bisa disesuaikan
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logo = new JLabel(scaledIcon);

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(logo);

        JButton btnDashboard = createSidebarButtonWithIcon("Dashboard", "/home.png");
        JButton btnFilm = createSidebarButtonWithIcon("Film", "/film.png");
        JButton btnJadwal = createSidebarButtonWithIcon("Jadwal", "/jadwal.png");
        JButton btnLaporan = createSidebarButtonWithIcon("Laporan", "/laporan.png");
        JButton btnLogout = createSidebarButtonWithIcon("Logout", "/logout.png");


        sidebar.add(btnDashboard);
        sidebar.add(btnFilm);
        sidebar.add(btnJadwal);
        sidebar.add(btnLaporan);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        JPanel contentPanel = createContentPanel();

        // Action Sidebar
        btnDashboard.addActionListener(e -> {
            // Sekarang stay di halaman dashboard
            JOptionPane.showMessageDialog(this, "Kamu sudah di halaman Dashboard.");
        });

        btnFilm.addActionListener(e -> {
            dispose();
            new FilmFrame(user).setVisible(true);
        });

        btnJadwal.addActionListener(e -> {
            dispose();
            new JadwalFrame(user).setVisible(true);
        });

        btnLaporan.addActionListener(e -> {
            dispose();
            new LaporanFrame(user).setVisible(true);
        });

        btnLogout.addActionListener((ActionEvent e) -> {
            userController.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        startRealTimeClock();
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#EB1C24"));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }

    private JButton createSidebarButtonWithIcon(String text, String iconPath) {
        ImageIcon icon = null;
        try {
            java.net.URL resource = getClass().getResource(iconPath);
            if (resource != null) {
                Image img = new ImageIcon(resource).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } else {
                System.err.println("Icon tidak ditemukan: " + iconPath);
                icon = new ImageIcon(); // fallback icon kosong
            }
        } catch (Exception e) {
            e.printStackTrace();
            icon = new ImageIcon(); // fallback juga
        }

        JButton btn = new JButton(text, icon);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#EB1C24"));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10)); // Beri sedikit jarak antar komponen
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding untuk keseluruhan content panel

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Transparan agar background contentPanel terlihat

        JLabel lblWelcome = new JLabel("<html>Selamat Datang, <b>" + (user != null ? user.getUsername() : "Admin") + "</b></html>");
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 22)); // Sesuaikan font
        headerPanel.add(lblWelcome, BorderLayout.WEST);

        JPanel dateTimeLocationPanel = new JPanel();
        dateTimeLocationPanel.setOpaque(false);
        dateTimeLocationPanel.setLayout(new BoxLayout(dateTimeLocationPanel, BoxLayout.Y_AXIS));

        lblDateTime = new JLabel(); // Akan diupdate oleh Timer
        lblDateTime.setFont(new Font("Arial", Font.BOLD, 16));
        lblDateTime.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblLocation = new JLabel("KRAMAT JATI SQUARE");
        lblLocation.setFont(new Font("Arial", Font.PLAIN, 14));
        lblLocation.setForeground(Color.DARK_GRAY);
        lblLocation.setAlignmentX(Component.RIGHT_ALIGNMENT);

        dateTimeLocationPanel.add(lblDateTime);
        dateTimeLocationPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spasi kecil
        dateTimeLocationPanel.add(lblLocation);

        headerPanel.add(dateTimeLocationPanel, BorderLayout.EAST);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Dashboard Info Panel (untuk kartu)
        JPanel dashboardInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // FlowLayout untuk kartu
        dashboardInfoPanel.setOpaque(false);

        // Data untuk kartu
        int totalStaff = userController.getUserCount();
        int totalFilmTayang = filmController.getFilmTayang().size();

        JPanel cardStaff = createDataCard("TOTAL STAFF", String.valueOf(totalStaff), new Color(220, 237, 255), new Color(0, 82, 159));
        JPanel cardFilmTayang = createDataCard("FILM TAYANG", String.valueOf(totalFilmTayang), new Color(215, 255, 229), new Color(0, 103, 71));

        dashboardInfoPanel.add(cardStaff);
        dashboardInfoPanel.add(cardFilmTayang);

        // Menambahkan panel kartu ke tengah, tapi kita bisa bungkus lagi agar tidak memenuhi seluruh area tengah
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); //Wrapper untuk menengahkan kartu jika ruang lebih
        centerWrapper.setOpaque(false);
        centerWrapper.add(dashboardInfoPanel);


        contentPanel.add(centerWrapper, BorderLayout.CENTER);

        return contentPanel;
    }

    private void startRealTimeClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
        timer = new Timer(1000, e -> {
            lblDateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    private JPanel createDataCard(String title, String value, Color bgColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout(10,10));
        card.setPreferredSize(new Dimension(275, 129)); // Ukuran seperti di gambar Anda
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(textColor.darker(), 1), // Border luar
                new EmptyBorder(15, 15, 15, 15) // Padding dalam
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(textColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 48)); // Ukuran besar untuk angka
        lblValue.setForeground(textColor);
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

}
