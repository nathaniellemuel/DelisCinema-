package Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KursiTerpesanController {
    private static Connection conn;

    static {
        conn = Utility.DBUtil.getConnection();
    }

    // Method to save a booked seat
    public static boolean saveKursiTerpesan(int idTransaksi, int idJadwal, String nomorKursi) {
        try {
            String query = "INSERT INTO kursi_terpesan (id_transaksi, id_jadwal, nomor_kursi) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idTransaksi);
            ps.setInt(2, idJadwal);
            ps.setString(3, nomorKursi);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error saving kursi terpesan: " + e.getMessage());
            return false;
        }
    }

    // Method to save multiple booked seats at once
    public static boolean saveMultipleKursiTerpesan(int idTransaksi, int idJadwal, Set<String> nomorKursiSet) {
        try {
            conn.setAutoCommit(false);
            String query = "INSERT INTO kursi_terpesan (id_transaksi, id_jadwal, nomor_kursi) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            for (String nomorKursi : nomorKursiSet) {
                ps.setInt(1, idTransaksi);
                ps.setInt(2, idJadwal);
                ps.setString(3, nomorKursi);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            // Check if all inserts were successful
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error rolling back: " + ex.getMessage());
            }
            System.out.println("Error saving multiple kursi terpesan: " + e.getMessage());
            return false;
        }
    }

    // Method to get all booked seats for a specific jadwal
    public static List<String> getKursiTerpesanByJadwal(int idJadwal) {
        List<String> kursiTerpesanList = new ArrayList<>();
        try {
            String query = "SELECT nomor_kursi FROM kursi_terpesan WHERE id_jadwal = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idJadwal);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                kursiTerpesanList.add(rs.getString("nomor_kursi"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting kursi terpesan: " + e.getMessage());
        }
        return kursiTerpesanList;
    }

    // Method to check if a seat is already booked
    public static boolean isKursiTerpesan(int idJadwal, String nomorKursi) {
        try {
            String query = "SELECT COUNT(*) as count FROM kursi_terpesan WHERE id_jadwal = ? AND nomor_kursi = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idJadwal);
            ps.setString(2, nomorKursi);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking kursi terpesan: " + e.getMessage());
        }
        return false;
    }
}