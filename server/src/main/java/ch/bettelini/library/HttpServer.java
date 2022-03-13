package ch.bettelini.library;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {
    
    private int port;

    private Thread thread;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private static record Router(PathMatcher matcher, Route route) {}

    private List<Router> getRoutes = new LinkedList<>();
    private List<Router> postRoutes = new LinkedList<>();

    private Route onNotFound = (req, res) -> {
        res.code(HttpCode.NOT_FOUND);
        return """
            <html>
            <body>
            <p>Error 404 Not Found</p>
            </body>
            </html>    
            """.getBytes();
    };

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
        get(new RegexPathMatcher(path), route);
    }

    public void post(String path, Route route) {
        post(new RegexPathMatcher(path), route);
    }

    public void get(PathMatcher matcher, Route route) {
        getRoutes.add(new Router(matcher, route));
    }

    public void post(PathMatcher matcher, Route route) {
        postRoutes.add(new Router(matcher, route));
    }

    public void onNotFound(Route onNotFound) {
        this.onNotFound = onNotFound;
    }

    Response processRequest(Request request) {
        var routes = switch (request.method()) {
            case GET -> getRoutes;
            case POST -> postRoutes;
        };

        Router router = null;

        for (var r : routes) {
            if (r.matcher.matches(request.path())) {
                router = r;
                break;
            }
        }

        Route route;
        if (router != null) {
            request.params(router.matcher.params(request.path()));
            route = router.route;
        } else {
            route = onNotFound;
        }
        
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