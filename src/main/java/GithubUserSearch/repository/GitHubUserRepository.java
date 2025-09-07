package GithubUserSearch.repository;

import GithubUserSearch.model.GitHubUser;
import GithubUserSearch.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class GitHubUserRepository {
    public void saveAll(List<GitHubUser> users) throws Exception {
        String insertSQL = "INSERT INTO github_users (id, login, avatar_url, html_url, type, score) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

        try (Connection dbConn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = dbConn.prepareStatement(insertSQL)) {
            for (GitHubUser user : users) {
                pstmt.setLong(1, user.id);
                pstmt.setString(2, user.login);
                pstmt.setString(3, user.avatar_url);
                pstmt.setString(4, user.html_url);
                pstmt.setString(5, user.type);
                pstmt.setDouble(6, user.score);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void saveHistory(String searchTerm, String response, List<GitHubUser> users) throws Exception {
        String insertHistorySQL = "INSERT INTO history (search_string, response_data, user_id) VALUES (?, ?, ?)";

        try (Connection dbConn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = dbConn.prepareStatement(insertHistorySQL)) {
            for (GitHubUser user : users) {
                pstmt.setString(1, searchTerm);
                pstmt.setString(2, truncate(response, 2000)); // Limit to 2000 chars
                pstmt.setLong(3, user.id);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private String truncate(String input, int maxLength) {
        return input.length() <= maxLength ? input : input.substring(0, maxLength);
    }
}
