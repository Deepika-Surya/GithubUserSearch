package GithubUserSearch.model;

import java.util.List;

public class GitHubResponse {
    public int total_count;
    public boolean incomplete_results;
    public List<GitHubUser> items;
}
