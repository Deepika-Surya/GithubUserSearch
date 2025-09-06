package GithubUserSearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import GithubUserSearch.model.GitHubResponse;
import GithubUserSearch.model.GitHubUser;
import GithubUserSearch.util.DatabaseUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GithubUserSearchService {
    public String fetchAndStoreUsers(String searchTerm) throws Exception {
        String apiUrl = "https://api.github.com/search/users?q=" + searchTerm;
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Java-App");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        ObjectMapper mapper = new ObjectMapper();
        GitHubResponse gitHubResponse = mapper.readValue(response.toString(), GitHubResponse.class);

        try (Connection dbConn = DatabaseUtil.getConnection()) {
            String insertUserSQL = "INSERT INTO github_users (id, login, avatar_url, html_url, type, score) " +
                    "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";
            PreparedStatement userStmt = dbConn.prepareStatement(insertUserSQL);

            for (GitHubUser user : gitHubResponse.items) {
                userStmt.setLong(1, user.id);
                userStmt.setString(2, user.login);
                userStmt.setString(3, user.avatar_url);
                userStmt.setString(4, user.html_url);
                userStmt.setString(5, user.type);
                userStmt.setDouble(6, user.score);
                userStmt.addBatch();
            }
            userStmt.executeBatch();

            String insertHistorySQL = "INSERT INTO history (search_string, response_data, user_id) VALUES (?, ?, ?)";
            PreparedStatement historyStmt = dbConn.prepareStatement(insertHistorySQL);

            for (GitHubUser user : gitHubResponse.items) {
                historyStmt.setString(1, searchTerm);
                historyStmt.setString(2, response.toString());
                historyStmt.setLong(3, user.id);
                historyStmt.addBatch();
            }
            historyStmt.executeBatch();
        }

        return response.toString();
    }
}

