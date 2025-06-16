package Logging.logFileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class generalLogParser implements parser {
    private final String filePath;
    public HashMap<String, ArrayList<ArrayList<String>>> data;

    public generalLogParser() {
        this.filePath = "src/logsFiles/generalLog.txt";
        this.data = new HashMap<>();
    }

    @Override
    public Object parse() {
        ArrayList<ArrayList<String>> query = new ArrayList<>();
        ArrayList<ArrayList<String>> state = new ArrayList<>();
        try {
            File fileObj = new File(this.filePath);
            Scanner fileScanner = new Scanner(fileObj);
            while (fileScanner.hasNext()) {
                String data = fileScanner.nextLine();

                String[] dataArr = data.split("\\|");

                ArrayList<String> dataVal = new ArrayList<>();
                dataVal.add(dataArr[1]);
                dataVal.add(dataArr[2]);
                dataVal.add(dataArr[3]);

                if (Objects.equals(dataArr[0].strip(), "query")) {
                    query.add(dataVal);
                } else if (Objects.equals(dataArr[0].strip(), "state")) {
                    state.add(dataVal);
                }
            }
        } catch (Exception e) {
            //
        }
        this.data.put("query", query);
        this.data.put("state", state);
        return this.data;
    }
}
