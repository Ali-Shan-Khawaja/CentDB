package Logging.logs;

import java.util.ArrayList;

public interface logger {
    //void write(String eventName, String eventDesc);
    void AddData(String query, String timeStamp, String user, String dbName);

    ArrayList<String> prepareData();

    void write(String query, String timeStamp, String user, String dbName);

}
