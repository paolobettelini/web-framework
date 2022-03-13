package ch.bettelini.library;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {
    
    private byte[] content;

    private HttpCode code;

    private Map<String, String> headers = new HashMap<>();

    public void content(byte[] content) {
        this.content = content;
    }

    public void code(HttpCode code) {
        this.code = code;
    }

    public void code(int code) {
        this.code = HttpCode.parse(code);
    }

    public void header(String key, String value) {
        headers.put(key, value);
    }

    public void type(String type) {
        headers.put(HttpHeaders.CONTENT_TYPE, type);
    }

    public void redirect(String path) {
        redirect(path, HttpCode.MOVED_PERMANENTLY);
    }

    public void redirect(String path, HttpCode code) {
        this.code = code;
        header(HttpHeaders.LOCATION, path);
    }

    public void write(OutputStream out) throws IOException {
        headers.put(HttpHeaders.CONTENT_LENGTH, Integer.toString(content.length));
        
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.put(HttpHeaders.CONTENT_TYPE, "text/html");
        }
        
        if (code == null) {
            code = HttpCode.OK;
        }
        
        /*var builder = new StringBuilder();
        builder.append(HttpVersion.HTTP_1_1.toString());
        builder.append(" ");
        builder.append(code.toInteger());
        builder.append(" ");
        builder.append(code);
        builder.append("\r\n");

        for (var key : headers.keySet()) {
            builder.append(key);
            builder.append(": ");
            builder.append(headers.get(key));
            builder.append("\r\n");
        }

        builder.append("\r\n");
        out.write(builder.toString().getBytes());

        out.write(content);*/

        out.write(HttpVersion.HTTP_1_1.toString().getBytes());
        out.write(" ".getBytes());
        out.write(String.valueOf(code.toInteger()).getBytes());
        out.write(" ".getBytes());
        out.write(String.valueOf(code).getBytes());
        out.write("\r\n".getBytes());

        for (var key : headers.keySet()) {
            out.write(key.getBytes());
            out.write(": ".getBytes());
            out.write(headers.get(key).getBytes());
            out.write("\r\n".getBytes());
        }

        out.write("\r\n".getBytes());

        out.write(content);
        //System.out.println("Written: " + content.length);
    }

}
