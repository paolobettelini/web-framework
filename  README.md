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
    server.get("/", (req, res) -> {
        return "Hello, World!".getBytes();
    });
```

### Post request

```java
    server.post("/", (req, res) -> {
        return "Hello, World!".getBytes();
    });
```

### Wildcard

```java
    server.get("/some/*/path", (req, res) -> {
        return "Some, Path!".getBytes();
    });
```

### Variables

```java
    // greet_John
    server.get("/greet_{name}", (req, res) -> {
        System.out.println(req.param("name"));
        return ("Hello, " + req.param("name") + "!").getBytes();
    });

    // /compute32+10
    server.get("/compute{A}+{B}", (req, res) -> {
        try {
            int a = Integer.parseInt(req.param("A"));
            int b = Integer.parseInt(req.param("A"));
            return Integer.toString(a+b).getBytes();
        } catch (NumberFormatException e) {
            return "Invalid numbers!".getBytes();
        }
    });
```

### Serving htdocs

```java
    var htdocs = new Htdocs("/path/to/www");
    htdocs.addDefaultFile("index.html");

    // Simple serve
    server.get("/*", htdocs.route());

    // Serve /path/to/www/{path} when the request is /htdocs/{path)}
    server.get("/htdocs/{path}", htdocs.route(req -> req.param("path")));
```