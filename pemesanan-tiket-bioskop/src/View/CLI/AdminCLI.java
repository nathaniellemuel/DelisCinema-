package View.CLI;
import Controller.UserController;
import Controller.FilmController;
import Controller.JadwalController;
import Model.User;
import Model.Film;
import Model.Jadwal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class AdminCLI {
    static Scanner sc = new Scanner(System.in);
    static UserController userController = new UserController();
    static FilmController filmController = new FilmController();
    static JadwalController jadwalController = new JadwalController();

    public static void main(String[] args) {
        System.out.println("=== LOGIN ADMIN ===");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        String passwordHashed = hashPassword(password); // Ganti dengan yang sama seperti seeder

        User user = userController.login(username, passwordHashed);

        if (user == null || !user.getRole().equals("admin")) {
            System.out.println("Login gagal.");
            return;
        }

        System.out.println("Login berhasil sebagai admin!");

        while (true) {
            System.out.println("\n==== MENU ADMIN ====");
            System.out.println("1. Lihat Semua Film");
            System.out.println("2. Tambah Film");
            System.out.println("3. Update Film");
            System.out.println("4. Hapus Film");
            System.out.println("5. Lihat Semua Jadwal");
            System.out.println("6. Tambah Jadwal");
            System.out.println("7. Hapus Jadwal");
            System.out.println("0. Logout");
            System.out.print("Pilih: ");
            int pilih = Integer.parseInt(sc.nextLine());

            switch (pilih) {
                case 1 -> {
                    List<Film> filmList = filmController.getAllFilm();
                    filmList.forEach(f -> System.out.println(f.getIdFilm() + ". " + f.getJudul()));
                }
                case 2 -> {
                    System.out.print("Judul: ");
                    String judul = sc.nextLine();
                    System.out.print("Durasi (menit): ");
                    int durasi = Integer.parseInt(sc.nextLine());
                    System.out.print("Genre: ");
                    String genre = sc.nextLine();
                    System.out.print("Status (tayang/selesai): ");
                    String status = sc.nextLine();
                    Film film = new Film(0, judul, durasi, genre, status);
                    System.out.println(filmController.tambahFilm(film) ? "Film ditambahkan!" : "Gagal tambah film.");
                }
                case 3 -> {
                    System.out.print("ID Film yang mau diupdate: ");
                    int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Judul baru: ");
                    String judul = sc.nextLine();
                    System.out.print("Durasi: ");
                    int durasi = Integer.parseInt(sc.nextLine());
                    System.out.print("Genre: ");
                    String genre = sc.nextLine();
                    System.out.print("Status: ");
                    String status = sc.nextLine();
                    Film film = new Film(id, judul, durasi, genre, status);
                    System.out.println(filmController.updateFilm(film) ? "Update berhasil." : "Gagal update.");
                }
                case 4 -> {
                    System.out.print("ID Film yang mau dihapus: ");
                    int id = Integer.parseInt(sc.nextLine());
                    System.out.println(filmController.hapusFilm(id) ? "Hapus berhasil." : "Gagal hapus.");
                }
                case 5 -> {
                    List<String> list = jadwalController.getAllJadwalWithDetail();
                    list.forEach(System.out::println);
                }
                case 6 -> {
                    System.out.print("ID Film: ");
                    int idFilm = Integer.parseInt(sc.nextLine());
                    System.out.print("ID Studio: ");
                    int idStudio = Integer.parseInt(sc.nextLine());
                    System.out.print("Tanggal (yyyy-mm-dd): ");
                    LocalDate tgl = LocalDate.parse(sc.nextLine());
                    System.out.print("Jam (hh:mm): ");
                    LocalTime jam = LocalTime.parse(sc.nextLine());
                    System.out.print("Harga: ");
                    int harga = Integer.parseInt(sc.nextLine());
                    Jadwal jadwal = new Jadwal(0, idFilm, idStudio, tgl, jam, harga);
                    System.out.println(jadwalController.tambahJadwal(jadwal) ? "Jadwal ditambahkan!" : "Gagal.");
                }
                case 7 -> {
                    System.out.print("ID Jadwal: ");
                    int id = Integer.parseInt(sc.nextLine());
                    System.out.println(jadwalController.hapusJadwal(id) ? "Berhasil dihapus." : "Gagal hapus.");
                }
                case 0 -> {
                    userController.logout();
                    return;
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
