package ch.bettelini.library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexPathMatcher implements PathMatcher {
    
    private List<PathSegment> segments = new LinkedList<>();
    private Pattern pattern;

    public static void main(String[] args) {
        var matcher = new RegexPathMatcher("/sometext/[.+]/{var1}/{var2:.+}");
        
        var input = "/sometext/REGEX/1/VAR2";

        System.out.println(matcher.matches(input));

        matcher.params(input)
            .forEach((key, value) -> System.out.println(key + ":\t" + value));
    }
    
    public RegexPathMatcher(String path) {
        // EXAMPLE:
        //          /sometext/[.*]/{var1}/{var2:.+}
        
        // Construct path segments

        List<String> variableNames = new LinkedList<>();
        var buffer = new StringBuilder();
        
        boolean variableToken = false;
        boolean variableRegexToken = false;
        boolean regexToken = false;
        boolean escaping = false;

        for (char c : path.toCharArray()) {
            if (!escaping && c == '\\') {
                escaping = true;
                continue;
            }

            if (escaping) {
                buffer.append(c);
                escaping = false;
            }

            if (!variableToken && c == '{') {
                segments.add(new PathSegment(PathSegmentType.TEXT, buffer.toString()));
                buffer.setLength(0);
                variableToken = true;
            } else if (variableToken && c == '}') {
                if (variableRegexToken) {
                    segments.add(new PathSegment(PathSegmentType.VARIABLE, buffer.toString()));
                } else {
                    segments.add(new PathSegment(PathSegmentType.VARIABLE, ".*")); // default regex
                    variableNames.add(buffer.toString());
                }
                buffer.setLength(0);
                variableToken = false;
                variableRegexToken = false;
            } else if (variableToken && !variableRegexToken && c == ':') {
                variableNames.add(buffer.toString());
                buffer.setLength(0);
                variableRegexToken = true;
            }  else if (!variableToken && !regexToken && c == '[') {
                segments.add(new PathSegment(PathSegmentType.TEXT, buffer.toString()));
                buffer.setLength(0);
                regexToken = true;
            } else if (regexToken && c == ']') {
                segments.add(new PathSegment(PathSegmentType.REGEX, buffer.toString()));
                buffer.setLength(0);
                regexToken = false;
            } else {
                buffer.append(c);
            }
        }

        if (buffer.length() != 0) {
            segments.add(new PathSegment(PathSegmentType.TEXT, buffer.toString()));
        }

        // EXAMPLE:
        //          TEXT /sometext/
        //          REGEX .*
        //          TEXT /
        //          VARIABLE var1 .*
        //          TEXT /
        //          VARIABLE var2 .+


        int name = 0;
        for (var seg : segments) {
            System.out.print(seg.type + " ");

            if (seg.type == PathSegmentType.VARIABLE) {
                System.out.println(variableNames.get(name++) + " " + seg.value);
            } else {
                System.out.println(seg.value);
            }
        }

        // Create regex for path matching
        var regex = new StringBuilder();
        var namesIterator = variableNames.iterator();
        for (var segment : segments) {
            switch (segment.type) {
                case REGEX -> {
                    regex.append(segment.value);
                }
                case TEXT -> {
                    regex.append(disableRegex(segment.value));
                }
                case VARIABLE -> {
                    regex.append(segment.value);

                    // replace value (regex) with variable name
                    segment.value = namesIterator.next();
                }
            }
        }
        System.out.println(regex.toString());
        this.pattern = Pattern.compile(regex.toString());
     
        // EXAMPLE:
        //          \/sometext\/.*\/.*\/.+

        // TODO: check duplicate variables
        // TODO: Simplify (remove everything after last VARIABLE)
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
        REGEX
    }

    private static class PathSegment {

        public PathSegmentType type;
        public String value;
        
        public PathSegment(PathSegmentType type, String value) {
            this.type = type;
            this.value = value;
        }

    }
    

}
