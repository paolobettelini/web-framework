package ch.bettelini.library;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Connection implements Runnable {

    private HttpServer server;
    private Socket client;

    public Connection(HttpServer server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            
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
                //System.out.println(line);
            }

            try {
                var req = new Request(lines, client.getRemoteSocketAddress());
                var res = server.processRequest(req);

                res.write(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}