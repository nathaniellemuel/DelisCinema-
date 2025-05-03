package Model;

public class Studio {
    private int idStudio;
    private String namaStudio;
    private int kapasitas;

    public Studio() {}

    public Studio(int idStudio, String namaStudio, int kapasitas) {
        this.idStudio = idStudio;
        this.namaStudio = namaStudio;
        this.kapasitas = kapasitas;
    }

    // Getter-setter
    public int getIdStudio() {
        return idStudio;
    }

    public void setIdStudio(int idStudio) {
        this.idStudio = idStudio;
    }

    public String getNamaStudio() {
        return namaStudio;
    }

    public void setNamaStudio(String namaStudio) {
        this.namaStudio = namaStudio;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    @Override
    public String toString() {
        return namaStudio;
    }


}
