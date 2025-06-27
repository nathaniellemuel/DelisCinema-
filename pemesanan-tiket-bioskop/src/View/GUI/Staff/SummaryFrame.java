package View.GUI.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.NumberFormat;

import Utility.SoundUtil;
import java.awt.print.*;
import java.util.List;

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
        List<String> kursiList = new ArrayList<>(kursiTerpilih);
        Collections.sort(kursiList);

        StringBuilder kursiStr = new StringBuilder();
        for (String kursi : kursiList) {
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
            printTicket(film, studio, date, time, kursiTerpilih, totalHarga);
            JOptionPane.showMessageDialog(this,
                    "Tiket berhasil dicetak!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            btnSelesai.setEnabled(true);
            btnSelesai.setBackground(new Color(220, 0, 0));
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

    private void printTicket(String film, String studio, String date, String time, Set<String> kursiTerpilih, int totalHarga) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Delis Cinema Ticket");

        // Set custom paper size: 45mm x 150mm (1 point = 1/72 inch)
        double width = 45 / 25.4 * 72;   // 45mm to points
        double height = 150 / 25.4 * 72; // 150mm to points
        Paper paper = new Paper();
        paper.setSize(width, height);
        paper.setImageableArea(5, 5, width - 10, height - 10); // small margin

        PageFormat pf = job.defaultPage();
        pf.setPaper(paper);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            int x = (int) pageFormat.getImageableX();
            int y = (int) pageFormat.getImageableY();

            int maxTextWidth = (int) pageFormat.getImageableWidth() - 20;

            try {
                ImageIcon logoIcon = new ImageIcon(getClass().getResource("/LogoBlack.jpg"));
                Image logo = logoIcon.getImage();

                // Calculate max width and scale height proportionally
                int maxLogoWidth = (int) pageFormat.getImageableWidth();
                int originalLogoWidth = logo.getWidth(null);
                int originalLogoHeight = logo.getHeight(null);

                // Maintain aspect ratio
                int logoWidth = maxLogoWidth;
                int logoHeight = (int) ((double) originalLogoHeight / originalLogoWidth * logoWidth);

                int logoX = x + (maxLogoWidth - logoWidth) / 2;
                g2d.drawImage(logo, logoX, y, logoWidth, logoHeight, null);
                y += logoHeight + 5;
            } catch (Exception ex) {
                // Logo not found, skip drawing
            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
            int lineHeight = g2d.getFontMetrics().getHeight();

            y += lineHeight;
            g2d.drawString("=== DELIS CINEMA ===", x, y); y += lineHeight;
            g2d.drawString("Tanggal: " + date, x, y); y += lineHeight;

            // Film (wrap if panjang)
            String filmLabel = "Film   : ";
            List<String> filmLines = wrapFilmTitle(g2d, filmLabel, film, maxTextWidth);
            if (!filmLines.isEmpty()) {
                g2d.drawString(filmLabel + filmLines.get(0), x, y); y += lineHeight;
                for (int i = 1; i < filmLines.size(); i++) {
                    g2d.drawString(filmLines.get(i), x + 5, y); y += lineHeight;
                }
            }


            g2d.drawString("Studio : " + studio, x, y); y += lineHeight;
            g2d.drawString("Jadwal : " + time, x, y); y += lineHeight;

            // Kursi (wrap if panjang)
            String kursiLabel = "Kursi  : ";
            String kursiStr = String.join(", ", kursiTerpilih);
            List<String> kursiLines = wrapSeats(g2d, kursiLabel, new ArrayList<>(kursiTerpilih), maxTextWidth);
            if (!kursiLines.isEmpty()) {
                g2d.drawString(kursiLabel + kursiLines.get(0), x, y); y += lineHeight;
                for (int i = 1; i < kursiLines.size(); i++) {
                    g2d.drawString(kursiLines.get(i), x + 5, y); y += lineHeight;
                }
            }


            g2d.drawString("Total  : Rp. " + totalHarga, x, y); y += lineHeight;
            y += lineHeight / 2;
            g2d.drawString("===========================", x, y);

            return Printable.PAGE_EXISTS;
        }, pf);

        try {
            job.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to print ticket: " + ex.getMessage());
        }
    }

    private List<String> wrapFilmTitle(Graphics2D g2, String label, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);

        String[] words = text.split(" ");
        if (words.length == 0) return lines;

        StringBuilder firstLine = new StringBuilder(words[0]);
        int i = 1;

        // Only add the second word to the first line if it has 3 or fewer characters
        if (words.length > 1 && words[1].length() <= 3) {
            firstLine.append(" ").append(words[1]);
            i = 2;
        }

        lines.add(firstLine.toString());

        // Add the rest of the words to the next lines
        StringBuilder nextLine = new StringBuilder();
        for (; i < words.length; i++) {
            if (nextLine.length() > 0) nextLine.append(" ");
            nextLine.append(words[i]);
        }
        if (nextLine.length() > 0) lines.add(nextLine.toString());

        return lines;
    }

    private List<String> wrapSeats(Graphics2D g2, String label, List<String> seats, int maxWidth) {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);

        StringBuilder firstLine = new StringBuilder();
        int currentWidth = labelWidth;
        int i = 0;

        // Minimal 3 kursi di baris pertama jika muat
        while (i < seats.size() && (i < 3 || fm.stringWidth(firstLine + (firstLine.length() > 0 ? ", " : "") + seats.get(i)) + currentWidth <= maxWidth)) {
            if (firstLine.length() > 0) firstLine.append(", ");
            firstLine.append(seats.get(i));
            i++;
        }
        lines.add(firstLine.toString());

        // Sisanya ke bawah
        StringBuilder nextLine = new StringBuilder();
        while (i < seats.size()) {
            if (nextLine.length() > 0) nextLine.append(", ");
            nextLine.append(seats.get(i));
            if (fm.stringWidth(nextLine.toString()) > maxWidth - 5) {
                lines.add(nextLine.toString().trim());
                nextLine = new StringBuilder();
            }
            i++;
        }
        if (nextLine.length() > 0) lines.add(nextLine.toString().trim());

        return lines;
    }


}
