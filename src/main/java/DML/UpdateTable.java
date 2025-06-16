package DML;

import Authentication.userName;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateTable {

    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
    userName userInstance = userName.getInstance();
    private Date startDate;
    private Date endDate;

    public static void main(String[] args) {
//        UpdateTable updateTable = new UpdateTable();
//        updateTable.updateTable("UPDATE Persons SET PersonID = 35 WHERE FirstName = 'Abhishek';");
//        updateTable.updateTable("UPDATE Orders SET PersonID = 35 WHERE OrderID = 1;");
    }

    public void generateLog(String query) {
        long queryExecutionTime = endDate.getTime() - startDate.getTime();
        logWriter logWriter = new logWriter();
        logWriter.Query(query, this.startDate.toString(), userInstance.getUser(), alterDataBase.getSelectedDBPath().split("/")[3]);
        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], query, Long.toString(queryExecutionTime));

    }

    public void updateTable(String query) {
        this.startDate = new Date();
        String[] queryData = query.split("SET");
        String tableName = queryData[0].trim().split(" ")[1];
        String[] whereQueryData = queryData[1].split("WHERE");
        String[] columnData = whereQueryData[0].trim().split("=");
        String[] whereColumnData = whereQueryData[1].trim().split("=");

        String columnName = columnData[0].trim();
        String columnValue = columnData[1].trim();

        String whereName = whereColumnData[0].trim();
        String whereValue = whereColumnData[1].replaceAll("\\'", "").trim().
                replaceAll(";", "");

        String tableFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt";
        String tableMetaDataFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt";

        CommonCode commonCode = new CommonCode();

        if (!commonCode.checkIfFileExists(tableFilePath)) {
            System.out.println("Table doesn't exist");
            return;
        }

        Validate validation = new Validate();

        HashMap<String, String> primaryKeyMap = new HashMap();
        ArrayList<HashMap<String, String>> foreignKeyList = new ArrayList<>();

        // check if user is trying to update primary key or foreign key and data type validation
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

                    if (whereName.equals(values[0])) {
                        if (!validation.isValidDataType(values[1], whereValue, Integer.parseInt(values[2]))) {
                            System.out.println("Invalid data types (or) invalid data length");
                            return;
                        }
                    }

                    if (columnName.equals(values[0]) && Objects.equals(values[3], "PRIMARY KEY")) {
                        primaryKeyMap.put("column", values[0]);
                        primaryKeyMap.put("value", columnValue);
                        break;
                    }

                    if (columnName.equals(values[0]) && values[3].contains("FOREIGN KEY")) {
                        String[] tempValues = values[3].split("FOREIGN KEY")[1].trim().split(" ");

                        HashMap<String, String> foreignKeyMap = new HashMap<>();
                        foreignKeyMap.put("column", columnName);
                        foreignKeyMap.put("table", tempValues[0]);
                        foreignKeyMap.put("foreignColumn", tempValues[1]);
                        foreignKeyMap.put("value", columnValue);
                        foreignKeyList.add(foreignKeyMap);
                    }
                }
                index += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check if primary key exists
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFilePath));
            String line;
            int index = 0;
            int columnIndex = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (index > 0) {
                    String[] values = line.split("\\|");
                    if (values[columnIndex].equals(primaryKeyMap.get("column"))) {
                        System.out.println("Primary Key already exists");
                        return;
                    }
                } else {
                    String[] values = line.split("\\|");
                    int tempIndex = 0;
                    for (String value : values) {
                        if (value.equals(primaryKeyMap.get("value"))) {
                            columnIndex = tempIndex;
                            break;
                        }
                        tempIndex += 1;
                    }
                }
                index += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (HashMap<String, String> foreignKey : foreignKeyList) {
            // check if foreign key exists
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(alterDataBase.getSelectedDBPath() + "/"
                        + foreignKey.get("table") + ".txt"));
                String line;
                int currentIndex = 0;
                int columnIndex = 0;
                boolean containsForeignKey = false;
                while ((line = bufferedReader.readLine()) != null) {
                    if (currentIndex == 0) {
                        String[] currentValues = line.split("\\|");
                        int tempIndex = 0;
                        for (String currentValue : currentValues) {
                            if (currentValue.equals(foreignKey.get("column"))) {
                                columnIndex = tempIndex;
                                break;
                            }
                        }
                    } else {
                        String[] currentValues = line.split("\\|");
                        if (currentValues[columnIndex].equals(foreignKey.get("value"))) {
                            containsForeignKey = true;
                            break;
                        }
                    }
                    currentIndex += 1;
                }
                if (!containsForeignKey) {
                    System.out.println("Foreign key constraint violation");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String delimiter = "\\|";

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFilePath));
            String line = bufferedReader.readLine();
            int i = 0;
            int columnIndexToUpdate = -1;
            int columnIndexToCheck = -1;

            TransactionInstance transactionInstance = TransactionInstance.getInstance();
            boolean isTransactionQuery = transactionInstance != null;

            Locking locking = new Locking();
            if (!(locking.canQueryBeStarted(tableName, isTransactionQuery, transactionInstance))) {
                System.out.println("Lock exists on the table");
                return;
            }

            if (!isTransactionQuery) {
                ArrayList<String> buffer = new ArrayList<>();
                while (line != null) {
                    if (i == 0) {
                        String[] columnNames = line.split(delimiter);
                        int j = 0;
                        for (String name : columnNames) {
                            if (name.equals(columnName)) {
                                columnIndexToUpdate = j;
                            }
                            if (name.equals(whereName)) {
                                columnIndexToCheck = j;
                            }
                            j += 1;
                        }
                        if (columnIndexToUpdate == -1 || columnIndexToCheck == -1) {
                            System.out.println("Invalid SQL Query");
                            return;
                        }
                        buffer.add(line);
                    } else {
                        String[] columnValues = line.split(delimiter);
                        int j = 0;
                        boolean shouldUpdate = false;
                        for (String value : columnValues) {
                            if (j == columnIndexToCheck) {
                                if (value.equals(whereValue)) {
                                    shouldUpdate = true;
                                    break;
                                }
                            }
                            j += 1;
                        }
                        if (shouldUpdate) {
                            String[] newLine = line.split(delimiter);
                            newLine[columnIndexToUpdate] = columnValue;
                            line = Stream.of(newLine).map(String::valueOf).collect(Collectors.joining("|"));
                        }
                        buffer.add(line);
                    }
                    line = bufferedReader.readLine();
                    i += 1;
                }
                if (buffer.size() > 1) {
                    commonCode.writeBufferToFile(tableFilePath, buffer);

                    endDate = new Date();
                    generateLog(query);

                    System.out.println("Updated Successfully");


                } else {
                    System.out.println("No values to update");
                }
            } else {
                locking.addToLockTable(tableName, transactionInstance.hashCode(), "LOCK");
                GetInMemoryData getInMemoryData = new GetInMemoryData();
                LinkedList<HashMap<String, String>> linkedList = getInMemoryData.getInMemoryData(tableName);
                for (HashMap<String, String> hashMap : linkedList) {
                    if (hashMap.containsKey(whereName) && hashMap.containsKey(columnName) &&
                            Objects.equals(hashMap.get(whereName), whereValue.replaceAll("'", ""))) {
                        hashMap.put(columnName, columnValue);
                    }
                }
                InMemoryData.getHashMap().put(tableName, linkedList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}