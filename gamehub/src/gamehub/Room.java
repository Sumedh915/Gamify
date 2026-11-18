package gamehub;

import gamehub.core.*;
import java.security.SecureRandom;
import java.util.*;

public class Room {
    private static final SecureRandom SR = new SecureRandom();
    public final String code, mode;       // mode: pvp | easy | hard | unbeatable
    public final boolean tournament;
    public GameDef def; public GameState state;
    private final String[] token = new String[2], cid = new String[2];
    private final String aiLevel;
    public int version = 1; public long touched = System.currentTimeMillis();
    private boolean recorded;
    private List<GameDef> tGames = new ArrayList<>(); private int tIndex; private final int[] tWins = new int[2]; private int tDraws, tWinner = -1; private boolean tDone;
    private final Random rng = new Random();

    public Room(String code, GameDef def, String mode, String hostCid, boolean tournament) {
        this.code = code; this.mode = mode; this.tournament = tournament;
        this.aiLevel = mode.equals("pvp") ? null : mode;
        token[0] = tok(); cid[0] = hostCid;
        if (tournament) {
            List<GameDef> all = new ArrayList<>(GameRegistry.all()); Collections.shuffle(all, rng);
            tGames = new ArrayList<>(all.subList(0, Math.min(5, all.size()))); this.def = tGames.get(0);
        } else this.def = def;
        state = this.def.factory().get();
        settle();
    }

    private static String tok() { byte[] b = new byte[9]; SR.nextBytes(b); return HexFormat.of().formatHex(b); }
    public String hostToken() { return token[0]; }
    public boolean started() { return aiLevel != null || token[1] != null; }

    public synchronized String[] join(String c) {
        if (aiLevel != null) throw new ApiError("That room is a game against the computer.");
        if (token[1] != null) throw new ApiError("That game already has two players.");
        token[1] = tok(); cid[1] = c; version++; touched = System.currentTimeMillis();
        return new String[]{"1", token[1]};
    }

    private int auth(int slot, String t) {
        if (slot < 0 || slot > 1 || token[slot] == null || !token[slot].equals(t)) throw new ApiError("Not a player in this room.");
        touched = System.currentTimeMillis(); return slot;
    }

    public synchronized Map<String, Object> view(int slot, String t) { auth(slot, t); return build(slot); }

    public synchronized Map<String, Object> move(int slot, String t, int[] m) {
        auth(slot, t);
        if (!started()) throw new ApiError("Waiting for an opponent.");
        boolean ok = false;
        if (state.canMove(slot)) for (int[] x : state.moves(slot)) if (Arrays.equals(x, m)) { ok = true; break; }
        if (!ok) throw new ApiError("Illegal move or not your turn.");
        state.apply(slot, m); version++;
        settle();
        return build(slot);
    }

    public synchronized Map<String, Object> next(int slot, String t) {
        auth(slot, t);
        if (tournament && state.over() && !tDone) { tIndex++; def = tGames.get(tIndex); newGame(); }
        return build(slot);
    }

    public synchronized Map<String, Object> rematch(int slot, String t) {
        auth(slot, t);
        if (!tournament && state.over()) newGame();
        return build(slot);
    }

    private void newGame() { state = def.factory().get(); recorded = false; version++; settle(); }

    private void settle() {
        int guard = 0;
        while (aiLevel != null && !state.over() && state.canMove(1) && guard++ < 500) {
            int[] m = def.ai().choose(state.copy(), 1, aiLevel, rng);
            state.apply(1, m); version++;
        }
        if (state.over() && !recorded) {
            recorded = true;
            String sm = aiLevel != null ? aiLevel : "pvp";
            int w = state.winner;
            Stats.record(cid[0], sm, def.id(), w == 0 ? 'w' : w == 1 ? 'l' : 'd');
            if (aiLevel == null) Stats.record(cid[1], sm, def.id(), w == 1 ? 'w' : w == 0 ? 'l' : 'd');
            if (tournament) {
                if (w == 2) tDraws++; else tWins[w]++;
                if (tWins[0] >= 3 || tWins[1] >= 3 || tIndex == tGames.size() - 1) {
                    tDone = true; tWinner = tWins[0] > tWins[1] ? 0 : tWins[1] > tWins[0] ? 1 : 2;
                    Stats.record(cid[0], sm, "_tournament", tWinner == 0 ? 'w' : tWinner == 1 ? 'l' : 'd');
                    if (aiLevel == null) Stats.record(cid[1], sm, "_tournament", tWinner == 1 ? 'w' : tWinner == 0 ? 'l' : 'd');
                }
            }
        }
    }

    private Map<String, Object> build(int slot) {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("code", code); v.put("game", def.id()); v.put("name", def.name()); v.put("mode", mode);
        v.put("slot", slot); v.put("started", started()); v.put("version", version);
        v.put("over", state.over()); v.put("winner", state.winner);
        boolean can = started() && state.canMove(slot);
        v.put("canMove", can); v.put("legal", can ? state.moves(slot) : List.of());
        v.put("s", state.view(slot));
        if (tournament) {
            Map<String, Object> t = new LinkedHashMap<>();
            List<Map<String, String>> gl = new ArrayList<>();
            for (GameDef g : tGames) gl.add(Map.of("id", g.id(), "name", g.name()));
            t.put("games", gl); t.put("index", tIndex); t.put("wins", tWins); t.put("draws", tDraws); t.put("done", tDone); t.put("winner", tWinner);
            v.put("t", t);
        }
        return v;
    }
}
