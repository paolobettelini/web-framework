package ch.bettelini.examples;

import ch.bettelini.library.HttpServer;

public class RegexExamples {
    
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        server.get("/some/[[a-zA-Z\\]+]/path", (req, res) -> "Pattern 1".getBytes());
        
        // Matches /api/token where token is a string of 10 alphabetic letters
        server.get("/api/{token:[a-zA-Z]{10\\}}", (req, res) -> "Pattern 2".getBytes());

        // Matches anything that starts with "/"
        server.get("/[.*]", (req, res) -> "Pattern 3".getBytes());

        server.start();
    }

}
