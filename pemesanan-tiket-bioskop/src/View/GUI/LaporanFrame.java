package View.GUI;

import Controller.TransaksiController;
import Controller.UserController;
import Model.Transaksi;
import Model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LaporanFrame extends JFrame {
    private User currentUser;
    private UserController userController;

    public LaporanFrame(User user) {
        this.currentUser = user;
        this.userController = new UserController();


        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Laporan - Delis Cinema");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.decode("#EB1C24"));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/Logo.png"));
        Image scaledImage = rawIcon.getImage().getScaledInstance(120, 50, Image.SCALE_SMOOTH);
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

        btnDashboard.addActionListener(e -> {
            dispose();
            new AdminDashboard(currentUser).setVisible(true);
        });

        btnFilm.addActionListener(e -> {
            dispose();
            new FilmFrame(currentUser).setVisible(true);
        });

        btnJadwal.addActionListener(e -> {
            dispose();
            new JadwalFrame(currentUser).setVisible(true);
        });

        btnLaporan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Kamu sudah di halaman Laporan.");
        });

        btnLogout.addActionListener(e -> {
            userController.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
//        add(contentPanel, BorderLayout.CENTER);

        // Konten utama
        JPanel contentPanel = new JPanel(new BorderLayout());

        String[] opsiSort = {"Transaksi Terbaru", "Pendapatan per Bulan", "Pendapatan per Studio", "Pendapatan per Film", "Film Paling Banyak Ditonton"};
        JComboBox<String> filterCombo = new JComboBox<>(opsiSort);
        filterCombo.setFont(new Font("Poppins", Font.PLAIN, 14));
        filterCombo.setPreferredSize(new Dimension(300, 30));
        contentPanel.add(filterCombo, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        TransaksiController tc = new TransaksiController();
        updateTableData("Transaksi Terbaru", tableModel, tc);

        filterCombo.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            updateTableData(selected, tableModel, tc);
        });


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
        btn.setFont(new Font("Poppins", Font.PLAIN, 16));
        return btn;
    }

    private void updateTableData(String mode, DefaultTableModel model, TransaksiController tc) {
        model.setRowCount(0);
        model.setColumnCount(0);

        switch (mode) {
            case "Transaksi Terbaru":
                model.setColumnIdentifiers(new Object[]{"ID", "User", "Jadwal", "Waktu Beli", "Kursi", "Total Bayar"});
                for (Transaksi t : tc.getAllTransaksi()) {
                    model.addRow(new Object[]{
                            t.getIdTransaksi(), t.getIdUser(), t.getIdJadwal(),
                            t.getWaktuBeli(), t.getTotalKursi(), t.getTotalBayar()
                    });
                }
                break;

            case "Pendapatan per Bulan":
                model.setColumnIdentifiers(new Object[]{"Bulan", "Pendapatan"});
                tc.getPendapatanPerBulan().forEach((k, v) -> model.addRow(new Object[]{k, v}));
                break;

            case "Pendapatan per Studio":
                model.setColumnIdentifiers(new Object[]{"Studio", "Pendapatan"});
                tc.getPendapatanPerStudio().forEach((k, v) -> model.addRow(new Object[]{k, v}));
                break;

            case "Pendapatan per Film":
                model.setColumnIdentifiers(new Object[]{"Film", "Pendapatan"});
                tc.getPendapatanPerFilm().forEach((k, v) -> model.addRow(new Object[]{k, v}));
                break;

            case "Film Paling Banyak Ditonton":
                model.setColumnIdentifiers(new Object[]{"Film", "Jumlah Penonton"});
                tc.getFilmPalingBanyakDitonton().forEach((k, v) -> model.addRow(new Object[]{k, v}));
                break;
        }
    }


}
