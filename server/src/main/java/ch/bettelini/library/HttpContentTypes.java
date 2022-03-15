package ch.bettelini.library;

public class HttpContentTypes {

    public static String fromExtension(String ext) {
        return switch (ext) {
            case "mp4",
                 "webm" -> "video/" + ext;
            case "css",
                 "csv",
                 "xml",
                 "html",
                 "txt" -> "text/" + ext;
            case "gif",
                 "jpeg",
                 "jpg",
                 "png",
                 "tiff",
                 "svg" -> "image/" + ext;
            case "ogg",
                 "zip",
                 "json",
                 "javascript" -> "application/" + ext;
            default -> "*/*";
        };
    }

}