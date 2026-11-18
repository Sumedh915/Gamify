package gamehub;

import java.io.*;
import java.util.*;

/** Per-player win/loss/draw counters saved to data/stats.properties. Key: cid.mode.game.result */
public final class Stats {
    private static final Properties P = new Properties();
    private static final File F = new File("data/stats.properties");
    public static final String[] MODES = {"easy", "hard", "unbeatable", "pvp"};

    static {
        if (F.exists()) try (Reader r = new FileReader(F)) { P.load(r); } catch (IOException e) { e.printStackTrace(); }
    }

    public static synchronized void record(String cid, String mode, String game, char res) {
        if (cid == null) return;
        String k = cid + "." + mode + "." + game + "." + res;
        P.setProperty(k, String.valueOf(Integer.parseInt(P.getProperty(k, "0")) + 1));
        F.getParentFile().mkdirs();
        try (Writer w = new FileWriter(F)) { P.store(w, "gamehub stats"); } catch (IOException e) { e.printStackTrace(); }
    }

    private static int idx(char r) { return r == 'w' ? 0 : r == 'l' ? 1 : 2; }

    public static synchronized Map<String, Object> get(String cid) {
        Map<String, int[]> totals = new LinkedHashMap<>(), tour = new LinkedHashMap<>();
        Map<String, Map<String, int[]>> games = new LinkedHashMap<>();
        for (String m : MODES) { totals.put(m, new int[3]); tour.put(m, new int[3]); }
        for (String k : P.stringPropertyNames()) {
            if (cid == null || !k.startsWith(cid + ".")) continue;
            String[] p = k.substring(cid.length() + 1).split("\\.");
            if (p.length != 3) continue;
            int n = Integer.parseInt(P.getProperty(k));
            if (p[1].equals("_tournament")) { tour.computeIfAbsent(p[0], x -> new int[3])[idx(p[2].charAt(0))] += n; continue; }
            totals.computeIfAbsent(p[0], x -> new int[3])[idx(p[2].charAt(0))] += n;
            games.computeIfAbsent(p[1], x -> new LinkedHashMap<>()).computeIfAbsent(p[0], x -> new int[3])[idx(p[2].charAt(0))] += n;
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("modes", totals); out.put("games", games); out.put("tournaments", tour);
        return out;
    }
}
