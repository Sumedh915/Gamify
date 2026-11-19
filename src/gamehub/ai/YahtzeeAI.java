package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Yahtzee;
import java.util.*;

public class YahtzeeAI implements AI {
    private static final double[] AVG = {2.1, 5.3, 8.6, 12.1, 15.7, 19.2, 21.7, 13.1, 22.9, 29.5, 32.7, 16.9, 22.0};

    private static double val(int cat, int sc) { return cat < 6 ? (sc - 3.0 * (cat + 1)) * 1.2 + (sc >= 3 * (cat + 1) ? 2 : 0) : sc - AVG[cat]; }

    private static int bestCat(int[] open, int[] dice, double[] out) {
        int bc = -1; double bv = -1e9;
        for (int c = 0; c < 13; c++) if (open[c] == -1) { double v = val(c, Yahtzee.score(c, dice)); if (v > bv) { bv = v; bc = c; } }
        out[0] = bv; return bc;
    }

    // ---- exact expectimax over rerolls (per decision, card fixed) ----
    private HashMap<Integer, Double> memo = new HashMap<>();
    private int[] card;

    private static int key(int[] d, int rolls) { int[] s = d.clone(); Arrays.sort(s); int k = rolls; for (int x : s) k = k * 7 + x; return k; }

    private double best(int[] d, int rolls) {
        int k = key(d, rolls); Double c = memo.get(k); if (c != null) return c;
        double[] o = new double[1]; bestCat(card, d, o); double v = o[0];
        if (rolls > 0) for (int mask = 1; mask < 32; mask++) v = Math.max(v, rerollEv(d, mask, rolls));
        memo.put(k, v); return v;
    }

    private double rerollEv(int[] d, int mask, int rolls) {
        int[] idx = new int[5]; int n = 0;
        for (int i = 0; i < 5; i++) if ((mask >> i & 1) != 0) idx[n++] = i;
        int total = 1; for (int i = 0; i < n; i++) total *= 6;
        int[] nd = d.clone(); double sum = 0;
        for (int t = 0; t < total; t++) {
            int x = t; for (int i = 0; i < n; i++) { nd[idx[i]] = x % 6 + 1; x /= 6; }
            sum += best(nd, rolls - 1);
        }
        return sum / total;
    }

    public int[] choose(GameState st, int me, String level, Random rng) {
        Yahtzee y = (Yahtzee) st; card = y.card[me];
        if (level.equals("easy")) {
            if (y.rolls > 0 && rng.nextDouble() < 0.5) return new int[]{0, 1 + rng.nextInt(31)};
            double[] o = new double[1]; return new int[]{1, bestCat(card, y.dice, o)};
        }
        double[] o = new double[1]; int cat = bestCat(card, y.dice, o); double now = o[0];
        if (y.rolls == 0) return new int[]{1, cat};
        if (level.equals("hard")) {
            if (Yahtzee.score(cat, y.dice) >= 25 && cat >= 8) return new int[]{1, cat};
            int[] n = new int[7]; for (int x : y.dice) n[x]++;
            int face = 1; for (int f = 1; f <= 6; f++) if (n[f] > n[face] || (n[f] == n[face] && f > face)) face = f;
            int mask = 0; for (int i = 0; i < 5; i++) if (y.dice[i] != face) mask |= 1 << i;
            if (mask == 0) return new int[]{1, cat};
            return new int[]{0, mask};
        }
        memo.clear();
        int bm = 0; double bv = now;
        for (int mask = 1; mask < 32; mask++) { double v = rerollEv(y.dice, mask, y.rolls); if (v > bv) { bv = v; bm = mask; } }
        return bm == 0 ? new int[]{1, cat} : new int[]{0, bm};
    }
}
