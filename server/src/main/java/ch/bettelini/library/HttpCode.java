package ch.bettelini.library;

public enum HttpCode {
    OK,
    NOT_FOUND,
    MOVED_PERMANENTLY,
    SEE_OTHER;

    @Override
    public String toString() {
        return switch (this) {
            case OK -> "OK";
            case NOT_FOUND -> "Not Found";
            case MOVED_PERMANENTLY -> "Moved Permanently";
            case SEE_OTHER -> "See Other";
        };
    }

    public int toInteger() {
        return switch (this) {
            case OK -> 200;
            case NOT_FOUND -> 404;
            case MOVED_PERMANENTLY -> 301;
            case SEE_OTHER -> 303;
        };
    }

    public static HttpCode parse(int code) {
        return switch (code) {
            case 200 -> OK;
            case 404 -> NOT_FOUND;
            case 301 -> MOVED_PERMANENTLY;
            case 303 -> SEE_OTHER;
            default -> null;
        };
    }
}
