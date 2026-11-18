package gamehub.ai;

import gamehub.core.*;
import gamehub.games.*;

public class UltimateTicTacToeAI extends Minimax {
    protected int hardDepth() { return 3; }

    private static double lines(int[] a, int off, int me, double two, double one) {
        double s = 0;
        for (int[] l : TicTacToe.LINES) {
            int m = 0, o = 0; boolean dead = false;
            for (int i : l) { int v = a[off + i]; if (v == me) m++; else if (v == 2) dead = true; else if (v != -1) o++; }
            if (dead) continue;
            if (o == 0) s += m == 2 ? two : m == 1 ? one : 0;
            if (m == 0) s -= o == 2 ? two : o == 1 ? one : 0;
        }
        return s;
    }

    protected double eval(GameState st, int me) {
        UltimateTicTacToe u = (UltimateTicTacToe) st;
        double s = lines(u.bw, 0, me, 25, 4);
        for (int b = 0; b < 9; b++) {
            if (u.bw[b] == me) s += 12; else if (u.bw[b] == 1 - me) s -= 12;
            else {
                s += lines(u.c, b * 9, me, 3, 0.5);
                if (u.c[b * 9 + 4] == me) s += 1.5; else if (u.c[b * 9 + 4] == 1 - me) s -= 1.5;
            }
        }
        if (u.bw[4] == me) s += 6; else if (u.bw[4] == 1 - me) s -= 6;
        // being sent to a decided board (free choice) is good for the mover
        if (u.next == -1) s += (u.turn == me ? 2 : -2);
        return s;
    }
}
