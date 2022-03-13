package ch.bettelini.examples;

import ch.bettelini.library.Htdocs;
import ch.bettelini.library.HttpServer;

public class HtdocsExample {
 
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        var htdocs = new Htdocs("/home/paolo/Scrivania/web-framework/www");
        
        htdocs.addDefaultFile("index.html");
        htdocs.addDefaultFile("file.html");
    
        // htdocs/<path>   ->   /path/to/www/<path>
        server.get("/htdocs{path}", htdocs.route(req -> req.param("path")));

        // Simple serve
        server.get("/[.*]", htdocs.route());

        server.start();
    }

}
