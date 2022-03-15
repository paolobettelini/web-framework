package ch.bettelini.library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Htdocs {
    
    private List<String> defaultFiles = new LinkedList<>();

    private static final Route DEFAULT_ON_NOT_FOUND = (req, res) -> {
        res.code(HttpCode.NOT_FOUND);
        return """
            <html>
            <body>
            <p>Error 404 Not Found</p>
            </body>
            </html>
            """.getBytes();
    };

    private static final Route DEFAULT_ON_FORBIDDEN = (req, res) -> {
        res.code(HttpCode.FORBIDDEN);
        return """
            <html>
            <body>
            <p>Error 403 Forbidden</p>
            </body>
            </html>
            """.getBytes();
    };

    private Route onNotFound = DEFAULT_ON_NOT_FOUND;
    private Route onForbidden = DEFAULT_ON_FORBIDDEN;

    private String absRoot;

    public Htdocs(String first, String... more) {
        this.absRoot = Path.of(first, more).toAbsolutePath().toString();
    }

    public Htdocs(String root) {
        this.absRoot = Path.of(root).toAbsolutePath().toString();
    }

    public void addDefaultFile(String fileName) {
        defaultFiles.add(fileName);
    }

    public Route route() {
        return (req, res) -> process(req, res, req.path().split("\\/"));
    }

    public Route route(String filePath) {
        return (req, res) -> process(req, res, new String[]{ filePath });
    }

    public Route route(String[] filePath) {
        return (req, res) -> process(req, res, filePath);
    }

    public Route route(Function<Request, String[]> filePathConstructor) {
        return (req, res) -> process(req, res, filePathConstructor.apply(req));
    }

    private byte[] process(Request req, Response res, String[] filePath) {
        var path = Path.of(absRoot, filePath);

        if (Files.isDirectory(path)) {
            boolean found = false;
            
            for (var defaultFile : defaultFiles) {
                var newPath = Path.of(path.toAbsolutePath().toString(), defaultFile);
                if (Files.exists(newPath)) {
                    found = true;
                    path = newPath;
                    break;
                }
            }

            if (!found) {
                return onNotFound.process(req, res);
            }
        }
        
        // Check forbidden
        if (!path.toAbsolutePath().startsWith(absRoot)) {
            return onForbidden.process(req, res);
        }

        if (!Files.exists(path)) {
            return onNotFound.process(req, res);
        }

        String ext = getExtension(path.getFileName().toString());
        res.type(HttpContentTypes.fromExtension(ext));

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return onNotFound.process(req, res);
        }
    }

    private static String getExtension(String file) {
        int index = file.lastIndexOf(".");
        return index == -1 ? "" : file.substring(index + 1, file.length());
    }

    public void onNotFound(Route onNotFound) {
        this.onNotFound = onNotFound;
    }

    public void onForbidden(Route onForbidden) {
        this.onForbidden = onForbidden;
    }

    public Route onNotFound() {
        return onNotFound;
    }

    public Route onForbidden() {
        return onForbidden;
    }

}
