import GithubUserSearch.service.GithubUserSearchService;

import java.util.Scanner;


public class GithubUserSearch {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter search term: ");
        String searchTerm = sc.nextLine();

        GithubUserSearchService service = new GithubUserSearchService();
        try {
            service.fetchAndStoreUsers(searchTerm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
