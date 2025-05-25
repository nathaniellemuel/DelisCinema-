package Controller;
import Model.User;
import Utility.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public String getUsernameById(int idUser) {
        String sql = "SELECT username FROM user WHERE id_user = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public int getUserCount() {
        String sql = "SELECT COUNT(*) AS total_users FROM user";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total_users");
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil jumlah pengguna: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY username ASC"; // Diurutkan berdasarkan username
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"), // Hati-hati mengembalikan password jika tidak dienkripsi
                        rs.getString("role")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua pengguna: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public void logout() {
        System.out.println("Logout berhasil. Sampai jumpa!");
    }

}
