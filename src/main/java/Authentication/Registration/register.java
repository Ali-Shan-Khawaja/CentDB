package Authentication.Registration;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface register {
    boolean registerUser() throws UnsupportedEncodingException, NoSuchAlgorithmException;

    String getUserId();
}
