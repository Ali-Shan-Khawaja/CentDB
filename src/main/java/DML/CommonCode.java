package DML;

import java.io.*;
import java.util.ArrayList;


public class CommonCode {

    public boolean checkIfFileExists(String filePath) {
        File f = new File(filePath);
        return f.exists();
    }

    public void writeBufferToFile(String filePath, ArrayList<String> buffer) {

        PrintWriter writer;
        try {
            writer = new PrintWriter(filePath);
            writer.print("");
            writer.flush();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            int index = 0;
            for (String value : buffer) {
                bufferedWriter.write(value);
                if (index < buffer.size() - 1) {
                    bufferedWriter.newLine();
                }
                index += 1;
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
