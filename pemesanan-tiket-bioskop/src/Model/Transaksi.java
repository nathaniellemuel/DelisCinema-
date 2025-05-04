package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Transaksi {
    private int idTransaksi;
    private int idUser;
    private int idJadwal;
    private LocalDateTime waktuBeli;
    private int totalKursi;
    private int totalBayar;
    private String deskripsiJadwal;
    private List<String> kursiDipesan;

    private Jadwal jadwal; //

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
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdJadwal() { return idJadwal; }
    public void setIdJadwal(int idJadwal) { this.idJadwal = idJadwal; }

    public LocalDateTime getWaktuBeli() { return waktuBeli; }
    public void setWaktuBeli(LocalDateTime waktuBeli) { this.waktuBeli = waktuBeli; }

    public int getTotalKursi() { return totalKursi; }
    public void setTotalKursi(int totalKursi) { this.totalKursi = totalKursi; }

    public int getTotalBayar() { return totalBayar; }
    public void setTotalBayar(int totalBayar) { this.totalBayar = totalBayar; }

    public Jadwal getJadwal() { return jadwal; }
    public void setJadwal(Jadwal jadwal) { this.jadwal = jadwal; }

    public String getDeskripsiJadwal() {
        return deskripsiJadwal;
    }

    public void setDeskripsiJadwal(String deskripsiJadwal) {
        this.deskripsiJadwal = deskripsiJadwal;
    }

    public List<String> getKursiDipesan() {
        return kursiDipesan;
    }

    public void setKursiDipesan(List<String> kursiDipesan) {
        this.kursiDipesan = kursiDipesan;
    }
}
