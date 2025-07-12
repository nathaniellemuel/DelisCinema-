package View.GUI.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.text.NumberFormat;
import java.util.Locale;
import java.sql.Connection;

import Model.Film;
import Utility.DBUtil;
import Controller.KursiController;
import Utility.SoundUtil;

public class PilihKursiFrame extends JFrame {
    private JFrame frameSebelumnya;
    private final Set<String> kursiTerpilih = new HashSet<>();
    private final JButton btnLanjut = new JButton("LANJUT");
    private JLabel lblHarga;
    private JLabel lblSubtotal;
    private final int hargaRegular;
    private final int hargaPremium;
    private int totalHarga = 0;
    private NumberFormat currencyFormatter;
    private StaffDashboard dashboardFrame;
    private Film film;
    private String studio;
    private String date;
    private String time;
    private KursiController kursiController;

    public PilihKursiFrame(JFrame frameSebelumnya, Film film, String studio, String date, String time, int hargaFromDB) {
        this(frameSebelumnya, film, studio, date, time, hargaFromDB,
                (frameSebelumnya instanceof StaffDashboard) ? (StaffDashboard)frameSebelumnya : null);
    }

    public PilihKursiFrame(JFrame frameSebelumnya, Film film, String studio, String date, String time, int hargaFromDB, StaffDashboard dashboardFrame) {
        this.frameSebelumnya = frameSebelumnya;
        this.film = film;
        this.studio = studio;
        this.date = date;
        this.time = time;
        this.dashboardFrame = dashboardFrame;

        // Setup price from database
        this.hargaRegular = hargaFromDB;
        this.hargaPremium = (int)(hargaRegular * 1.5);

        // Initialize KursiController
        Connection conn = DBUtil.getConnection();
        this.kursiController = new KursiController(conn);

        // Setup custom currency formatter without decimal places
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        this.currencyFormatter.setMaximumFractionDigits(0);

        // Get full screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        setTitle("Pilih Kursi");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Top Panel
        JPanel panelAtas = new JPanel(null);
        panelAtas.setBackground(Color.DARK_GRAY);
        panelAtas.setBounds(0, 0, width, 80);
        add(panelAtas);

        // Back button (icon)
        ImageIcon kembaliIcon = new ImageIcon(getClass().getResource("/kembali.png"));
        JButton btnBack = new JButton(kembaliIcon);
        btnBack.setBounds(15, 15, 50, 50);
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setOpaque(false);
        btnBack.addActionListener(e -> {
            SoundUtil.playSound("/exit.wav");
            frameSebelumnya.setVisible(true);
            dispose();
        });
        panelAtas.add(btnBack);

        JLabel lblFilm = new JLabel(film.getJudul().toUpperCase() + " / " + film.getDurasi() + " Minutes");
        lblFilm.setForeground(Color.WHITE);
        lblFilm.setFont(new Font("Arial", Font.BOLD, 24));
        lblFilm.setBounds(80, 10, width - 300, 30);
        panelAtas.add(lblFilm);

        JLabel lblJadwal = new JLabel("JADWAL : " + time);
        lblJadwal.setForeground(Color.WHITE);
        lblJadwal.setFont(new Font("Arial", Font.PLAIN, 18));
        lblJadwal.setBounds(80, 40, 400, 25);
        panelAtas.add(lblJadwal);

        String cleanStudio = studio.trim().toUpperCase();
        if (cleanStudio.startsWith("STUDIO")) {
            cleanStudio = cleanStudio.replaceFirst("STUDIO", "").trim();
        }

        JLabel lblStudio = new JLabel("STUDIO " + cleanStudio);
        lblStudio.setForeground(Color.WHITE);
        lblStudio.setFont(new Font("Arial", Font.BOLD, 40));
        lblStudio.setBounds(width - 200, 25, 200, 30);
        panelAtas.add(lblStudio);

        // Screen label
        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Arial", Font.BOLD, 55));
        lblScreen.setForeground(Color.GRAY);
        lblScreen.setBounds((width - 400) / 2, 100, 400, 50);
        add(lblScreen);

        // Seat legend
        JLabel regColor = new JLabel();
        regColor.setOpaque(true);
        regColor.setBackground(Color.LIGHT_GRAY);
        regColor.setBounds(100, 180, 40, 40);
        add(regColor);

        JLabel regLabel = new JLabel("REGULAR");
        regLabel.setBounds(150, 185, 150, 30);
        regLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        regLabel.setForeground(Color.GRAY);
        add(regLabel);

        JLabel premColor = new JLabel();
        premColor.setOpaque(true);
        premColor.setBackground(Color.BLACK);
        premColor.setBounds(100, 240, 40, 40);
        add(premColor);

        JLabel premLabel = new JLabel("PREMIUM");
        premLabel.setBounds(150, 245, 150, 30);
        premLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        premLabel.setForeground(Color.BLACK);
        add(premLabel);

        JLabel soldColor = new JLabel();
        soldColor.setOpaque(true);
        soldColor.setBackground(Color.ORANGE);
        soldColor.setBounds(100, 300, 40, 40);
        add(soldColor);

        JLabel soldLabel = new JLabel("SOLD");
        soldLabel.setBounds(150, 305, 150, 30);
        soldLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        soldLabel.setForeground(Color.BLACK);
        add(soldLabel);

        // Get booked seats from database
        int idJadwal = kursiController.getJadwalId(film.getJudul(), studio, date, time);
        Set<String> kursiTerpesan = kursiController.getBookedSeats(idJadwal);

        // Get studio capacity dynamically
        int kapasitas = kursiController.getStudioKapasitas(studio);
        int columns = 5;
        int rows = (int) Math.ceil((double) kapasitas / columns);

        // Generate row labels dynamically
        String[] baris = new String[rows];
        for (int i = 0; i < rows; i++) {
            baris[i] = String.valueOf((char) ('A' + i));
        }

        // Seat Panel
        JPanel kursiPanel = new JPanel(new GridLayout(rows, columns, 30, 30));
        kursiPanel.setOpaque(false);

        int kursiWidth = 80 * columns + 30 * (columns - 1);
        int kursiHeight = 80 * rows + 30 * (rows - 1);
        kursiPanel.setBounds((width - kursiWidth) / 2, 200, kursiWidth, kursiHeight);

        boolean hasPremium = kapasitas >= 10;
        int seatNumber = 1;
        for (int i = 0; i < rows; i++) {
            final int rowIdx = i;
            for (int j = 1; j <= columns; j++) {
                if (seatNumber > kapasitas) break;
                String kodeKursi = baris[rowIdx] + j;
                JButton btnKursi = new JButton(kodeKursi);

                btnKursi.setFocusPainted(false);
                btnKursi.setFont(new Font("Arial", Font.BOLD, 20));
                btnKursi.setPreferredSize(new Dimension(80, 80));

                if (kursiTerpesan.contains(kodeKursi)) {
                    btnKursi.setBackground(Color.ORANGE);
                    btnKursi.setForeground(Color.BLACK);
                    btnKursi.setEnabled(false);
                } else {
                    // Only make last row premium if hasPremium is true
                    boolean isPremium = hasPremium && (rowIdx == rows - 1);
                    if (isPremium) {
                        btnKursi.setBackground(Color.BLACK);
                        btnKursi.setForeground(Color.WHITE);
                    } else {
                        btnKursi.setBackground(Color.LIGHT_GRAY);
                        btnKursi.setForeground(Color.BLACK);
                    }
                    btnKursi.addActionListener(e -> {
                        SoundUtil.playSound("/click-sound.wav");
                        if (kursiTerpilih.contains(kodeKursi)) {
                            kursiTerpilih.remove(kodeKursi);
                            if (isPremium) {
                                totalHarga -= hargaPremium;
                                btnKursi.setBackground(Color.BLACK);
                            } else {
                                totalHarga -= hargaRegular;
                                btnKursi.setBackground(Color.LIGHT_GRAY);
                            }
                        } else {
                            kursiTerpilih.add(kodeKursi);
                            btnKursi.setBackground(new Color(220, 0, 0));
                            if (isPremium) {
                                totalHarga += hargaPremium;
                            } else {
                                totalHarga += hargaRegular;
                            }
                        }
                        lblHarga.setText(currencyFormatter.format(totalHarga));
                        updateSubtotalLabel();
                        updateLanjutButton();
                    });
                }
                kursiPanel.add(btnKursi);
                seatNumber++;
            }
        }
        add(kursiPanel);

        // Bottom Panel
        JPanel panelBawah = new JPanel(null);
        panelBawah.setBackground(Color.DARK_GRAY);
        panelBawah.setBounds(0, height - 140, width, 140);
        add(panelBawah);

        lblSubtotal = new JLabel("SUBTOTAL");
        lblSubtotal.setBounds(30, 30, 300, 30);
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblSubtotal.setForeground(Color.WHITE);
        panelBawah.add(lblSubtotal);

        lblHarga = new JLabel(currencyFormatter.format(0));
        lblHarga.setBounds(30, 70, 200, 30);
        lblHarga.setFont(new Font("Arial", Font.BOLD, 28));
        lblHarga.setForeground(Color.WHITE);
        panelBawah.add(lblHarga);

        btnLanjut.setBounds(width - 200, 30, 180, 60);
        btnLanjut.setFont(new Font("Arial", Font.BOLD, 24));
        btnLanjut.setBackground(Color.LIGHT_GRAY);
        btnLanjut.setForeground(Color.WHITE);
        btnLanjut.setFocusPainted(false);
        btnLanjut.setEnabled(false);

        btnLanjut.addActionListener(e -> {
            SoundUtil.playSound("/select-click.wav");
            StaffDashboard targetDashboard = dashboardFrame;
            if (targetDashboard == null && frameSebelumnya instanceof StaffDashboard) {
                targetDashboard = (StaffDashboard) frameSebelumnya;
            }
            // Do not block here, let SummaryFrame handle invalid idJadwal
            new SummaryFrame(this, film.getJudul(), studio, date, time, kursiTerpilih, totalHarga, targetDashboard, idJadwal);
        });
        panelBawah.add(btnLanjut);

        setVisible(true);
    }

    private void updateSubtotalLabel() {
        lblSubtotal.setText("SUBTOTAL (" + kursiTerpilih.size() + " Tickets)");
    }

    private void updateLanjutButton() {
        btnLanjut.setEnabled(!kursiTerpilih.isEmpty());
        btnLanjut.setBackground(!kursiTerpilih.isEmpty() ? new Color(220, 0, 0) : Color.LIGHT_GRAY);
    }
}
