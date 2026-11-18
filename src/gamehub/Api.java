package gamehub;

import com.sun.net.httpserver.HttpExchange;
import gamehub.core.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Api {
    static Map<String, String> query(String raw) {
        Map<String, String> m = new HashMap<>();
        if (raw == null) return m;
        for (String kv : raw.split("&")) {
            int i = kv.indexOf('=');
            if (i > 0) m.put(URLDecoder.decode(kv.substring(0, i), StandardCharsets.UTF_8), URLDecoder.decode(kv.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    public static void handle(HttpExchange ex) throws IOException {
        int status = 200; Object out;
        try { out = route(ex.getRequestURI().getPath(), query(ex.getRequestURI().getRawQuery())); }
        catch (ApiError e) { status = 400; out = Map.of("error", e.getMessage()); }
        catch (Exception e) { e.printStackTrace(); status = 500; out = Map.of("error", "Server error"); }
        byte[] body = Json.write(out).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, body.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(body); }
    }

    private static String cid(Map<String, String> q) {
        String c = q.get("cid"); return c != null && c.matches("[A-Za-z0-9-]{8,64}") ? c : null;
    }
    private static int slot(Map<String, String> q) { try { return Integer.parseInt(q.getOrDefault("slot", "-1")); } catch (NumberFormatException e) { return -1; } }

    private static Object route(String path, Map<String, String> q) {
        switch (path) {
            case "/api/games": {
                List<Map<String, String>> l = new ArrayList<>();
                for (GameDef d : GameRegistry.all()) l.add(Map.of("id", d.id(), "name", d.name(), "desc", d.desc(), "icon", d.icon()));
                return l;
            }
            case "/api/create": {
                String mode = q.getOrDefault("mode", "pvp");
                if (!List.of("pvp", "easy", "hard", "unbeatable").contains(mode)) throw new ApiError("Unknown mode.");
                boolean tour = "1".equals(q.get("tournament"));
                GameDef d = tour ? null : GameRegistry.get(q.get("game"));
                if (!tour && d == null) throw new ApiError("Unknown game.");
                Room r = Rooms.create(d, mode, cid(q), tour);
                return Map.of("code", r.code, "slot", 0, "token", r.hostToken(), "game", tour ? "tournament" : r.def.id());
            }
            case "/api/join": {
                Room r = Rooms.get(q.get("code")); String[] s = r.join(cid(q));
                return Map.of("code", r.code, "slot", 1, "token", s[1], "game", r.tournament ? "tournament" : r.def.id());
            }
            case "/api/state": {
                Room r = Rooms.get(q.get("code"));
                if (String.valueOf(r.version).equals(q.get("v"))) { r.view(slot(q), q.get("t")); return Map.of("same", true); }
                return r.view(slot(q), q.get("t"));
            }
            case "/api/move": {
                Room r = Rooms.get(q.get("code"));
                try {
                    String[] p = q.getOrDefault("m", "").split(",");
                    int[] m = new int[p.length]; for (int i = 0; i < p.length; i++) m[i] = Integer.parseInt(p[i].trim());
                    return r.move(slot(q), q.get("t"), m);
                } catch (NumberFormatException e) { throw new ApiError("Bad move."); }
            }
            case "/api/next": return Rooms.get(q.get("code")).next(slot(q), q.get("t"));
            case "/api/rematch": return Rooms.get(q.get("code")).rematch(slot(q), q.get("t"));
            case "/api/stats": return Stats.get(cid(q));
            default: throw new ApiError("Unknown endpoint.");
        }
    }
}
