package Transaction;

import java.util.HashMap;
import java.util.LinkedList;


public class InMemoryData {

    private static HashMap<String, LinkedList<HashMap<String, String>>> hashMap;

    private InMemoryData() {

    }

    public static HashMap<String, LinkedList<HashMap<String, String>>> getHashMap() {
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        return hashMap;
    }
}
