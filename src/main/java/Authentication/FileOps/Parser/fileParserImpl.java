package Authentication.FileOps.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class fileParserImpl implements fileParser {
    String filePath;
    HashMap<String, HashMap<String, String>> UserDetails = new HashMap<>();

    public fileParserImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public HashMap<String, HashMap<String, String>> parse() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.filePath));

        String data = bufferedReader.readLine();

        while (data != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(data);
            HashMap<String, String> user = new HashMap<>();
            stringBuilder.append(data);
            String[] userDetail = stringBuilder.toString().split("\\|");
            //System.out.println(userDetail[0]+ " " + userDetail[1]+ " " + userDetail[2] + " " + userDetail[3]);
            user.put("password", userDetail[1]);
            user.put("question", userDetail[2]);
            user.put("answer", userDetail[3]);
            this.UserDetails.put(userDetail[0], user);
            data = bufferedReader.readLine();
        }
        //this.printFile();
        return this.UserDetails;
    }

    public void printFile() {
        this.UserDetails.forEach((key1, value1) -> {
            System.out.println(value1);
            value1.forEach((key, value) -> {
                System.out.println(key + " ->" + value);
            });
        });
    }
}
