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
        String sqlCekJudul = "SELECT COUNT(*) FROM film WHERE LOWER(judul) = LOWER(?) AND id_film != ?";
        String sqlUpdate = "UPDATE film SET judul = ?, durasi = ?, genre = ?, status = ? WHERE id_film = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // 1. Cek apakah judul baru sudah ada untuk film LAIN
            try (PreparedStatement cekJudulStmt = conn.prepareStatement(sqlCekJudul)) {
                cekJudulStmt.setString(1, film.getJudul());
                cekJudulStmt.setInt(2, film.getIdFilm()); // Pengecualian untuk film yang sedang diedit
                try (ResultSet rsJudul = cekJudulStmt.executeQuery()) {
                    if (rsJudul.next() && rsJudul.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Judul film '" + film.getJudul() + "' sudah digunakan oleh film lain!", "Duplikat Judul", JOptionPane.WARNING_MESSAGE);
                        return false; // Judul duplikat dengan film lain
                    }
                }
            }

            // 2. Cek status sebelumnya untuk logika khusus (selesai -> tayang)
            String sqlCekStatus = "SELECT status FROM film WHERE id_film = ?";
            try (PreparedStatement cekStatusStmt = conn.prepareStatement(sqlCekStatus)) {
                cekStatusStmt.setInt(1, film.getIdFilm());
                ResultSet rsStatus = cekStatusStmt.executeQuery();
                if (rsStatus.next()) {
                    String statusLama = rsStatus.getString("status");

                    if (statusLama.equalsIgnoreCase("selesai") && film.getStatus().equalsIgnoreCase("tayang")) {
                        // Ambil studio yang pernah dipakai film ini
                        String sqlStudioFilm = """
                        SELECT DISTINCT id_studio FROM jadwal WHERE id_film = ?
                        """;
                        try (PreparedStatement stmtStudio = conn.prepareStatement(sqlStudioFilm)) {
                            stmtStudio.setInt(1, film.getIdFilm());
                            ResultSet rsStudio = stmtStudio.executeQuery();
                            List<Integer> studioIds = new ArrayList<>();
                            while (rsStudio.next()) {
                                studioIds.add(rsStudio.getInt("id_studio"));
                            }

                            for (int idStudio : studioIds) {
                                // Cek apakah studio ini sedang dipakai film lain yang tayang
                                String sqlCekTabrakan = """
                                SELECT f.judul FROM jadwal j
                                JOIN film f ON j.id_film = f.id_film
                                WHERE j.id_studio = ? AND f.status = 'tayang' AND f.id_film != ?
                                """;
                                try (PreparedStatement stmtCekTabrakan = conn.prepareStatement(sqlCekTabrakan)) {
                                    stmtCekTabrakan.setInt(1, idStudio);
                                    stmtCekTabrakan.setInt(2, film.getIdFilm()); // Jangan cek dengan film ini sendiri
                                    ResultSet rsCekTabrakan = stmtCekTabrakan.executeQuery();
                                    if (rsCekTabrakan.next()) {
                                        JOptionPane.showMessageDialog(null, "Gagal update status ke 'tayang'. Studio yang pernah digunakan film ini (ID Studio: " + idStudio + ") sedang dipakai oleh film '" + rsCekTabrakan.getString("judul") + "' yang juga tayang.", "Konflik Jadwal Studio", JOptionPane.WARNING_MESSAGE);
                                        return false; // Ada konflik studio
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Film yang akan diupdate tidak ditemukan (seharusnya tidak terjadi jika alur aplikasi benar)
                    JOptionPane.showMessageDialog(null, "Film dengan ID " + film.getIdFilm() + " tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // 3. Jika aman (tidak ada duplikasi judul dengan film lain DAN tidak ada konflik studio), lanjut update film
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat update film: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }



}
