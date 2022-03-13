package ch.bettelini.examples;

import ch.bettelini.library.HttpServer;

public class RegexExamples {
    
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        server.get("/api/{token:[a-zA-Z]{10\\}}", (req, res) -> {
            return "funziona".getBytes();
        });

        server.start();
    }

}
