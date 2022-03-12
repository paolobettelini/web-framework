package ch.bettelini.examples;

import ch.bettelini.library.Htdocs;
import ch.bettelini.library.HttpServer;

public class Main {
 
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        var htdocs = new Htdocs("/home/paolo/Scrivania/rest-api/www");
        htdocs.addDefaultFile("index.jpg");
        htdocs.addDefaultFile("index.png");
        htdocs.addDefaultFile("index.html");

        server.get("/", htdocs.route());

        server.start();
    }

}
