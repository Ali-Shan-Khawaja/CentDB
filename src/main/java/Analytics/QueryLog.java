package Analytics;

public class QueryLog {
    String query;
    double queryTime;
    String userID;
    String dbName;

    public QueryLog(String query, double queryTime, String userID, String dbName) {
        this.query = query;
        this.queryTime = queryTime;
        this.userID = userID;
        this.dbName = dbName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public double getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(double queryTime) {
        this.queryTime = queryTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
