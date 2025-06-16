package Transaction;

import DDL.AlterDataBase;
import DDL.PrintTable;

import java.util.HashMap;
import java.util.LinkedList;


public class GetInMemoryData {
    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
    PrintTable printTable = new PrintTable(alterDataBase.getSelectedDBPath());
    LinkedList<HashMap<String, String>> linkedList;
    HashMap<String, LinkedList<HashMap<String, String>>> hashMap = InMemoryData.getHashMap();

    public LinkedList<HashMap<String, String>> getInMemoryData(String tableName) {
        if (hashMap.containsKey(tableName)) {
            linkedList = hashMap.get(tableName);
        } else {
            linkedList = printTable.readTableData(tableName + ".txt");
        }
        return linkedList;
    }
}
