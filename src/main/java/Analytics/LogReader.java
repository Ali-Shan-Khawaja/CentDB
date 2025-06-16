package Analytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class LogReader {
    final private String BASE_PATH = "src/logsFiles/";
    final private String DELIMITER = "\\|";
    private final HashMap<Integer, QueryLog> queryLogHashMap = new HashMap<>();

    public LogReader() {
        this.readQueryLogfile();
    }

    public void readQueryLogfile() {
        try {
            FileReader fileReader = new FileReader(BASE_PATH + "queryLog.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {

                String[] tmpLogDetails = line.split(DELIMITER);
                queryLogHashMap.put(i, new QueryLog(tmpLogDetails[0], Double.parseDouble(tmpLogDetails[1]), tmpLogDetails[2], tmpLogDetails[3]));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, QueryLog> getQueryLogHashMap() {
        return queryLogHashMap;
    }

}
