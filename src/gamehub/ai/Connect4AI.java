package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Connect4;
import java.util.*;

public class Connect4AI extends Minimax {
    private static final List<int[]> WIN = new ArrayList<>();
    static {
        int[][] ds = {{0,1},{1,0},{1,1},{1,-1}};
        for (int r = 0; r < Connect4.R; r++) for (int c = 0; c < Connect4.C; c++) for (int[] d : ds) {
            int er = r + 3 * d[0], ec = c + 3 * d[1];
            if (er < 0 || er >= Connect4.R || ec < 0 || ec >= Connect4.C) continue;
            int[] w = new int[4]; for (int i = 0; i < 4; i++) w[i] = (r + i * d[0]) * Connect4.C + c + i * d[1];
            WIN.add(w);
        }
    }
    protected int hardDepth() { return 4; }
    protected long unbeatableMs() { return 3000; }

    protected double eval(GameState st, int me) {
        Connect4 g = (Connect4) st; double s = 0;
        for (int[] w : WIN) {
            int m = 0, o = 0;
            for (int i : w) { int v = g.b[i]; if (v == me) m++; else if (v != -1) o++; }
            if (m > 0 && o > 0) continue;
            if (m == 3) s += 6; else if (m == 2) s += 2; else if (m == 1) s += 0.2;
            if (o == 3) s -= 7; else if (o == 2) s -= 2; else if (o == 1) s -= 0.2;
        }
        for (int r = 0; r < Connect4.R; r++) { int v = g.b[r * Connect4.C + 3]; if (v == me) s += 3; else if (v != -1) s -= 3; }
        return s + (g.turn == me ? 1 : -1);
    }
}
