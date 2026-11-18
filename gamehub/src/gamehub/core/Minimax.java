package gamehub.core;

import java.util.*;

/** Generic alpha-beta search with iterative deepening. Shared by most game AIs. */
public abstract class Minimax implements AI {
    protected static final double WIN = 100000, INF = 1e18;
    private long deadline; private boolean timeout; private long nodes;

    protected abstract double eval(GameState s, int me);
    protected int hardDepth() { return 3; }
    protected int unbeatableDepth() { return 60; }
    protected long unbeatableMs() { return 2500; }
    protected double easyRandom() { return 0.7; }
    protected double hardRandom() { return 0.1; }
    protected List<int[]> order(GameState s, List<int[]> moves) { return moves; }
    /** Moves considered by the search (override to prune). */
    protected List<int[]> candidates(GameState s) { return order(s, s.legalMoves()); }

    public int[] choose(GameState s, int me, String level, Random rng) {
        List<int[]> all = s.moves(me);
        switch (level) {
            case "easy":
                if (rng.nextDouble() < easyRandom()) return all.get(rng.nextInt(all.size()));
                return search(s, me, 1, 0, rng);
            case "hard":
                if (rng.nextDouble() < hardRandom()) return all.get(rng.nextInt(all.size()));
                return search(s, me, hardDepth(), 0, rng);
            default:
                return search(s, me, unbeatableDepth(), unbeatableMs(), rng);
        }
    }

    protected int[] search(GameState s, int me, int maxDepth, long ms, Random rng) {
        List<int[]> moves = candidates(s);
        if (moves.size() == 1) return moves.get(0);
        deadline = ms > 0 ? System.nanoTime() + ms * 1_000_000 : Long.MAX_VALUE;
        nodes = 0;
        int[] best = moves.get(0);
        for (int d = 1; d <= maxDepth; d++) {
            timeout = false;
            double bv = -INF; List<int[]> bms = new ArrayList<>();
            List<int[]> ord = new ArrayList<>(moves);
            ord.remove(best); ord.add(0, best);
            for (int[] m : ord) {
                GameState c = s.copy(); c.apply(me, m);
                double v = ab(c, me, d - 1, -INF, INF);
                if (timeout) break;
                if (v > bv + 1e-9) { bv = v; bms.clear(); bms.add(m); }
                else if (Math.abs(v - bv) <= 1e-9) bms.add(m);
            }
            if (timeout) break;
            best = bms.get(rng.nextInt(bms.size()));
            if (Math.abs(bv) >= WIN) break;
        }
        return best;
    }

    private double ab(GameState s, int me, int depth, double a, double b) {
        if (s.over()) return s.winner == me ? WIN + depth : s.winner == 2 ? 0 : -(WIN + depth);
        if (depth <= 0) return eval(s, me);
        if ((++nodes & 511) == 0 && System.nanoTime() > deadline) timeout = true;
        if (timeout) return 0;
        List<int[]> mv = candidates(s);
        if (mv.isEmpty()) return eval(s, me);
        int p = s.turn;
        if (p == me) {
            double v = -INF;
            for (int[] m : mv) {
                GameState c = s.copy(); c.apply(p, m);
                v = Math.max(v, ab(c, me, depth - 1, a, b));
                a = Math.max(a, v); if (a >= b || timeout) break;
            }
            return v;
        } else {
            double v = INF;
            for (int[] m : mv) {
                GameState c = s.copy(); c.apply(p, m);
                v = Math.min(v, ab(c, me, depth - 1, a, b));
                b = Math.min(b, v); if (a >= b || timeout) break;
            }
            return v;
        }
    }

    /** Helper: random element. */
    protected static int[] pick(List<int[]> l, Random r) { return l.get(r.nextInt(l.size())); }
}
