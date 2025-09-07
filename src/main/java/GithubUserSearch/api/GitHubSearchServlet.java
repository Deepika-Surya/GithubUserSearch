package GithubUserSearch.api;

import GithubUserSearch.service.GithubUserSearchService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/github/search")
public class GitHubSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("username");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username");
            return;
        }

        GithubUserSearchService service = new GithubUserSearchService();
        try {
            String jsonResponse = service.fetchAndStoreUsers(searchTerm);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}

