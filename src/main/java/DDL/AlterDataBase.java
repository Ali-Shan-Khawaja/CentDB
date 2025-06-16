//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package DDL;

import Authentication.userName;
import Logging.logWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlterDataBase {
    private static final String Base_Path = "src/main/resources";
    private static String selectedDataBase = null;
    private static AlterDataBase alterDataBase = null;
    userName userInstance = userName.getInstance();

    private AlterDataBase() {

    }

    public static AlterDataBase getDBInstance() {
        if (alterDataBase == null) {
            alterDataBase = new AlterDataBase();
        }

        return alterDataBase;
    }

    private void generateLog(String eventName, String description, String dbName) {
        logWriter logWriter = new logWriter();
        logWriter.Event(eventName, description, userInstance.getUser(), dbName);

    }

    public List<String> getAllDatabases() {
        File dbBaseDir = new File(Base_Path);
        String[] contents = dbBaseDir.list();
        List<String> listDB = new ArrayList();

        for (int i = 0; i < contents.length; ++i) {
            if (!contents[i].contains(".txt")) {
                System.out.println(contents[i]);
                listDB.add(contents[i]);
            }
        }

        return listDB;
    }

    public boolean addDatabase(String dbName) {
        String dbPath = Base_Path + "/" + dbName;
        File newDBDir = new File(dbPath);
        if (newDBDir.exists()) {
            System.out.println("Given DB Exists :" + dbName);
            return false;
        } else {
            newDBDir.mkdir();
            generateLog("CREATION", "DB CREATED", dbName);
            System.out.println("DB Created: " + dbName);
            return true;
        }
    }

    public void parseDBQuery(String query) {
        String[] tokenizedQuery = query.replace(";", "").split(" ");
        if (query.contains("CREATE")) {
            this.addDatabase(tokenizedQuery[2]);
        } else if (query.contains("USE")) {
            this.useDatabase(tokenizedQuery[1]);
        }
    }

    public boolean dropDatabase(String query) {

        String[] tokenizeQuery = query.replace(";", "").split(" ");
        String dbName = tokenizeQuery[tokenizeQuery.length - 1];

        String dbPath = Base_Path + "/" + dbName;
        File newDBDir = new File(dbPath);
        if (!newDBDir.exists()) {
            System.out.println(dbName + " Database does not exists");
            return false;
        } else {
            String[] allContent = newDBDir.list();
            String[] var5 = allContent;
            int var6 = allContent.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String file = var5[var7];
                File currFilePath = new File(newDBDir.getPath(), file);
                currFilePath.delete();
            }

            newDBDir.delete();
            generateLog("DELETION", "DB DELETED", dbName);
            System.out.println(dbName + " Database dropped");
            return true;
        }
    }

    public void useDatabase(String dbName) {
        String dbPath = Base_Path + "/" + dbName;
        File newDBDir = new File(dbPath);
        if (newDBDir.exists()) {
            System.out.println("Selecting Database :" + dbName);
            selectedDataBase = dbName;
        } else {
            System.out.println("Database does not exists: " + dbName);
            selectedDataBase = null;
        }

    }

    public String getSelectedDBPath() {
//        return Base_Path + "/database_1";
        return selectedDataBase == null ? null : Base_Path + "/" + selectedDataBase;
    }
}
