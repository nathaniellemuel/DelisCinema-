package Controller;
import Model.Film;
import Model.Jadwal;
import Model.Studio;
import Utility.DBUtil;

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

    public boolean tambahJadwal(Jadwal jadwal) {
        String sql = "INSERT INTO jadwal (id_film, id_studio, tanggal, jam, harga) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Get current time and format it to HH:mm (without seconds)
            LocalTime currentTime = LocalTime.now();
            LocalTime timeWithoutSeconds = LocalTime.of(currentTime.getHour(), currentTime.getMinute());

            stmt.setInt(1, jadwal.getIdFilm());
            stmt.setInt(2, jadwal.getIdStudio());
            stmt.setDate(3, Date.valueOf(jadwal.getTanggal()));
            stmt.setTime(4, Time.valueOf(timeWithoutSeconds)); // Use real-time HH:mm
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

    public static Map<String, List<Jadwal>> getGroupedJadwal() {
        Map<String, List<Jadwal>> grouped = new LinkedHashMap<>();

        String sql = """
    SELECT 
        j.id_jadwal, j.id_film, j.id_studio, j.tanggal, j.jam, j.harga,
        f.judul, f.durasi, f.genre, f.status,
        s.nama_studio, s.kapasitas
    FROM jadwal j
    JOIN film f ON j.id_film = f.id_film
    JOIN studio s ON j.id_studio = s.id_studio
    WHERE f.status = 'tayang'
    ORDER BY f.judul, s.nama_studio, j.jam
""";

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
                        rs.getString("status")
                );
                Studio studio = new Studio(
                        rs.getInt("id_studio"),
                        rs.getString("nama_studio"),
                        rs.getInt("kapasitas")
                );

                jadwal.setFilm(film);
                jadwal.setStudio(studio);

                // Gunakan ID film + studio sebagai key agar unik
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
        String sql = "UPDATE jadwal SET jam = ? WHERE id_jadwal = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTime(1, Time.valueOf(newJam));
            stmt.setInt(2, idJadwal);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
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
