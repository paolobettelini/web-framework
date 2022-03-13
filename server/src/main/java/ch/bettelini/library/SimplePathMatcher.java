package ch.bettelini.library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimplePathMatcher implements PathMatcher {
    
    private List<PathSegment> segments = new LinkedList<>();
    private Pattern pattern;

    public SimplePathMatcher(String path) {
        // Construct path segments
        var builder = new StringBuilder();
        boolean variable = false;
        char lastChar = ' ';
        for (char c : path.toCharArray()) {
            if (!variable && c == '{' && lastChar != '\\') {
                segments.add(new PathSegment(PathSegmentType.TEXT, builder.toString()));
                builder.setLength(0);
                variable = true;
            } else if (variable && c == '}' && lastChar != '\\') {
                segments.add(new PathSegment(PathSegmentType.VARIABLE, builder.toString()));
                builder.setLength(0);
                variable = false;
            } else if (!variable && c == '*' && lastChar != '\\') {
                segments.add(new PathSegment(PathSegmentType.TEXT, builder.toString()));
                segments.add(new PathSegment(PathSegmentType.WILDCARD, ""));
                builder.setLength(0);
                
            } else {
                builder.append(c);
            }

            lastChar = c;
        }
        if (builder.length() != 0) {
            segments.add(new PathSegment(PathSegmentType.TEXT, builder.toString()));
        }

        // Create regex for path matching
        var regex = new StringBuilder();
        for (var segment : segments) {
            switch (segment.type) {
                case WILDCARD -> {
                    regex.append(".*");
                }
                case TEXT -> {
                    regex.append(disableRegex(segment.value()));
                }
                case VARIABLE -> {
                    regex.append(".+");
                }
            }
        }
        this.pattern = Pattern.compile(regex.toString());

        // Verify structure
        //
        // The following combinations are invalid
        // VARIABLE|VARIABLE
        // WILDCARD|VARIABLE
        // VARIABLE|WILDCARD
        PathSegment lastSegment = null;
        for (var segment : segments) {
            switch (segment.type) {
                case WILDCARD -> {
                    if (lastSegment != null) {
                        if (lastSegment.type == PathSegmentType.VARIABLE) {
                            throw new IllegalArgumentException("Invalid path construct: {var}*");
                        }
                    }
                }
                case VARIABLE -> {
                    if (lastSegment != null) {
                        if (lastSegment.type == PathSegmentType.WILDCARD) {
                            throw new IllegalArgumentException("Invalid path construct: *{var}");
                        }
                        if (lastSegment.type == PathSegmentType.VARIABLE) {
                            throw new IllegalArgumentException("Invalid path construct: {var}{var}");
                        }
                    }
                }
                case TEXT -> {}
            }

            
            lastSegment = segment;
        }
     
        // TODO: Simplify (remove everything after last VARIABLE, remove WILDCARD + ?)
        // and VARIABLE before VARIABLE
    }

    @Override
    public boolean matches(String path) {
        return pattern.matcher(path).matches();
    }

    @Override
    public Map<String, String> params(String path) {
        // Assuming the arguments matches

        Map<String, String> params = new HashMap<>();

        int pos = 0;
        PathSegment lastSegment = null;
        for (var segment : segments) {
            int oldPos = pos;
            if (segment.type == PathSegmentType.TEXT) {
                pos = path.indexOf(segment.value, pos);

                if (lastSegment != null && lastSegment.type == PathSegmentType.VARIABLE) {
                    params.put(lastSegment.value, path.substring(oldPos, pos));
                }

                pos += segment.value.length();
            }


            lastSegment = segment;
        }

        if (lastSegment.type == PathSegmentType.VARIABLE) {
            params.put(lastSegment.value, path.substring(pos, path.length()));
        }
        
        return params;
    }

    private static String disableRegex(String str) {
        return str
            .replaceAll("\\\\", "\\\\\\\\")
            .replaceAll("\\.", "\\\\.")
            .replaceAll("/", "\\\\/")
            .replaceAll("\\^", "\\\\^")
            .replaceAll("\\?", "\\\\?")
            .replaceAll("\\+", "\\\\+")
            .replaceAll("\\|", "\\\\|")
            .replaceAll("\\*", "\\\\*")
            .replaceAll("\\[", "\\\\[")
            .replaceAll("\\]", "\\\\]");
    }

    private static enum PathSegmentType {
        TEXT,
        VARIABLE,
        WILDCARD
    }

    private static record PathSegment(PathSegmentType type, String value) {}

    /*
    private static interface PathSegment {}
    private static record Text(String value)    implements PathSegment {}
    private static record Variable(String name) implements PathSegment {}
    private static class Wildcard               implements PathSegment {}
    
    switch (v) {
        case Text ss -> {}
        case Variable v -> {}
        case Wildcard w -> {}
    }
    */
}
