package Model;
import java.time.LocalDate;
import java.time.LocalTime;

public class Jadwal {
    private int idJadwal;
    private int idFilm;
    private int idStudio;
    private LocalDate tanggal;
    private LocalTime jam;
    private int harga;
    private Film film;
    private Studio studio;


    public Jadwal() {}

    public Jadwal(int idJadwal, int idFilm, int idStudio, LocalDate tanggal, LocalTime jam, int harga) {
        this.idJadwal = idJadwal;
        this.idFilm = idFilm;
        this.idStudio = idStudio;
        this.tanggal = tanggal;
        this.jam = jam;
        this.harga = harga;
    }

    // Getter-setter
    public int getIdJadwal() {
        return idJadwal;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public int getIdStudio() {
        return idStudio;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public LocalTime getJam() {
        return jam;
    }

    public int getHarga() {
        return harga;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Studio getStudio() {
        return studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }


}
