package View.GUI;

import Controller.FilmController;
import Controller.UserController;
import Model.Film;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FilmFrame extends JFrame {
    private FilmController filmController;
    private User currentUser;
    private UserController userController;

    public FilmFrame(User user) {
        this.currentUser = user;
        this.filmController = new FilmController();
        this.userController = new UserController();


        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Manajemen Film - Delis Cinema");
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

        // Konten utama
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.WHITE);
        JButton btnTambah = new JButton("Tambah Film");
        btnTambah.setBackground(Color.GREEN.darker());
        btnTambah.setForeground(Color.WHITE);
        topPanel.add(btnTambah);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        btnTambah.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Tambah Film", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField tfJudul = new JTextField(20);
            JTextField tfDurasi = new JTextField(20);
            JTextField tfGenre = new JTextField(20);
            JComboBox<String> cbStatus = new JComboBox<>(new String[]{"tayang", "selesai"});

            gbc.gridx = 0; gbc.gridy = 0;
            dialog.add(new JLabel("Judul:"), gbc);
            gbc.gridx = 1;
            dialog.add(tfJudul, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            dialog.add(new JLabel("Durasi (menit):"), gbc);
            gbc.gridx = 1;
            dialog.add(tfDurasi, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            dialog.add(new JLabel("Genre:"), gbc);
            gbc.gridx = 1;
            dialog.add(tfGenre, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            dialog.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            dialog.add(cbStatus, gbc);

            JButton btnSimpan = new JButton("Simpan");
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            dialog.add(btnSimpan, gbc);

            btnSimpan.addActionListener(ev -> {
                try {
                    String judul = tfJudul.getText().trim();
                    int durasi = Integer.parseInt(tfDurasi.getText().trim());
                    String genre = tfGenre.getText().trim();
                    String status = cbStatus.getSelectedItem().toString();

                    if (judul.isEmpty() || genre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!");
                        return;
                    }

                    Film newFilm = new Film(0, judul, durasi, genre, status);
                    if (filmController.tambahFilm(newFilm)) {
                        JOptionPane.showMessageDialog(dialog, "Film berhasil ditambahkan!");
                        dialog.dispose();
                        dispose();
                        new FilmFrame(currentUser).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Gagal menambahkan film.");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Durasi harus berupa angka!");
                }
            });

            dialog.setVisible(true);
        });

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        List<Film> films = filmController.getAllFilm();
        for (Film film : films) {
            cardPanel.add(createFilmCard(film));
            cardPanel.add(Box.createVerticalStrut(10));
        }

        btnDashboard.addActionListener(e -> {
            dispose();
            new AdminDashboard(currentUser).setVisible(true);
        });

        btnJadwal.addActionListener(e -> {
            dispose();
             new JadwalFrame(currentUser).setVisible(true);
        });

        btnLaporan.addActionListener(e -> {
            dispose();
            new LaporanFrame(user).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            userController.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFilmCard(Film film) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        card.setBackground(Color.LIGHT_GRAY);
        card.setMaximumSize(new Dimension(700, 80));
        card.setPreferredSize(new Dimension(700, 80));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel lblJudul = new JLabel(film.getJudul());
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel lblInfo = new JLabel(film.getGenre() + " | " + film.getDurasi() + " Minutes");
        left.add(lblJudul);
        left.add(lblInfo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        ImageIcon editIcon = new ImageIcon(getClass().getResource("/edit.png"));
        editIcon = new ImageIcon(editIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        JButton btnEdit = new JButton("Edit", editIcon);
        btnEdit.setBackground(Color.YELLOW);
        btnEdit.addActionListener(e -> showEditDialog(film));

        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("/delete.png"));
        deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        JButton btnDelete = new JButton("Delete", deleteIcon);
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);

        btnDelete.addActionListener(e -> {
            Film latestFilm = filmController.getAllFilm().stream()
                    .filter(f -> f.getIdFilm() == film.getIdFilm())
                    .findFirst()
                    .orElse(film);

            int konfirmasi = JOptionPane.showConfirmDialog(
                    this,
                    "Yakin ingin menghapus film \"" + latestFilm.getJudul() + "\"?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );

            if (konfirmasi == JOptionPane.YES_OPTION) {
                if (filmController.hapusFilm(film.getIdFilm())) {
                    JOptionPane.showMessageDialog(this, "Film berhasil dihapus.");
                    dispose();
                    new FilmFrame(currentUser).setVisible(true); // reload
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus film.");
                }
            }
        });

        right.add(btnEdit);
        right.add(btnDelete);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
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

    private void showEditDialog(Film film) {
        JDialog dialog = new JDialog(this, "Edit Film", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        // Komponen
        JTextField tfJudul = new JTextField(film.getJudul());
        JTextField tfDurasi = new JTextField(String.valueOf(film.getDurasi()));
        JTextField tfGenre = new JTextField(film.getGenre());
        String[] statusOptions = {"tayang", "selesai"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setSelectedItem(film.getStatus());

        JButton btnSimpan = new JButton("Simpan");

        // Layout
        dialog.add(new JLabel("Judul:"));
        dialog.add(tfJudul);
        dialog.add(new JLabel("Durasi (menit):"));
        dialog.add(tfDurasi);
        dialog.add(new JLabel("Genre:"));
        dialog.add(tfGenre);
        dialog.add(new JLabel("Status:"));
        dialog.add(cbStatus);
        dialog.add(new JLabel());
        dialog.add(btnSimpan);

        // Aksi Simpan
        btnSimpan.addActionListener(e -> {
            try {
                film.setJudul(tfJudul.getText());
                film.setDurasi(Integer.parseInt(tfDurasi.getText()));
                film.setGenre(tfGenre.getText());
                film.setStatus(cbStatus.getSelectedItem().toString());

                if (filmController.updateFilm(film)) {
                    JOptionPane.showMessageDialog(this, "Film berhasil diperbarui.");
                    dialog.dispose();
                    dispose();
                    new FilmFrame(currentUser).setVisible(true); // refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui film.");
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
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

}
