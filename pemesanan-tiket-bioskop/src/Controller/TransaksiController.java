package Controller;

import Model.Transaksi;
import Utility.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransaksiController {
    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();

        String sql = "SELECT t.id_transaksi, t.id_user, u.username, t.id_jadwal, t.waktu_beli, t.total_kursi, t.total_bayar, " +
                "j.tanggal, j.jam, j.harga, f.judul, s.nama_studio " +
                "FROM transaksi t " +
                "JOIN user u ON t.id_user = u.id_user " +
                "JOIN jadwal j ON t.id_jadwal = j.id_jadwal " +
                "JOIN film f ON j.id_film = f.id_film " +
                "JOIN studio s ON j.id_studio = s.id_studio " +
                "ORDER BY t.waktu_beli DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setIdUser(rs.getInt("id_user"));
                t.setIdJadwal(rs.getInt("id_jadwal"));
                t.setWaktuBeli(rs.getTimestamp("waktu_beli").toLocalDateTime());
                t.setTotalKursi(rs.getInt("total_kursi"));
                t.setTotalBayar(rs.getInt("total_bayar"));

                // deskripsi lengkap jadwal
                String deskripsiJadwal = rs.getString("judul") + ", " +
                        rs.getString("tanggal") + " " +
                        rs.getString("jam") + ", Studio " +
                        rs.getString("nama_studio");
                t.setDeskripsiJadwal(deskripsiJadwal);

                // ambil semua kursi dari kursi_terpesan
                List<String> kursi = new ArrayList<>();
                String kursiSQL = "SELECT nomor_kursi FROM kursi_terpesan WHERE id_transaksi = ?";
                try (PreparedStatement ps = conn.prepareStatement(kursiSQL)) {
                    ps.setInt(1, t.getIdTransaksi());
                    ResultSet rsKursi = ps.executeQuery();
                    while (rsKursi.next()) {
                        kursi.add(rsKursi.getString("nomor_kursi"));
                    }
                }
                t.setKursiDipesan(kursi);

                transaksiList.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaksiList;
    }

    // Pendapatan per bulan
    public Map<String, Integer> getPendapatanPerBulan() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(waktu_beli, '%Y-%m') AS bulan, SUM(total_bayar) AS pendapatan " +
                "FROM transaksi GROUP BY bulan ORDER BY bulan";
        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("bulan"), rs.getInt("pendapatan"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Pendapatan per studio
    public Map<String, Integer> getPendapatanPerStudio() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT s.nama_studio, SUM(t.total_bayar) AS pendapatan " +
                "FROM transaksi t JOIN jadwal j ON t.id_jadwal = j.id_jadwal " +
                "JOIN studio s ON j.id_studio = s.id_studio GROUP BY s.nama_studio";
        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("nama_studio"), rs.getInt("pendapatan"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Pendapatan per film
    public Map<String, Integer> getPendapatanPerFilm() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT f.judul, SUM(t.total_bayar) AS pendapatan " +
                "FROM transaksi t JOIN jadwal j ON t.id_jadwal = j.id_jadwal " +
                "JOIN film f ON j.id_film = f.id_film GROUP BY f.judul";
        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("judul"), rs.getInt("pendapatan"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Film paling banyak ditonton
    public Map<String, Integer> getFilmPalingBanyakDitonton() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT f.judul, SUM(t.total_kursi) AS jumlah_penonton " +
                "FROM transaksi t JOIN jadwal j ON t.id_jadwal = j.id_jadwal " +
                "JOIN film f ON j.id_film = f.id_film GROUP BY f.judul ORDER BY jumlah_penonton DESC";
        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("judul"), rs.getInt("jumlah_penonton"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
}

//komentar
