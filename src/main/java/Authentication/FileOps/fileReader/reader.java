package Authentication.FileOps.fileReader;

import java.io.IOException;
import java.util.HashMap;

public interface reader {
    HashMap<String, HashMap<String, String>> read() throws IOException;
}
