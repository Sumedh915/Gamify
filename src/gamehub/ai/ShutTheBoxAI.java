package gamehub.ai;

import gamehub.core.*;
import gamehub.games.ShutTheBox;
import java.util.*;

/** Hard: minimises expected final score (exact DP). Unbeatable: same, but once the opponent has finished it maximises win probability against their known total. */
public class ShutTheBoxAI implements AI {
    private static final double[] E = new double[512];
    private static final double[][] W = new double[60][];

    private static double[] probs(int mask) {   // probability of each roll total 0..12
        double[] p = new double[13];
        if (ShutTheBox.oneDie(mask)) for (int r = 1; r <= 6; r++) p[r] = 1.0 / 6;
        else for (int a = 1; a <= 6; a++) for (int b = 1; b <= 6; b++) p[a + b] += 1.0 / 36;
        return p;
    }

    static {
        for (int mask = 1; mask < 512; mask++) {
            double[] p = probs(mask); double ev = 0;
            for (int r = 1; r <= 12; r++) {
                if (p[r] == 0) continue;
                double best = ShutTheBox.tileSum(mask);
                for (int s = mask; s > 0; s = (s - 1) & mask) if (ShutTheBox.tileSum(s) == r) best = Math.min(best, E[mask & ~s]);
                ev += p[r] * best;
            }
            E[mask] = ev;
        }
    }

    private static double[] win(int t) {
        if (W[t] != null) return W[t];
        double[] w = new double[512];
        java.util.function.IntToDoubleFunction res = x -> x < t ? 1 : x == t ? 0.5 : 0;
        w[0] = res.applyAsDouble(0);
        for (int mask = 1; mask < 512; mask++) {
            double[] p = probs(mask); double v = 0;
            for (int r = 1; r <= 12; r++) {
                if (p[r] == 0) continue;
                double best = -1; boolean any = false;
                for (int s = mask; s > 0; s = (s - 1) & mask) if (ShutTheBox.tileSum(s) == r) { any = true; best = Math.max(best, w[mask & ~s]); }
                v += p[r] * (any ? best : res.applyAsDouble(ShutTheBox.tileSum(mask)));
            }
            w[mask] = v;
        }
        return W[t] = w;
    }

    public int[] choose(GameState st, int me, String level, Random rng) {
        ShutTheBox s = (ShutTheBox) st;
        List<int[]> mv = s.moves(me);
        if (mv.size() == 1 && mv.get(0)[0] == 0) return mv.get(0);   // no available move
        if (level.equals("easy") && rng.nextDouble() < 0.8) return mv.get(rng.nextInt(mv.size()));
        boolean useWin = level.equals("unbeatable") && s.score[1 - me] >= 0;
        int[] best = mv.get(0); double bv = Double.NaN;
        for (int[] m : mv) {
            int rest = s.open[me] & ~m[0];
            double v = useWin ? win(s.score[1 - me])[rest] : -E[rest];
            if (Double.isNaN(bv) || v > bv) { bv = v; best = m; }
        }
        return best;
    }
}
