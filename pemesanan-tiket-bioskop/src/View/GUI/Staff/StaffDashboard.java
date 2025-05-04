package View.GUI.Staff;
import Controller.UserController;
import Model.User;
import View.GUI.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StaffDashboard extends JFrame{
    private User currentUser;
    private UserController userController;

    public StaffDashboard(User user){
        this.currentUser = user;
        this.userController = new UserController();

        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Staff Dashboard - Delis Cinema");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }
}
