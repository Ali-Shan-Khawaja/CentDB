package Authentication.FileOps.fileWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class writerImpl implements writer {
    private final String filePath = "src/User_Profile.txt";
    private String userRegisterValue = "";

    @Override
    public Boolean write(ArrayList<String> userDetails) {
        try {
            FileWriter myWriter = new FileWriter(this.filePath, true);
            this.buildString(userDetails);
            myWriter.write(this.userRegisterValue + "\n");
            myWriter.close();
            System.out.println("User Registered Successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void buildString(ArrayList<String> userDetails) {
        StringBuilder strBuilder = new StringBuilder(this.userRegisterValue);
        for (String value : userDetails) {
            strBuilder.append(value);
            strBuilder.append("|");
        }
        this.userRegisterValue = strBuilder.toString();

    }
}
