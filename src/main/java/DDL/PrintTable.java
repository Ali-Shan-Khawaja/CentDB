package DDL;


import Authentication.userName;
import Logging.logWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrintTable {

    final String DELIMITER = "\\|";
    final private LinkedList<HashMap<String, String>> hashMapLinkedList = new LinkedList<>();
    final private HashMap<String, TableMetaData> metaData = new HashMap<>();
    private final AlterTable alterTable = new AlterTable();
    userName userInstance = userName.getInstance();
    private String BASE_PATH = null;
    private String columnNames;
    private Date startDate;
    private Date endDate;

    public PrintTable(String dbPath) {
        BASE_PATH = dbPath;
    }

    private void printTableValues(LinkedList<HashMap<String, String>> hashMapLinkedList) {
        for (HashMap<String, String> hashMap : hashMapLinkedList) {
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                System.out.println(stringStringEntry.getKey() + " = " + stringStringEntry.getValue());
            }
            System.out.println();
        }
    }

    private void generateLog(String query) {
        AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
        long queryExecutionTime = endDate.getTime() - startDate.getTime();
        logWriter logWriter = new logWriter();
        logWriter.Query(query, this.startDate.toString(), userInstance.getUser(), alterDataBase.getSelectedDBPath().split("/")[3]);
        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], query, Long.toString(queryExecutionTime));

    }

    public void parseSelectQuery(String query) {

        this.startDate = new Date();
        query = query.replaceAll(";", "");
        List<String> allTableList = alterTable.showTables();
        String[] tokenizedQuery = query.split(" ");
        Boolean isSelectedColumns = false;
        Boolean isWhereClause = false;
        String tableName = tokenizedQuery[3];
        if (allTableList.contains(tableName + ".txt")) {
            List<String> listColumns = new ArrayList<>();
            String whereClause = null;
            if (!tokenizedQuery[1].equalsIgnoreCase("*")) {
                isSelectedColumns = true;
                listColumns = extractColumnNames(tokenizedQuery[1]);
            }
            if (tokenizedQuery.length > 4) {
                isWhereClause = true;
                whereClause = query.substring(query.indexOf("WHERE") + 6);
            }
            readTableValues(tableName + ".txt");
            LinkedList<HashMap<String, String>> selectedTable = new LinkedList<>();
            if (isSelectedColumns) {
                selectedTable = filterSelectedColumns(listColumns);
            } else {
                selectedTable = (LinkedList<HashMap<String, String>>) hashMapLinkedList.clone();
            }
            if (isWhereClause) {
                printFilteredTableValues(whereClause, selectedTable, listColumns);
            } else {
                printTableValues(selectedTable);
            }

            this.endDate = new Date();
            generateLog(query);
        } else {
            System.out.println("Table does not exist.");
        }
    }

    public List<String> extractColumnNames(String query) {
        List<String> listColumns = Arrays.asList(query.split(","));
        return listColumns;
    }


    public LinkedList<HashMap<String, String>> readTableData(String tableName) {
        try {
            FileReader fileReader = new FileReader(BASE_PATH + "/" + tableName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            boolean isFirstLine = false;
            String[] columns = {};
            while ((line = bufferedReader.readLine()) != null) {
                if (!isFirstLine) {
                    isFirstLine = true;
                    columns = line.split(DELIMITER);
                    columnNames = line;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMapLinkedList;
    }

    public LinkedList<HashMap<String, String>> readTableValues(String tableName) {
        LinkedList<HashMap<String, String>> linkedList = readTableData(tableName);
        try {
            readMetaData(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    private LinkedList<HashMap<String, String>> filterSelectedColumns(List<String> columnNames) {

        LinkedList<HashMap<String, String>> selectedTable = new LinkedList<>();

        for (HashMap<String, String> hashMap : hashMapLinkedList) {
            HashMap<String, String> tmpHashMap = new HashMap<>();
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                if (columnNames.contains(stringStringEntry.getKey())) {
                    tmpHashMap.put(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
            selectedTable.add(tmpHashMap);
        }
        return selectedTable;
    }

    private void printFilteredTableValues(String filterQuery, LinkedList<HashMap<String, String>> modifiedList, List<String> columnList) {
        String[] tokenizeQuery = filterQuery.split(" ");
        String filterColumn = tokenizeQuery[0].toLowerCase();
        String filterOperator = tokenizeQuery[1];
        String filterValue = tokenizeQuery[2];

        StringBuilder finalString = new StringBuilder();
        if (columnList.isEmpty()) {
            finalString.append(columnNames + "\n");
        } else {
            for (String columnName : columnList) {
                finalString.append(columnName + "|");
            }
            finalString.append("\n");
        }
        for (HashMap<String, String> hashMap : modifiedList) {
            StringBuilder tmpString = new StringBuilder();
            Boolean showRow = true;
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {

                if (filterColumn.equalsIgnoreCase(stringStringEntry.getKey().toLowerCase())) {
                    if (metaData.get(filterColumn).getData_type().equalsIgnoreCase("INT")) {
                        showRow = matchData(new Integer(filterValue), filterOperator, new Integer(stringStringEntry.getValue()));
                    } else {
                        showRow = matchData((filterValue), filterOperator, (stringStringEntry.getValue()));
                    }
                }
                if (showRow) {
                    tmpString.append(stringStringEntry.getValue() + "|");
                }
            }
            if (showRow) {
                finalString.append(tmpString).append("\n");
            }


        }
        System.out.println(finalString);
    }

    private boolean matchData(String filterValue, String operator, String actualValue) {
        return filterValue.equalsIgnoreCase(actualValue);
    }

    private boolean matchData(int filterValue, String operator, int actualValue) {
        switch (operator) {
            case (">"):
                return actualValue > filterValue;
            case ("<"):
                return actualValue < filterValue;
            case (">="):
                return actualValue >= filterValue;
            case ("<="):
                return actualValue <= filterValue;
            case ("="):
                return actualValue == filterValue;
            default:
                System.out.println("Operator not recognized");
                return false;
        }
    }


    private void readMetaData(String tableName) throws IOException {
        FileReader fileReader = new FileReader(BASE_PATH + "/" + tableName.replace(".txt", "MetaData.txt"));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            int i = 0;
            String[] tempColumns = line.split(DELIMITER);
            metaData.put(tempColumns[0].toLowerCase(), new TableMetaData(tempColumns[0], tempColumns[1], tempColumns[2], tempColumns[3]));
        }
    }

}
