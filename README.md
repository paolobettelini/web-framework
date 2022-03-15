# Web Framework

HTTP/1.1 Web Framework

[WORKING PROGRESS]

## Starting a server

```java
    var server = new HttpServer(9090);

    // configuration

    server.start();
```

## Configuring the server

### Get request

```java
    // Matches the path "/"
    server.get("/", (req, res) -> "Hello, World!".getBytes());
```

### Post request

```java
    // Matches the path "/"
    server.post("/", (req, res) -> "Hello, World!".getBytes());
```

Note that the order of definition matters.
<br>
The first path to be matched is the one to be routed,
even if paths defined after would match the request.

### Variables

You can use a {...} scope to declare a variable.

```java
    // greet_John
    server.get("/greet_{name}", (req, res) -> {
        return ("Hello, " + req.param("name") + "!").getBytes();
    });

    // /compute32+10
    server.get("/compute{A}+{B}", (req, res) -> {
        try {
            int a = Integer.parseInt(req.param("A"));
            int b = Integer.parseInt(req.param("B"));
            return Integer.toString(a+b).getBytes();
        } catch (NumberFormatException e) {
            return "Invalid numbers!".getBytes();
        }
    });
```

### Advanced Pattern Matching

You can use a [...] scope to match any regular expression.

```java
    // Matches anything that starts with "/"
    server.get("/[.*]", (req, res) -> {
        // ...
    });

    server.get("/some/[[a-zA-Z\\]+]/path", (req, res) -> {
        // ...
    });
```

You can add a :regex token after a variable declaration to specify its pattern
<br>
The default regex for a variable is .* 

```java
    // Name must be at least 1 character
    server.get("/home/{name:.+}", (req, res) -> {
        // ...
    });

    // Matches /api/token where token is a string of 10 alphabetic letters
    server.get("/api/{token:[a-zA-Z]{10\\}}", (req, res) -> {
        // ...
    });
```

Make sure to always escape ], [, }, {, : when it is ambiguous.

### Redirecting

```java    
    // Redirecting /<name> to /home/<name>

    server.get("/home/{name}", (req, res) -> 
        ("Welcome to your home, " + req.param("name") + "!").getBytes());

    server.get("/{name}", (req, res) -> {
        res.redirect("http://127.0.0.1:9090/home/" + req.param("name"));
        
        return "".getBytes();
    });
```

### Serving htdocs

```java
    var htdocs = new Htdocs("/path/to/www");
    htdocs.addDefaultFile("index.html");

    // Simple serve
    server.get("/[.*]", htdocs.route());
```
