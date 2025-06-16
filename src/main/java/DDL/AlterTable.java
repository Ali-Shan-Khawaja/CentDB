//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package DDL;

import Authentication.userName;
import Logging.logWriter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class AlterTable {
    private final String tableBasePath = null;
    private final AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
    userName userInstance = userName.getInstance();

    private Date startDate;
    private Date endDate;

    public AlterTable() {
    }

    private void generateLog(String query) {
        long queryExecutionTime = endDate.getTime() - startDate.getTime();
        logWriter logWriter = new logWriter();
        logWriter.Query(query, this.startDate.toString(), userInstance.getUser(), alterDataBase.getSelectedDBPath().split("/")[3]);
        logWriter.General("query", alterDataBase.getSelectedDBPath().split("/")[3], query, Long.toString(queryExecutionTime));

    }

    public List<String> showTables() {
        String pathOfTables = this.alterDataBase.getSelectedDBPath();
        if (pathOfTables == null) {
            System.out.println("No Database selected");
            return null;
        } else {
            File dbBaseDir = new File(pathOfTables);
            List<String> listDB = new ArrayList();
            if (dbBaseDir.exists()) {
                String[] contents = dbBaseDir.list();

                for (int i = 0; i < contents.length; ++i) {
                    if (contents[i].contains(".txt")) {
                        // System.out.println(contents[i]);
                        listDB.add(contents[i]);
                    }
                }
            } else {
                System.out.println("No Data Found");
            }

            return listDB;
        }
    }


    public void parseCreateQuery(String query) {
        try {
            Boolean queryContainsConstraint = false;
            String queryBeforeConstraint = "";
            String queryAfterConstraint = "";
            if (query.contains("PRIMARY KEY") || query.contains("FOREIGN KEY")) {
                queryContainsConstraint = true;
                queryBeforeConstraint = query.substring(0, query.indexOf("PRIMARY KEY"));
                queryAfterConstraint = query.substring(query.indexOf("PRIMARY KEY"));
                //System.out.println(queryBeforeConstraint);
                //System.out.println(queryAfterConstraint);
                System.out.println();
                HashMap<String, String> hashMapConstraints = constraintHashMap(queryAfterConstraint);
//            Iterator constraintIterator = hashMapConstraints.entrySet().iterator();
//            while (constraintIterator.hasNext()){
//                Entry mapElement = (Entry)constraintIterator.next();
//                System.out.println("Column Name "+mapElement.getKey()+" Constraint "+mapElement.getValue());
//            }

                Boolean tablecreated = createTable(queryBeforeConstraint, hashMapConstraints);

            } else {
                // System.out.println(query);
                Boolean tableCreated = createTable(query, null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public HashMap<String, String> constraintHashMap(String query) {

        String cleanQuery = query.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\;", "").replaceAll("  ", " ");
        String[] listConstraints = cleanQuery.split(",");
        HashMap<String, String> hashMapConstraints = new HashMap<>();
        List<String> allTableName = showTables();
        for (int i = 0; i < listConstraints.length; i++) {
            if (listConstraints[i].contains("PRIMARY KEY")) {
                String[] tokenizQuery = listConstraints[i].split(" ");
                hashMapConstraints.put(tokenizQuery[2], "PRIMARY KEY");
            }
            if (listConstraints[i].contains("FOREIGN KEY")) {
                //String referenceTable = listConstraints[i].substring(listConstraints[i].indexOf("REFERENCES")+11);
                String[] tokenizQuery = listConstraints[i].trim().split(" ");
                if (!allTableName.contains(tokenizQuery[tokenizQuery.length - 2] + ".txt")) {
                    System.out.println("FOREIGN KEY table name is wrong");
                } else {
                    hashMapConstraints.put(tokenizQuery[2], "FOREIGN KEY " + tokenizQuery[tokenizQuery.length - 2] + " " + tokenizQuery[tokenizQuery.length - 1]);
                }
            }
        }

        return hashMapConstraints;
    }


    public boolean createTable(String query, HashMap<String, String> constraints) throws IOException {
        this.startDate = new Date();
        String cleanedQuery = query.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\;", "").replaceAll("  ", " ");
        String[] queryTokenized = cleanedQuery.split(" ");
        String tableName = queryTokenized[2];
        String newQuery = cleanedQuery.substring(cleanedQuery.indexOf(tableName) + tableName.length());
        queryTokenized = newQuery.split(",");
        HashMap<Integer, TableMetaData> metaDataHashMap = new HashMap();
        int count = 0;
        String[] singleColumnDetails = queryTokenized;
        int totalColumns = queryTokenized.length;

        for (int i = 0; i < totalColumns; ++i) {
            if (singleColumnDetails[i].length() > 1) {
                String token = singleColumnDetails[i];
                String[] tokenizeRow = token.trim().split(" ");
                if (tokenizeRow.length < 2) {
                    System.out.println("Invalid Query");
                    return false;
                }
                String columnConstraint = null;
                if (constraints != null && constraints.containsKey(tokenizeRow[0])) {
                    columnConstraint = constraints.get(tokenizeRow[0]);
                }

                if (tokenizeRow.length == 2) {
                    metaDataHashMap.put(count, new TableMetaData(tokenizeRow[0], tokenizeRow[1].toUpperCase(Locale.ROOT), null, columnConstraint));
                } else if (tokenizeRow.length == 3) {
                    metaDataHashMap.put(count, new TableMetaData(tokenizeRow[0], tokenizeRow[1].toUpperCase(Locale.ROOT), tokenizeRow[2], columnConstraint));
                } else if (tokenizeRow.length == 4) {
                    metaDataHashMap.put(count, new TableMetaData(tokenizeRow[0], tokenizeRow[1].toUpperCase(Locale.ROOT), tokenizeRow[2], columnConstraint));
                }

                // System.out.println(token);
                ++count;
            }
        }

        if (this.alterDataBase.getSelectedDBPath() != null) {
            File tableMetadataFile = new File(this.alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt");
            File tableDataFile = new File(this.alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt");
            if (!tableMetadataFile.createNewFile()) {
                System.out.println("Table already exists");
                return false;
            }

            try {
                tableDataFile.createNewFile();
                FileWriter myDataWriter = new FileWriter(this.alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt");
                FileWriter myWriter = new FileWriter(this.alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt");
                myWriter.write("column_name|data_type|size|constraint\n");
                Iterator var20 = metaDataHashMap.entrySet().iterator();

                while (var20.hasNext()) {
                    Entry mapElement = (Entry) var20.next();
                    TableMetaData tableMetaData = (TableMetaData) mapElement.getValue();
                    myWriter.write(tableMetaData.printMetaData() + "\n");
                    if ((Integer) mapElement.getKey() == 0) {
                        myDataWriter.write(tableMetaData.getColumn_name());
                    } else {
                        myDataWriter.write("|" + tableMetaData.getColumn_name());
                    }
                }

                myWriter.close();
                myDataWriter.close();
                this.endDate = new Date();
                generateLog(query);
                return true;
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        System.out.println("No Database Selected");
        return false;
    }


    public Boolean dropTable(String query) {
        this.startDate = new Date();
        String[] tokenizeQuery = query.replace(";", "").split(" ");
        String tableName = tokenizeQuery[tokenizeQuery.length - 1];
        File tableMetadataFile = new File(this.alterDataBase.getSelectedDBPath() + "/" + tableName + "MetaData.txt");
        File tableDataFile = new File(this.alterDataBase.getSelectedDBPath() + "/" + tableName + ".txt");
        List<String> allTableNames = showTables();

        ArrayList<HashMap<String, TableMetaData>> metaDataAllTables = new ArrayList<>();
        for (String table : allTableNames) {
            if (table.contains("MetaData.txt") && !table.contains(tableName)) {
                metaDataAllTables.add(readMetaDataTable(table));
            }
        }

        if (tableMetadataFile.exists()) {
            for (int i = 0; i < metaDataAllTables.size(); i++) {
                HashMap<String, TableMetaData> hashMapMetadata = metaDataAllTables.get(i);
                Iterator constraintIterator = hashMapMetadata.entrySet().iterator();
                while (constraintIterator.hasNext()) {
                    Entry mapElement = (Entry) constraintIterator.next();
                    //System.out.println("Column Name "+mapElement.getKey()+" Constraint "+mapElement.getValue());
                    TableMetaData tableMetaData = (TableMetaData) mapElement.getValue();
                    if (tableMetaData.getConstraint().contains("FOREIGN KEY")) {
                        String[] tokenizeConstrain = tableMetaData.getConstraint().split(" ");
                        if (tokenizeConstrain[2].equalsIgnoreCase(tableName)) {
                            System.out.println("FOREIGN KEY Constraint Error");
                            return false;
                        }
                        //System.out.println("FOREIGN KEY Constraint on "+tokenizeConstrain[3]);

                    }
                }
            }
            tableDataFile.delete();
            tableMetadataFile.delete();
            this.endDate = new Date();
            generateLog(query);
            System.out.println("Table Dropped");
            return true;
        } else {
            System.out.println("Table Don't Exist");
            return false;
        }
    }

    public HashMap<String, TableMetaData> readMetaDataTable(String tableName) {
        try {
            HashMap<String, TableMetaData> metaData = new HashMap<>();
            FileReader fileReader = new FileReader(this.alterDataBase.getSelectedDBPath() + "/" + tableName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int i = 0;
                String[] tempColumns = line.split("\\|");
                metaData.put(tempColumns[0].toLowerCase(), new TableMetaData(tempColumns[0], tempColumns[1], tempColumns[2], tempColumns[3]));

            }
            return metaData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
