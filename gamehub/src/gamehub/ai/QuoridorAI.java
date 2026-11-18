package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Quoridor;
import java.util.*;

/** Path-length evaluation; walls are only considered near the opponent to keep the branching factor small. */
public class QuoridorAI extends Minimax {
    protected int hardDepth() { return 2; }
    protected long unbeatableMs() { return 3000; }

    protected double eval(GameState st, int me) {
        Quoridor q = (Quoridor) st;
        int a = q.pathLen(me), b = q.pathLen(1 - me);
        return (b - a) * 10 + (q.walls[me] - q.walls[1 - me]) * 1.5 + (q.turn == me ? 1 : -1);
    }

    protected List<int[]> candidates(GameState st) {
        Quoridor q = (Quoridor) st;
        if (q.over()) return new ArrayList<>();
        int p = q.turn, o = 1 - p;
        List<int[]> l = q.pawnMoves(p);
        // pawn moves that shorten my path first
        Quoridor tmp = (Quoridor) q.copy();
        Map<int[], Integer> key = new IdentityHashMap<>();
        for (int[] m : l) { int or = tmp.pr[p], oc = tmp.pc[p]; tmp.pr[p] = m[1]; tmp.pc[p] = m[2]; key.put(m, tmp.pathLen(p)); tmp.pr[p] = or; tmp.pc[p] = oc; }
        l.sort(Comparator.comparingInt(key::get));
        // walls around the opponent (and ahead of them)
        int ar = Math.max(0, Math.min(7, q.pr[o] + (o == 0 ? -1 : 1) - 1)), ac = Math.max(0, Math.min(7, q.pc[o] - 1));
        for (int[] w : q.wallMoves(p, ar, ac, 1)) l.add(w);
        return l;
    }
}
