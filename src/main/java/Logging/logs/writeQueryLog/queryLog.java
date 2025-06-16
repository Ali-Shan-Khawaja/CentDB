package Logging.logs.writeQueryLog;

import Logging.fileWriter.writeToFile;
import Logging.logFileParser.parser;
import Logging.logFileParser.queryLogParser;
import Logging.logs.logger;

import java.util.ArrayList;

public class queryLog implements logger {
    ArrayList<ArrayList<String>> data;

    public queryLog() {
        parser queryParser = new queryLogParser();
        this.data = (ArrayList<ArrayList<String>>) queryParser.parse();
    }

    public void write(String query, String timeStamp, String user, String dbName) {
        this.AddData(query, timeStamp, user, dbName);
        ArrayList<String> data = this.prepareData();
        writeToFile writeToFile = new writeToFile(data, "src/logsFiles/queryLog.txt");
        writeToFile.write();

    }


    public void AddData(String query, String timeStamp, String user, String dbName) {
        ArrayList<String> writeData = new ArrayList<>();
        writeData.add(query);
        writeData.add(timeStamp);
        writeData.add(user);
        writeData.add(dbName);

        this.data.add(writeData);
    }

    public ArrayList<String> prepareData() {
        ArrayList<String> response = new ArrayList<>();
        for (ArrayList<String> data : this.data) {
            String value = "";
            for (String dataVal : data) {
                value += dataVal + "|";
            }
            response.add(value);
        }
        return response;
    }


}
