package sagrada.model;

public class Account {
    private final String username;
    private final String password;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account(String username) {
        this.username = username;
        this.password = "";
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
