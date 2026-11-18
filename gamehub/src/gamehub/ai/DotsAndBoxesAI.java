package gamehub.ai;

import gamehub.core.*;
import gamehub.games.*;
import java.util.*;

public class DotsAndBoxesAI extends Minimax {
    protected int hardDepth() { return 4; }
    protected long unbeatableMs() { return 3000; }

    protected double eval(GameState st, int me) {
        DotsAndBoxes d = (DotsAndBoxes) st;
        double s = (d.sc[me] - d.sc[1 - me]) * 10;
        int three = 0;
        for (int b = 0; b < DotsAndBoxes.N * DotsAndBoxes.N; b++) if (d.own[b] == -1 && d.sides(b) == 3) three++;
        return s + (d.turn == me ? three : -three) * 6;
    }

    /** Captures first, then safe moves, then sacrifices. */
    protected List<int[]> order(GameState st, List<int[]> moves) {
        DotsAndBoxes d = (DotsAndBoxes) st;
        int[] key = new int[DotsAndBoxes.E];
        for (int[] m : moves) {
            int k = 1;
            for (int b : d.boxesOf(m[0])) { int n = d.sides(b); if (n == 3) { k = 0; break; } if (n == 2) k = 2; }
            key[m[0]] = k;
        }
        List<int[]> l = new ArrayList<>(moves);
        l.sort(Comparator.comparingInt(m -> key[m[0]]));
        return l;
    }
}
