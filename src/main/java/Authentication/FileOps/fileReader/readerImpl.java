package Authentication.FileOps.fileReader;

import Authentication.FileOps.Parser.fileParser;
import Authentication.FileOps.Parser.fileParserImpl;

import java.io.IOException;
import java.util.HashMap;

public class readerImpl implements reader {
    private final String filePath = "src/User_Profile.txt";
    HashMap<String, HashMap<String, String>> userDetail;

    @Override
    public HashMap<String, HashMap<String, String>> read() throws IOException {
        this.parseFile();
        return this.userDetail;
    }

    public void parseFile() throws IOException {
        fileParser fileParser = new fileParserImpl(this.filePath);
        this.userDetail = fileParser.parse();
    }
}
