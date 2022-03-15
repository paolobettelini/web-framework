package ch.bettelini.library;

import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.tinylog.Logger;

public class Connection implements Runnable {

    private HttpServer server;
    private final Socket client;

    public Connection(HttpServer server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try (client) {
            var in = new Scanner(client.getInputStream());
            var out = client.getOutputStream();
            
            in.useDelimiter("\n\r");

            List<String> lines = new LinkedList<>();

            while (in.hasNext()) {
                var line = in.nextLine();

                if (line.isBlank()) {
                    break;
                }

                lines.add(line);
            }

            var req = new Request(lines, client.getRemoteSocketAddress());
            var res = server.processRequest(req);

            Logger.info("Request {} {}", req.method(), req.path());

            res.write(out);
        } catch (SocketException e) {
            
        } catch (Exception e) {
            Logger.error("Error {}", e.getMessage());
        }

    }
    
}