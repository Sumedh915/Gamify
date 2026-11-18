package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Phase 0: both players arrange fleets ({0}=reshuffle, {1}=ready). Phase 1: alternate shots ({cell}). */
public class Battleship extends GameState {
    public static final int[] LEN = {5, 4, 3, 3, 2};
    public static final String[] NAMES = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
    public int phase = 0;
    public boolean[] ready = {false, false};
    public int[][] ship = new int[2][100];   // -1 water else ship index
    public int[][] shot = new int[2][100];   // shots received: 0 none, 1 miss, 2 hit, 3 hit on sunk ship
    public int[][] left = new int[2][5];
    public String note = ""; public int lastShot = -1, lastBy = -1;
    private Random rng = new Random();

    public Battleship() { shuffle(0); shuffle(1); }
    private Battleship(boolean x) {}

    private void shuffle(int p) {
        Arrays.fill(ship[p], -1);
        for (int s = 0; s < 5; s++) {
            while (true) {
                boolean h = rng.nextBoolean(); int r = rng.nextInt(h ? 10 : 11 - LEN[s]), c = rng.nextInt(h ? 11 - LEN[s] : 10);
                boolean ok = true;
                for (int i = 0; i < LEN[s]; i++) if (ship[p][(r + (h ? 0 : i)) * 10 + c + (h ? i : 0)] != -1) ok = false;
                if (!ok) continue;
                for (int i = 0; i < LEN[s]; i++) ship[p][(r + (h ? 0 : i)) * 10 + c + (h ? i : 0)] = s;
                left[p][s] = LEN[s]; break;
            }
        }
    }

    public GameState copy() {
        Battleship c = base(new Battleship(true));
        c.phase = phase; c.ready = ready.clone(); c.note = note; c.lastShot = lastShot; c.lastBy = lastBy;
        for (int i = 0; i < 2; i++) { c.ship[i] = ship[i].clone(); c.shot[i] = shot[i].clone(); c.left[i] = left[i].clone(); }
        return c;
    }

    public boolean canMove(int p) { return !over() && (phase == 0 ? !ready[p] : p == turn); }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        if (phase == 0) { l.add(mv(0)); l.add(mv(1)); return l; }
        for (int i = 0; i < 100; i++) if (shot[1 - p][i] == 0) l.add(mv(i));
        return l;
    }

    public void apply(int p, int[] m) {
        if (phase == 0) {
            if (m[0] == 0) shuffle(p); else { ready[p] = true; if (ready[0] && ready[1]) { phase = 1; turn = 0; } }
            return;
        }
        int o = 1 - p, cell = m[0]; lastShot = cell; lastBy = p;
        if (ship[o][cell] >= 0) {
            int id = ship[o][cell]; shot[o][cell] = 2;
            if (--left[o][id] == 0) {
                for (int i = 0; i < 100; i++) if (ship[o][i] == id) shot[o][i] = 3;
                note = NAMES[id] + " sunk";
                boolean all = true; for (int x : left[o]) if (x > 0) all = false;
                if (all) { winner = p; return; }
            } else note = "hit";
        } else { shot[o][cell] = 1; note = "miss"; }
        turn = o;
    }

    protected void fill(Map<String, Object> m, int v) {
        int o = 1 - v;
        m.put("phase", phase); m.put("ready", ready); m.put("mine", ship[v]); m.put("incoming", shot[v]); m.put("outgoing", shot[o]);
        m.put("enemy", over() ? ship[o] : null); m.put("myLeft", left[v]); m.put("enemyLeft", left[o]);
        m.put("note", note); m.put("lastShot", lastShot); m.put("lastBy", lastBy);
        m.put("names", NAMES); m.put("lens", LEN);
    }
}
