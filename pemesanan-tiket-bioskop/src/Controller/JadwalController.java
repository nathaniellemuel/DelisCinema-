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
        ORDER BY s.id_studio, f.judul, j.tanggal ASC, j.jam DESC     
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

    public boolean hasTransaksiForJadwalIds(List<Integer> jadwalIds) {
        if (jadwalIds.isEmpty()) return false;
        String placeholders = String.join(",", Collections.nCopies(jadwalIds.size(), "?"));
        String sql = "SELECT COUNT(*) FROM transaksi WHERE id_jadwal IN (" + placeholders + ")";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < jadwalIds.size(); i++) {
                stmt.setInt(i + 1, jadwalIds.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateJadwalStudioHargaAll(int idFilm, int oldStudioId, int newStudioId, int newHarga) {
        String sql = "UPDATE jadwal SET id_studio = ?, harga = ? WHERE id_film = ? AND id_studio = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStudioId);
            stmt.setInt(2, newHarga);
            stmt.setInt(3, idFilm);
            stmt.setInt(4, oldStudioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateJadwalJamTanggal(int idJadwal, LocalDate newTanggal, LocalTime newJam) {
        // Normalize time
        LocalTime jamUntukDb = newJam.withSecond(0).withNano(0);
        String sqlGetDetails = "SELECT id_film, id_studio FROM jadwal WHERE id_jadwal = ?";
        String sqlCekDuplikat = "SELECT COUNT(*) FROM jadwal WHERE id_film = ? AND id_studio = ? AND tanggal = ? AND jam = ? AND id_jadwal != ?";
        String sqlUpdate = "UPDATE jadwal SET tanggal = ?, jam = ? WHERE id_jadwal = ?";

        try (Connection conn = DBUtil.getConnection()) {
            int filmId = -1, studioId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sqlGetDetails)) {
                stmt.setInt(1, idJadwal);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    filmId = rs.getInt("id_film");
                    studioId = rs.getInt("id_studio");
                } else {
                    return false;
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlCekDuplikat)) {
                stmt.setInt(1, filmId);
                stmt.setInt(2, studioId);
                stmt.setDate(3, java.sql.Date.valueOf(newTanggal));
                stmt.setTime(4, java.sql.Time.valueOf(jamUntukDb));
                stmt.setInt(5, idJadwal);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setDate(1, java.sql.Date.valueOf(newTanggal));
                stmt.setTime(2, java.sql.Time.valueOf(jamUntukDb));
                stmt.setInt(3, idJadwal);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
