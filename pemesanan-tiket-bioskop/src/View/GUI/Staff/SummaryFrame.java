package View.GUI.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.text.NumberFormat;
import java.util.Locale;
import Utility.SoundUtil;

public class SummaryFrame extends JFrame {
    private JFrame frameSebelumnya;
    private StaffDashboard dashboardFrame;
    private int idUser;

    public SummaryFrame(JFrame frameSebelumnya, String film, String studio, String date, String time,
                        Set<String> kursiTerpilih, int totalHarga, StaffDashboard dashboardFrame, int idJadwal) {
        this.frameSebelumnya = frameSebelumnya;
        this.dashboardFrame = dashboardFrame;
        this.idUser = dashboardFrame.getUser().getIdUser();

        // Setup currency formatter without decimal places
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currencyFormatter.setMaximumFractionDigits(0);

        // Get full screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        setTitle("Summary");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Top Panel with X button and "SUMMARY" label
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.setBounds(0, 0, width, 100);
        add(topPanel);

        // Back button (icon)
        ImageIcon kembaliIcon = new ImageIcon(getClass().getResource("/kembali.png"));
        JButton btnBack = new JButton(kembaliIcon);
        btnBack.setBounds(24, 24, 52, 52);
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setOpaque(false);
        btnBack.addActionListener(e -> {
            SoundUtil.playSound("/exit.wav");
            frameSebelumnya.setVisible(true);
            dispose();
        });
        topPanel.add(btnBack);

        // SUMMARY label
        JLabel lblSummary = new JLabel("SUMMARY");
        lblSummary.setForeground(Color.WHITE);
        lblSummary.setFont(new Font("Arial", Font.BOLD, 40));
        lblSummary.setBounds(100, 25, 300, 50);
        topPanel.add(lblSummary);

        // Content panel
        JPanel contentPanel = new JPanel(null);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(0, 100, width, height - 200);
        add(contentPanel);

        // Details - each on its own line with large spacing
        int startY = 40;
        int lineHeight = 80;

        // Film title
        JLabel lblJudulFilmTitle = new JLabel("JUDUL FILM");
        lblJudulFilmTitle.setBounds(24, startY, 350, 50);
        lblJudulFilmTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblJudulFilmTitle);

        JLabel lblJudulFilmValue = new JLabel(": " + film);
        lblJudulFilmValue.setBounds(430, startY, width - 500, 50);
        lblJudulFilmValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblJudulFilmValue);

        // Date
        JLabel lblTanggalTitle = new JLabel("TANGGAL");
        lblTanggalTitle.setBounds(24, startY + lineHeight, 350, 50);
        lblTanggalTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblTanggalTitle);

        JLabel lblTanggalValue = new JLabel(": " + date);
        lblTanggalValue.setBounds(430, startY + lineHeight, width - 500, 50);
        lblTanggalValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblTanggalValue);

        // Schedule
        JLabel lblJadwalTitle = new JLabel("JADWAL");
        lblJadwalTitle.setBounds(24, startY + lineHeight * 2, 350, 50);
        lblJadwalTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblJadwalTitle);

        JLabel lblJadwalValue = new JLabel(": " + time);
        lblJadwalValue.setBounds(430, startY + lineHeight * 2, width - 500, 50);
        lblJadwalValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblJadwalValue);

        // Seats
        JLabel lblKursiTitle = new JLabel("KURSI");
        lblKursiTitle.setBounds(24, startY + lineHeight * 3, 350, 50);
        lblKursiTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblKursiTitle);

        // Convert set to comma-separated string
        StringBuilder kursiStr = new StringBuilder();
        for (String kursi : kursiTerpilih) {
            if (kursiStr.length() > 0) {
                kursiStr.append(", ");
            }
            kursiStr.append(kursi);
        }

        JLabel lblKursiValue = new JLabel(": " + kursiStr.toString());
        lblKursiValue.setBounds(430, startY + lineHeight * 3, width - 500, 50);
        lblKursiValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblKursiValue);

        // Studio
        JLabel lblStudioTitle = new JLabel("STUDIO");
        lblStudioTitle.setBounds(24, startY + lineHeight * 4, 350, 50);
        lblStudioTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblStudioTitle);

        JLabel lblStudioValue = new JLabel(": " + studio);
        lblStudioValue.setBounds(430, startY + lineHeight * 4, width - 500, 50);
        lblStudioValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblStudioValue);

        // Total
        JLabel lblTotalTitle = new JLabel("TOTAL");
        lblTotalTitle.setBounds(24, startY + lineHeight * 5, 350, 50);
        lblTotalTitle.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblTotalTitle);

        String totalDisplay = String.format(": %s (%d tickets)",
                currencyFormatter.format(totalHarga).replace("Rp", "Rp."),
                kursiTerpilih.size());

        JLabel lblTotalValue = new JLabel(totalDisplay);
        lblTotalValue.setBounds(430, startY + lineHeight * 5, width - 500, 50);
        lblTotalValue.setFont(new Font("Arial", Font.BOLD, 40));
        contentPanel.add(lblTotalValue);

        // Bottom panel with CETAK TIKET and SELESAI buttons
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBounds(0, height - 100, width, 100);
        add(bottomPanel);

        // Create CETAK TIKET button (red background)
        JButton btnCetakTiket = new JButton("CETAK TIKET");
        btnCetakTiket.setBounds(width / 2, 0, width / 2, 90);
        btnCetakTiket.setFont(new Font("Arial", Font.BOLD, 36));
        btnCetakTiket.setBackground(new Color(220, 0, 0)); // Red background
        btnCetakTiket.setForeground(Color.WHITE);
        btnCetakTiket.setFocusPainted(false);
        btnCetakTiket.setBorderPainted(false);

        // Create SELESAI button (initially disabled)
        JButton btnSelesai = new JButton("SELESAI");
        btnSelesai.setBounds(0, 0, width / 2, 90);
        btnSelesai.setFont(new Font("Arial", Font.BOLD, 36));
        btnSelesai.setBackground(Color.GRAY); // Initially gray (disabled appearance)
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.setBorderPainted(false);
        btnSelesai.setEnabled(false); // Initially disabled

        // Add action listener to CETAK TIKET button
        btnCetakTiket.addActionListener(e -> {
            SoundUtil.playSound("/click-sound.wav");
            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Tiket berhasil dicetak!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            // Enable SELESAI button and change its color to red
            btnSelesai.setEnabled(true);
            btnSelesai.setBackground(new Color(220, 0, 0)); // Red background

            // Optionally disable CETAK TIKET button after printing
            btnCetakTiket.setEnabled(false);
            btnCetakTiket.setBackground(Color.GRAY);
        });

        // Inside SummaryFrame constructor, replace the existing btnSelesai action listener
        btnSelesai.addActionListener(e -> {
            SoundUtil.playSound("/selesai-click.wav");
            Controller.TransaksiController transaksiController = new Controller.TransaksiController();
            boolean success = transaksiController.prosesTransaksi(
                    idUser,
                    idJadwal,
                    kursiTerpilih,
                    totalHarga
            );
            if (success) {
                dashboardFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save seat bookings!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add buttons to bottom panel
        bottomPanel.add(btnSelesai);
        bottomPanel.add(btnCetakTiket);

        setVisible(true);
    }
}
