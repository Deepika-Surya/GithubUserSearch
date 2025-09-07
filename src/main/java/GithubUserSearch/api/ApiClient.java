package GithubUserSearch.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    public static String fetch(String urlStr, String method, String params) throws Exception {
        if (params != null && !params.isEmpty() && method.equalsIgnoreCase("GET")) {
            urlStr = urlStr + "?" + params;
        }

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", "Java-App");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }
}
