package Controller;
import Model.Film;
import Model.Jadwal;
import Model.Studio;
import Utility.DBUtil;

import javax.swing.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class JadwalController {

    public List<String> getAllJadwalWithDetail() {
        List<String> list = new ArrayList<>();
        String sql = """
        SELECT j.id_jadwal, f.judul, s.nama_studio, j.tanggal, j.jam, j.harga
        FROM jadwal j
        JOIN film f ON j.id_film = f.id_film
        JOIN studio s ON j.id_studio = s.id_studio
        """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String baris = String.format(
                        "Jadwal #%d | Film: %s | Studio: %s | Tanggal: %s | Jam: %s | Harga: %d",
                        rs.getInt("id_jadwal"),
                        rs.getString("judul"),
                        rs.getString("nama_studio"),
                        rs.getDate("tanggal"),
                        rs.getTime("jam"),
                        rs.getInt("harga")
                );
                list.add(baris);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Jadwal> getJadwalHariIni() {
        List<Jadwal> list = new ArrayList<>();
        String sql = """
        SELECT j.*, f.judul, f.durasi, f.genre, f.status AS film_status, s.nama_studio, s.kapasitas
        FROM jadwal j
        JOIN film f ON j.id_film = f.id_film
        JOIN studio s ON j.id_studio = s.id_studio
        WHERE j.tanggal = ? AND f.status = 'tayang'
        ORDER BY s.id_studio, f.judul, j.jam
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                        rs.getInt("id_jadwal"),
                        rs.getInt("id_film"),
                        rs.getInt("id_studio"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getTime("jam").toLocalTime(),
                        rs.getInt("harga")
                );
                Film film = new Film(
                        rs.getInt("id_film"),
                        rs.getString("judul"),
                        rs.getInt("durasi"),
                        rs.getString("genre"),
                        rs.getString("film_status")
                );
                Studio studio = new Studio(
                        rs.getInt("id_studio"),
                        rs.getString("nama_studio"),
                        rs.getInt("kapasitas")
                );
                jadwal.setFilm(film);
                jadwal.setStudio(studio);
                list.add(jadwal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean tambahJadwal(Jadwal jadwal) {

        if (jadwal.getTanggal().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(null,
                    "Tanggal jadwal tidak boleh sebelum hari ini!",
                    "Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalTime jamDariInputPengguna = jadwal.getJam();
        // Eksplisit set detik dan nanodetik menjadi 0
        LocalTime jamUntukDb = jamDariInputPengguna.withSecond(0).withNano(0); // <--- PERUBAHAN DI SINI

        Date tanggalUntukDb = Date.valueOf(jadwal.getTanggal());

        String sqlCekDuplikat = "SELECT COUNT(*) FROM jadwal WHERE id_film = ? AND id_studio = ? AND tanggal = ? AND jam = ?";
        String sqlInsert = "INSERT INTO jadwal (id_film, id_studio, tanggal, jam, harga) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            // 1. Cek apakah jadwal sudah ada
            try (PreparedStatement stmtCek = conn.prepareStatement(sqlCekDuplikat)) {
                stmtCek.setInt(1, jadwal.getIdFilm());
                stmtCek.setInt(2, jadwal.getIdStudio());
                stmtCek.setDate(3, tanggalUntukDb);
                stmtCek.setTime(4, Time.valueOf(jamUntukDb)); // <--- GUNAKAN jamUntukDb

                ResultSet rs = stmtCek.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Jadwal untuk film dan studio yang sama pada tanggal " + jadwal.getTanggal() +
                                    " dan jam " + jamUntukDb.toString().substring(0, Math.min(jamUntukDb.toString().length(), 5)) + // Format HH:mm
                                    " sudah ada!",
                            "Duplikat Jadwal", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // 2. Jika tidak ada duplikat, lanjutkan proses insert
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, jadwal.getIdFilm());
                stmtInsert.setInt(2, jadwal.getIdStudio());
                stmtInsert.setDate(3, tanggalUntukDb);
                stmtInsert.setTime(4, Time.valueOf(jamUntukDb)); // <--- GUNAKAN jamUntukDb
                stmtInsert.setInt(5, jadwal.getHarga());
                return stmtInsert.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan database: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
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

    public static Map<String, List<Jadwal>> getGroupedJadwal() {
        Map<String, List<Jadwal>> grouped = new LinkedHashMap<>();

        String sql = """
        SELECT 
            j.id_jadwal, j.id_film, j.id_studio, j.tanggal, j.jam, j.harga,
            f.judul, f.durasi, f.genre, f.status AS film_status, -- Alias untuk kolom status dari film
            s.nama_studio, s.kapasitas
        FROM jadwal j
        JOIN film f ON j.id_film = f.id_film
        JOIN studio s ON j.id_studio = s.id_studio
        WHERE f.status = 'tayang'
        ORDER BY s.id_studio, f.judul, j.jam 
        """;
        // Akhir dari perubahan ORDER BY

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

                Film film = new Film(
                        rs.getInt("id_film"),
                        rs.getString("judul"),
                        rs.getInt("durasi"),
                        rs.getString("genre"),
                        rs.getString("film_status") // Menggunakan alias
                );
                Studio studio = new Studio(
                        rs.getInt("id_studio"),
                        rs.getString("nama_studio"),
                        rs.getInt("kapasitas")
                );

                jadwal.setFilm(film);
                jadwal.setStudio(studio);

                String key = film.getIdFilm() + "-" + studio.getIdStudio();

                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(jadwal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grouped;
    }

    public List<Jadwal> getJadwalByFilmId(int filmId) {
        List<Jadwal> list = new ArrayList<>();
        String sql = """
        SELECT j.*, s.nama_studio, s.kapasitas
        FROM jadwal j
        JOIN studio s ON j.id_studio = s.id_studio
        WHERE j.id_film = ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                        rs.getInt("id_jadwal"),
                        rs.getInt("id_film"),
                        rs.getInt("id_studio"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getTime("jam").toLocalTime(),
                        rs.getInt("harga")
                );

                Studio studio = new Studio(
                        rs.getInt("id_studio"),
                        rs.getString("nama_studio"),
                        rs.getInt("kapasitas")
                );

                jadwal.setStudio(studio);
                list.add(jadwal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Studio> getStudiosBelumAdaJadwal() {
        List<Studio> list = new ArrayList<>();
        String sql = """
    SELECT * FROM studio
    WHERE id_studio NOT IN (
        SELECT DISTINCT j.id_studio
        FROM jadwal j
        JOIN film f ON j.id_film = f.id_film
        WHERE f.status = 'tayang'
    )
    """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Studio studio = new Studio(
                        rs.getInt("id_studio"),
                        rs.getString("nama_studio"),
                        rs.getInt("kapasitas")
                );
                list.add(studio);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean hapusSemuaJadwalFilmDiStudio(int idFilm, int idStudio) {
        String sql = "DELETE FROM jadwal WHERE id_film = ? AND id_studio = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFilm);
            stmt.setInt(2, idStudio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusJadwalPerJam(int idJadwal) {
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

    public boolean updateJadwalJam(int idJadwal, LocalTime newJam) {
        // 1. Normalkan newJam agar detiknya selalu :00
        LocalTime jamUntukDb = newJam.withSecond(0).withNano(0);

        String sqlGetJadwalDetails = "SELECT id_film, id_studio, tanggal FROM jadwal WHERE id_jadwal = ?";
        String sqlCekDuplikat = "SELECT COUNT(*) FROM jadwal WHERE id_film = ? AND id_studio = ? AND tanggal = ? AND jam = ? AND id_jadwal != ?";
        String sqlUpdate = "UPDATE jadwal SET jam = ? WHERE id_jadwal = ?";

        try (Connection conn = DBUtil.getConnection()) {
            int filmId = -1;
            int studioId = -1;
            Date tanggalJadwal = null;

            try (PreparedStatement stmtGetDetails = conn.prepareStatement(sqlGetJadwalDetails)) {
                stmtGetDetails.setInt(1, idJadwal);
                ResultSet rsDetails = stmtGetDetails.executeQuery();
                if (rsDetails.next()) {
                    filmId = rsDetails.getInt("id_film");
                    studioId = rsDetails.getInt("id_studio");
                    tanggalJadwal = rsDetails.getDate("tanggal");
                } else {
                    JOptionPane.showMessageDialog(null, "Jadwal dengan ID " + idJadwal + " tidak ditemukan untuk diupdate.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Jadwal tidak ditemukan
                }
            }

            //Cek apakah jam baru (jamUntukDb) akan duplikat dengan jadwal LAIN
            // pada film, studio, dan tanggal yang sama.
            try (PreparedStatement stmtCek = conn.prepareStatement(sqlCekDuplikat)) {
                stmtCek.setInt(1, filmId);
                stmtCek.setInt(2, studioId);
                stmtCek.setDate(3, tanggalJadwal);
                stmtCek.setTime(4, Time.valueOf(jamUntukDb));
                stmtCek.setInt(5, idJadwal);

                ResultSet rsCek = stmtCek.executeQuery();
                if (rsCek.next() && rsCek.getInt(1) > 0) {
                    String jamBentrokStr = jamUntukDb.toString();
                    if (jamBentrokStr.length() > 5) { // Ambil HH:mm saja
                        jamBentrokStr = jamBentrokStr.substring(0, 5);
                    }
                    JOptionPane.showMessageDialog(null,
                            "Jam tayang " + jamBentrokStr +
                                    " sudah ada untuk film ini di studio dan tanggal yang sama (pada jadwal lain).",
                            "Duplikat Jam Tayang", JOptionPane.WARNING_MESSAGE);
                    return false; // Gagal update karena duplikat/bentrok
                }
            }

            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setTime(1, Time.valueOf(jamUntukDb)); // Gunakan jam yang sudah dinormalkan
                stmtUpdate.setInt(2, idJadwal);
                return stmtUpdate.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat update jam jadwal: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateJadwalStudioHarga(int idJadwal, int newStudioId, int newHarga) {
        String sql = "UPDATE jadwal SET id_studio = ?, harga = ? WHERE id_jadwal = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStudioId);
            stmt.setInt(2, newHarga);
            stmt.setInt(3, idJadwal);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
