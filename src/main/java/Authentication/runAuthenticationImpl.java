package Authentication;

import Authentication.Authenticators.authenticator;
import Authentication.Authenticators.authenticatorImpl;

public class runAuthenticationImpl implements runAuthentication {
    public String user;

    @Override
    public String run() {
        try {
            authenticator authenticator = new authenticatorImpl();
            while (true) {
                if (authenticator.authenticate()) {
                    String user = authenticator.getUserName();
                    userName userNameInstance = userName.getInstance();
                    userNameInstance.setUser(user);

                    return this.user;
                } else {
                    System.out.println("Error Occurred while Authentication. Try again");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
