package ch.bettelini.library;

import java.util.Map;

public interface PathMatcher {
    
    boolean matches(String path);

    Map<String, String> params(String path);

}
