package ch.bettelini.library;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {
    
    private int port;

    private Thread thread;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private Map<String, Route> getRoutes = new HashMap<>();
    private Map<String, Route> postRoutes = new HashMap<>();

    public HttpServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (var server = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Receive connection
                    var client = server.accept();

                    threadPool.execute(new Connection(this, client));
                } catch (IOException e) {

                }
            }
        } catch (IOException e) {

        }
    }

    public void get(String path, Route route) {
        getRoutes.put(path, route);
    }

    public void post(String path, Route route) {
        postRoutes.put(path, route);
    }

    Response processRequest(Request request) {
        var routes = switch (request.method()) {
            case GET -> getRoutes;
            case POST -> postRoutes;
        };

        if (!routes.containsKey(request.path())) {
            return null;
        }

        var route = routes.get(request.path());

        var response = new Response();
        response.content(route.process(request, response));
        return response;
    }

    public int getPort() {
        return port;
    }

    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }

}