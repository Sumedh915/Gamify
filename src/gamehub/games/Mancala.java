package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Kalah. Pits 0-5 (player 0), store 6, pits 7-12 (player 1), store 13. */
public class Mancala extends GameState {
    public int[] pit = new int[14];

    public Mancala() { for (int i = 0; i < 14; i++) pit[i] = (i == 6 || i == 13) ? 0 : 4; }

    public GameState copy() { Mancala c = base(new Mancala()); c.pit = pit.clone(); return c; }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (over()) return l;
        for (int i = 5; i >= 0; i--) if (pit[p * 7 + i] > 0) l.add(mv(p * 7 + i));
        return l;
    }

    public void apply(int p, int[] m) {
        int i = m[0], n = pit[i]; pit[i] = 0;
        int mine = p == 0 ? 6 : 13, opp = p == 0 ? 13 : 6, idx = i;
        while (n > 0) { idx = (idx + 1) % 14; if (idx == opp) continue; pit[idx]++; n--; }
        boolean ownSide = p == 0 ? idx <= 5 : (idx >= 7 && idx <= 12);
        if (idx == mine) turn = p;
        else {
            if (ownSide && pit[idx] == 1 && pit[12 - idx] > 0) { pit[mine] += pit[12 - idx] + 1; pit[idx] = 0; pit[12 - idx] = 0; }
            turn = 1 - p;
        }
        int a = 0, b = 0;
        for (int k = 0; k < 6; k++) { a += pit[k]; b += pit[7 + k]; }
        if (a == 0 || b == 0) {
            pit[6] += a; pit[13] += b;
            for (int k = 0; k < 6; k++) { pit[k] = 0; pit[7 + k] = 0; }
            winner = pit[6] > pit[13] ? 0 : pit[13] > pit[6] ? 1 : 2;
        }
    }

    protected void fill(Map<String, Object> m, int v) { m.put("pit", pit); }
}
