package ch.bettelini.library;

@FunctionalInterface
public interface Route {
    
    byte[] process(Request request, Response response);

}
