import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    static Properties prop = null;
    static InputStream in = null;
    static {
        try {
            in = new FileInputStream("conf"+ File.separator+"userParam.properties");
            prop = new Properties();
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return  prop.getProperty(key);
    }
}
