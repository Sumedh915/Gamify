package gamehub.games;

import gamehub.core.GameState;
import java.util.*;

/** Turn-based tag on a 9x9 grid. Whoever spends fewer turns as "it" after MAX plies wins. */
public class Tag extends GameState {
    public static final int SIZE = 9, MAX = 60;
    public int[] r = {0, 8}, c = {0, 8}, itCount = {0, 0};
    public int it = 0, cool = 0, plies = 0;
    public boolean tagged;

    public GameState copy() {
        Tag t = base(new Tag());
        t.r = r.clone(); t.c = c.clone(); t.itCount = itCount.clone(); t.it = it; t.cool = cool; t.plies = plies; return t;
    }

    public int dist() { return Math.abs(r[0] - r[1]) + Math.abs(c[0] - c[1]); }

    public List<int[]> moves(int p) {
        List<int[]> l = new ArrayList<>();
        if (!canMove(p)) return l;
        for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
            int nr = r[p] + d[0], nc = c[p] + d[1];
            if (nr < 0 || nc < 0 || nr >= SIZE || nc >= SIZE || (nr == r[1 - p] && nc == c[1 - p])) continue;
            l.add(mv(nr, nc));
        }
        return l;
    }

    public void apply(int p, int[] m) {
        r[p] = m[0]; c[p] = m[1]; plies++; tagged = false;
        if (p == it && cool == 0 && dist() == 1) { it = 1 - it; cool = 2; tagged = true; }
        else if (cool > 0) cool--;
        itCount[it]++;
        if (plies >= MAX) { winner = itCount[0] < itCount[1] ? 0 : itCount[1] < itCount[0] ? 1 : 2; return; }
        turn = 1 - p;
    }

    protected void fill(Map<String, Object> m, int v) {
        m.put("size", SIZE); m.put("r", r); m.put("c", c); m.put("it", it); m.put("cool", cool);
        m.put("left", MAX - plies); m.put("itCount", itCount); m.put("tagged", tagged);
    }
}
