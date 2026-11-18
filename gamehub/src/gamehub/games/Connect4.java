package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

public class Connect4 extends GameState {
    public static final int R = 6, C = 7;
    public int[] b = new int[R * C];
    public int n, lastCell = -1;
    public int[] line;
    private static final int[] ORDER = {3, 2, 4, 1, 5, 0, 6};

    public Connect4() { Arrays.fill(b, -1); }

    public GameState copy() { Connect4 c = base(new Connect4()); c.b = b.clone(); c.n = n; c.lastCell = lastCell; return c; }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        for (int c : ORDER) if (b[c] == -1) l.add(mv(c));
        return l;
    }

    public void apply(int p, int[] m) {
        int col = m[0], r = R - 1;
        while (b[r * C + col] != -1) r--;
        b[r * C + col] = p; lastCell = r * C + col; n++;
        for (int[] d : new int[][]{{0,1},{1,0},{1,1},{1,-1}}) {
            List<Integer> cells = new ArrayList<>(); cells.add(lastCell);
            for (int s = -1; s <= 1; s += 2) {
                int rr = r + s * d[0], cc = col + s * d[1];
                while (rr >= 0 && rr < R && cc >= 0 && cc < C && b[rr * C + cc] == p) { cells.add(rr * C + cc); rr += s * d[0]; cc += s * d[1]; }
            }
            if (cells.size() >= 4) { winner = p; line = cells.stream().mapToInt(i -> i).toArray(); return; }
        }
        if (n == R * C) { winner = 2; return; }
        turn = 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) { m.put("b", b); m.put("last", lastCell); m.put("line", line); }
}
