package Logging;

import Logging.logFileParser.generalLogParser;
import Logging.logFileParser.parser;
import Logging.logs.logger;
import Logging.logs.writeEventLog.eventLog;
import Logging.logs.writeQueryLog.queryLog;
import Logging.logs.wrtieGeneralLog.generalLog;

import java.util.ArrayList;
import java.util.HashMap;

public class logWriter {
    public void Event(String eventName, String eventDesc, String user, String dbName) {
        logger eventLogger = new eventLog();
        eventLogger.write(eventName, eventDesc, user, dbName);
    }

    public void Query(String query, String timeStamp, String user, String dbName) {
        logger queryLogger = new queryLog();
        queryLogger.write(query, timeStamp, user, dbName);

    }

    public void General(String Category, String dbName, String arg1, String arg2) {
        // Category -> query / state
        // DbName -> database Name
        // arg1 -> Category whether the state of the table // OR Query statement
        // arg2 -> Time taken for execution of // No. of rows in the table if the log is about state
        parser parser = new generalLogParser();
        HashMap<String, ArrayList<ArrayList<String>>> data = (HashMap<String, ArrayList<ArrayList<String>>>) parser.parse();
        generalLog generalLog = new generalLog();
        generalLog.write(Category, dbName, arg1, arg2);


    }

}
