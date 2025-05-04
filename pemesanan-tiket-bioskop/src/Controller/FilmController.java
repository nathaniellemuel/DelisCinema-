package Controller;
import Model.Film;
import Utility.DBUtil;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmController {
    public List<Film> getAllFilm() {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT * FROM film";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Film film = new Film(
                        rs.getInt("id_film"),
                        rs.getString("judul"),
                        rs.getInt("durasi"),
                        rs.getString("genre"),
                        rs.getString("status")
                );
                list.add(film);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Film> getFilmTayang() {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT * FROM film WHERE status = 'tayang'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Film film = new Film(
                        rs.getInt("id_film"),
                        rs.getString("judul"),
                        rs.getInt("durasi"),
                        rs.getString("genre"),
                        rs.getString("status")
                );
                list.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public boolean tambahFilm(Film film) {
        String cekJudulSql = "SELECT COUNT(*) FROM film WHERE LOWER(judul) = LOWER(?)";
        String insertSql = "INSERT INTO film (judul, durasi, genre, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            // Cek apakah judul sudah ada
            try (PreparedStatement cekStmt = conn.prepareStatement(cekJudulSql)) {
                cekStmt.setString(1, film.getJudul());
                try (ResultSet rs = cekStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Judul film sudah ada!", "Duplikat", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }

            // Lanjut insert kalau belum ada
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, film.getJudul());
                insertStmt.setInt(2, film.getDurasi());
                insertStmt.setString(3, film.getGenre());
                insertStmt.setString(4, film.getStatus());
                return insertStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean hapusFilm(int idFilm) {
        String sqlDeleteJadwal = "DELETE FROM jadwal WHERE id_film = ?";
        String sqlDeleteFilm = "DELETE FROM film WHERE id_film = ?";
        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            // Mulai transaksi
            conn.setAutoCommit(false);

            // Hapus jadwal terkait film
            try (PreparedStatement stmtJadwal = conn.prepareStatement(sqlDeleteJadwal)) {
                stmtJadwal.setInt(1, idFilm);
                stmtJadwal.executeUpdate();
            }

            // Hapus film
            try (PreparedStatement stmtFilm = conn.prepareStatement(sqlDeleteFilm)) {
                stmtFilm.setInt(1, idFilm);
                int rowsAffected = stmtFilm.executeUpdate();

                // Jika film berhasil dihapus, commit transaksi
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    // Jika film tidak berhasil dihapus, rollback transaksi
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Jika ada error, rollback transaksi
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                // Mengembalikan autocommit ke true setelah transaksi
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();  // Jangan lupa menutup koneksi setelah selesai
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean updateFilm(Film film) {
        String sqlUpdate = "UPDATE film SET judul = ?, durasi = ?, genre = ?, status = ? WHERE id_film = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // Cek status sebelumnya
            String sqlCekStatus = "SELECT status FROM film WHERE id_film = ?";
            try (PreparedStatement cekStmt = conn.prepareStatement(sqlCekStatus)) {
                cekStmt.setInt(1, film.getIdFilm());
                ResultSet rs = cekStmt.executeQuery();
                if (rs.next()) {
                    String statusLama = rs.getString("status");

                    if (statusLama.equals("selesai") && film.getStatus().equals("tayang")) {
                        // Ambil studio yang pernah dipakai film ini
                        String sqlStudioFilm = """
                        SELECT DISTINCT id_studio FROM jadwal WHERE id_film = ?
                    """;
                        try (PreparedStatement stmtStudio = conn.prepareStatement(sqlStudioFilm)) {
                            stmtStudio.setInt(1, film.getIdFilm());
                            ResultSet rsStudio = stmtStudio.executeQuery();
                            while (rsStudio.next()) {
                                int idStudio = rsStudio.getInt("id_studio");

                                // Cek apakah studio ini sedang dipakai film lain yang tayang
                                String sqlCekTabrakan = """
                                SELECT f.judul FROM jadwal j
                                JOIN film f ON j.id_film = f.id_film
                                WHERE j.id_studio = ? AND f.status = 'tayang' AND f.id_film != ?
                            """;
                                try (PreparedStatement stmtCek = conn.prepareStatement(sqlCekTabrakan)) {
                                    stmtCek.setInt(1, idStudio);
                                    stmtCek.setInt(2, film.getIdFilm());
                                    ResultSet rsCek = stmtCek.executeQuery();
                                    if (rsCek.next()) {
                                        System.out.println("Studio sedang dipakai film lain: " + rsCek.getString("judul"));
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Jika aman, lanjut update film
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, film.getJudul());
                stmt.setInt(2, film.getDurasi());
                stmt.setString(3, film.getGenre());
                stmt.setString(4, film.getStatus());
                stmt.setInt(5, film.getIdFilm());
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
