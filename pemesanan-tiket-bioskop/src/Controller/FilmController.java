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
        if (hasTransaksiForFilm(idFilm)) {
            JOptionPane.showMessageDialog(null, "Film cannot be deleted because it already has transaction history.");
            return false;
        }

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
        boolean hasTransaksi = hasTransaksiForFilm(film.getIdFilm());

        // Fetch current film data
        String sqlGetFilm = "SELECT judul, durasi, genre, status FROM film WHERE id_film = ?";
        String currentJudul = null, currentGenre = null, currentStatus = null;
        int currentDurasi = 0;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmtGet = conn.prepareStatement(sqlGetFilm)) {
            stmtGet.setInt(1, film.getIdFilm());
            ResultSet rs = stmtGet.executeQuery();
            if (rs.next()) {
                currentJudul = rs.getString("judul");
                currentDurasi = rs.getInt("durasi");
                currentGenre = rs.getString("genre");
                currentStatus = rs.getString("status");
            } else {
                JOptionPane.showMessageDialog(null, "Film dengan ID " + film.getIdFilm() + " tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // If there are transactions, only allow status to change, block other fields
        if (hasTransaksi) {
            boolean onlyStatusChanged =
                    film.getJudul().equals(currentJudul) &&
                            film.getDurasi() == currentDurasi &&
                            film.getGenre().equals(currentGenre);

            if (!onlyStatusChanged) {
                JOptionPane.showMessageDialog(null, "Film cannot be edited (except status) because it already has transaction history.");
                return false;
            }
        }

        // Continue with update logic
        String sqlUpdate = "UPDATE film SET judul = ?, durasi = ?, genre = ?, status = ? WHERE id_film = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setString(1, film.getJudul());
            stmtUpdate.setInt(2, film.getDurasi());
            stmtUpdate.setString(3, film.getGenre());
            stmtUpdate.setString(4, film.getStatus());
            stmtUpdate.setInt(5, film.getIdFilm());
            int rowsAffected = stmtUpdate.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Film berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Update film tidak mempengaruhi data apapun. ID mungkin tidak valid atau data sama.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat update film: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    private boolean hasTransaksiForFilm(int idFilm) {
        String sql = "SELECT COUNT(*) FROM transaksi t JOIN jadwal j ON t.id_jadwal = j.id_jadwal WHERE j.id_film = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFilm);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
