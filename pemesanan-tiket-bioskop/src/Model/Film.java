package Model;

public class Film {
    private int idFilm;
    private String judul;
    private int durasi;
    private String genre;
    private String status; // tayang / selesai

    public Film() {}

    public Film(int idFilm, String judul, int durasi, String genre, String status) {
        this.idFilm = idFilm;
        this.judul = judul;
        this.durasi = durasi;
        this.genre = genre;
        this.status = status;
    }

    // Getter-setter
    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return judul;
    }

}
