package View.GUI;

import Controller.TransaksiController;
import Controller.UserController;
import Model.Transaksi;
import Model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DateTimeFormatter waktuBeliFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm", new Locale("id", "ID"));
        DateTimeFormatter bulanFormat = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

        switch (mode) {
            case "Transaksi Terbaru":
                model.setColumnIdentifiers(new Object[]{"No", "Username", "Jadwal", "Waktu Beli", "Kursi", "Total Bayar"});
                int no1 = 1;
                for (Transaksi t : tc.getAllTransaksi()) {
                    String kursiGabung = String.join(", ", t.getKursiDipesan());
                    String username = new UserController().getUsernameById(t.getIdUser());
//                    String jadwalJam = t.getJam().toString();
                    String waktuBeli = t.getWaktuBeli().format(waktuBeliFormat);
                    String totalBayar = rupiahFormat.format(t.getTotalBayar());

                    model.addRow(new Object[]{
                            no1++,
                            username,
                            t.getDeskripsiJadwal() + " ",
                            waktuBeli,
                            kursiGabung,
                            totalBayar
                    });
                }
                break;

            case "Pendapatan per Bulan":
                model.setColumnIdentifiers(new Object[]{"No", "Bulan", "Pendapatan"});
                int no2 = 1;
                for (Map.Entry<String, Integer> entry : tc.getPendapatanPerBulan().entrySet()) {
                    String bulan = YearMonth.parse(entry.getKey()).format(bulanFormat); // pastikan key = 2025-05
                    String pendapatan = rupiahFormat.format(entry.getValue());
                    model.addRow(new Object[]{no2++, bulan, pendapatan});
                }
                break;

            case "Pendapatan per Studio":
                model.setColumnIdentifiers(new Object[]{"No", "Studio", "Pendapatan"});
                int no3 = 1;
                for (Map.Entry<String, Integer> entry : tc.getPendapatanPerStudio().entrySet()) {
                    String pendapatan = rupiahFormat.format(entry.getValue());
                    model.addRow(new Object[]{no3++, entry.getKey(), pendapatan});
                }
                break;

            case "Pendapatan per Film":
                model.setColumnIdentifiers(new Object[]{"No", "Film", "Pendapatan"});
                int no4 = 1;
                for (Map.Entry<String, Integer> entry : tc.getPendapatanPerFilm().entrySet()) {
                    String pendapatan = rupiahFormat.format(entry.getValue());
                    model.addRow(new Object[]{no4++, entry.getKey(), pendapatan});
                }
                break;

            case "Film Paling Banyak Ditonton":
                model.setColumnIdentifiers(new Object[]{"No", "Film", "Jumlah Penonton"});
                int no5 = 1;
                for (Map.Entry<String, Integer> entry : tc.getFilmPalingBanyakDitonton().entrySet()) {
                    model.addRow(new Object[]{no5++, entry.getKey(), entry.getValue()});
                }
                break;
        }
    }



}
