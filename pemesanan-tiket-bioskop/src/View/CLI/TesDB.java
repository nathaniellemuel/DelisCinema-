package View.CLI;
import Utility.DBUtil;

public class TesDB {
    public static void main(String[] args) {
        if (DBUtil.getConnection() != null) {
            System.out.println("Koneksi sukses!");
        } else {
            System.out.println("Koneksi gagal.");
        }

    }
}
