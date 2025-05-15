package View.GUI.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.text.NumberFormat;
import java.util.Locale;

public class PilihKursiFrame extends JFrame {
    private JFrame frameSebelumnya;
    private final Set<String> kursiTerpilih = new HashSet<>();
    private final JButton btnLanjut = new JButton("LANJUT");
    private JLabel lblHarga;
    private JLabel lblSubtotal;
    private final int hargaRegular; // Price from database
    private final int hargaPremium; // Will be calculated as 150% of regular price
    private int totalHarga = 0;
    private NumberFormat currencyFormatter;

    public PilihKursiFrame(JFrame frameSebelumnya, String film, String studio, String date, String time, int hargaFromDB) {
        this.frameSebelumnya = frameSebelumnya;

        // Setup price from database
        this.hargaRegular = hargaFromDB;
        this.hargaPremium = (int)(hargaRegular * 1.5); // Premium seats cost 50% more

        // Setup custom currency formatter without decimal places
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        this.currencyFormatter.setMaximumFractionDigits(0);

        // Ambil ukuran layar penuh
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        setTitle("Pilih Kursi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Panel Atas
        JPanel panelAtas = new JPanel(null);
        panelAtas.setBackground(Color.DARK_GRAY);
        panelAtas.setBounds(0, 0, width, 80);
        add(panelAtas);

        JButton btnBack = new JButton("X");
        btnBack.setBounds(15, 15, 50, 50);
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setFocusPainted(false);
        panelAtas.add(btnBack);

        JLabel lblFilm = new JLabel(film.toUpperCase() + " / 102 Minutes");
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

        // Label SCREEN
        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Arial", Font.BOLD, 55));
        lblScreen.setForeground(Color.GRAY);
        lblScreen.setBounds((width - 400) / 2, 100, 400, 50);
        add(lblScreen);

        // Keterangan Kursi
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

        // Panel Kursi
        JPanel kursiPanel = new JPanel(new GridLayout(4, 5, 30, 30));
        kursiPanel.setOpaque(false);

        int kursiWidth = 80 * 5 + 30 * 4;
        int kursiHeight = 80 * 4 + 30 * 3;
        kursiPanel.setBounds((width - kursiWidth) / 2, 200, kursiWidth, kursiHeight);

        String[] baris = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            final int row = i; // Create a final copy of i for use in lambda
            for (int j = 1; j <= 5; j++) {
                String kodeKursi = baris[row] + j;
                JButton btnKursi = new JButton(kodeKursi);
                btnKursi.setFocusPainted(false);
                btnKursi.setFont(new Font("Arial", Font.BOLD, 20));
                btnKursi.setPreferredSize(new Dimension(80, 80));

                if (row == 3) {
                    btnKursi.setBackground(Color.BLACK);
                    btnKursi.setForeground(Color.WHITE);
                } else {
                    btnKursi.setBackground(Color.LIGHT_GRAY);
                    btnKursi.setForeground(Color.WHITE);
                }

                // Tambahkan action listener untuk toggle warna
                btnKursi.addActionListener(e -> {
                    if (kursiTerpilih.contains(kodeKursi)) {
                        // Deselect kursi
                        kursiTerpilih.remove(kodeKursi);
                        if (row == 3) {
                            btnKursi.setBackground(Color.BLACK);
                            totalHarga -= hargaPremium; // Subtract premium price
                        } else {
                            btnKursi.setBackground(Color.LIGHT_GRAY);
                            totalHarga -= hargaRegular; // Subtract regular price
                        }
                    } else {
                        // Pilih kursi
                        kursiTerpilih.add(kodeKursi);
                        btnKursi.setBackground(Color.RED);
                        if (row == 3) {
                            totalHarga += hargaPremium; // Add premium price
                        } else {
                            totalHarga += hargaRegular; // Add regular price
                        }
                    }

                    // Update price display
                    lblHarga.setText(currencyFormatter.format(totalHarga));

                    // Update ticket count in subtotal label
                    int ticketCount = kursiTerpilih.size();
                    if (ticketCount == 0) {
                        lblSubtotal.setText("SUBTOTAL");
                    } else if (ticketCount == 1) {
                        lblSubtotal.setText("SUBTOTAL (" + ticketCount + " Ticket)");
                    } else {
                        lblSubtotal.setText("SUBTOTAL (" + ticketCount + " Tickets)");
                    }

                    // Update status tombol LANJUT
                    btnLanjut.setEnabled(!kursiTerpilih.isEmpty());
                    if (!kursiTerpilih.isEmpty()) {
                        btnLanjut.setBackground(Color.RED);
                    } else {
                        btnLanjut.setBackground(Color.LIGHT_GRAY);
                    }
                });

                kursiPanel.add(btnKursi);
            }
        }
        add(kursiPanel);

        // Panel Bawah
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
        btnLanjut.setForeground(Color.WHITE);  // Changed text color to white
        btnLanjut.setFocusPainted(false);
        btnLanjut.setEnabled(false); // awalnya tidak aktif
        panelBawah.add(btnLanjut);

        // Tombol Kembali
        btnBack.addActionListener(e -> {
            frameSebelumnya.setVisible(true);
            dispose();
        });

        setVisible(true);
    }
}
