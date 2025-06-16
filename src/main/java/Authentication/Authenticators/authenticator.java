package Authentication.Authenticators;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface authenticator {
    boolean authenticate() throws IOException, NoSuchAlgorithmException;

    String getUserName();
}
