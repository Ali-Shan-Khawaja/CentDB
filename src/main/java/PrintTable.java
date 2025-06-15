import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PrintTable {

    final String DELIMITER = "\\|";
    final String BASE_PATH = "src/main/resources";


    final private LinkedList<HashMap<String, String>> hashMapLinkedList = new LinkedList<>();

    private void printTableValues() {
        for (HashMap<String, String> hashMap : hashMapLinkedList) {
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                System.out.println(stringStringEntry.getKey() + " = " + stringStringEntry.getValue());
            }
            System.out.println();
        }
    }

    public void readTableValues(String tableName) {
        try {
            FileReader fileReader = new FileReader(BASE_PATH + tableName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            boolean isFirstLine = false;
            String[] columns = {};
            while ((line = bufferedReader.readLine()) != null) {
                if (!isFirstLine) {
                    isFirstLine = true;
                    columns = line.split(DELIMITER);
                    continue;
                }

                int i = 0;
                String[] tempRows = line.split(DELIMITER);

                HashMap<String, String> hashMap = new HashMap<>();
                for (String rowValue : tempRows) {
                    hashMap.put(columns[i], rowValue);
                    i++;
                }
                hashMapLinkedList.add(hashMap);
            }
            printTableValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
