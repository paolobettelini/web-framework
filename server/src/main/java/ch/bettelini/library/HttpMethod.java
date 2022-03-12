package ch.bettelini.library;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod parse(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
