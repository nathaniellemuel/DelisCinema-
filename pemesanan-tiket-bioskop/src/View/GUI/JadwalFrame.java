package View.GUI;

import Controller.FilmController;
import Controller.UserController;
import Controller.JadwalController;
import Model.Film;
import Model.User;
import Model.Jadwal;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
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
        JButton btnLogout = createSidebarButtonWithIcon("Logout", "/logout.png");

        sidebar.add(btnDashboard);
        sidebar.add(btnFilm);
        sidebar.add(btnJadwal);
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
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new GridBagLayout());


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
            JLabel lblJam = new JLabel(jamStr);
            lblJam.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            lblJam.setFont(new Font("Poppins", Font.PLAIN, 14));
            lblJam.setHorizontalAlignment(SwingConstants.CENTER);
            lblJam.setPreferredSize(new Dimension(60, 25));
            jamPanel.add(lblJam);
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

        right.add(lblInfo);
        right.add(lblStudio);
        right.add(buttonPanel);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }



}
