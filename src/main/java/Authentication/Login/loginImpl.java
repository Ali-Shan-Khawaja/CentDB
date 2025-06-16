package Authentication.Login;

import Authentication.FileOps.fileReader.reader;
import Authentication.FileOps.fileReader.readerImpl;
import Authentication.Hashing.hasher;
import Authentication.Hashing.hasherImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class loginImpl implements login {
    String userId;
    String password;
    String userIdHash;
    String passwordHash;
    String answer;
    HashMap<String, HashMap<String, String>> userDetails;

    @Override
    public boolean userLogin() throws IOException {
        this.showMenu();

        return isUserValid() && checkSecurityAns();
    }

    public String getLoginUserId() {
        //System.out.println("Login: "+this.userId);
        return this.userId;
    }

    public Boolean checkSecurityAns() {
        String question = this.userDetails.get(this.userIdHash).get("question");
        System.out.println("\tEnter Answer for :");
        System.out.print("\t" + question + ": ");
        Scanner ansScanner = new Scanner(System.in);
        this.answer = ansScanner.nextLine();

        System.out.println("------------------------------------------------------------");

        Boolean response = Objects.equals(userDetails.get(this.userIdHash).get("answer"), this.answer);
        return response;
    }

    public void showMenu() throws IOException {
        System.out.println("--------------------------Login-----------------------------");
        System.out.print("\t1.Enter UserId: ");
        Scanner userIdScanner = new Scanner(System.in);
        this.userId = userIdScanner.nextLine();

        System.out.print("\t2.Enter Password: ");
        Scanner passwordScanner = new Scanner(System.in);
        this.password = passwordScanner.nextLine();


    }

    public String gethashCode(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        hasher hashCode = new hasherImpl(value);
        return hashCode.generateHash();

    }

    public Boolean isUserValid() throws IOException {
        try {

            this.userIdHash = this.gethashCode(this.userId);
            this.passwordHash = this.gethashCode(this.password);
        } catch (Exception e) {
            //do nothing
        }
        reader reader = new readerImpl();
        this.userDetails = reader.read();
        if (userDetails.containsKey(this.userIdHash)) {
            Boolean response = Objects.equals(userDetails.get(this.userIdHash).get("password"), this.passwordHash);
            return response;
        } else {
            return false;
        }
    }
}
