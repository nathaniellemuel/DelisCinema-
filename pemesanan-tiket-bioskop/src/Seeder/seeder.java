package Seeder;
import Utility.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class seeder {
    public static void main(String[] args) {
//        seedUser();
    }

    public static void seedUser() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, "staff2");
            stmt.setString(2, hashPassword("staff123")); // password di-hash
            stmt.setString(3, "staff");

            stmt.executeUpdate();
            System.out.println("Seeder user berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    public static void seedStudio() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO studio (nama_studio, kapasitas) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 1; i <= 4; i++) {
                stmt.setString(1, "Studio " + i);
                stmt.setInt(2, 40);
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Seeder studio berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seedTransaksi() {
        try (Connection conn = DBUtil.getConnection()) {
            String sqlTransaksi = "INSERT INTO transaksi (id_user, id_jadwal, waktu_beli, total_kursi, total_bayar) VALUES (?, ?, ?, ?, ?)";
            String sqlKursi = "INSERT INTO kursi_terpesan (id_transaksi, id_jadwal, nomor_kursi) VALUES (?, ?, ?)";

            PreparedStatement stmtTransaksi = conn.prepareStatement(sqlTransaksi, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement stmtKursi = conn.prepareStatement(sqlKursi);

            Random rand = new Random();
            int[] idJadwalList = {2, 3, 4}; // sesuai data jadwal yang sudah ada
            int kapasitasKursi = 25;

            for (int i = 0; i < 10; i++) {
                int idUser = 2; // asumsi staff
                int idJadwal = idJadwalList[rand.nextInt(idJadwalList.length)];
                int totalKursi = rand.nextInt(4) + 1; // 1 - 4 kursi
                int hargaPerKursi = (idJadwal == 4) ? 44000 : 55000;
                int totalBayar = totalKursi * hargaPerKursi;

                LocalDateTime waktuBeli = LocalDateTime.now().minusDays(rand.nextInt(5)); // hari random beberapa hari lalu
                stmtTransaksi.setInt(1, idUser);
                stmtTransaksi.setInt(2, idJadwal);
                stmtTransaksi.setTimestamp(3, Timestamp.valueOf(waktuBeli));
                stmtTransaksi.setInt(4, totalKursi);
                stmtTransaksi.setInt(5, totalBayar);
                stmtTransaksi.executeUpdate();

                var generatedKeys = stmtTransaksi.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idTransaksi = generatedKeys.getInt(1);

                    // Generate kursi unik
                    Set<Integer> nomorDipilih = new HashSet<>();
                    while (nomorDipilih.size() < totalKursi) {
                        int nomor = rand.nextInt(kapasitasKursi) + 1;
                        if (nomorDipilih.add(nomor)) {
                            stmtKursi.setInt(1, idTransaksi);
                            stmtKursi.setInt(2, idJadwal);
                            stmtKursi.setString(3, "A" + nomor); // kursi format A1, A2, ...
                            stmtKursi.addBatch();
                        }
                    }

                    stmtKursi.executeBatch();
                }
            }

            System.out.println("Seeder transaksi dan kursi_terpesan berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
