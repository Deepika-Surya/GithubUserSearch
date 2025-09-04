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
    public void fetchAndStoreUsers(String searchTerm) throws Exception {
        String apiUrl = "https://api.github.com/search/users?q=" + searchTerm;
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

    // Deserialize JSON
    ObjectMapper mapper = new ObjectMapper();
    GitHubResponse gitHubResponse = mapper.readValue(response.toString(), GitHubResponse.class);

    // Insert into DB
        try (Connection dbConn = DatabaseUtil.getConnection()) {
        String insertSQL = "INSERT INTO github_users (id, login, avatar_url, html_url, type, score) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

        try (PreparedStatement pstmt = dbConn.prepareStatement(insertSQL)) {
            for (GitHubUser userObj : gitHubResponse.items) {
                pstmt.setLong(1, userObj.id);
                pstmt.setString(2, userObj.login);
                pstmt.setString(3, userObj.avatar_url);
                pstmt.setString(4, userObj.html_url);
                pstmt.setString(5, userObj.type);
                pstmt.setDouble(6, userObj.score);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("✅ Data inserted into database successfully!");
        }
    }
}
}

