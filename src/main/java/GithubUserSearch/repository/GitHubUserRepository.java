package GithubUserSearch.repository;

import GithubUserSearch.model.GitHubUser;
import GithubUserSearch.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class GitHubUserRepository {
    public void saveAll(List<GitHubUser> users) throws Exception {
        try (Connection dbConn = DatabaseUtil.getConnection()) {
            String insertSQL = "INSERT INTO github_users (id, login, avatar_url, html_url, type, score) " +
                    "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

            try (PreparedStatement pstmt = dbConn.prepareStatement(insertSQL)) {
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
    }
}
