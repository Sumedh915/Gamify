package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

public class UltimateTicTacToe extends GameState {
    public int[] c = new int[81];   // cell b*9+k, -1 empty
    public int[] bw = new int[9];   // -1 open, 0/1 won, 2 drawn
    public int next = -1;           // forced sub-board or -1
    public int[] last;

    public UltimateTicTacToe() { Arrays.fill(c, -1); Arrays.fill(bw, -1); }

    public GameState copy() {
        UltimateTicTacToe u = base(new UltimateTicTacToe());
        u.c = c.clone(); u.bw = bw.clone(); u.next = next; u.last = last; return u;
    }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (over()) return l;
        for (int b : new int[]{4,0,2,6,8,1,3,5,7}) {
            if (bw[b] != -1 || (next != -1 && next != b)) continue;
            for (int k : new int[]{4,0,2,6,8,1,3,5,7}) if (c[b * 9 + k] == -1) l.add(mv(b, k));
        }
        return l;
    }

    private static boolean wins(int[] a, int off, int p) {
        for (int[] l : TicTacToe.LINES) if (a[off + l[0]] == p && a[off + l[1]] == p && a[off + l[2]] == p) return true;
        return false;
    }

    public void apply(int p, int[] m) {
        int b = m[0], k = m[1];
        c[b * 9 + k] = p; last = m;
        if (wins(c, b * 9, p)) bw[b] = p;
        else { boolean full = true; for (int i = 0; i < 9; i++) if (c[b * 9 + i] == -1) full = false; if (full) bw[b] = 2; }
        next = bw[k] == -1 ? k : -1;
        if (wins(bw, 0, p)) { winner = p; return; }
        int open = 0, n0 = 0, n1 = 0;
        for (int v : bw) { if (v == -1) open++; else if (v == 0) n0++; else if (v == 1) n1++; }
        if (open == 0) { winner = n0 > n1 ? 0 : n1 > n0 ? 1 : 2; return; }
        turn = 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) { m.put("c", c); m.put("bw", bw); m.put("next", next); m.put("last", last); }
}
