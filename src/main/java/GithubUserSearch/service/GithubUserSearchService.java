package GithubUserSearch.service;

import GithubUserSearch.api.ApiClient;
import GithubUserSearch.model.GitHubResponse;
import GithubUserSearch.model.GitHubUser;
import GithubUserSearch.repository.GitHubUserRepository;
import GithubUserSearch.util.PropertyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GithubUserSearchService {
    private final GitHubUserRepository repo = new GitHubUserRepository();

    public String fetchAndStoreUsers(String searchTerm) throws Exception {
        String baseUrl = PropertyUtil.get("github.api.url");
        String encodedTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
        String apiUrl = baseUrl + "?q=" + encodedTerm;

        String response = ApiClient.fetch(apiUrl, "GET", null);

        ObjectMapper mapper = new ObjectMapper();
        GitHubResponse gitHubResponse = mapper.readValue(response, GitHubResponse.class);

        List<GitHubUser> users = gitHubResponse.items;
        repo.saveAll(users);
        repo.saveHistory(searchTerm, response, users);

        return response;
    }
}

