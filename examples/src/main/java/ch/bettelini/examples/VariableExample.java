package ch.bettelini.examples;

import ch.bettelini.library.HttpServer;

public class VariableExample {
    
    public static void main(String[] args) {
        var server = new HttpServer(9090);

        // greet_John
        server.get("/greet_{name}", (req, res) -> {
            return ("Hello, " + req.param("name") + "!").getBytes();
        });

        // /compute32+10
        server.get("/compute{A}+{B}", (req, res) -> {
            try {
                int a = Integer.parseInt(req.param("A"));
                int b = Integer.parseInt(req.param("B"));
                return Integer.toString(a+b).getBytes();
            } catch (NumberFormatException e) {
                return "Invalid numbers!".getBytes();
            }
        });

        server.start();
    }

}
