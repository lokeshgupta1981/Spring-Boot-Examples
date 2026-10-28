# Spring Boot DevTools demo

This sample accompanies the HowToDoInJava DevTools article. It uses Spring Boot 4.1.2 and Java 17 or later.

Run the application from this directory:

```bash
mvn spring-boot:run
```

Request `http://localhost:8080/status` to see `Catalog ready v1`.

To observe DevTools restart, change the return value in `StatusController` to `Catalog ready v2` and run `mvn compile` in a second terminal. Request the endpoint again. The running process should log a classpath change and return `Catalog ready v2`. Restore the source to `v1` when finished.

To observe a static resource change, edit `src/main/resources/static/note.txt`, run `mvn resources:resources`, and request `http://localhost:8080/note.txt`. The file changes without a full application context restart.

`mvn package` creates a JAR without the DevTools library in `BOOT-INF/lib` by default. A `java -jar` launch does not use the development restart loop.
