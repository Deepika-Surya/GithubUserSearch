    package GithubUserSearch.util;

    import java.sql.Connection;
    import java.sql.DriverManager;

    public class DatabaseUtil {
        private static final String DB_URL = "jdbc:postgresql://localhost:5432/jdbc_demo";
        private static final String DB_USER = "postgres";
        private static final String DB_PASSWORD = "postgres";

        public static Connection getConnection() throws Exception {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

    }
