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

    private Route onNotFound = DEFAULT_ON_NOT_FOUND;

    private String root;

    public Htdocs(String root) {
        this.root = root;
    }

    public void addDefaultFile(String fileName) {
        defaultFiles.add(fileName);
    }

    public Route route() {
        return route(null);
    }

    public Route route(Function<Request, String> filePathConstructor) {
        return (req, res) -> {
            var filePath = filePathConstructor == null ? req.path() : filePathConstructor.apply(req);
            var path = Path.of(root, filePath);
            //System.out.println(path);

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
        };
    }

    private static String getExtension(String file) {
        int index = file.lastIndexOf(".");
        return index == -1 ? "" : file.substring(index + 1, file.length());
    }

    public void onNotFound(Route onNotFound) {
        this.onNotFound = onNotFound;
    }

}
