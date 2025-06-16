package Logging.logFileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class queryLogParser implements parser {
    public ArrayList<ArrayList<String>> data;
    String filePath;

    public queryLogParser() {
        this.filePath = "src/logsFiles/queryLog.txt";
        this.data = new ArrayList<>();
    }

    @Override
    public Object parse() {
        try {
            File fileObj = new File(filePath);
            Scanner fileScanner = new Scanner(fileObj);
            while (fileScanner.hasNext()) {
                String data = fileScanner.nextLine();
                String[] dataArr = data.split("\\|");
                ArrayList<String> dataVal = new ArrayList<>();
                dataVal.add(dataArr[0]);
                dataVal.add(dataArr[1]);
                dataVal.add(dataArr[2]);
                dataVal.add(dataArr[3]);
                this.data.add(dataVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.data;

    }
}
