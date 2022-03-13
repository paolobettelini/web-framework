package ch.bettelini.examples;

import ch.bettelini.library.HttpServer;

public class RedirectExample {
 
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        server.get("/home/{name}", (req, res) -> 
            ("Welcome to your home, " + req.param("name") + "!").getBytes());

        server.get("/google", (req, res) -> {
            res.redirect("https://wwww.google.com"); 
            return "".getBytes();
        });

        server.get("/{name}", (req, res) -> {
            res.redirect("http://127.0.0.1:9090/home/" + req.param("name")); 
            return "".getBytes();
        });

        server.start();
    }

}
