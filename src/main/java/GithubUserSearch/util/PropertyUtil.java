package GithubUserSearch.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = PropertyUtil.class.getClassLoader().
                getResourceAsStream("config.properties")) {
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
