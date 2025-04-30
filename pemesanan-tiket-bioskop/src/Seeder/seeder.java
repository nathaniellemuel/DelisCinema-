package Seeder;
import Utility.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;

public class seeder {
    public static void main(String[] args) {
        seedUser();
    }

    public static void seedUser() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, "staff");
            stmt.setString(2, hashPassword("staff123")); // password di-hash
            stmt.setString(3, "staff");

            stmt.executeUpdate();
            System.out.println("Seeder user berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    public static void seedStudio() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO studio (nama_studio, kapasitas) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 1; i <= 4; i++) {
                stmt.setString(1, "Studio " + i);
                stmt.setInt(2, 40);
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Seeder studio berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
