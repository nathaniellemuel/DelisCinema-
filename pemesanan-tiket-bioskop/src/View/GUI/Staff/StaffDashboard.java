package View.GUI.Staff;

import Controller.JadwalController;
import Model.Film;
import Model.Jadwal;
import Model.Studio;
import Model.User;
import View.GUI.LoginFrame;
import View.GUI.Staff.PilihKursiFrame;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;


public class StaffDashboard extends JFrame {
    private JLabel dateTimeLabel;
    private JLabel selectedDateLabel, selectedFilmLabel, selectedStudioLabel, selectedTimeLabel;
    private JButton selectedTimeButton = null; // Tombol jam yang dipilih
    private JButton pilihKursi; // ubah ini jadi variabel global supaya bisa diakses
    private Jadwal selectedJadwal = null; // Tambahkan variabel untuk menyimpan jadwal yang dipilih


    public StaffDashboard(User user) {
        if (user == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Dashboard Staff - Delis Cinema");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Color merahDelis = Color.decode("#EB1C24");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP PANEL: DATE + LOCATION
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel locationLabel = new JLabel("KRAMAT JATI SQUARE");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(dateTimeLabel);
        topPanel.add(locationLabel);
        add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL: MOVIE LIST
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(); //box jadwal
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        outerPanel.add(Box.createVerticalStrut(20)); // spasi atas
        outerPanel.add(centerPanel);
        outerPanel.add(Box.createVerticalGlue()); // dorong ke tengah

        JScrollPane scrollPane = new JScrollPane(outerPanel);
        add(scrollPane, BorderLayout.CENTER);

        List<List<Jadwal>> sortedJadwalList = new ArrayList<>(JadwalController.getGroupedJadwal().values());

        // Urutkan berdasarkan nomor studio (dari objek Studio)
        sortedJadwalList.sort(Comparator.comparingInt(jList -> {
            if (jList.isEmpty()) return Integer.MAX_VALUE;
            return jList.get(0).getStudio().getIdStudio(); // pastikan ada method getIdStudio()
        }));

        for (List<Jadwal> jadwals : sortedJadwalList) {
            if (!jadwals.isEmpty()) {
                Jadwal j = jadwals.get(0);
                Film f = j.getFilm();
                Studio s = j.getStudio();

                addMovieBlock(centerPanel, f.getJudul(), f.getGenre(), f.getDurasi(), s.getNamaStudio(), jadwals);
            }
        }


        // BOTTOM PANEL: SUMMARY + BUTTON
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        Font summaryFont = new Font("Arial", Font.PLAIN, 20);

        // Panel untuk summary dengan GridBagLayout
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("SUMMARY"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spasi antar komponen
        gbc.anchor = GridBagConstraints.WEST; // Menempatkan komponen di kiri

        // Menambahkan label dan selectedLabel ke panel
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(summaryFont);
        summaryPanel.add(lblDate, gbc);

        gbc.gridx = 1;
        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(summaryFont);
        summaryPanel.add(selectedDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblFilm = new JLabel("Film:");
        lblFilm.setFont(summaryFont);
        summaryPanel.add(lblFilm, gbc);

        gbc.gridx = 1;
        selectedFilmLabel = new JLabel();
        selectedFilmLabel.setFont(summaryFont);
        summaryPanel.add(selectedFilmLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblStudio = new JLabel("Studio:");
        lblStudio.setFont(summaryFont);
        summaryPanel.add(lblStudio, gbc);

        gbc.gridx = 1;
        selectedStudioLabel = new JLabel();
        selectedStudioLabel.setFont(summaryFont);
        summaryPanel.add(selectedStudioLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblTime = new JLabel("Time:");
        lblTime.setFont(summaryFont);
        summaryPanel.add(lblTime, gbc);

        gbc.gridx = 1;
        selectedTimeLabel = new JLabel();
        selectedTimeLabel.setFont(summaryFont);
        summaryPanel.add(selectedTimeLabel, gbc);

        // Panel summaryPanel diposisikan di kiri dalam bottomPanel
        bottomPanel.add(summaryPanel, BorderLayout.WEST);

        // Tombol Pilih Kursi
        pilihKursi = new JButton("PILIH KURSI");
        pilihKursi.setPreferredSize(new Dimension(180, 60));
        pilihKursi.setBackground(Color.DARK_GRAY);
        pilihKursi.setForeground(Color.WHITE);
        pilihKursi.setFont(new Font("Arial", Font.BOLD, 18)); // Font tombol lebih besar
        pilihKursi.setEnabled(false);

        pilihKursi.addActionListener(e -> {
            String film = selectedFilmLabel.getText();
            String studio = selectedStudioLabel.getText();
            String date = selectedDateLabel.getText();
            String time = selectedTimeLabel.getText();

            if (film.isEmpty() || studio.isEmpty() || date.isEmpty() || time.isEmpty() || selectedJadwal == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih jadwal terlebih dahulu!");
            } else {
                // Ambil harga dari jadwal yang dipilih
                int harga = selectedJadwal.getHarga();

                // Buat PilihKursiFrame dengan harga dari jadwal yang dipilih
                PilihKursiFrame pilihKursi = new PilihKursiFrame(this, film, studio, date, time, harga);
                this.setVisible(false);
            }
        });

        // Letakkan tombol di kanan bawah
        bottomPanel.add(pilihKursi, BorderLayout.EAST);

        // Tambahkan bottomPanel ke dalam layout utama
        add(bottomPanel, BorderLayout.SOUTH);

        // Real-time clock
        startClock();

        setVisible(true);
    }

    private void addMovieBlock(JPanel parent, String title, String genre, int durasi, String studio, List<Jadwal> jadwals){
        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        moviePanel.setBackground(new Color(230, 230, 230));

        JPanel topInfo = new JPanel(new BorderLayout());
        topInfo.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel genreLabel = new JLabel(genre.toUpperCase() + " / " + durasi + " Minutes");
        genreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        genreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topInfo.add(titleLabel, BorderLayout.WEST);
        topInfo.add(genreLabel, BorderLayout.EAST);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.setOpaque(false);

        // Urutkan jadwal berdasarkan jam
        jadwals.sort((j1, j2) -> j1.getJam().compareTo(j2.getJam()));

        for (Jadwal jadwal : jadwals) {
            String jamFormatted = jadwal.getJam().format(DateTimeFormatter.ofPattern("HH:mm"));
            JButton timeButton = new JButton(jamFormatted);
            timeButton.setPreferredSize(new Dimension(80, 30));
            timeButton.setFont(new Font("Monospaced", Font.BOLD, 14));

            timeButton.addActionListener(e -> {
                // Jika tombol yang sama diklik dua kali
                if (selectedTimeButton == timeButton) {
                    // Reset tombol ke default
                    timeButton.setBackground(null);
                    timeButton.setForeground(Color.BLACK);
                    selectedTimeButton = null;
                    selectedJadwal = null; // Reset jadwal yang dipilih

                    // Reset summary label
                    selectedDateLabel.setText("");
                    selectedFilmLabel.setText("");
                    selectedStudioLabel.setText("");
                    selectedTimeLabel.setText("");

                    // Reset warna tombol PILIH KURSI
                    pilihKursi.setBackground(Color.DARK_GRAY);
                    pilihKursi.setForeground(Color.WHITE);
                    pilihKursi.setEnabled(false);
                } else {
                    // Jika tombol berbeda diklik

                    // Reset tombol sebelumnya
                    if (selectedTimeButton != null) {
                        selectedTimeButton.setBackground(null);
                        selectedTimeButton.setForeground(Color.BLACK);
                    }

                    // Set tombol saat ini jadi aktif (merah)
                    timeButton.setBackground(Color.RED);
                    timeButton.setForeground(Color.WHITE);
                    selectedTimeButton = timeButton;
                    selectedJadwal = jadwal; // Simpan jadwal yang dipilih

                    // Update summary
                    selectedDateLabel.setText(jadwal.getTanggal().toString());
                    selectedFilmLabel.setText(title);
                    selectedStudioLabel.setText(studio);
                    selectedTimeLabel.setText(jamFormatted);

                    // Ubah tombol PILIH KURSI jadi merah
                    pilihKursi.setBackground(Color.RED);
                    pilihKursi.setForeground(Color.WHITE);
                    pilihKursi.setEnabled(true);
                }
            });

            timePanel.add(timeButton);
        }

        JPanel bottomInfo = new JPanel(new BorderLayout());
        bottomInfo.setOpaque(false);
        JLabel studioLabel = new JLabel(studio.toUpperCase());
        studioLabel.setFont(new Font("Arial", Font.BOLD, 20));
        studioLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomInfo.add(studioLabel, BorderLayout.EAST);

        moviePanel.add(topInfo);
        moviePanel.add(Box.createVerticalStrut(5));
        moviePanel.add(timePanel);
        moviePanel.add(Box.createVerticalStrut(5));
        moviePanel.add(bottomInfo);

        parent.add(moviePanel);
        parent.add(Box.createVerticalStrut(10));
    }

    private void startClock() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    String time = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss").format(new Date());
                    dateTimeLabel.setText(time);
                });
            }
        }, 0, 1000);
    }
}