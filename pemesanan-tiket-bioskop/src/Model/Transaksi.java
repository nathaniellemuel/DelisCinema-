package Model;
import java.time.LocalDateTime;

public class Transaksi {
    private int idTransaksi;
    private int idUser;
    private int idJadwal;
    private LocalDateTime waktuBeli;
    private int totalKursi;
    private int totalBayar;

    public Transaksi() {}

    public Transaksi(int idTransaksi, int idUser, int idJadwal, LocalDateTime waktuBeli, int totalKursi, int totalBayar) {
        this.idTransaksi = idTransaksi;
        this.idUser = idUser;
        this.idJadwal = idJadwal;
        this.waktuBeli = waktuBeli;
        this.totalKursi = totalKursi;
        this.totalBayar = totalBayar;
    }

    // Getter-setter
}
