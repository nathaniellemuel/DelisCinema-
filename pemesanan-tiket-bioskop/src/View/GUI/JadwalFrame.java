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
import java.util.*;
import java.util.List;

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
            spTanggal.setValue(java.sql.Date.valueOf(java.time.LocalDate.now()));

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
                    // Film sudah punya jadwal
                    Set<Integer> studioIdsSudahDipakai = new HashSet<>();
                    for (Jadwal j : existing) {
                        Studio s = j.getStudio();
                        if (!studioIdsSudahDipakai.contains(s.getIdStudio())) {
                            cbStudio.addItem(s);
                            studioIdsSudahDipakai.add(s.getIdStudio());
                        }
                    }
                    for (Studio s : studiosKosong) {
                        cbStudio.addItem(s);
                    }
                    cbStudio.setEnabled(true);
                    spTanggal.setEnabled(true);

                    // Set harga sesuai studio yang dipilih
                    cbStudio.addActionListener(studioEvent -> {
                        Studio selectedStudio = (Studio) cbStudio.getSelectedItem();
                        boolean isStudioSudahDipakai = false;
                        for (Jadwal j : existing) {
                            if (j.getStudio().getIdStudio() == selectedStudio.getIdStudio()) {
                                isStudioSudahDipakai = true;
                                tfHarga.setText(String.valueOf(j.getHarga()));
                                break;
                            }
                        }
                        tfHarga.setEnabled(!isStudioSudahDipakai);
                        if (!isStudioSudahDipakai && !studiosKosong.isEmpty()) {
                            tfHarga.setText(""); // Kosongkan jika studio baru
                        }
                    });

                    // Trigger sekali untuk set harga awal
                    if (cbStudio.getItemCount() > 0) {
                        cbStudio.setSelectedIndex(0);
                    }
                } else {
                    // Film belum punya jadwal sama sekali
                    for (Studio s : studiosKosong) {
                        cbStudio.addItem(s);
                    }
                    cbStudio.setEnabled(true);
                    spTanggal.setEnabled(true);
                    tfHarga.setEnabled(true);
                    tfHarga.setText("");
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
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
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
        lblJudul.setFont(new Font("Arial", Font.BOLD, 18));
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblJudul.setHorizontalAlignment(SwingConstants.LEFT);

        // Panel jam tayang
        JPanel jamPanel = new JPanel();
        jamPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        jamPanel.setOpaque(false);


        for (Jadwal jadwal : group) {
            String jamStr = jadwal.getJam().toString().substring(0, 5);
            JButton btnJam = new JButton(jamStr){
                @Override
                protected void paintComponent(Graphics g) {
                    if (!isEnabled()) {
                        super.paintComponent(g);
                        g.setColor(Color.RED);
                        FontMetrics fm = g.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g.drawString(getText(), x, y);
                    } else {
                        super.paintComponent(g);
                    }
                }
            };
            btnJam.setFont(new Font("Arial", Font.PLAIN, 14));
            btnJam.setPreferredSize(new Dimension(60, 25));
            btnJam.setBackground(Color.WHITE);
            btnJam.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            btnJam.setToolTipText("Klik untuk mengedit atau hapus jam tayang ini");

            if (jadwal.getTanggal().isBefore(java.time.LocalDate.now())) {
                btnJam.setEnabled(false);
                btnJam.setBackground(new Color(180, 180, 180)); // darker gray
                btnJam.setForeground(Color.RED);
                btnJam.setToolTipText("Showtime: " + jadwal.getTanggal().toString());
            } else {
                // Menambahkan aksi saat tombol jam diklik
                btnJam.addActionListener(e -> {
                JPopupMenu popupMenu = new JPopupMenu();

                // Opsi Edit Jam
                JMenuItem editItem = new JMenuItem("Edit Jam & Tanggal");
                editItem.addActionListener(editEvent -> {
                    List<Integer> singleJadwalId = Collections.singletonList(jadwal.getIdJadwal());


                    JDialog editDialog = new JDialog(this, "Edit Date & Time", true);
                    editDialog.setSize(350, 200);
                    editDialog.setLocationRelativeTo(this);

                    JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                    editPanel.add(new JLabel("Date:"));
                    JSpinner spEditDate = new JSpinner(new SpinnerDateModel(java.sql.Date.valueOf(jadwal.getTanggal()), null, null, java.util.Calendar.DAY_OF_MONTH));
                    spEditDate.setEditor(new JSpinner.DateEditor(spEditDate, "yyyy-MM-dd"));
                    editPanel.add(spEditDate);

                    editPanel.add(new JLabel("Time:"));
                    JSpinner spEditTime = new JSpinner(new SpinnerDateModel(java.sql.Time.valueOf(jadwal.getJam()), null, null, java.util.Calendar.MINUTE));
                    spEditTime.setEditor(new JSpinner.DateEditor(spEditTime, "HH:mm"));
                    editPanel.add(spEditTime);

                    int result = JOptionPane.showConfirmDialog(editDialog, editPanel, "Edit Date & Time", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        if (new JadwalController().hasTransaksiForJadwalIds(singleJadwalId)) {
                            JOptionPane.showMessageDialog(null, "Jadwal ini sudah memiliki riwayat transaksi dan tidak dapat diubah.");
                            return;
                        }
                        LocalDate newDate = ((java.util.Date) spEditDate.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                        LocalTime newTime = ((java.util.Date) spEditTime.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

                        // Check for duplicate (date, time, studio, film) except current id_jadwal
                        boolean isDuplicate = false;
                        for (Jadwal other : group) {
                            if (other.getIdJadwal() != jadwal.getIdJadwal()
                                    && other.getTanggal().equals(newDate)
                                    && other.getJam().equals(newTime)) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (isDuplicate) {
                            JOptionPane.showMessageDialog(editDialog, "A showtime with this date and time already exists for this film and studio.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        // Update in DB
                        boolean success = new JadwalController().updateJadwalJamTanggal(jadwal.getIdJadwal(), newDate, newTime);
                        if (success) {
                            JOptionPane.showMessageDialog(editDialog, "Showtime updated.");
                            editDialog.dispose();
                            dispose();
                            new JadwalFrame(currentUser).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(editDialog, "Failed to update showtime.");
                        }
                    }
                });

                // Opsi Hapus Jam
                JMenuItem deleteItem = new JMenuItem("Hapus Jam");
                deleteItem.addActionListener(deleteEvent -> {
                    List<Integer> singleJadwalId = Collections.singletonList(jadwal.getIdJadwal());

                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Yakin ingin menghapus jadwal jam " + jamStr + "?",
                            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (new JadwalController().hasTransaksiForJadwalIds(singleJadwalId)) {
                            JOptionPane.showMessageDialog(null, "Jadwal ini sudah memiliki riwayat transaksi dan tidak dapat dihapus.");
                            return;
                        }
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
            }

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
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblStudio = new JLabel("STUDIO " + sample.getStudio().getNamaStudio().toUpperCase());
        lblStudio.setFont(new Font("Arial", Font.BOLD, 14));

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
            List<Jadwal> allJadwal = new JadwalController().getJadwalByFilmId(sample.getFilm().getIdFilm());
            List<Integer> jadwalIds = new ArrayList<>();
            for (Jadwal j : allJadwal) {
                if (j.getStudio().getIdStudio() == sample.getStudio().getIdStudio()) {
                    jadwalIds.add(j.getIdJadwal());
                }
            }
            if (new JadwalController().hasTransaksiForJadwalIds(jadwalIds)) {
                JOptionPane.showMessageDialog(this, "This schedule already has transaction history and cannot be edited or deleted.");
                return;
            }

            JDialog editDialog = new JDialog(this, "Edit Studio dan Harga", true);
            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo(this);
            editDialog.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Studio dropdown: current studio + available studios (no duplicates)
            JLabel lblEditStudio = new JLabel("Pilih Studio:");
            JComboBox<Studio> cbEditStudio = new JComboBox<>();
            JadwalController jadwalController = new JadwalController();
            Set<Integer> studioIds = new HashSet<>();
            // Add current studio first
            cbEditStudio.addItem(sample.getStudio());
            studioIds.add(sample.getStudio().getIdStudio());
            // Add available studios
            for (Studio studio : jadwalController.getStudiosBelumAdaJadwal()) {
                if (!studioIds.contains(studio.getIdStudio())) {
                    cbEditStudio.addItem(studio);
                    studioIds.add(studio.getIdStudio());
                }
            }

            JLabel lblEditHarga = new JLabel("Harga:");
            JTextField tfEditHarga = new JTextField(String.valueOf(sample.getHarga()));

            gbc.gridx = 0; gbc.gridy = 0; editDialog.add(lblEditStudio, gbc);
            gbc.gridx = 1; editDialog.add(cbEditStudio, gbc);
            gbc.gridx = 0; gbc.gridy++; editDialog.add(lblEditHarga, gbc);
            gbc.gridx = 1; editDialog.add(tfEditHarga, gbc);

            JButton btnSaveEdit = new JButton("Simpan");
            gbc.gridx = 0; gbc.gridy++; editDialog.add(btnSaveEdit, gbc);

            btnSaveEdit.addActionListener(saveEvent -> {
                Studio selectedStudio = (Studio) cbEditStudio.getSelectedItem();
                int newHarga = Integer.parseInt(tfEditHarga.getText());
                // Use updateJadwalStudioHargaAll to update all schedules for this film & studio
                boolean success = new JadwalController().updateJadwalStudioHargaAll(
                        sample.getFilm().getIdFilm(),
                        sample.getStudio().getIdStudio(),
                        selectedStudio.getIdStudio(),
                        newHarga
                );
                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Data jadwal berhasil diupdate.");
                    editDialog.dispose();
                    dispose();
                    new JadwalFrame(currentUser).setVisible(true);
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
            List<Jadwal> allJadwal = new JadwalController().getJadwalByFilmId(sample.getFilm().getIdFilm());
            List<Integer> jadwalIds = new ArrayList<>();
            for (Jadwal j : allJadwal) {
                if (j.getStudio().getIdStudio() == sample.getStudio().getIdStudio()) {
                    jadwalIds.add(j.getIdJadwal());
                }
            }
            if (new JadwalController().hasTransaksiForJadwalIds(jadwalIds)) {
                JOptionPane.showMessageDialog(this, "This schedule already has transaction history and cannot be edited or deleted.");
                return;
            }
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
