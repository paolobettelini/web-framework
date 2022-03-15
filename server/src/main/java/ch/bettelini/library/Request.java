package ch.bettelini.library;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private HttpMethod method;
    
    private String path;

    private HttpVersion version;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> params = new HashMap<>();

    private SocketAddress address;

    protected Request(List<String> lines, SocketAddress address) {
        this.address = address;
        var iterator = lines.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Badly formatted request"); 
        }
        processMethod(iterator.next());
        while (iterator.hasNext()) {
            String line = iterator.next();

            int index = line.indexOf(": ");
            
            if (index == -1) {
                throw new IllegalArgumentException("Badly formatted header");
            }

            String key = line.substring(0, index);
            String value = line.substring(index + 2, line.length());
            
            headers.put(key, value);
        }
    }

    /**
     * Processes the first line of the request
     * 
     * @param line <METHOD> <PATH> <HTTP_VERSION>
     */
    private void processMethod(String line) {
        var args = line.split(" ");

        if (args.length != 3) {
            throw new IllegalArgumentException("Badly formatted request");
        }

        // Parse method
        var method = HttpMethod.parse(args[0]);
        if (method == null) {
            throw new IllegalArgumentException("Unsupported HTTP Method: " + args[0]);
        }
        this.method = method;
        
        // Parse path
        this.path = args[1];

        // Parse version
        var version = HttpVersion.parse(args[2]);
        if (version == null) {
            throw new IllegalArgumentException("Unsupported HTTP Method: " + args[0]);
        }
        this.version = version;
    }


    public String param(String name) {
        return params.get(name);
    }

    protected void params(Map<String, String> params) {
        this.params = params;
    }

    public HttpMethod method() {
        return method;
    }

    public String path() {
        return path;
    }

    public HttpVersion version() {
        return version;
    }

    public String userAgent() {
        return headers.containsKey(HttpHeaders.USER_AGENT)
            ? headers.get(HttpHeaders.USER_AGENT)
            : null;
    }

    public SocketAddress ip() {
        return address;
    }

}
