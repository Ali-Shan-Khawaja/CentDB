package ERD;

import Transaction.Locking;
import Transaction.TransactionInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class Erd_functionclass {

    public void make_erd(String databasename) throws InterruptedException {
        //Implemented everything in a thread which is called to create the erd
        Thread createerd = new Thread(() -> {
            //opening the file where the files data is kept.
            File dir = new File("src/main/resources/" + databasename);
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                //going through each file and checking if its a metadata file
                // or not
                if (!(file.getName().endsWith("MetaData.txt"))) {
                    continue;
                }
                //getting the tablename from the file
                String tablename = file.getName();
                tablename = tablename.substring(0, tablename.length() - 13);
                //checking if there is a lock on the table and exiting if
                // there is
                if (!this.checklock(tablename)) {
                    System.out.println("Lock on Table exiting");
                    System.exit(0);
                }
                //Reading the file and looking for a primary key.Once we find
                // it we look for foreign keys and print out the connection we
                // found for the table along with the cardinality and attribute
                // names
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String sCurrentLine;
                    String attributeName = "";
                    while ((sCurrentLine = br.readLine()) != null) {
                        String[] attribute = sCurrentLine.split("[|]");
                        int linelength = attribute.length;
                        //splitting the line to check for primary and
                        // foreign keys
                        if (attribute[linelength - 1].contains("PRIMARY KEY")) {
                            attributeName = attribute[0];
                        }
                        if (attribute[linelength - 1].contains("FOREIGN" +
                                " KEY") && attributeName != "") {
                            int indexofKEY = attribute[linelength - 1].lastIndexOf("FOREIGN KEY");
                            String foriegnkey =
                                    attribute[linelength - 1].substring(indexofKEY + 11);
                            //check cardinality by running query's
                            String cardinality = "\t 1---> M";
                            System.out.println(tablename + "\t" +
                                    attributeName + cardinality + foriegnkey);
                        }
                    }
                } catch (IOException e) {
                    //Throwing an exception and error message if the file is
                    // not found ideally code will never reach here
                    System.out.println("No such file found");
                    e.printStackTrace();
                }
            }
        });

        createerd.start();
        createerd.join();

    }

    boolean checklock(String tablename) {
        //creating a locking object to check for the locks
        Locking checklock = new Locking();
        return checklock.canQueryBeStarted(tablename, true,
                TransactionInstance.getInstance());

    }

}
