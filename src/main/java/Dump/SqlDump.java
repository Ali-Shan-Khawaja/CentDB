package Dump;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SqlDump {

    public static void main(String[] args) {
    }

    public static void createDump() throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter("src\\main\\resources\\dump.txt"));
        Scanner db = new Scanner(System.in);
        System.out.println("Enter the database name:");
        String dbname = db.nextLine();

        //Fetch all the files of the database.
        String path = "src\\main\\resources\\" + dbname;
        File f = new File(path);
        String[] files = f.list();

        ArrayList<String> metaDataList = new ArrayList<>();

        // Creating DUMP for the MetaData.
        assert files != null;
        for (String file : files) {

            if (file.contains("MetaData.txt") && !file.contains("TransactionMetaData.txt")) {
                String path1 = path + "\\" + file;
                try (BufferedReader metaDataFile = new BufferedReader(new FileReader(path1))) {
                    String data = metaDataFile.readLine();

                    String[] line = data.split("\\|");
                    data = metaDataFile.readLine();
                    while (data != null) {
                        line = data.split("\\|");
                        metaDataList.addAll(Arrays.asList(line));
                        data = metaDataFile.readLine();
                    }

                    StringBuilder query = new StringBuilder("CREATE TABLE ");
                    String fileName = file.substring(0, file.indexOf("Meta"));
                    query.append(fileName);
                    query.append(" ( ");
                    for (int k = 0; k < metaDataList.size(); k = k + line.length) {
                        query.append(", ").append(metaDataList.get(k)).append(" ").append(metaDataList.get(k + 1)).append(" ");
                        if (!metaDataList.get(k + 2).contains("null")) {
                            query.append(" (").append(metaDataList.get(k + 2)).append(")  ");
                        }
                        query.append(metaDataList.get(k + 3));
                    }
                    query.append(" );");
                    query = new StringBuilder(query.toString().replaceFirst(",", ""));
                    out.write(query + "\n");
                    metaDataList.clear();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            //-----------------------------------------------------------------------------------------------
        }
        for (String file : files) {
            if (!file.contains("MetaData.txt")) {
                String path1 = path + "\\" + file;
                BufferedReader metaDataFile = new BufferedReader(new FileReader(path1));
                String data = metaDataFile.readLine();

                String[] line = data.split("\\|");
                data = metaDataFile.readLine();
                while (data != null) {
                    line = data.split("\\|");
                    metaDataList.addAll(Arrays.asList(line));
                    data = metaDataFile.readLine();
                }

                StringBuilder query1 = null;
                for (int z = 0; z < metaDataList.size() - 1; z = z + line.length) {
                    query1 = new StringBuilder("INSERT INTO ").append(file, 0, file.indexOf(".txt")).append(" VALUES ").append(" ( ").append(", ");
                    for (int i = 0; i < line.length - 1; i++) {
                        query1.append(" '").append(metaDataList.get(z + i)).append("'").append(" , ");
                    }
                    query1.append(" '").append(metaDataList.get(z + line.length - 1)).append("'").append(" );");
                    query1 = new StringBuilder(query1.toString().replaceFirst(",", ""));
                    out.write(query1 + "\n");
                }
                metaDataList.clear();
            }
        }
        System.out.println("Dump file for the database: " + dbname + " has been created.\n");
        out.close();
    }

}






