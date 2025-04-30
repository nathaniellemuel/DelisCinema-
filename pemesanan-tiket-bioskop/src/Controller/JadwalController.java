package Controller;
import Model.Jadwal;
import Utility.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JadwalController {

    public List<Jadwal> getAllJadwal() {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal"; //get data

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                        rs.getInt("id_jadwal"),
                        rs.getInt("id_film"),
                        rs.getInt("id_studio"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getTime("jam").toLocalTime(),
                        rs.getInt("harga")
                );
                list.add(jadwal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean tambahJadwal(Jadwal jadwal) {
        String sql = "INSERT INTO jadwal (id_film, id_studio, tanggal, jam, harga) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jadwal.getIdFilm());
            stmt.setInt(2, jadwal.getIdStudio());
            stmt.setDate(3, Date.valueOf(jadwal.getTanggal()));
            stmt.setTime(4, Time.valueOf(jadwal.getJam()));
            stmt.setInt(5, jadwal.getHarga());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusJadwal(int idJadwal) {
        String sql = "DELETE FROM jadwal WHERE id_jadwal = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idJadwal);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
