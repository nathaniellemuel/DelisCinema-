package Model;

public class KursiTerpesan {
    private int idKursi;
    private int idTransaksi;
    private int idJadwal;
    private String nomorKursi;

    public KursiTerpesan() {}

    public KursiTerpesan(int idKursi, int idTransaksi, int idJadwal, String nomorKursi) {
        this.idKursi = idKursi;
        this.idTransaksi = idTransaksi;
        this.idJadwal = idJadwal;
        this.nomorKursi = nomorKursi;
    }

    // Getter-setter
}
