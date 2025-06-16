package Transaction;

import DDL.AlterDataBase;

import java.io.*;
import java.util.Objects;
import java.util.Stack;


public class Locking {

    AlterDataBase alterDataBase = AlterDataBase.getDBInstance();

    String filePath = alterDataBase.getSelectedDBPath() + "/" + "TransactionMetaData.txt";

    Stack<String> stack;

    String delimiter = "|";

    private void writeToFile(String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
            if (new File(filePath).length() != 0) {
                bufferedWriter.newLine();
            }
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToLockTable(String tableName, int transaction, String data) {
        writeToFile(tableName + delimiter + transaction + delimiter + data);
    }

    public void beginTransaction(String data) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = bufferedReader.readLine();
            Stack<String> stack = new Stack<>();
            while (line != null) {
                line = bufferedReader.readLine();
                stack.push(line);
            }
            if (stack.size() == 0) {
                writeToFile(data);
            }
            if (stack.size() > 0 && Objects.equals(stack.get(stack.size() - 1), "END TRANSACTION")) {
                writeToFile(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endTransaction(String data) {
        writeToFile(data);
    }

    private boolean checkIfLockExists(String tableName) {
        try {
            if (!new File(filePath).exists()) {
                return false;
            }
            stack = new Stack<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stack.push(line);
            }
            if (stack.size() > 0) {
                if (Objects.equals(stack.get(stack.size() - 1), "END TRANSACTION")) {
                    return false;
                }
            }
            for (String ele : stack) {
                String[] array = ele.split("\\|");
                if (array[0].equals(tableName) && Objects.equals(array[2], "LOCK")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getTransactionIdForLocking(String tableName) {
        try {
            stack = new Stack<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stack.push(line);
            }
            for (String ele : stack) {
                String[] array = ele.split("\\|");
                if (Objects.equals(array[0], tableName)) {
                    return Integer.parseInt(array[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean canQueryBeStarted(String tableName, boolean isTransactionQuery, TransactionInstance transaction) {
        if (this.checkIfLockExists(tableName)) {
            if (!isTransactionQuery) {
                return false;
            }

            return this.getTransactionIdForLocking(tableName) == transaction.hashCode();
        }
        return true;
    }
}
