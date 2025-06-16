package DML;

import java.util.Objects;



public class Validate {


    public boolean isValidDataType(String type, String value, int size) {
        if (Objects.equals(type, "INT")) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException exception) {
                return false;
            }
        } else if (Objects.equals(type, "VARCHAR")) {
            return value.length() < size;
        }
        return false;
    }
}
