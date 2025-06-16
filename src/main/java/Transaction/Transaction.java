package Transaction;

import DDL.AlterDataBase;
import DML.DeleteFromTable;
import DML.InsertIntoTable;
import DML.UpdateTable;
import QueryParser.QueryParser;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;


public class Transaction {

    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();

    String filePath = alterDataBase.getSelectedDBPath() + "/" + "TransactionMetaData.txt";

    File file = new File(filePath);
    QueryParser queryParser = new QueryParser();

    public Transaction() {
        if (!file.exists()) {
            File file = new File(filePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextTransaction(String query) {
        Locking locking = new Locking();
        TransactionInstance.setInstance();
        if (Objects.equals(queryParser.queryType(query), "BEGIN_TRANSACTION")) {
            locking.beginTransaction("BEGIN TRANSACTION");
        } else if (Objects.equals(queryParser.queryType(query), "END_TRANSACTION")) {
            TransactionInstance.clearInstance();
            locking.endTransaction("END TRANSACTION");
            for (Map.Entry mapElement : InMemoryData.getHashMap().entrySet()) {
                System.out.println(mapElement.getKey());

                try {
                    String fileName = AlterDataBase.getDBInstance().getSelectedDBPath() + "/" + mapElement.getKey() + ".txt";
                    PrintWriter pw = new PrintWriter(fileName);
                    pw.close();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));

                    int index = 0;
                    LinkedList<HashMap<String, String>> values = (LinkedList<HashMap<String, String>>) mapElement.getValue();
                    for (HashMap<String, String> value : values) {
                        String keys = "";
                        String tempValues = "";
                        for (Map.Entry currentElement : value.entrySet()) {
                            String currentValue = currentElement.getValue().toString().replaceAll("'", "");
                            if (index == 0) {
                                keys += currentElement.getKey() + "|";
                            }
                            tempValues += currentValue + "|";
                        }
                        if (index == 0) {
                            bufferedWriter.write(keys.substring(0, keys.length() - 1));
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.write(tempValues.substring(0, tempValues.length() - 1));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        index += 1;
                    }
                    bufferedWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            TransactionInstance.getInstance();
            switch (queryParser.queryType(query)) {
                case "INSERT_TABLE":
                    InsertIntoTable insertIntoTable = new InsertIntoTable();
                    insertIntoTable.insertIntoTable(query);
                    break;
                case "UPDATE_TABLE":
                    UpdateTable updateTable = new UpdateTable();
                    updateTable.updateTable(query);
                    break;
                case "DELETE_TABLE":
                    DeleteFromTable deleteFromTable = new DeleteFromTable();
                    deleteFromTable.deleteFromTable(query);
                    break;
                default:
                    System.out.println("Error! Only insert, update and delete are supported in transaction");
            }
        }
    }
}
