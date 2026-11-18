package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Checkers;

public class CheckersAI extends Minimax {
    protected int hardDepth() { return 4; }
    protected long unbeatableMs() { return 3000; }

    protected double eval(GameState st, int me) {
        Checkers k = (Checkers) st; double s = 0;
        for (int sq = 0; sq < 64; sq++) {
            int v = k.b[sq]; if (v == 0) continue;
            int p = (v - 1) / 2, r = sq / 8, c = sq % 8;
            boolean king = v % 2 == 0;
            double x = king ? 160 : 100;
            if (!king) x += (p == 0 ? 7 - r : r) * 4;
            if (c >= 2 && c <= 5 && r >= 2 && r <= 5) x += 3;
            if (!king && (p == 0 ? r == 7 : r == 0)) x += 5;   // back-rank guard
            s += p == me ? x : -x;
        }
        return s;
    }
}
