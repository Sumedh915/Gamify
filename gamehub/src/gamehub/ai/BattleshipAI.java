package gamehub.ai;

import gamehub.core.*;
import gamehub.games.Battleship;
import java.util.*;

/** Uses only public information (shots and sunk ships), never the opponent's fleet layout. */
public class BattleshipAI implements AI {
    public int[] choose(GameState st, int me, String level, Random rng) {
        Battleship b = (Battleship) st;
        if (b.phase == 0) return GameState.mv(1);
        int[] sh = b.shot[1 - me];
        List<Integer> free = new ArrayList<>(), hits = new ArrayList<>();
        for (int i = 0; i < 100; i++) { if (sh[i] == 0) free.add(i); if (sh[i] == 2) hits.add(i); }
        if (level.equals("easy")) return GameState.mv(free.get(rng.nextInt(free.size())));

        if (level.equals("hard")) {
            List<Integer> target = new ArrayList<>();
            for (int h : hits) for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int r = h / 10 + d[0], c = h % 10 + d[1];
                if (r >= 0 && r < 10 && c >= 0 && c < 10 && sh[r * 10 + c] == 0) target.add(r * 10 + c);
            }
            if (!target.isEmpty()) return GameState.mv(target.get(rng.nextInt(target.size())));
            List<Integer> par = new ArrayList<>();
            for (int f : free) if ((f / 10 + f % 10) % 2 == 0) par.add(f);
            List<Integer> pool = par.isEmpty() ? free : par;
            return GameState.mv(pool.get(rng.nextInt(pool.size())));
        }

        // unbeatable: probability density over every placement still consistent with what we know
        double[] score = new double[100];
        for (int s = 0; s < 5; s++) {
            if (b.left[1 - me][s] == 0) continue;     // sunk ships are public
            int len = Battleship.LEN[s];
            for (int dir = 0; dir < 2; dir++)
                for (int r = 0; r < (dir == 0 ? 10 : 11 - len); r++)
                    for (int c = 0; c < (dir == 0 ? 11 - len : 10); c++) {
                        int[] cells = new int[len]; boolean ok = true; int hitCover = 0;
                        for (int i = 0; i < len; i++) {
                            cells[i] = (r + (dir == 1 ? i : 0)) * 10 + c + (dir == 0 ? i : 0);
                            int v = sh[cells[i]];
                            if (v == 1 || v == 3) { ok = false; break; }
                            if (v == 2) hitCover++;
                        }
                        if (!ok) continue;
                        double w = hitCover > 0 ? 50.0 * hitCover * hitCover : 1;
                        for (int x : cells) if (sh[x] == 0) score[x] += w;
                    }
        }
        double best = -1; List<Integer> bs = new ArrayList<>();
        for (int f : free) { if (score[f] > best + 1e-9) { best = score[f]; bs.clear(); bs.add(f); } else if (Math.abs(score[f] - best) <= 1e-9) bs.add(f); }
        return GameState.mv(bs.get(rng.nextInt(bs.size())));
    }
}
