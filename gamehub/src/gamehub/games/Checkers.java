package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** American checkers. Board values: 0 empty, 1/2 = p0 man/king, 3/4 = p1 man/king. Move = path of squares. */
public class Checkers extends GameState {
    public int[] b = new int[64];
    public int quiet = 0;
    public int[] last;
    private List<int[]> cache;

    public Checkers() {
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) if ((r + c) % 2 == 1) { if (r < 3) b[r * 8 + c] = 3; else if (r > 4) b[r * 8 + c] = 1; }
    }

    public GameState copy() { Checkers k = base(new Checkers()); k.b = b.clone(); k.quiet = quiet; k.last = last; return k; }

    private static boolean mine(int v, int p) { return v > 0 && v < 9 && (v - 1) / 2 == p; }

    public List<int[]> moves(int p) {
        if (!canMove(p)) return new ArrayList<>();
        if (p == turn && cache != null) return cache;
        List<int[]> jumps = new ArrayList<>(), steps = new ArrayList<>();
        for (int sq = 0; sq < 64; sq++) {
            int v = b[sq];
            if (!mine(v, p)) continue;
            boolean king = v % 2 == 0;
            b[sq] = 0;
            jump(p, sq, king, new int[]{sq}, jumps);
            b[sq] = v;
            int r = sq / 8, c = sq % 8;
            for (int dr : new int[]{-1, 1}) {
                if (!king && dr != (p == 0 ? -1 : 1)) continue;
                for (int dc : new int[]{-1, 1}) {
                    int nr = r + dr, nc = c + dc;
                    if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && b[nr * 8 + nc] == 0) steps.add(new int[]{sq, nr * 8 + nc});
                }
            }
        }
        List<int[]> res = jumps.isEmpty() ? steps : jumps;
        if (p == turn) cache = res;
        return res;
    }

    private void jump(int p, int sq, boolean king, int[] path, List<int[]> out) {
        boolean any = false; int r = sq / 8, c = sq % 8;
        for (int dr : new int[]{-1, 1}) {
            if (!king && dr != (p == 0 ? -1 : 1)) continue;
            for (int dc : new int[]{-1, 1}) {
                int mr = r + dr, mc = c + dc, lr = r + 2 * dr, lc = c + 2 * dc;
                if (lr < 0 || lr > 7 || lc < 0 || lc > 7) continue;
                int mid = b[mr * 8 + mc];
                if (!mine(mid, 1 - p) || b[lr * 8 + lc] != 0) continue;
                any = true;
                int[] np = Arrays.copyOf(path, path.length + 1); np[path.length] = lr * 8 + lc;
                b[mr * 8 + mc] = 9;                       // captured, still blocks
                if (!king && lr == (p == 0 ? 0 : 7)) out.add(np);   // promotion ends the jump
                else jump(p, lr * 8 + lc, king, np, out);
                b[mr * 8 + mc] = mid;
            }
        }
        if (!any && path.length > 1) out.add(path);
    }

    public void apply(int p, int[] m) {
        int v = b[m[0]]; boolean cap = false; b[m[0]] = 0;
        for (int i = 1; i < m.length; i++) {
            int fr = m[i - 1] / 8, fc = m[i - 1] % 8, tr = m[i] / 8, tc = m[i] % 8;
            if (Math.abs(tr - fr) == 2) { b[(fr + tr) / 2 * 8 + (fc + tc) / 2] = 0; cap = true; }
        }
        int end = m[m.length - 1];
        if (v % 2 == 1 && end / 8 == (p == 0 ? 0 : 7)) v++;
        b[end] = v; last = m;
        quiet = (cap || v % 2 == 1) ? 0 : quiet + 1;
        turn = 1 - p; cache = null;
        if (moves(turn).isEmpty()) winner = p;
        else if (quiet >= 80) winner = 2;
    }

    protected void fill(Map<String, Object> m, int v) { m.put("b", b); m.put("last", last); }
}
