package Model;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(int idUser, String username, String password, String role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS ---
    public int getIdUser() {
        return idUser;
    }

    public String getUsername() { // Metode yang dibutuhkan
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // --- SETTERS ---
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
