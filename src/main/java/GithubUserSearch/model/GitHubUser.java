package GithubUserSearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubUser {
    public String login;
    public long id;
    public String avatar_url;
    public String html_url;
    public String type;
    public double score;
}
