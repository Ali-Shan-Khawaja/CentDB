package Authentication.Login;

import java.io.IOException;

public interface login {
    boolean userLogin() throws IOException;

    String getLoginUserId();
}
