package Analytics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AnalyticsService {


    public void queryParser(String query) {

        if (query.contains("COUNT QUERIES")) {
            String cleanQuery = query.replace(";", "");
            String[] tokenizeQuery = cleanQuery.split(" ");
            String dbName = tokenizeQuery[tokenizeQuery.length - 1];
            countAllQueriesByUser(dbName);
            countAllQueriesByTable(dbName);
        } else {
            String cleanQuery = query.replace(";", "");
            String[] tokenizeQuery = cleanQuery.split(" ");
            String dbName = tokenizeQuery[tokenizeQuery.length - 1];
            String queryType = tokenizeQuery[tokenizeQuery.length - 2];
            countSpecificQueriesByUser(dbName, queryType);
            countSpecificQueriesByTable(dbName, queryType);
        }
    }

    public void countAllQueriesByUser(String dbName) {
        LogReader logReader = new LogReader();
        HashMap<Integer, QueryLog> queryLogHashMap = logReader.getQueryLogHashMap();
        HashMap<String, Integer> userQueryCount = new HashMap<>();
        for (Map.Entry<Integer, QueryLog> singleEntry : queryLogHashMap.entrySet()) {
            QueryLog queryLog = singleEntry.getValue();
            if (queryLog.getDbName().equalsIgnoreCase(dbName)) {
                if (!userQueryCount.containsKey(queryLog.getUserID())) {
                    userQueryCount.put(queryLog.getUserID(), 1);
                } else {
                    int previousCount = userQueryCount.get(queryLog.getUserID());
                    userQueryCount.put(queryLog.getUserID(), previousCount + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : userQueryCount.entrySet()) {
            System.out.println("user " + entry.getKey() + " submitted " + entry.getValue() + " queries on " + dbName);
        }
    }

    public void countSpecificQueriesByUser(String dbName, String queryType) {
        LogReader logReader = new LogReader();
        HashMap<Integer, QueryLog> queryLogHashMap = logReader.getQueryLogHashMap();
        HashMap<String, Integer> userQueryCount = new HashMap<>();
        for (Map.Entry<Integer, QueryLog> singleEntry : queryLogHashMap.entrySet()) {
            QueryLog queryLog = singleEntry.getValue();
            if (queryLog.getDbName().equalsIgnoreCase(dbName) && queryLog.getQuery().contains(queryType.toUpperCase(Locale.ROOT))) {
                if (!userQueryCount.containsKey(queryLog.getUserID())) {
                    userQueryCount.put(queryLog.getUserID(), 1);
                } else {
                    int previousCount = userQueryCount.get(queryLog.getUserID());
                    userQueryCount.put(queryLog.getUserID(), previousCount + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : userQueryCount.entrySet()) {
            System.out.println("user " + entry.getKey() + " submitted " + entry.getValue() + " " + queryType.toUpperCase(Locale.ROOT) + " queries on " + dbName);
        }
    }

    public void countAllQueriesByTable(String dbName) {
        LogReader logReader = new LogReader();
        HashMap<Integer, QueryLog> queryLogHashMap = logReader.getQueryLogHashMap();
        HashMap<String, Integer> tblQueryCount = new HashMap<>();
        for (Map.Entry<Integer, QueryLog> singleEntry : queryLogHashMap.entrySet()) {
            QueryLog queryLog = singleEntry.getValue();
            if (queryLog.getDbName().equalsIgnoreCase(dbName)) {
                String tableName = "";
                if (queryLog.getQuery().contains("CREATE")) {
                    tableName = queryLog.getQuery().split(" ")[2];
                } else if (queryLog.getQuery().contains("INSERT")) {
                    tableName = queryLog.getQuery().split(" ")[2];
                } else if (queryLog.getQuery().contains("UPDATE")) {
                    tableName = queryLog.getQuery().split(" ")[1];
                } else if (queryLog.getQuery().contains("SELECT")) {
                    tableName = queryLog.getQuery().substring(queryLog.getQuery().indexOf("FROM"))
                            .split(" ")[1];
                }
                if (!tblQueryCount.containsKey(tableName)) {
                    tblQueryCount.put(tableName, 1);
                } else {
                    int previousCount = tblQueryCount.get(tableName);
                    tblQueryCount.put(tableName, previousCount + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : tblQueryCount.entrySet()) {
            System.out.println("Total " + entry.getValue() + " operations are performed on " + entry.getKey() + " on DataBase " + dbName);
        }
    }

    public void countSpecificQueriesByTable(String dbName, String queryType) {
        LogReader logReader = new LogReader();
        HashMap<Integer, QueryLog> queryLogHashMap = logReader.getQueryLogHashMap();
        HashMap<String, Integer> tblQueryCount = new HashMap<>();
        for (Map.Entry<Integer, QueryLog> singleEntry : queryLogHashMap.entrySet()) {
            QueryLog queryLog = singleEntry.getValue();
            if (queryLog.getDbName().equalsIgnoreCase(dbName) && queryLog.getQuery().contains(queryType.toUpperCase(Locale.ROOT))) {
                String tableName = "";
                if (queryLog.getQuery().contains("CREATE")) {
                    tableName = queryLog.getQuery().split(" ")[2];
                } else if (queryLog.getQuery().contains("INSERT")) {
                    tableName = queryLog.getQuery().split(" ")[2];
                } else if (queryLog.getQuery().contains("UPDATE")) {
                    tableName = queryLog.getQuery().split(" ")[1];
                }
                if (!tblQueryCount.containsKey(tableName)) {
                    tblQueryCount.put(tableName, 1);
                } else {
                    int previousCount = tblQueryCount.get(tableName);
                    tblQueryCount.put(tableName, previousCount + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : tblQueryCount.entrySet()) {
            System.out.println("Total " + entry.getValue() + " " + queryType.toUpperCase(Locale.ROOT) + " are performed on " + entry.getKey() + " on DataBase " + dbName);
        }
    }

}
