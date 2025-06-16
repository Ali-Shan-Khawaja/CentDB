package DML;

import DDL.AlterDataBase;
import Logging.logWriter;
import Transaction.GetInMemoryData;
import Transaction.InMemoryData;
import Transaction.Locking;
import Transaction.TransactionInstance;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class InsertIntoTable {

    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
    private boolean isTransactionQuery = false;
    private Date startDate;
    private Date endDate;
    private String LogTableName;

    public static void main(String[] args) {
//        InsertIntoTable insertIntoTable = new InsertIntoTable();
//        insertIntoTable.insertIntoTable("INSERT INTO Orders (id, name, age, gender) VALUES (3, 'temp1', 21, 'female');");
//        insertIntoTable.insertIntoTable("INSERT INTO Orders (OrderID, OrderNumber, PersonID) VALUES (10, 100, 1);");
//        insertIntoTable.insertIntoTable("INSERT INTO Orders (OrderID, OrderNumber, PersonID) VALUES (10, 100, 2);");

    }

    public void generateLog(String query) {
        long queryExecutionTime = endDate.getTime() - startDate.getTime();
        logWriter logWriter = new logWriter();
        logWriter.Query(query, this.startDate.toString(), "user", alterDataBase.getSelectedDBPath().split("/")[3]);
        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], query, Long.toString(queryExecutionTime));
        logWriter.General("state", alterDataBase.getSelectedDBPath().split("/")[3], this.LogTableName, "1");
    }

    public void insertIntoTable(String query) {
        this.startDate = new Date();
        TransactionInstance transactionInstance = TransactionInstance.getInstance();

        if (transactionInstance != null) {
            isTransactionQuery = true;
        }

        String path = alterDataBase.getSelectedDBPath();
        if (path == null) {
            System.out.println("No Database selected");
        } else {
            String[] tableValues = query.split("INTO")[1].split("VALUES");
            tableValues[0] = tableValues[0].trim();
            tableValues[1] = tableValues[1].trim();

            String tableName = tableValues[0].substring(0, tableValues[0].indexOf(" "));

            this.LogTableName = tableName;

            String columnNames = tableValues[0].substring(tableValues[0].indexOf(" ") + 1);
            String values = tableValues[1];

            String[] actualColumnValues = columnNames.substring(1, columnNames.length() - 1).split(", ");
            String[] actualValues = values.substring(1, values.length() - 1).split(", ");

            String tableFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt";
            String tableMetaDataFilePath = alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt";

            CommonCode commonCode = new CommonCode();
            if (!commonCode.checkIfFileExists(tableFilePath)) {
                System.out.println("Table doesn't exist");
                return;
            }

            if (actualColumnValues.length != actualValues.length) {
                System.out.println("Column names and Column Values are of different length");
                return;
            }
            Validate validation = new Validate();
            String primaryKey = "";

            ArrayList<HashMap<String, String>> foreignKeys = new ArrayList<>();

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(tableMetaDataFilePath));
                String line;
                int index = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    if (index > 0) {
                        String[] data = line.split("\\|");
                        if (Objects.equals(data[2], "null")) {
                            data[2] = String.valueOf(0);
                        }
                        if (!validation.isValidDataType(data[1], actualValues[index - 1], Integer.parseInt(data[2]))) {
                            System.out.println("Invalid data types (or) invalid data length");
                            return;
                        }
                        if (Objects.equals(data[3], "PRIMARY KEY")) {
                            primaryKey = data[0];
                        }
                        if (data[3].contains("FOREIGN KEY")) {
                            HashMap<String, String> foreignKey = new HashMap<>();
                            foreignKey.put("column", data[0]);
                            String[] currentForeignKeys = data[3].split("FOREIGN KEY")[1].trim().split(" ");
                            foreignKey.put("table", currentForeignKeys[0]);
                            foreignKey.put("foreignColumn", currentForeignKeys[1]);

                            int actualColumnIndex = 0;
                            for (String val : actualColumnValues) {
                                if (Objects.equals(val, data[0])) {
                                    foreignKey.put("columnData", actualValues[actualColumnIndex]);
                                    break;
                                }
                                actualColumnIndex += 1;
                            }
                            foreignKeys.add(foreignKey);
                        }
                    }
                    index += 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!Objects.equals(primaryKey, "") && !Arrays.asList(actualColumnValues).contains(primaryKey)) {
                System.out.println("Primary key is not present in the query");
                return;
            }

            for (HashMap<String, String> foreignKey : foreignKeys) {
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
                            if (currentValues[columnIndex].equals(foreignKey.get("columnData"))) {
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

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFilePath));
                String line;
                int index = 0;
                int primaryKeyIndex = 0;

                HashSet<String> primaryKeyHashSet = new HashSet<>();

                while ((line = bufferedReader.readLine()) != null) {
                    if (index > 0) {
                        String[] currentLine = line.split("\\|");
                        primaryKeyHashSet.add(currentLine[primaryKeyIndex]);
                    } else {
                        String[] currentLine = line.split("\\|");

                        int tempIndex = 0;
                        for (String columnName : currentLine) {
                            if (Objects.equals(columnName, primaryKey)) {
                                primaryKeyIndex = tempIndex;
                                break;
                            }
                            tempIndex += 1;
                        }
                    }
                    index += 1;
                }
                String[] tempArray = values.split("\\(")[1].split("\\)");
                if (tempArray.length > 0) {
                    if (primaryKeyHashSet.contains(tempArray[0].split(",")[primaryKeyIndex])) {
                        System.out.println("Duplicate Primary Key");
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Locking locking = new Locking();
            if (!(locking.canQueryBeStarted(tableName, isTransactionQuery, transactionInstance))) {
                System.out.println("Lock exists on the table");
                return;
            }

            if (!isTransactionQuery) {
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tableFilePath, true));
                    String result = Stream.of(actualValues).map(String::valueOf).collect(Collectors.joining("|"));
                    result = result.replaceAll("\\'", "");
                    bufferedWriter.newLine();
                    bufferedWriter.append(result);
                    bufferedWriter.close();

                    endDate = new Date();
                    generateLog(query);

                    System.out.println("Inserted Successfully");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                locking.addToLockTable(tableName, transactionInstance.hashCode(), "LOCK");
                HashMap<String, String> tempHashMap = new HashMap<>();

                GetInMemoryData getInMemoryData = new GetInMemoryData();
                LinkedList<HashMap<String, String>> linkedList = getInMemoryData.getInMemoryData(tableName);

                for (int i = 0; i < actualColumnValues.length; i++) {
                    tempHashMap.put(actualColumnValues[i], actualValues[i]);
                }

                linkedList.add(tempHashMap);
                InMemoryData.getHashMap().put(tableName, linkedList);
            }
        }
    }
}
