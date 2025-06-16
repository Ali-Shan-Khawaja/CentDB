package DML;

import DDL.AlterDataBase;
import Logging.logWriter;
import Transaction.GetInMemoryData;
import Transaction.InMemoryData;
import Transaction.Locking;
import Transaction.TransactionInstance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class DeleteFromTable {
    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
    private Date startDate;
    private Date endDate;
    private String LogTableName;

    public static void main(String[] args) {
//        DeleteFromTable deleteFromTable = new DeleteFromTable();
//        deleteFromTable.deleteFromTable("DELETE FROM Orders WHERE OrderID = 1;");
    }

    public void generateLog(String query) {
        long queryExecutionTime = endDate.getTime() - startDate.getTime();
        logWriter logWriter = new logWriter();
        logWriter.Query(query, this.startDate.toString(), "user", alterDataBase.getSelectedDBPath().split("/")[3]);

        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], query, Long.toString(queryExecutionTime));

        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], this.LogTableName, "-1");

    }

    public void deleteFromTable(String query) {
        this.startDate = new Date();
        String[] queryData = query.split("WHERE");
        String tableName = queryData[0].split(" ")[2];
        this.LogTableName = tableName;

        String tableFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt";
        String tableMetaDataFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt";

        CommonCode commonCode = new CommonCode();

        if (!commonCode.checkIfFileExists(tableFilePath)) {
            System.out.println("Table doesn't exist");
            return;
        }

        String[] arrayQueryData = queryData[1].split(" ");

        String columnName = arrayQueryData[1].trim();
        String columnValue = arrayQueryData[3].replaceAll("\\'", "").
                replaceAll(";", "").trim();
        String delimiter = "\\|";

        Validate validation = new Validate();

        // Data type validation
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableMetaDataFilePath));
            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (index > 0) {
                    String[] values = line.split("\\|");

                    if (Objects.equals(values[2], "null")) {
                        values[2] = String.valueOf(0);
                    }

                    if (columnName.equals(values[0])) {
                        if (!validation.isValidDataType(values[1], columnValue, Integer.parseInt(values[2]))) {
                            System.out.println("Invalid data types (or) invalid data length");
                            return;
                        }
                    }
                }
                index += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFilePath));
            String line = bufferedReader.readLine();
            int i = 0;
            int columnIndexToCheck = -1;

            TransactionInstance transactionInstance = TransactionInstance.getInstance();
            boolean isTransactionQuery = transactionInstance != null;

            Locking locking = new Locking();
            if (!(locking.canQueryBeStarted(tableName, isTransactionQuery, transactionInstance))) {
                System.out.println("Lock exists on the table");
                return;
            }

            // Hash
            int deletedCount = 0;
            if (!isTransactionQuery) {
                ArrayList<String> buffer = new ArrayList<>();
                while (line != null) {
                    if (i == 0) {
                        String[] columnNames = line.split(delimiter);
                        int j = 0;
                        for (String name : columnNames) {
                            if (name.equals(columnName)) {
                                columnIndexToCheck = j;
                            }
                            j += 1;
                        }
                        if (columnIndexToCheck == -1) {
                            System.out.println("Invalid SQL Query");
                            return;
                        }
                        buffer.add(line);
                    } else {
                        String[] columnValues = line.split(delimiter);
                        int j = 0;
                        boolean shouldDelete = false;
                        for (String value : columnValues) {
                            if (j == columnIndexToCheck) {
                                if (value.equals(columnValue)) {
                                    shouldDelete = true;
                                    break;
                                }
                            }
                            j += 1;
                        }
                        if (!shouldDelete) {
                            buffer.add(line);
                        } else {
                            deletedCount += 1;
                        }
                    }
                    line = bufferedReader.readLine();
                    i += 1;
                }
                if (deletedCount == 0) {
                    System.out.println("No rows to delete");
                } else {
                    commonCode.writeBufferToFile(tableFilePath, buffer);

                    this.endDate = new Date();
                    generateLog(query);

                    System.out.println("Deleted Successfully");

                }
            } else {
                locking.addToLockTable(tableName, transactionInstance.hashCode(), "LOCK");
                GetInMemoryData getInMemoryData = new GetInMemoryData();
                LinkedList<HashMap<String, String>> linkedList = getInMemoryData.getInMemoryData(tableName);

                LinkedList<HashMap<String, String>> newLinkedList = new LinkedList<>();

                for (HashMap<String, String> hashMap : linkedList) {
                    if (hashMap.containsKey(columnName) &&
                            Objects.equals(hashMap.get(columnName), columnValue)) {
                        continue;
                    } else {
                        newLinkedList.add(hashMap);
                    }
                }
                InMemoryData.getHashMap().put(tableName, newLinkedList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
