package Logging.logs.wrtieGeneralLog;

import Logging.fileWriter.writeToFile;
import Logging.logFileParser.generalLogParser;
import Logging.logFileParser.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class generalLog {
    public HashMap<String, ArrayList<ArrayList<String>>> data;

    public generalLog() {
        parser parser = new generalLogParser();
        this.data = (HashMap<String, ArrayList<ArrayList<String>>>) parser.parse();
    }

    public void write(String category, String dbName, String arg1, String arg2) {
        this.AddData(category, dbName, arg1, arg2);
        ArrayList<String> data = this.prepareData();
        writeToFile writeToFile = new writeToFile(data, "src/logsFiles/generalLog.txt");
        writeToFile.write();

        //System.out.println(data);
    }

    public void AddData(String category, String dbName, String arg1, String arg2) {
        ArrayList<String> argsVal = new ArrayList<>();
        argsVal.add(dbName);
        argsVal.add(arg1);
        argsVal.add(arg2);
        ArrayList<ArrayList<String>> dataValQuery = this.data.get("query");
        ArrayList<ArrayList<String>> dataValState = this.data.get("state");
        if (Objects.equals(category, "query")) {

            dataValQuery.add(argsVal);

        } else if (Objects.equals(category, "state")) {

            dataValState.add(argsVal);

        }

        this.data.put("query", dataValQuery);
        this.data.put("state", dataValState);

    }

    public ArrayList<String> prepareData() {
        ArrayList<String> response = new ArrayList<>();
        this.data.forEach((key, value) -> {

            for (ArrayList<String> data : value) {
                String respData = "";
                for (String dataVal : data) {
                    respData += dataVal + "|";
                }
                respData = key + "|" + respData;
                response.add(respData);
            }

        });
        return response;
    }


}
