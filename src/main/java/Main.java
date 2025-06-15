import Authentication.runAuthenticationImpl;
import Menu.Menu;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        runAuthenticationImpl runAuthentication = new runAuthenticationImpl();
        runAuthentication.run();


        Menu menu = new Menu();
        menu.run();
    }
}
