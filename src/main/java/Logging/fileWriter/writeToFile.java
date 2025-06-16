package Logging.fileWriter;

import java.io.FileWriter;
import java.util.ArrayList;

public class writeToFile {
    private final ArrayList<String> data;
    private final String filePath;

    public writeToFile(ArrayList<String> data, String filePath) {
        this.data = data;
        this.filePath = filePath;
    }

    public Boolean write() {
        try {
            FileWriter fileWriter = new FileWriter(this.filePath);
            for (String dataVal : this.data) {
                fileWriter.write(dataVal + System.lineSeparator());
            }
            fileWriter.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
