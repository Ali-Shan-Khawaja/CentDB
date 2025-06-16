package Authentication;

public class userName {
    private static userName userName = null;
    private String user = null;

    private userName() {

    }

    public static userName getInstance() {
        if (userName == null) {
            userName = new userName();
        }
        return userName;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
