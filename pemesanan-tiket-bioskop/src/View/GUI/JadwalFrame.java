package View.GUI;

import Controller.FilmController;
import Controller.UserController;
import Controller.JadwalController;
import Model.Film;
import Model.Studio;
import Model.User;
import Model.Jadwal;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JadwalFrame extends JFrame {
    private FilmController filmController;
    private User currentUser;
    private UserController userController;

    public JadwalFrame(User user) {
        this.currentUser = user;
        this.filmController = new FilmController();
        this.userController = new UserController();


        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Manajemen Jadwal - Delis Cinema");
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
        JButton btnTambah = new JButton("Tambah Jadwal");
        btnTambah.setBackground(Color.GREEN.darker());
        btnTambah.setForeground(Color.WHITE);
        topPanel.add(btnTambah);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        btnTambah.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Tambah Tayangan", true);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Dropdown film
            JLabel lblFilm = new JLabel("Pilih Film:");
            JComboBox<Film> cbFilm = new JComboBox<>();
            for (Film f : filmController.getFilmTayang()) {
                cbFilm.addItem(f);
            }

            // Components lainnya
            JLabel lblStudio = new JLabel("Studio:");
            JComboBox<Studio> cbStudio = new JComboBox<>();

            JLabel lblTanggal = new JLabel("Tanggal:");
            JSpinner spTanggal = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor de = new JSpinner.DateEditor(spTanggal, "yyyy-MM-dd");
            spTanggal.setEditor(de);

            JLabel lblJam = new JLabel("Jam:");
            JSpinner spJam = new JSpinner(new SpinnerDateModel());
            spJam.setEditor(new JSpinner.DateEditor(spJam, "HH:mm"));

            JLabel lblHarga = new JLabel("Harga:");
            JTextField tfHarga = new JTextField();

            JButton btnSimpan = new JButton("Simpan");

            // Dynamic logic saat film dipilih
            cbFilm.addActionListener(filmEvent -> {
                Film selected = (Film) cbFilm.getSelectedItem();
                List<Jadwal> existing = new JadwalController().getJadwalByFilmId(selected.getIdFilm());
                cbStudio.removeAllItems();

                JadwalController jadwalController = new JadwalController();
                List<Studio> studiosKosong = jadwalController.getStudiosBelumAdaJadwal();

                if (!existing.isEmpty()) {
                    if (!studiosKosong.isEmpty()) {
                        // Film sudah punya jadwal → bisa tambah ke studio lain yang belum terisi
                        // TAPI juga bisa tambah jam di studio yang sudah pernah dipakai
                        Set<Integer> studioIdsSudahDipakai = new HashSet<>();
                        for (Jadwal j : existing) {
                            Studio s = j.getStudio();
                            if (!studioIdsSudahDipakai.contains(s.getIdStudio())) {
                                cbStudio.addItem(s);
                                studioIdsSudahDipakai.add(s.getIdStudio());
                            }
                        }

                        // Tambahkan juga studio kosong
                        for (Studio s : studiosKosong) {
                            cbStudio.addItem(s);
                        }

                        cbStudio.setEnabled(true);
                        spTanggal.setEnabled(false); // Tanggal dikunci
                        tfHarga.setEnabled(true);
                        Jadwal ref = existing.get(0);
                        spTanggal.setValue(java.sql.Date.valueOf(ref.getTanggal()));
                        tfHarga.setText(String.valueOf(ref.getHarga()));
                    } else {
                        // Film sudah punya jadwal dan semua studio sudah terisi → hanya bisa tambah jam di studio yang sama
                        Set<Integer> studioIdsSudahDipakai = new HashSet<>();
                        for (Jadwal j : existing) {
                            Studio s = j.getStudio();
                            if (!studioIdsSudahDipakai.contains(s.getIdStudio())) {
                                cbStudio.addItem(s);
                                studioIdsSudahDipakai.add(s.getIdStudio());
                            }
                        }

                        cbStudio.setEnabled(true);
                        spTanggal.setValue(java.sql.Date.valueOf(existing.get(0).getTanggal()));
                        spTanggal.setEnabled(false);
                        tfHarga.setText(String.valueOf(existing.get(0).getHarga()));
                        tfHarga.setEnabled(false);
                    }
                } else {
                    // Film belum punya jadwal sama sekali
                    for (Studio s : studiosKosong) {
                        cbStudio.addItem(s);
                    }
                    cbStudio.setEnabled(true);
                    spTanggal.setEnabled(true);
                    tfHarga.setEnabled(true);
                }

            });



            // Layouting
            gbc.gridx = 0; gbc.gridy = 0; dialog.add(lblFilm, gbc);
            gbc.gridx = 1; dialog.add(cbFilm, gbc);
            gbc.gridx = 0; gbc.gridy++; dialog.add(lblStudio, gbc);
            gbc.gridx = 1; dialog.add(cbStudio, gbc);
            gbc.gridx = 0; gbc.gridy++; dialog.add(lblTanggal, gbc);
            gbc.gridx = 1; dialog.add(spTanggal, gbc);
            gbc.gridx = 0; gbc.gridy++; dialog.add(lblJam, gbc);
            gbc.gridx = 1; dialog.add(spJam, gbc);
            gbc.gridx = 0; gbc.gridy++; dialog.add(lblHarga, gbc);
            gbc.gridx = 1; dialog.add(tfHarga, gbc);
            gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy++; dialog.add(btnSimpan, gbc);

            btnSimpan.addActionListener(saveEvent -> {
                // Validasi dan simpan ke DB
                Film selectedFilm = (Film) cbFilm.getSelectedItem();
                Studio selectedStudio = (Studio) cbStudio.getSelectedItem();
                LocalDate tanggal = ((java.util.Date) spTanggal.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalTime jam = ((java.util.Date) spJam.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                int harga = Integer.parseInt(tfHarga.getText());

                Jadwal newJadwal = new Jadwal(0, selectedFilm.getIdFilm(), selectedStudio.getIdStudio(), tanggal, jam, harga);
                if (new JadwalController().tambahJadwal(newJadwal)) {
                    JOptionPane.showMessageDialog(dialog, "Berhasil tambah jadwal!");
                    dialog.dispose();
                    dispose(); // Refresh frame
                    new JadwalFrame(currentUser).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gagal tambah jadwal!");
                }
            });

            dialog.setVisible(true);
        });


        JPanel panelList = new JPanel(); //
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));
        panelList.setBackground(Color.WHITE);


        JScrollPane scrollPane = new JScrollPane(panelList);

        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JadwalController jadwalController = new JadwalController();
        HashMap<String, List<Jadwal>> groupedJadwal = (HashMap<String, List<Jadwal>>) jadwalController.getGroupedJadwal();


        for (List<Jadwal> group : groupedJadwal.values()) {
            JPanel card = createGroupedJadwalCard(group);
            panelList.add(card);
            panelList.add(Box.createRigidArea(new Dimension(0, 10)));
        }



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

    private JPanel createGroupedJadwalCard(List<Jadwal> group) {
        Jadwal sample = group.get(0);

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        card.setBackground(Color.LIGHT_GRAY);
        card.setMaximumSize(new Dimension(700, 120));
        card.setPreferredSize(new Dimension(700, 120));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // BoxLayout vertikal
        left.setOpaque(false);

        // Label judul film
        JLabel lblJudul = new JLabel(sample.getFilm().getJudul());
        lblJudul.setFont(new Font("Poppins", Font.BOLD, 18));
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblJudul.setHorizontalAlignment(SwingConstants.LEFT);

        // Panel jam tayang
        JPanel jamPanel = new JPanel();
        jamPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        jamPanel.setOpaque(false);


        for (Jadwal jadwal : group) {
            String jamStr = jadwal.getJam().toString().substring(0, 5);
            JButton btnJam = new JButton(jamStr);
            btnJam.setFont(new Font("Poppins", Font.PLAIN, 14));
            btnJam.setPreferredSize(new Dimension(60, 25));
            btnJam.setBackground(Color.WHITE);
            btnJam.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            btnJam.setToolTipText("Klik untuk mengedit atau hapus jam tayang ini");

            // Menambahkan aksi saat tombol jam diklik
            btnJam.addActionListener(e -> {
                JPopupMenu popupMenu = new JPopupMenu();

                // Opsi Edit Jam
                JMenuItem editItem = new JMenuItem("Edit Jam");
                editItem.addActionListener(editEvent -> {
                    JDialog editDialog = new JDialog(this, "Edit Jam Tayang", true);
                    editDialog.setSize(300, 200);
                    editDialog.setLocationRelativeTo(this);

                    // Komponen untuk memilih jam baru
                    JSpinner spEditJam = new JSpinner(new SpinnerDateModel());
                    spEditJam.setEditor(new JSpinner.DateEditor(spEditJam, "HH:mm"));

                    JPanel editPanel = new JPanel(new FlowLayout());
                    editPanel.add(new JLabel("Pilih Jam:"));
                    editPanel.add(spEditJam);

                    int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Jam Tayang", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        LocalTime newJam = ((java.util.Date) spEditJam.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                        // Update jam tayang ke database
                        boolean success = new JadwalController().updateJadwalJam(jadwal.getIdJadwal(), newJam);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Jam tayang berhasil diupdate.");
                            dispose();
                            new JadwalFrame(currentUser).setVisible(true); // Refresh tampilan
                        } else {
                            JOptionPane.showMessageDialog(null, "Gagal update jam tayang.");
                        }
                    }
                });

                // Opsi Hapus Jam
                JMenuItem deleteItem = new JMenuItem("Hapus Jam");
                deleteItem.addActionListener(deleteEvent -> {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Yakin ingin menghapus jadwal jam " + jamStr + "?",
                            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = new JadwalController().hapusJadwalPerJam(jadwal.getIdJadwal());
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Jam tayang berhasil dihapus.");
                            dispose();
                            new JadwalFrame(currentUser).setVisible(true); // Refresh tampilan
                        } else {
                            JOptionPane.showMessageDialog(null, "Gagal menghapus jam tayang.");
                        }
                    }
                });

                // Menambahkan item ke popup menu
                popupMenu.add(editItem);
                popupMenu.add(deleteItem);

                // Menampilkan popup menu di lokasi tombol yang diklik
                popupMenu.show(btnJam, 0, btnJam.getHeight());
            });

            jamPanel.add(btnJam);
        }




        // Menambahkan label judul dan jam tayang ke dalam panel kiri
        left.add(lblJudul);
        left.add(jamPanel); // Menambahkan jam tayang di bawah judul film

        // Panel kanan untuk detail info
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel lblInfo = new JLabel(sample.getFilm().getGenre() + " / " +
                sample.getFilm().getDurasi() + " Minutes");
        lblInfo.setFont(new Font("Poppins", Font.PLAIN, 14));

        JLabel lblStudio = new JLabel("STUDIO " + sample.getStudio().getNamaStudio().toUpperCase());
        lblStudio.setFont(new Font("Poppins", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);

        ImageIcon editIcon = new ImageIcon(getClass().getResource("/edit.png"));
        editIcon = new ImageIcon(editIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        JButton btnEdit = new JButton(editIcon);
        btnEdit.setToolTipText("Edit");
        btnEdit.setBackground(Color.YELLOW);
        btnEdit.setOpaque(true);
        btnEdit.setBorderPainted(false);
        btnEdit.setEnabled(true);
        btnEdit.setForeground(Color.BLACK);
        btnEdit.addActionListener(e -> {
            JDialog editDialog = new JDialog(this, "Edit Studio dan Harga", true);
            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo(this);
            editDialog.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Edit studio dropdown
            JLabel lblEditStudio = new JLabel("Pilih Studio:");
            JComboBox<Studio> cbEditStudio = new JComboBox<>();
            JadwalController jadwalController = new JadwalController();
            for (Studio studio : jadwalController.getStudiosBelumAdaJadwal()) {
                cbEditStudio.addItem(studio);
            }

            // Edit price field
            JLabel lblEditHarga = new JLabel("Harga:");
            JTextField tfEditHarga = new JTextField(String.valueOf(sample.getHarga()));

            // Add components to dialog
            gbc.gridx = 0; gbc.gridy = 0; editDialog.add(lblEditStudio, gbc);
            gbc.gridx = 1; editDialog.add(cbEditStudio, gbc);
            gbc.gridx = 0; gbc.gridy++; editDialog.add(lblEditHarga, gbc);
            gbc.gridx = 1; editDialog.add(tfEditHarga, gbc);

            JButton btnSaveEdit = new JButton("Simpan");
            gbc.gridx = 0; gbc.gridy++; editDialog.add(btnSaveEdit, gbc);

            btnSaveEdit.addActionListener(saveEvent -> {
                Studio selectedStudio = (Studio) cbEditStudio.getSelectedItem();
                int newHarga = Integer.parseInt(tfEditHarga.getText());
                boolean success = new JadwalController().updateJadwalStudioHarga(sample.getIdJadwal(), selectedStudio.getIdStudio(), newHarga);
                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Data jadwal berhasil diupdate.");
                    editDialog.dispose();
                    dispose();
                    new JadwalFrame(currentUser).setVisible(true); // Refresh tampilan
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Gagal mengupdate data.");
                }
            });

            editDialog.setVisible(true);
        });


        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("/delete.png"));
        deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        JButton btnDelete = new JButton(deleteIcon);
        btnDelete.setToolTipText("Delete");
        btnDelete.setBackground(Color.RED);
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(false);
        btnDelete.setEnabled(true);
        btnDelete.setForeground(Color.WHITE);

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showOptionDialog(
                    null,
                    "Hapus seluruh jadwal film di studio ini atau hanya jam ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Hapus Semua", "Batal"},
                    "Batal"
            );

            if (confirm == 2 || confirm == JOptionPane.CLOSED_OPTION) return;

            JadwalController jc = new JadwalController();
            boolean success = false;

            if (confirm == 0) { // Hapus Semua
                success = jc.hapusSemuaJadwalFilmDiStudio(sample.getIdFilm(), sample.getIdStudio());
            }

            if (success) {
                JOptionPane.showMessageDialog(null, "Jadwal berhasil dihapus.");
                dispose();
                new JadwalFrame(currentUser).setVisible(true); // refresh tampilan
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus jadwal.");
            }
        });


        right.add(lblInfo);
        right.add(lblStudio);
        right.add(buttonPanel);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }



}
