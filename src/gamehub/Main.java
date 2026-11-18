package gamehub;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.Executors;

public final class Main {
    private static final Map<String, String> TYPES = Map.of(
        "html", "text/html; charset=utf-8", "js", "text/javascript; charset=utf-8", "css", "text/css; charset=utf-8",
        "svg", "image/svg+xml", "png", "image/png", "ico", "image/x-icon", "json", "application/json");

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Path web = Paths.get(System.getProperty("web", "web")).toAbsolutePath().normalize();
        if (!Files.isDirectory(web)) { System.err.println("Cannot find web folder at " + web + " - run from the project root."); System.exit(1); }
        HttpServer s = HttpServer.create(new InetSocketAddress(port), 0);
        s.createContext("/api/", Api::handle);
        s.createContext("/", ex -> serve(ex, web));
        s.setExecutor(Executors.newCachedThreadPool());
        s.start();
        System.out.println("GameHub running on http://localhost:" + port);
    }

    private static void serve(HttpExchange ex, Path web) throws IOException {
        String p = ex.getRequestURI().getPath();
        Path f = web.resolve("." + p).normalize();
        boolean file = f.startsWith(web) && Files.isRegularFile(f);
        if (!file) f = web.resolve("index.html");          // single-page app: /tag, /stats, /tictactoe/ABCDE ...
        String name = f.getFileName().toString();
        byte[] body = Files.readAllBytes(f);
        ex.getResponseHeaders().set("Content-Type", TYPES.getOrDefault(name.substring(name.lastIndexOf('.') + 1), "application/octet-stream"));
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.sendResponseHeaders(200, body.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(body); }
    }
}
