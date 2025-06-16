package Logging.logs.writeEventLog;

import Logging.fileWriter.writeToFile;
import Logging.logFileParser.eventLogParser;
import Logging.logFileParser.parser;
import Logging.logs.logger;

import java.util.ArrayList;

public class eventLog implements logger {
    ArrayList<ArrayList<String>> data;

    public eventLog() {
        parser parser = new eventLogParser();
        this.data = (ArrayList<ArrayList<String>>) parser.parse();

    }

    public void write(String eventName, String eventDesc, String user, String dbName) {
        this.AddData(eventName, eventDesc, user, dbName);
        ArrayList<String> data = this.prepareData();
        writeToFile writeToFile = new writeToFile(data, "src/logsFiles/eventLog.txt");
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
