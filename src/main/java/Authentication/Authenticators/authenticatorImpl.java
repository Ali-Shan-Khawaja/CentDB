package Authentication.Authenticators;

import Authentication.Login.login;
import Authentication.Login.loginImpl;
import Authentication.Registration.register;
import Authentication.Registration.registerImpl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;

public class authenticatorImpl implements authenticator {

    String userName = "";

    @Override
    public boolean authenticate() throws IOException, NoSuchAlgorithmException {

        return this.choseOptions();
    }

    @Override
    public String getUserName() {

        //System.out.println("Authenticator: "+this.userName);
        return this.userName;
    }

    public boolean choseOptions() throws IOException, NoSuchAlgorithmException {
        boolean response;
        while (true) {
            String option = this.ShowMenu();
            if (Objects.equals(option, "1")) {
                login login = new loginImpl();

                response = login.userLogin();
                this.userName = login.getLoginUserId();
                break;
            } else if (Objects.equals(option, "2")) {
                register register = new registerImpl();
                response = register.registerUser();
                this.userName = register.getUserId();
                break;
            } else {
                System.out.println("\tEnter Valid Option");
            }

        }
        return response;
    }

    public String ShowMenu() {
        System.out.println("-------------------Welcome Login Required--------------------------");
        System.out.println("\t1.Login");
        System.out.println("\t2.Register");
        System.out.print("\tEnter your option:");
        Scanner option = new Scanner(System.in);
        String optionValue = option.nextLine();
        System.out.println("-------------------------------------------------------------------");
        return optionValue;
    }
}
