package QueryParser;

import DDL.AlterDataBase;
import DDL.AlterTable;
import DDL.PrintTable;
import DML.DeleteFromTable;
import DML.InsertIntoTable;
import DML.UpdateTable;
import Transaction.Transaction;

import java.util.HashMap;
import java.util.regex.Pattern;

public class QueryParser {

    HashMap<String, String> queries = new HashMap<>();

    // INSERT INTO Table1 (id, name, age, gender) VALUES (10, 'ABHI', 24, 'male');

    public QueryParser() {
        setQueryDictionary();
    }

    public static void main(String[] args) {
        QueryParser queryParser = new QueryParser();
        queryParser.queryType("INSERT INTO Table1 (id, name, age, gender) VALUES (3, 'temp1', 21, 'female');");
        queryParser.queryType("INSERT INTO Table1 (id, name, age, gender) VALUES (4, 'temp1', 22, 'female');");
        queryParser.queryType("INSERT INTO Table1 (id, name, age, gender) VALUES (5, 'temp2', 22, 'female');");
        queryParser.queryType("UPDATE Table1 SET age = 35 WHERE name = 'temp1';");
        queryParser.queryType("DELETE FROM Table1 WHERE name = 'temp1';");
    }

    private void setQueryDictionary() {
        final String DATABASE_NAME = "[a-zA-Z][a-zA-Z0-9_]{0,255}";
        final String TABLE_NAME = "[a-zA-Z][a-zA-Z0-9_]{0,255}";
        final String COLUMN_NAME = "[a-zA-Z][a-zA-Z0-9_]{0,255}";
        final String COLUMN_VALUE = "[a-zA-Z0-9_\\']{1,255}";
        final String WHERE_CONDITION = "[a-zA-Z0-9_]{1,255}";

        final String DATA_TYPES = "(INT|VARCHAR\\([0-9]{1-255}\\))";
        final String ARITHMETIC_OPERATORS = "(=|>|<|>=|<=|!=)";

        queries.put(String.valueOf(QueryType.CREATE_DATABASE), "CREATE DATABASE " + DATABASE_NAME + ";");

        queries.put(String.valueOf(QueryType.USE_DATABASE), "USE " + DATABASE_NAME + ";");

//        queries.put(String.valueOf(QueryType.CREATE_TABLE), "CREATE TABLE " + TABLE_NAME + " "
//                + "\\((" + COLUMN_NAME + " " + DATA_TYPES + "\\, )*(" + COLUMN_NAME + " " + DATA_TYPES +"){1}\\);");

        queries.put(String.valueOf(QueryType.CREATE_TABLE), "CREATE TABLE " + TABLE_NAME + " " + "\\([a-zA-Z ,\\(\\)0-9]+\\);");

        queries.put(String.valueOf(QueryType.INSERT_TABLE), "INSERT INTO " + TABLE_NAME + " " + "\\((" + COLUMN_NAME +
                ", )*(" + COLUMN_NAME + ")\\)" + " " + "VALUES" + " " + "\\((" + COLUMN_VALUE + ", )*" + COLUMN_VALUE + "\\);");

        queries.put(String.valueOf(QueryType.SELECT_TABLE), "SELECT (\\*|(" + COLUMN_NAME + "\\,)*(" + COLUMN_NAME + ")+) FROM " + TABLE_NAME + "( WHERE " +
                COLUMN_NAME + " " + ARITHMETIC_OPERATORS + " " + WHERE_CONDITION + ")*;");

        queries.put(String.valueOf(QueryType.UPDATE_TABLE), "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = "
                + COLUMN_VALUE + " WHERE " + COLUMN_NAME + " = " + COLUMN_VALUE + ";");

        queries.put(String.valueOf(QueryType.DELETE_TABLE), "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME +
                " = " + COLUMN_VALUE + ";");

        queries.put(String.valueOf(QueryType.DROP_TABLE), "DROP TABLE " + TABLE_NAME + ";");

        queries.put(String.valueOf(QueryType.BEGIN_TRANSACTION), "BEGIN TRANSACTION");

        queries.put(String.valueOf(QueryType.END_TRANSACTION), "END TRANSACTION");

        queries.put(String.valueOf(QueryType.DROP_DATABASE), "DROP DATABASE " + DATABASE_NAME + ";");
    }

    public String queryType(String query) {
        QueryParser queryParser = new QueryParser();

        for (QueryType queryType : QueryType.values()) {
            if (Pattern.compile(queryParser.queries.get(String.valueOf(queryType))).matcher(query).matches()) {
                return String.valueOf(queryType);
            }
        }
        return "NOT FOUND";
    }

    public void getToSpecificQuery(String query) {
        switch (queryType(query.trim())) {
            case "INSERT_TABLE":
                InsertIntoTable insertIntoTable = new InsertIntoTable();
                insertIntoTable.insertIntoTable(query.replaceAll(";", ""));
                break;
            case "UPDATE_TABLE":
                UpdateTable updateTable = new UpdateTable();
                updateTable.updateTable(query.replaceAll(";", ""));
                break;
            case "DELETE_TABLE":
                DeleteFromTable deleteFromTable = new DeleteFromTable();
                deleteFromTable.deleteFromTable(query.replaceAll(";", ""));
                break;
            case "BEGIN_TRANSACTION":
            case "END_TRANSACTION":
                Transaction transaction = new Transaction();
                transaction.nextTransaction(query);
                break;
            case "CREATE_TABLE":
                AlterTable alterTable = new AlterTable();
                alterTable.parseCreateQuery(query);
                break;
            case "CREATE_DATABASE":
                AlterDataBase alterDataBase = AlterDataBase.getDBInstance();
                alterDataBase.parseDBQuery(query);
                break;
            case "USE_DATABASE":
                AlterDataBase alterDataBase1 = AlterDataBase.getDBInstance();
                alterDataBase1.parseDBQuery(query);
                break;
            case "SELECT_TABLE":
                PrintTable printTable = new PrintTable(AlterDataBase.getDBInstance().getSelectedDBPath());
                printTable.parseSelectQuery(query);
                break;
            case "DROP_TABLE":
                AlterTable alterTable1 = new AlterTable();
                alterTable1.dropTable(query);
                break;
            case "DROP_DATABASE":
                AlterDataBase alterDataBase2 = AlterDataBase.getDBInstance();
                alterDataBase2.dropDatabase(query);
                break;
            default:
                System.out.println("Invalid Query");
                break;
        }
    }
    // INSERT INTO table_1 (id, name, age, gender) VALUES (3, 'temp1', 21, 'female')
    // UPDATE table_1 SET age = 35 WHERE name = 'rahul'
    // DELETE FROM table_1 WHERE name = 'temp1'
}
