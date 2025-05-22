package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for managing seat bookings in the database
 */
public class KursiController {
    private Connection connection;

    public KursiController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get all seats that are already booked for a specific showtime
     *
     * @param idJadwal the schedule ID
     * @return Set of seat numbers that are booked
     */
    public Set<String> getBookedSeats(int idJadwal) {
        Set<String> bookedSeats = new HashSet<>();

        try {
            String query = "SELECT nomor_kursi FROM kursi_terpesan WHERE id_jadwal = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idJadwal);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bookedSeats.add(resultSet.getString("nomor_kursi"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error getting booked seats: " + e.getMessage());
            e.printStackTrace();
        }

        return bookedSeats;
    }

    /**
     * Save booked seats to the database
     *
     * @param idJadwal the schedule ID
     * @param idTransaksi the transaction ID
     * @param seats the set of seats to save
     * @return true if successful, false otherwise
     */
    public boolean saveBookedSeats(int idJadwal, int idTransaksi, Set<String> seats) {
        try {
            // Prepare the insert statement
            String query = "INSERT INTO kursi_terpesan (id_kursi, id_transaksi, id_jadwal, nomor_kursi) VALUES (NULL, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            // For each seat, execute the insert statement
            for (String seat : seats) {
                statement.setInt(1, idTransaksi);
                statement.setInt(2, idJadwal);
                statement.setString(3, seat);
                statement.executeUpdate();
            }

            statement.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving booked seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the next available transaction ID
     *
     * @return the next transaction ID
     */
    public int getNextTransactionId() {
        int nextId = 1; // Default starting ID if no records exist

        try {
            String query = "SELECT MAX(id_transaksi) AS max_id FROM kursi_terpesan";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                nextId = resultSet.getInt("max_id") + 1;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error getting next transaction ID: " + e.getMessage());
            e.printStackTrace();
        }

        return nextId;
    }

    /**
     * Get the schedule ID for a specific film, studio, date and time
     *
     * @param film the film title
     * @param studio the studio number
     * @param date the date
     * @param time the time
     * @return the schedule ID or -1 if not found
     */
    public int getJadwalId(String film, String studio, String date, String time) {
        try {
            // Normalize studio name to match DB (remove "STUDIO"/"Studio", trim, ignore case)
            String studioNumber = studio.trim();
            if (studioNumber.toUpperCase().startsWith("STUDIO")) {
                studioNumber = studioNumber.substring(6).trim();
            }
            // Try both "Studio X" and just "X"
            String[] studioVariants = {
                "Studio " + studioNumber,
                studioNumber
            };

            // Try both "HH:mm" and "HH:mm:ss" for time
            String timeHHmm = time;
            String timeHHmmss = time;
            if (time.length() == 5) {
                timeHHmmss = time + ":00";
            } else if (time.length() == 8 && time.endsWith(":00")) {
                timeHHmm = time.substring(0, 5);
            }

            String query = "SELECT jadwal.id_jadwal FROM jadwal " +
                    "JOIN film ON jadwal.id_film = film.id_film " +
                    "JOIN studio ON jadwal.id_studio = studio.id_studio " +
                    "WHERE film.judul = ? AND (studio.nama_studio = ? OR studio.nama_studio = ?) " +
                    "AND jadwal.tanggal = ? AND (jadwal.jam = ? OR jadwal.jam = ?) LIMIT 1";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, film);
            statement.setString(2, studioVariants[0]);
            statement.setString(3, studioVariants[1]);
            statement.setString(4, date);
            statement.setString(5, timeHHmm);
            statement.setString(6, timeHHmmss);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_jadwal");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error getting jadwal id: " + e.getMessage());
            e.printStackTrace();
        }

        return -1; // Not found
    }
}

// komentar
