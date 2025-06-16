package Authentication.Registration;

import Authentication.FileOps.fileReader.reader;
import Authentication.FileOps.fileReader.readerImpl;
import Authentication.FileOps.fileWriter.writer;
import Authentication.FileOps.fileWriter.writerImpl;
import Authentication.Hashing.hasher;
import Authentication.Hashing.hasherImpl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class registerImpl implements register {

    String userId;
    String password;
    String securityQue;
    String securityAns;
    String passwordHash;
    String userIdHash;
    HashMap<String, HashMap<String, String>> userDetails;
    ArrayList<String> registerDetails = new ArrayList<>();

    @Override
    public boolean registerUser() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.showMenu();

        this.passwordHash = this.gethashCode(this.password);
        this.userIdHash = this.gethashCode(this.userId);
        if (isUserRegistered()) {
            return false;
        }
        return this.writeToFile();


    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    public Boolean isUserRegistered() {
        try {

            reader reader = new readerImpl();
            this.userDetails = reader.read();
            return userDetails.containsKey(this.userIdHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public Boolean writeToFile() {
        registerDetails.add(this.userIdHash);
        registerDetails.add(this.passwordHash);
        registerDetails.add(this.securityQue);
        registerDetails.add(this.securityAns);
        writer fileWriter = new writerImpl();
        return fileWriter.write(registerDetails);

    }

    public String gethashCode(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        hasher hashCode = new hasherImpl(value);
        return hashCode.generateHash();

    }

    public void showMenu() {
        System.out.println("-------------------Registration------------------------------");
        System.out.print("\t1.Enter UserId: ");
        Scanner userIdScanner = new Scanner(System.in);
        this.userId = userIdScanner.nextLine();

        System.out.print("\t2.Enter Password: ");
        Scanner passwordScanner = new Scanner(System.in);
        this.password = passwordScanner.nextLine();

        System.out.print("\t3.Enter Security Que: ");
        Scanner securityQueScanner = new Scanner(System.in);
        this.securityQue = securityQueScanner.nextLine();

        System.out.print("\t4.Enter Security Ans: ");
        Scanner securityAnsScanner = new Scanner(System.in);
        this.securityAns = securityAnsScanner.nextLine();
        System.out.println(this.userId + " " + this.password + " " + this.securityQue + " " + this.securityAns);
        System.out.println("---------------------------------------------------------------");

    }

}
