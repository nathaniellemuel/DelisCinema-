package Controller;
import Model.User;
import Utility.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    public static User getCurrentUser() {
        return null;
    }

    // Login function
    public User login(String username, String passwordHashed) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHashed);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User ditemukan
                return new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login gagal
    }

    public void logout() {
        System.out.println("Logout berhasil. Sampai jumpa!");
    }

}
