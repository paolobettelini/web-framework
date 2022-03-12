package ch.bettelini.library;

public enum HttpCode {
    OK,
    NOT_FOUND;

    @Override
    public String toString() {
        return switch (this) {
            case OK -> "OK";
            case NOT_FOUND -> "Not Found";
        };
    }

    public int toInteger() {
        return switch (this) {
            case OK -> 200;
            case NOT_FOUND -> 404;
        };
    }

    public static HttpCode parse(int code) {
        return switch (code) {
            case 200 -> OK;
            case 404 -> NOT_FOUND;
            default -> null;
        };
    }
}
