package filereader;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


    /*
    Reads a text file as String
    */

public class FileReader {

    public static String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path));
    }


}
