package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

public class TicTacToe extends GameState {
    public static final int[][] LINES = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public int[] b = new int[9];
    public int[] line;

    public TicTacToe() { Arrays.fill(b, -1); }

    public GameState copy() { TicTacToe c = base(new TicTacToe()); c.b = b.clone(); c.line = line; return c; }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (over()) return l;
        for (int i : new int[]{4,0,2,6,8,1,3,5,7}) if (b[i] == -1) l.add(mv(i));
        return l;
    }

    public void apply(int p, int[] m) {
        b[m[0]] = p;
        for (int[] l : LINES) if (b[l[0]] == p && b[l[1]] == p && b[l[2]] == p) { winner = p; line = l; return; }
        boolean full = true; for (int v : b) if (v == -1) full = false;
        if (full) winner = 2;
        turn = 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) { m.put("board", b); m.put("line", line); }
}
