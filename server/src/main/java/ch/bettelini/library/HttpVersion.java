package ch.bettelini.library;

public enum HttpVersion {
    HTTP_1_1;

    @Override
    public String toString() {
        return switch (this) {
            case HTTP_1_1 -> "HTTP/1.1";
        };
    }

    public static HttpVersion parse(String version) {
        return switch (version) {
            case "HTTP/1.1" -> HTTP_1_1;
            default -> null;
        };
    }

}
