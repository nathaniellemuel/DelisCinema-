package Controller;
import Model.Film;
import Utility.DBUtil;

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

    public boolean tambahFilm(Film film) {
        String sql = "INSERT INTO film (judul, durasi, genre, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, film.getJudul());
            stmt.setInt(2, film.getDurasi());
            stmt.setString(3, film.getGenre());
            stmt.setString(4, film.getStatus());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusFilm(int idFilm) {
        String sql = "DELETE FROM film WHERE id_film = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFilm);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFilm(Film film) {
        String sql = "UPDATE film SET judul = ?, durasi = ?, genre = ?, status = ? WHERE id_film = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, film.getJudul());
            stmt.setInt(2, film.getDurasi());
            stmt.setString(3, film.getGenre());
            stmt.setString(4, film.getStatus());
            stmt.setInt(5, film.getIdFilm());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
