package Authentication.FileOps.Parser;

import java.io.IOException;
import java.util.HashMap;

public interface fileParser {
    HashMap<String, HashMap<String, String>> parse() throws IOException;
}
