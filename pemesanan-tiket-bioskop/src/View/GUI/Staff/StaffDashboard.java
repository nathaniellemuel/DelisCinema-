package View.GUI.Staff;

import Controller.JadwalController;
import Model.*;
import Utility.SoundUtil; // Add this import
import View.GUI.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class StaffDashboard extends JFrame {
    private JLabel dateTimeLabel;
    private JLabel selectedDateLabel, selectedFilmLabel, selectedStudioLabel, selectedTimeLabel;
    private JButton selectedTimeButton = null; // Tombol jam yang dipilih
    private JButton pilihKursi; // ubah ini jadi variabel global supaya bisa diakses
    private Film selectedFilmObj;
    private Jadwal selectedJadwal = null; // Tambahkan variabel untuk menyimpan jadwal yang dipilih
    private List<JButton> allTimeButtons = new ArrayList<>(); // Store all time buttons for updating
    private List<Jadwal> allJadwals = new ArrayList<>(); // Store all jadwals corresponding to buttons
    private Set<JButton> disabledButtons = new HashSet<>(); // Track which buttons are disabled
    private User user;


    public StaffDashboard(User user) {
        this.user = user;
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

        // TOP PANEL: DATE + LOCATION + MUTE BUTTON
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel locationLabel = new JLabel("KRAMAT JATI SQUARE");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(dateTimeLabel);
        topPanel.add(locationLabel);

        // Mute/Unmute button
        JButton muteButton = new JButton();
        muteButton.setFocusable(false);
        muteButton.setPreferredSize(new Dimension(48, 48));
        muteButton.setMaximumSize(new Dimension(48, 48));
        muteButton.setBorderPainted(false);
        muteButton.setContentAreaFilled(false);

        // Set initial icon/text
        updateMuteButtonIcon(muteButton);

        muteButton.addActionListener(e -> {
            SoundUtil.setMuted(!SoundUtil.isMuted());
            updateMuteButtonIcon(muteButton);
        });

        // Add mute button to the top right
        JPanel topRightPanel = new JPanel();
        topRightPanel.setOpaque(false);
        topRightPanel.setLayout(new BorderLayout());
        topRightPanel.add(muteButton, BorderLayout.EAST);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(topPanel, BorderLayout.WEST);
        topRow.add(topRightPanel, BorderLayout.EAST);

        add(topRow, BorderLayout.NORTH);

        // CENTER PANEL: MOVIE LIST
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(); //box jadwal
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        outerPanel.add(Box.createVerticalStrut(20)); // top spacing

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        centerPanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE)); // set max width
        centerWrapper.add(centerPanel);

        outerPanel.add(centerWrapper);

        JScrollPane scrollPane = new JScrollPane(outerPanel);
        add(scrollPane, BorderLayout.CENTER);

        List<Jadwal> jadwalHariIni = new JadwalController().getJadwalHariIni();

        // Group jadwal hari ini berdasarkan film dan studio (mirip groupedJadwal)
        Map<String, List<Jadwal>> grouped = new LinkedHashMap<>();
        for (Jadwal j : jadwalHariIni) {
            String key = j.getFilm().getIdFilm() + "-" + j.getStudio().getIdStudio();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(j);
        }
        List<List<Jadwal>> sortedJadwalList = new ArrayList<>(grouped.values());

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

            // Play sound when button is clicked
            SoundUtil.playSound("/select-click.wav");

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
                PilihKursiFrame pilihKursi = new PilihKursiFrame(this, selectedFilmObj, studio, date, time, harga);
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
        moviePanel.setMaximumSize(new Dimension(700, 130));
        moviePanel.setPreferredSize(new Dimension(700, 130));

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

            // Store button and jadwal for later reference
            allTimeButtons.add(timeButton);
            allJadwals.add(jadwal);

            timeButton.addActionListener(e -> {
                selectedFilmObj = jadwal.getFilm();
                // Check if button is disabled based on time without changing appearance
                if (disabledButtons.contains(timeButton)) {
                    return;
                }

                // Play sound when time button is clicked
                SoundUtil.playSound("/click-sound.wav");

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

                    // Update button states based on current time
                    updateButtonStates();
                });
            }
        }, 0, 1000);
    }

    private void updateButtonStates() {
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < allTimeButtons.size(); i++) {
            JButton button = allTimeButtons.get(i);
            Jadwal jadwal = allJadwals.get(i);

            // Get film start time as LocalDateTime (today's date)
            LocalDate today = LocalDate.now();
            LocalDateTime filmStartDateTime = LocalDateTime.of(today, jadwal.getJam());

            // Calculate film end time (start time + duration - 1 minute)
            LocalDateTime filmEndDateTime = filmStartDateTime.plusMinutes(jadwal.getFilm().getDurasi() - 1);

            // Check if current time has passed the film end time
            boolean shouldDisable = now.isAfter(filmEndDateTime);

            if (shouldDisable) {
                // Add to disabled set without changing button appearance
                disabledButtons.add(button);

                // If this was the selected button, clear the selection
                if (selectedTimeButton == button) {
                    // Reset selection
                    button.setBackground(null);
                    button.setForeground(Color.BLACK);
                    selectedTimeButton = null;
                    selectedJadwal = null;

                    // Clear summary
                    selectedDateLabel.setText("");
                    selectedFilmLabel.setText("");
                    selectedStudioLabel.setText("");
                    selectedTimeLabel.setText("");

                    // Reset PILIH KURSI button
                    pilihKursi.setBackground(Color.DARK_GRAY);
                    pilihKursi.setForeground(Color.WHITE);
                    pilihKursi.setEnabled(false);
                }
            } else {
                // Remove from disabled set if time hasn't passed
                disabledButtons.remove(button);
            }
        }
    }

    private void updateMuteButtonIcon(JButton muteButton) {
        if (SoundUtil.isMuted()) {
            // Use a simple Unicode icon or text for muted
            muteButton.setText("\uD83D\uDD07"); // Speaker with X
            muteButton.setToolTipText("Unmute");
        } else {
            muteButton.setText("\uD83D\uDD08"); // Speaker
            muteButton.setToolTipText("Mute");
        }
    }

    public User getUser() {
        return user;
    }


}
