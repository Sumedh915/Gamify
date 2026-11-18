package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Moves: {0,r,c} pawn, {1,r,c} horizontal wall, {2,r,c} vertical wall (wall at r,c spans two cells). */
public class Quoridor extends GameState {
    public static final int MAX_PLIES = 300;
    public int[] pr = {8, 0}, pc = {4, 4}, walls = {10, 10};
    public boolean[][] hw = new boolean[8][8], vw = new boolean[8][8];
    public int plies;

    public GameState copy() {
        Quoridor q = base(new Quoridor());
        q.pr = pr.clone(); q.pc = pc.clone(); q.walls = walls.clone(); q.plies = plies;
        for (int i = 0; i < 8; i++) { q.hw[i] = hw[i].clone(); q.vw[i] = vw[i].clone(); }
        return q;
    }

    private static boolean in(int r, int c) { return r >= 0 && r < 9 && c >= 0 && c < 9; }

    public boolean blocked(int r, int c, int dr, int dc) {
        if (dr == 1) return r < 8 && ((c < 8 && hw[r][c]) || (c > 0 && hw[r][c - 1]));
        if (dr == -1) return r > 0 && ((c < 8 && hw[r - 1][c]) || (c > 0 && hw[r - 1][c - 1]));
        if (dc == 1) return c < 8 && ((r < 8 && vw[r][c]) || (r > 0 && vw[r - 1][c]));
        return c > 0 && ((r < 8 && vw[r][c - 1]) || (r > 0 && vw[r - 1][c - 1]));
    }

    private static final int[][] D = {{-1,0},{1,0},{0,-1},{0,1}};

    public List<int[]> pawnMoves(int p) {
        List<int[]> l = new ArrayList<>();
        int r = pr[p], c = pc[p], or = pr[1 - p], oc = pc[1 - p];
        for (int[] d : D) {
            int nr = r + d[0], nc = c + d[1];
            if (!in(nr, nc) || blocked(r, c, d[0], d[1])) continue;
            if (nr == or && nc == oc) {
                int jr = nr + d[0], jc = nc + d[1];
                if (in(jr, jc) && !blocked(nr, nc, d[0], d[1])) l.add(mv(0, jr, jc));
                else for (int[] q : new int[][]{{d[1], d[0]}, {-d[1], -d[0]}}) {
                    int tr = nr + q[0], tc = nc + q[1];
                    if (in(tr, tc) && !blocked(nr, nc, q[0], q[1])) l.add(mv(0, tr, tc));
                }
            } else l.add(mv(0, nr, nc));
        }
        return l;
    }

    /** Shortest path (steps) to goal row ignoring pawns; -1 if none. */
    public int pathLen(int p) {
        int goal = p == 0 ? 0 : 8;
        int[] dist = new int[81]; Arrays.fill(dist, -1);
        int[] q = new int[81]; int h = 0, t = 0;
        q[t++] = pr[p] * 9 + pc[p]; dist[q[0]] = 0;
        while (h < t) {
            int cur = q[h++], r = cur / 9, c = cur % 9;
            if (r == goal) return dist[cur];
            for (int[] d : D) {
                int nr = r + d[0], nc = c + d[1];
                if (in(nr, nc) && dist[nr * 9 + nc] < 0 && !blocked(r, c, d[0], d[1])) { dist[nr * 9 + nc] = dist[cur] + 1; q[t++] = nr * 9 + nc; }
            }
        }
        return -1;
    }

    public boolean wallOk(int t, int r, int c) {
        if (t == 1) {
            if (hw[r][c] || vw[r][c] || (c > 0 && hw[r][c - 1]) || (c < 7 && hw[r][c + 1])) return false;
            hw[r][c] = true;
        } else {
            if (vw[r][c] || hw[r][c] || (r > 0 && vw[r - 1][c]) || (r < 7 && vw[r + 1][c])) return false;
            vw[r][c] = true;
        }
        boolean ok = pathLen(0) >= 0 && pathLen(1) >= 0;
        if (t == 1) hw[r][c] = false; else vw[r][c] = false;
        return ok;
    }

    public List<int[]> wallMoves(int p, int centerR, int centerC, int radius) {
        List<int[]> l = new ArrayList<>();
        if (walls[p] == 0) return l;
        for (int r = Math.max(0, centerR - radius); r <= Math.min(7, centerR + radius); r++)
            for (int c = Math.max(0, centerC - radius); c <= Math.min(7, centerC + radius); c++)
                for (int t = 1; t <= 2; t++) if (wallOk(t, r, c)) l.add(mv(t, r, c));
        return l;
    }

    public List<int[]> moves(int p) {
        if (!canMove(p)) return new ArrayList<>();
        List<int[]> l = pawnMoves(p);
        l.addAll(wallMoves(p, 4, 4, 4));
        return l;
    }

    public void apply(int p, int[] m) {
        if (m[0] == 0) { pr[p] = m[1]; pc[p] = m[2]; if (m[1] == (p == 0 ? 0 : 8)) { winner = p; return; } }
        else { if (m[0] == 1) hw[m[1]][m[2]] = true; else vw[m[1]][m[2]] = true; walls[p]--; }
        if (++plies >= MAX_PLIES) { int a = pathLen(0), b = pathLen(1); winner = a < b ? 0 : b < a ? 1 : 2; return; }
        turn = 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) {
        m.put("pr", pr); m.put("pc", pc); m.put("walls", walls);
        List<int[]> h = new ArrayList<>(), w = new ArrayList<>();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) { if (hw[r][c]) h.add(new int[]{r, c}); if (vw[r][c]) w.add(new int[]{r, c}); }
        m.put("hw", h); m.put("vw", w);
    }
}
