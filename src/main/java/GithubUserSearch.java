import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class GithubUserSearch {
    public static void main(String[] args) {
        // GitHub API endpoint
        String apiUrl = "https://api.github.com/search/users?q=revanth";

        // Database connection details
        String dbUrl = "jdbc:postgresql://localhost:5432/jdbc_demo";
        String dbUser = "postgres";
        String dbPassword = "postgres";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java-App");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();

            int startIndex = json.indexOf("\"items\":") + 8;
            int endIndex = json.lastIndexOf("]}");
            String itemsArray = json.substring(startIndex, endIndex);

            String[] users = itemsArray.split("\\},\\{");

            try (Connection dbConn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                String insertSQL = "INSERT INTO github_users (id, login, avatar_url, html_url, type, score) " +
                        "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

                try (PreparedStatement pstmt = dbConn.prepareStatement(insertSQL)) {

                    for (String userJson : users) {
                        String login = extractValue(userJson, "\"login\":\"");
                        String idStr = extractValue(userJson, "\"id\":");
                        String avatarUrl = extractValue(userJson, "\"avatar_url\":\"");
                        String htmlUrl = extractValue(userJson, "\"html_url\":\"");
                        String type = extractValue(userJson, "\"type\":\"");
                        String scoreStr = extractValue(userJson, "\"score\":");

                        if (login != null && idStr != null) {
                            pstmt.setLong(1, Long.parseLong(idStr));
                            pstmt.setString(2, login);
                            pstmt.setString(3, avatarUrl);
                            pstmt.setString(4, htmlUrl);
                            pstmt.setString(5, type);
                            pstmt.setBigDecimal(6, new java.math.BigDecimal(scoreStr));

                            pstmt.addBatch();
                        }
                    }

                    pstmt.executeBatch();
                    System.out.println("Data inserted into database successfully!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        char endChar = (key.endsWith(":") || json.charAt(start - 1) == ':') ? ',' : '"';
        int end = json.indexOf(endChar, start);
        if (end == -1) end = json.length();
        String value = json.substring(start, end);
        return value.replace("\"", "").replace("}", "").trim();
    }
}
