package View.GUI.Staff;

import Controller.UserController;
import Model.User;
import View.GUI.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StaffDashboard extends JFrame{
    private User user;
    private UserController userController;

    public StaffDashboard(User user){
        this.user = user;
        this.userController = new UserController();

        if (this.user == null) {
            JOptionPane.showMessageDialog(null, "Anda belum login!");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        ImageIcon appIcon = new ImageIcon(getClass().getResource("/Desktop.png"));
        setIconImage(appIcon.getImage());
        setIconImage(appIcon.getImage());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }
}
