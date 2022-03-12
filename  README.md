# Webserver

HTTP/1.1 Webserver

[NOT CURRENTLY WORKING]

```java
    var server = new HttpServer(9090);

    var htdocs = new Htdocs("/home/paolo/Desktop/webserver/www");
    htdocs.addDefaultFile("index.html");
    htdocs.addDefaultFile("home.html");

    server.get("/*", htdocs.route());

    server.start();
```